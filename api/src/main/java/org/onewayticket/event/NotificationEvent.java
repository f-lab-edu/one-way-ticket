package org.onewayticket.event;

import org.onewayticket.domain.Flight;
import org.onewayticket.domain.PriceAlert;

import java.util.UUID;

public record NotificationEvent(String eventId, PriceAlert alert, Flight flight) {
    public static NotificationEvent of(PriceAlert alert, Flight flight) {
        return new NotificationEvent(UUID.randomUUID().toString(), alert, flight);
    }
}
