package com.example.kafka.multithread;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pattern 1: One Consumer Per Thread
 * 
 * This is the simplest multi-threading model for Kafka consumers.
 * Each thread runs its own KafkaConsumer instance.
 * 
 * PROS:
 * - Easy to implement.
 * - No inter-thread coordination required.
 * - Maintains message ordering per partition naturally.
 * 
 * CONS:
 * - More TCP connections (one per thread).
 * - Total threads across all processes are limited by the total number of partitions.
 */
public class MultithreadedConsumerPerThread {
    private static final Logger logger = LoggerFactory.getLogger(MultithreadedConsumerPerThread.class);

    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String groupId = "multithread-per-thread-group";
        String topic = "test-topic";
        int threadCount = 3; // Should be <= Number of Partitions

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(new ConsumerWorker(bootstrapServers, groupId, topic, i));
        }

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown requested...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }

    public static class ConsumerWorker implements Runnable {
        private final KafkaConsumer<String, String> consumer;
        private final String topic;
        private final int threadId;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        public ConsumerWorker(String bootstrapServers, String groupId, String topic, int threadId) {
            this.topic = topic;
            this.threadId = threadId;

            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            this.consumer = new KafkaConsumer<>(props);
        }

        @Override
        public void run() {
            try {
                consumer.subscribe(Collections.singletonList(topic));
                logger.info("Thread-{} started consuming from {}", threadId, topic);

                while (!closed.get()) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Thread-{} | Partition: {} | Offset: {} | Key: {} | Value: {}",
                                threadId, record.partition(), record.offset(), record.key(), record.value());
                        
                        // Simulate high-latency processing
                        processRecord(record);
                    }
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
                logger.info("Thread-{} closed.", threadId);
            }
        }

        private void processRecord(ConsumerRecord<String, String> record) {
            try {
                Thread.sleep(50); // Simulate business logic
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
    }
}
