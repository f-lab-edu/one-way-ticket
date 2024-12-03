package org.onewayticket.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onewayticket.domain.Flight;
import org.onewayticket.repository.FlightRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    @Test
    @DisplayName("유효한 flightId로 항공권 상세 조회를 할 수 있습니다.")
    void Get_flightDetails_with_valid_flightId() {
        // given
        String flightId = "1";
        Flight mockFlight = Flight.builder().id(Long.parseLong(flightId)).build();
        Mockito.when(flightRepository.findById(1L)).thenReturn(Optional.of(mockFlight));

        // when
        Flight result = flightService.getFlightDetails(flightId);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        Mockito.verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("유효하지 않은 flightId로 항공권 상세 조회 시 404 에러가 발생합니다.")
    void Get_flightDetails_with_invalid_flightId_throws_exception() {
        // given
        String flightId = "2";
        Mockito.when(flightRepository.findById(2L)).thenReturn(Optional.empty());

        // when then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> flightService.getFlightDetails(flightId));
        assertEquals("해당 flightId가 존재하지 않습니다. 2", exception.getMessage());
        Mockito.verify(flightRepository, times(1)).findById(2L);
    }


    @Test
    @DisplayName("항공편이 존재하지 않는다면 404 예외를 발생시킵니다.")
    void Throw_404_if_cheapest_flights_not_exist_with_current_location() {
        // given
        String origin = "ICN";
        Mockito.when(flightRepository.findByOriginOrderByAmountAsc(origin)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> flightService.getCheapestFlights());
    }

    @Test
    @DisplayName("출발지, 목적지, 날짜 및 정렬 옵션으로 항공권 검색을 할 수 있습니다.")
    void Search_flights_with_valid_parameters() {
        // given
        String origin = "ICN";
        String destination = "JFK";
        String departureDate = "2024-12-01";
        String sort = "price";
        Flight flight1 = Flight.builder().amount(new BigDecimal(200)).build();
        Flight flight2 = Flight.builder().amount(new BigDecimal(150)).build();
        List<Flight> mockFlights = new ArrayList<>();
        mockFlights.add(flight1);
        mockFlights.add(flight2);
        Mockito.when(flightRepository.searchFlights(origin, destination, LocalDate.parse(departureDate)))
                .thenReturn(Optional.of(mockFlights));

        // when
        List<Flight> result = flightService.searchFlights(origin, destination, departureDate, sort);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal(150), result.get(0).getAmount());
        Mockito.verify(flightRepository, times(1))
                .searchFlights(origin, destination, LocalDate.parse(departureDate));
    }

    @Test
    @DisplayName("잘못된 날짜 포맷이 입력되면 Illegal 예외가 발생합니다.")
    void Search_flights_with_invalid_date_format() {
        // given
        String invalidDate = "01-12-2024";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.searchFlights("ICN", "JFK", invalidDate, "price")
        );
        assertTrue(exception.getMessage().contains("입력한 날짜 포맷을 확인해주세요"));
    }

    @Test
    @DisplayName("잘못된 정렬 방식이 입력되면 Illegal 예외가 발생합니다.")
    void Search_flights_with_invalid_sort_option() {
        // given
        String invalidSort = "invalidOption";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flightService.searchFlights("ICN", "JFK", "2024-12-01", invalidSort)
        );
        assertTrue(exception.getMessage().contains("잘못된 정렬 방식입니다"));
    }

    @Test
    @DisplayName("조건에 맞는 항공권이 존재하지 않으면 404가 반환됩니다.")
    void Search_flights_with_no_results() {
        // given
        Mockito.when(flightRepository.searchFlights(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        // when & then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                flightService.searchFlights("ICN", "JFK", "2024-12-01", "price")
        );
        assertTrue(exception.getMessage().contains("해당 조건에 맞는 항공권이 존재하지 않습니다"));
    }

}

