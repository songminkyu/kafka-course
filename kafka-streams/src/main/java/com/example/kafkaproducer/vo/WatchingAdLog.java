package com.example.kafkaproducer.vo;

import lombok.Data;

@Data
public class WatchingAdLog {

    // SAMPLE DATA
    // {"userId": "uid-0007", "productId": "pg-0007", "adId": "ad-101", "adType": "banner", "watchingTime": "30", "watchingDt": "20240201070000"}

    String userId; // uid-0001
    String productId; // pg-0001
    String adId; // ad-101
    String adType;  // banner, clip, main, live
    String watchingTime; // 머문시간
    String watchingDt; // 2024201070000
}
