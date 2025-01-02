package org.onewayticket;

import org.onewayticket.generator.FlightDataGenerator;
import org.onewayticket.generator.FlightDataSaver;
import org.onewayticket.generator.FlightProcessor;
import org.onewayticket.producer.FlightMessageProducer;

public class Main {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/my-db";
    private static final String DB_USER = "tester";
    private static final String DB_PASSWORD = "1234";
    private static final String KAFKA_BROKER = "localhost:9092";

    public static void main(String[] args) {
        FlightDataGenerator generator = new FlightDataGenerator();
        FlightDataSaver saver = new FlightDataSaver(DB_URL, DB_USER, DB_PASSWORD);
        FlightMessageProducer producer = new FlightMessageProducer(KAFKA_BROKER);
        FlightProcessor processor = new FlightProcessor(generator, saver, producer);

        try {
            processor.processFlights(1);
        } catch (Exception e) {
            System.err.println("Error processing flights: " + e.getMessage());
        } finally {
            producer.close();
        }
    }
}