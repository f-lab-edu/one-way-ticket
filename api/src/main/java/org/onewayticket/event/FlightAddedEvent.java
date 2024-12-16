package org.onewayticket.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.onewayticket.domain.Flight;

@AllArgsConstructor
@Getter
public class FlightAddedEvent {
    private final Flight flight;
}
