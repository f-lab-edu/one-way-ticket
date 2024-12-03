package org.onewayticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * TossPaymentController에서 사용되는 restTemplate
     *
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
