package org.onewayticket.event;

import org.onewayticket.domain.Flight;

public record PriceAlertRequestEvent(Flight flight) {
}
