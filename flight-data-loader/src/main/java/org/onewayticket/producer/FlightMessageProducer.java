package org.onewayticket.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class FlightMessageProducer {

    private final KafkaProducer<String, String> producer;
    private static final String TOPIC = "flight-added";

    public FlightMessageProducer(String kafkaBroker) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaBroker);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(String key, String message) {
        producer.send(new ProducerRecord<>(TOPIC, key, message));
    }

    public void close() {
        producer.close();
    }
}
