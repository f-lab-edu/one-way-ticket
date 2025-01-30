package org.onewayticket.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pg.toss")
@Getter
@Setter
public class TossPaymentProperties {
    private String baseUrl; // https://api.tosspayments.com
    private String secretKey; // sk_test_...
}
