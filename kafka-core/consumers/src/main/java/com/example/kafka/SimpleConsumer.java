package com.example.kafka;

import com.example.kafka.services.PropertiesService;
import com.example.kafka.services.PropertiesServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class SimpleConsumer {

    public static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class.getName());

    public static void main(String[] args) {

        String topicName = "simple-topic";

        PropertiesService configService = new PropertiesServiceImpl();
        Properties props  = configService.LoadProperties();

        String bootstrapServers = props.getProperty("bootstrap.servers");
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group-01");

        //인프런 권민철 kafka-core 강의 77번 강의에 대한 내용 - 설정에 따른 heart-bit 테스트
        //props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "simple-group");
        //props.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");
        //props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "90000");
        //props.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "600000");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);
        kafkaConsumer.subscribe(List.of(topicName));

        /**
         poll() 매커니즘 설명
         1. poll은 record가 바로 들어오면 1000ms 대기 없이 바로 수행 하지만,
            가져올 데이터가 1건도 없으면 poll() 인자에 주어진 시간만큼 대기 후 return 한다.
         2. 가져와야할 과거 데이터가 많을 경우 max.partition.fetch.bytes로 배치크기를 설정 하고,
            그렇지 않을 경우 fetch.min.byte로 배치 크기로 설정된다.
         3. 가장 최신의 offset 데이터를 가져오고 있다면 fetch.min.bytes 만큼 가져와서 return 하고,
            fetch.min.bytes 만큼 쌓이지 않는다면 fetch.max.wait.ms 만큼 기다린 후 return한다.
         4. 오랜 과거 offset 데이터를 가져온다면 최대 max.partition.fetch.bytes만큼 파티션에서 읽은뒤
            반환 한다.
         5. max.partions.fetch.bytes에 도달하지 못하여도 가장의 offset에 도달하면 반환한다.
         6. Topic에 파티션이 많아도 가져오는 데이터량은 fetch.max.bytes로 제한한다.
         7. Fetcher가 Linkd Queue에서 가져오는 레코드의 개수는 max.poll.records로 제한 된다.
         */

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord record : consumerRecords) {
                logger.info("record key:{}, record value:{}, partition:{}",
                        record.key(), record.value(), record.partition());
            }
        }
        //kafkaConsumer.close();
    }
}