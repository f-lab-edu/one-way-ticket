package org.onewayticket.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class FlightMessageProducer {
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC = "flight-added";

    public FlightMessageProducer(String kafkaBroker) {
        objectMapper.registerModule(new JavaTimeModule());
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaBroker);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(props);
    }


    public void produce(String key, Object flight) {
        try {
            String message = objectMapper.writeValueAsString(flight);
            producer.send(new ProducerRecord<>(TOPIC, key, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        producer.close();
    }
}