package com.example.kafkaproducer.service;

import com.example.kafkaproducer.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    KafkaConfig myConfig;

    String topicName = "defaultTopic";

    private KafkaTemplate<String, Object> kafkaTemplate;

//    public Producer(KafkaTemplate kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void pub(String msg) {
        kafkaTemplate = myConfig.KafkaTemplateForGeneral();
        kafkaTemplate.send(topicName, msg);
    }

    public void sendJoinedMsg (String topicNm, Object msg) {
        kafkaTemplate = myConfig.KafkaTemplateForGeneral();
        kafkaTemplate.send(topicNm, msg);
    }
    public void sendMsgForWatchingAdLog (String topicNm, Object msg) {
        kafkaTemplate = myConfig.KafkaTemplateForWatchingAdLog();
        kafkaTemplate.send(topicNm, msg);
    }

    public void sendMsgForPurchaseLog (String topicNm, Object msg) {
        kafkaTemplate = myConfig.KafkaTemplateForPurchaseLog();
        kafkaTemplate.send(topicNm, msg);
    }
}