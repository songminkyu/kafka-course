package com.practice.kafka.producer;

import com.practice.kafka.event.EventHandler;
import com.practice.kafka.event.FileEventHandler;
import com.practice.kafka.event.FileEventSource;
import com.practice.kafka.services.PropertiesService;
import com.practice.kafka.services.PropertiesServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public class FileAppendProducer {
    public static final Logger logger = LoggerFactory.getLogger(FileAppendProducer.class.getName());
    public static void main(String[] args) {
        String topicName = "file-topic";

        //KafkaProducer configuration setting
        // null, "hello world"

        PropertiesService configService = new PropertiesServiceImpl();
        Properties props  = configService.LoadProperties();

        String bootstrapServers = props.getProperty("bootstrap.servers");
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //KafkaProducer object creation
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
        boolean sync = false;

        File file = new File("C:\\Users\\q\\IdeaProjects\\my-app\\KafkaProj-01\\practice\\src\\main\\resources\\pizza_append.txt");
        EventHandler eventHandler = new FileEventHandler(kafkaProducer, topicName, sync);
        FileEventSource fileEventSource = new FileEventSource(100, file, eventHandler);
        Thread fileEventSourceThread = new Thread(fileEventSource);
        fileEventSourceThread.start();

        try {
            fileEventSourceThread.join();
        }catch(InterruptedException e) {
            logger.error(e.getMessage());
        }finally {
            kafkaProducer.close();
        }

    }
}
