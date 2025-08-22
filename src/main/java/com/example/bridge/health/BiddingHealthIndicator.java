package com.example.bridge.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BiddingHealthIndicator implements HealthIndicator {

    private boolean ready = true;

    @Override
    public Health health() {
        if (!ready) {
            return Health.down()
                    .withDetail("status", "Bidding service is not ready")
                    .build();
        }
        return Health.up()
                .withDetail("status", "Bidding service is ready")
                .withDetail("version", "1.0.0")
                .build();
    }

    // For testing purposes - can be called to simulate service degradation
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
