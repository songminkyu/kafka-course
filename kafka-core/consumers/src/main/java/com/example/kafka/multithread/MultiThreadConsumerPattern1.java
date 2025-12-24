package com.example.kafka.multithread;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

// ============================================================================
// 패턴 1: One Consumer Per Thread (간단하고 안전한 방식)
// ============================================================================
public class MultiThreadConsumerPattern1 {
    
    // 각 스레드가 독립적인 Consumer 인스턴스를 가짐
    static class ConsumerThread implements Runnable {
        private final KafkaConsumer<String, String> consumer;
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final int threadId;
        
        public ConsumerThread(int threadId, Properties props, List<String> topics) {
            this.threadId = threadId;
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(topics);
        }
        
        @Override
        public void run() {
            try {
                while (!closed.get()) {
                    ConsumerRecords<String, String> records = 
                        consumer.poll(Duration.ofMillis(1000));
                    
                    for (ConsumerRecord<String, String> record : records) {
                        processRecord(record);
                    }
                    
                    // 수동 커밋 (필요 시)
                    consumer.commitSync();
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
                System.out.println("Consumer thread " + threadId + " closed");
            }
        }
        
        private void processRecord(ConsumerRecord<String, String> record) {
            System.out.printf("[Thread-%d] Partition=%d, Offset=%d, Key=%s, Value=%s%n",
                threadId, record.partition(), record.offset(), 
                record.key(), record.value());
            
            // 실제 비즈니스 로직 처리
            try {
                Thread.sleep(100); // 처리 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
    }
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "multi-thread-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        
        List<String> topics = Arrays.asList("my-topic");
        int numThreads = 3;
        
        // 여러 Consumer 스레드 시작
        List<ConsumerThread> consumers = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            ConsumerThread consumer = new ConsumerThread(i, props, topics);
            consumers.add(consumer);
            executor.submit(consumer);
        }
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumers...");
            consumers.forEach(ConsumerThread::shutdown);
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }
}

// ============================================================================
// 패턴 2: Decouple Consumption and Processing (확장성 높은 방식)
// ============================================================================

class MultiThreadConsumerPattern2 {
    
    // Consumer 스레드: 메시지를 읽어서 큐에 넣음
    static class ConsumerWorker implements Runnable {
        private final KafkaConsumer<String, String> consumer;
        private final BlockingQueue<ConsumerRecord<String, String>> queue;
        private final AtomicBoolean closed = new AtomicBoolean(false);
        
        public ConsumerWorker(Properties props, List<String> topics, 
                            BlockingQueue<ConsumerRecord<String, String>> queue) {
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(topics);
            this.queue = queue;
        }
        
        @Override
        public void run() {
            try {
                while (!closed.get()) {
                    ConsumerRecords<String, String> records = 
                        consumer.poll(Duration.ofMillis(1000));
                    
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            queue.put(record);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
            } finally {
                consumer.close();
            }
        }
        
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
    }
    
    // Processor 스레드: 큐에서 메시지를 꺼내 처리
    static class ProcessorWorker implements Runnable {
        private final BlockingQueue<ConsumerRecord<String, String>> queue;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final int workerId;
        
        public ProcessorWorker(int workerId, 
                              BlockingQueue<ConsumerRecord<String, String>> queue) {
            this.workerId = workerId;
            this.queue = queue;
        }
        
        @Override
        public void run() {
            while (running.get()) {
                try {
                    ConsumerRecord<String, String> record = 
                        queue.poll(1, TimeUnit.SECONDS);
                    
                    if (record != null) {
                        processRecord(record);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        private void processRecord(ConsumerRecord<String, String> record) {
            System.out.printf("[Processor-%d] Processing: Partition=%d, Offset=%d%n",
                workerId, record.partition(), record.offset());
            
            // 실제 비즈니스 로직 (DB 저장, API 호출 등)
            try {
                Thread.sleep(200); // 처리 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        public void shutdown() {
            running.set(false);
        }
    }
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "decouple-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        
        // 공유 큐
        BlockingQueue<ConsumerRecord<String, String>> queue = 
            new LinkedBlockingQueue<>(10000);
        
        // Consumer 스레드 시작 (1개)
        List<String> topics = Arrays.asList("my-topic");
        ConsumerWorker consumerWorker = new ConsumerWorker(props, topics, queue);
        Thread consumerThread = new Thread(consumerWorker);
        consumerThread.start();
        
        // Processor 스레드들 시작 (여러 개)
        int numProcessors = 5;
        List<ProcessorWorker> processors = new ArrayList<>();
        ExecutorService processorPool = Executors.newFixedThreadPool(numProcessors);
        
        for (int i = 0; i < numProcessors; i++) {
            ProcessorWorker processor = new ProcessorWorker(i, queue);
            processors.add(processor);
            processorPool.submit(processor);
        }
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            consumerWorker.shutdown();
            processors.forEach(ProcessorWorker::shutdown);
            processorPool.shutdown();
        }));
    }
}

// ============================================================================
// 패턴 3: 파티션별 순서 보장이 필요한 경우
// ============================================================================

class MultiThreadConsumerPattern3 {
    
    static class PartitionAwareProcessor {
        private final KafkaConsumer<String, String> consumer;
        private final Map<Integer, BlockingQueue<ConsumerRecord<String, String>>> 
            partitionQueues;
        private final ExecutorService processorPool;
        private final AtomicBoolean closed = new AtomicBoolean(false);
        
        public PartitionAwareProcessor(Properties props, List<String> topics, 
                                      int numPartitions) {
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(topics);
            this.partitionQueues = new ConcurrentHashMap<>();
            this.processorPool = Executors.newFixedThreadPool(numPartitions);
            
            // 각 파티션마다 별도의 큐와 처리 스레드 할당
            for (int i = 0; i < numPartitions; i++) {
                final int partition = i;
                BlockingQueue<ConsumerRecord<String, String>> queue = 
                    new LinkedBlockingQueue<>(1000);
                partitionQueues.put(partition, queue);
                
                // 파티션별 처리 스레드
                processorPool.submit(() -> processPartition(partition, queue));
            }
        }
        
        public void start() {
            while (!closed.get()) {
                ConsumerRecords<String, String> records = 
                    consumer.poll(Duration.ofMillis(1000));
                
                // 각 레코드를 해당 파티션의 큐로 라우팅
                for (ConsumerRecord<String, String> record : records) {
                    int partition = record.partition();
                    BlockingQueue<ConsumerRecord<String, String>> queue = 
                        partitionQueues.get(partition);
                    
                    if (queue != null) {
                        try {
                            queue.put(record);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }
        }
        
        private void processPartition(int partition, 
                                     BlockingQueue<ConsumerRecord<String, String>> queue) {
            while (!closed.get()) {
                try {
                    ConsumerRecord<String, String> record = 
                        queue.poll(1, TimeUnit.SECONDS);
                    
                    if (record != null) {
                        System.out.printf("[Partition-%d] Processing offset %d: %s%n",
                            partition, record.offset(), record.value());
                        
                        // 같은 파티션 내에서는 순서가 보장됨
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
            processorPool.shutdown();
        }
    }
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "partition-aware-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class.getName());
        
        List<String> topics = Arrays.asList("my-topic");
        int numPartitions = 4;
        
        PartitionAwareProcessor processor = 
            new PartitionAwareProcessor(props, topics, numPartitions);
        
        Thread processorThread = new Thread(processor::start);
        processorThread.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(processor::shutdown));
    }
}