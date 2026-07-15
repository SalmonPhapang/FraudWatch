
package com.fraudwatch.fraudruleengine.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic fraudTransactionsTopic() {
        return TopicBuilder.name("fraud-transactions")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

