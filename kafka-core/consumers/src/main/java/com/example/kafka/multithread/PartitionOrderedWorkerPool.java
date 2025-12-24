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
 * Pattern 3: Partition-Ordered Worker Pool
 * 
 * This model ensures that records from the same partition are processed sequentially,
 * while records from different partitions can be processed in parallel across multiple threads.
 * 
 * HOW IT WORKS:
 * - A main consumer thread polls records.
 * - Records are grouped by partition.
 * - For each partition, a specific worker (or a single-threaded executor) is assigned.
 * - This prevents the "out-of-order" processing issue of a simple thread pool.
 */
public class PartitionOrderedWorkerPool {
    private static final Logger logger = LoggerFactory.getLogger(PartitionOrderedWorkerPool.class);

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    // Map of partition to a single-threaded executor to ensure order within that partition
    private final Map<TopicPartition, ExecutorService> partitionExecutors = new ConcurrentHashMap<>();

    public PartitionOrderedWorkerPool(String bootstrapServers, String groupId, String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // For simplicity in this example
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void start() {
        try {
            consumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    logger.info("Partitions revoked: {}. Shutting down associated executors.", partitions);
                    for (TopicPartition tp : partitions) {
                        ExecutorService executor = partitionExecutors.remove(tp);
                        if (executor != null) {
                            executor.shutdown();
                            try {
                                executor.awaitTermination(5, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    logger.info("Partitions assigned: {}. Initializing executors.", partitions);
                    for (TopicPartition tp : partitions) {
                        partitionExecutors.putIfAbsent(tp, Executors.newSingleThreadExecutor());
                    }
                }
            });

            while (!closed.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (TopicPartition tp : records.partitions()) {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(tp);
                    ExecutorService executor = partitionExecutors.get(tp);
                    
                    if (executor != null) {
                        // Submit the entire batch for this partition to the single-threaded executor
                        // or submit individually; the executor will process them in arrival order.
                        executor.submit(() -> {
                            for (ConsumerRecord<String, String> record : partitionRecords) {
                                processRecord(record);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in consumer loop", e);
        } finally {
            cleanup();
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        logger.info("Processing Partition: {}, Offset: {}, Value: {}", 
                record.partition(), record.offset(), record.value());
        try {
            // Simulate variable processing time
            Thread.sleep(new Random().nextInt(100)); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cleanup() {
        logger.info("Cleaning up PartitionOrderedWorkerPool...");
        for (ExecutorService executor : partitionExecutors.values()) {
            executor.shutdownNow();
        }
        partitionExecutors.clear();
        consumer.close();
        logger.info("Consumer closed.");
    }

    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
    }
}
