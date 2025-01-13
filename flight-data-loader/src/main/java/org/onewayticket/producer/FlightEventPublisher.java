package org.onewayticket.producer;

import lombok.RequiredArgsConstructor;
import org.onewayticket.flight.FlightProto;
import org.onewayticket.model.Flight;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightEventPublisher {
    private final KafkaTemplate<String, FlightProto.Flight> kafkaTemplate;

    public void publish(Flight flight) {
        // Protobuf 메시지 생성
        FlightProto.Flight flightProto = FlightProto.Flight.newBuilder()
                .setId(flight.getId())
                .setFlightNumber(flight.getFlightNumber())
                .setAmount(flight.getAmount().doubleValue())
                .setDepartureTime(flight.getDepartureTime().toString())
                .setArrivalTime(flight.getArrivalTime().toString())
                .setOrigin(flight.getOrigin())
                .setDestination(flight.getDestination())
                .setDurationInMinutes(flight.getDurationInMinutes())
                .setCarrier(flight.getCarrier())
                .build();

        // Protobuf 메시지 전송
        kafkaTemplate.send("flight-added", flightProto);
    }
}
