package com.practice.kafka.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesServiceImpl implements PropertiesService {
    public final Logger logger = LoggerFactory.getLogger(PropertiesServiceImpl.class.getName());

    @Override
    public Properties LoadProperties() {
        Properties props  = new Properties();

        // application.properties 파일 읽기
        try (InputStream input = PropertiesServiceImpl.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("파일을 찾을 수 없습니다: application.properties");
                return null;
            }
            props.load(input); // 파일의 모든 설정을 props에 로드
        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }
}
