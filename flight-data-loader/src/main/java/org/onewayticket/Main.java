package org.onewayticket;

import org.onewayticket.generator.FlightDataGenerator;
import org.onewayticket.model.Flight;
import org.onewayticket.saver.DataSaver;

import java.util.List;

public class Main {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/my-db";
    private static final String DB_USER = "tester";
    private static final String DB_PASSWORD = "1234";

    public static void main(String[] args) {
        FlightDataGenerator generator = new FlightDataGenerator();
        DataSaver saver = new DataSaver(DB_URL, DB_USER, DB_PASSWORD);
        try {
            List<Flight> flights = generator.generateRandomFlights(10);
            saver.saveFlights(flights);
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

}