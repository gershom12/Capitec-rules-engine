package com.fraud.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fraud.rules")
@Data
@Slf4j
public class RuleConfig {

    private HighValue highValue;
    private Location location;
    private Velocity velocity;

    @PostConstruct
    public void init() {
        log.info("event=rule_config_loaded highValue={} location={} velocity={}",
                highValue, location, velocity);
    }

    @Data
    public static class HighValue {
        private double threshold;
    }

    @Data
    public static class Location {
        private String allowedCountry;
    }

    @Data
    public static class Velocity {
        private int maxTransactions;
        private int windowSeconds;
    }
}