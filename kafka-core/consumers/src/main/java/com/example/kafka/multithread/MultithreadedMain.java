package com.example.kafka.multithread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Main class to run and demonstrate different multi-threaded Kafka consumer patterns.
 */
public class MultithreadedMain {
    private static final Logger logger = LoggerFactory.getLogger(MultithreadedMain.class);

    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "test-topic";
        String groupId = "multithread-demo-group";

        System.out.println("Select a Multi-threaded Consumer Pattern to run:");
        System.out.println("1: One Consumer Per Thread (Simple Scaling)");
        System.out.println("2: Decoupled Worker Pool (Backpressure & Manual Commit)");
        System.out.println("3: Partition-Ordered Worker Pool (Ordered Parallelism)");
        System.out.println("Type 'q' to quit.");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                runPattern1(bootstrapServers, groupId, topic);
                break;
            case "2":
                runPattern2(bootstrapServers, groupId, topic);
                break;
            case "3":
                runPattern3(bootstrapServers, groupId, topic);
                break;
            case "q":
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void runPattern1(String bootstrapServers, String groupId, String topic) {
        logger.info("Starting Pattern 1: One Consumer Per Thread...");
        MultithreadedConsumerPerThread.main(new String[]{});
    }

    private static void runPattern2(String bootstrapServers, String groupId, String topic) {
        logger.info("Starting Pattern 2: Decoupled Worker Pool...");
        DecoupledConsumerWorkerPool consumer = new DecoupledConsumerWorkerPool(bootstrapServers, groupId, topic, 10);
        
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::shutdown));
        consumer.start();
    }

    private static void runPattern3(String bootstrapServers, String groupId, String topic) {
        logger.info("Starting Pattern 3: Partition-Ordered Worker Pool...");
        PartitionOrderedWorkerPool consumer = new PartitionOrderedWorkerPool(bootstrapServers, groupId, topic);
        
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::shutdown));
        consumer.start();
    }
}
