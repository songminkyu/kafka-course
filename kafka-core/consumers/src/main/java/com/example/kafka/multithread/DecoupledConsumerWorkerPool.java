package com.example.kafka.multithread;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pattern 2: Decoupled Consumption and Processing
 * 
 * A single consumer thread polls messages and delegates them to a worker pool.
 * 
 * PROS:
 * - Can scale workers independently of partitions.
 * - Efficient resource usage (fewer TCP connections).
 * 
 * CONS:
 * - In-order processing is NOT guaranteed (messages in the same partition can be processed concurrently).
 * - Offset management is complex.
 * 
 * KEY FEATURES IN THIS EXAMPLE:
 * - Manual Offset Management: Tracking processed offsets.
 * - Backpressure: Pausing partitions if workers are overwhelmed.
 */
public class DecoupledConsumerWorkerPool {
    private static final Logger logger = LoggerFactory.getLogger(DecoupledConsumerWorkerPool.class);

    private final KafkaConsumer<String, String> consumer;
    private final ExecutorService workerPool;
    private final String topic;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    // Track offsets to commit
    private final Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new ConcurrentHashMap<>();

    public DecoupledConsumerWorkerPool(String bootstrapServers, String groupId, String topic, int workerThreadCount) {
        this.topic = topic;
        this.workerPool = Executors.newFixedThreadPool(workerThreadCount);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Manual commit is required
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void start() {
        try {
            consumer.subscribe(Collections.singletonList(topic));
            logger.info("Decoupled Consumer started...");

            while (!closed.get()) {
                // 1. Backpressure management (Simplified)
                // If the worker pool is too busy, we can pause the consumer
                handleBackpressure();

                // 2. Poll for records
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                // 3. Hand off records to workers
                for (ConsumerRecord<String, String> record : records) {
                    workerPool.submit(new WorkerTask(record));
                }

                // 4. Periodically commit offsets that have been processed
                commitProcessedOffsets();
            }
        } finally {
            cleanup();
        }
    }

    private void handleBackpressure() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) workerPool;
        int activeTasks = tpe.getActiveCount() + tpe.getQueue().size();
        
        Set<TopicPartition> assignment = consumer.assignment();
        if (activeTasks > 100) { // arbitrary threshold
            if (!consumer.paused().containsAll(assignment)) {
                logger.warn("Worker pool overwhelmed ({} tasks). Pausing consumption.", activeTasks);
                consumer.pause(assignment);
            }
        } else {
            if (!consumer.paused().isEmpty()) {
                logger.info("Worker pool recovered ({} tasks). Resuming consumption.", activeTasks);
                consumer.resume(consumer.paused());
            }
        }
    }

    private void commitProcessedOffsets() {
        if (!offsetsToCommit.isEmpty()) {
            synchronized (offsetsToCommit) {
                try {
                    consumer.commitAsync(offsetsToCommit, (offsets, exception) -> {
                        if (exception != null) {
                            logger.error("Offset commit failed for {}", offsets, exception);
                        }
                    });
                    offsetsToCommit.clear();
                } catch (Exception e) {
                    logger.error("Error during commit", e);
                }
            }
        }
    }

    private void cleanup() {
        logger.info("Cleaning up...");
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
        }
        consumer.close();
        logger.info("Consumer closed.");
    }

    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
    }

    private class WorkerTask implements Runnable {
        private final ConsumerRecord<String, String> record;

        public WorkerTask(ConsumerRecord<String, String> record) {
            this.record = record;
        }

        @Override
        public void run() {
            try {
                // Simulate processing
                logger.info("Processing Partition: {}, Offset: {}, Value: {}", 
                        record.partition(), record.offset(), record.value());
                Thread.sleep(100);

                // Mark as processed (offset to commit is next offset)
                TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                synchronized (offsetsToCommit) {
                    offsetsToCommit.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
