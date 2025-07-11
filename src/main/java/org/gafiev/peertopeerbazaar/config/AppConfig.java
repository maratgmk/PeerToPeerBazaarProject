package org.gafiev.peertopeerbazaar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.properties.DroneProperties;
import org.gafiev.peertopeerbazaar.properties.PaymentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@Configuration
@EnableScheduling
@AllArgsConstructor
@Import(value = {PaymentProperties.class, DroneProperties.class})
public class AppConfig {
    private final PaymentProperties paymentProperties;
    private final DroneProperties droneProperties;

    @Bean
    public RestClient droneOperatorClient() {
        return RestClient.builder()
                .baseUrl(droneProperties.getClientUri())
                .build();
    }

    @Bean
    public RestClient externalPaymentServiceClient() {
        return RestClient.builder()
                .baseUrl(paymentProperties.getClientUri())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
