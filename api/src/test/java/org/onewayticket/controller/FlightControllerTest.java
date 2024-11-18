package org.onewayticket.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("최저가 항공편 조회 - 상위 9개 목적지 반환")
    public void get_cheapest_flights_should_return_top_9_destinations_sorted_by_price() throws Exception {
        // Arrange: 테스트 데이터는 컨트롤러에서 처리
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/flights/cheapest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9))) // 상위 9개만 반환 확인
                .andExpect(jsonPath("$[0].destination").value("NRT")) // 가장 저렴한 항공편
                .andExpect(jsonPath("$[1].destination").value("HND")) // 두 번째 저렴한 항공편
                .andExpect(jsonPath("$[8].destination").value("SIN")); // 아홉 번째 항공편
    }


    @Test
    @DisplayName("항공권 검색 - 유효한 조건")
    public void search_flights_should_return_200_for_valid_conditions() throws Exception {
        // Arrange
        String validSearchRequest = """
                {
                    "departure": "Seoul",
                    "destination": "Beijing",
                    "departureDate": "2023-12-25T10:00:00",
                    "numberOfPassengers": 1
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSearchRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("항공권 검색 - 출발지와 목적지가 같은 경우")
    public void search_flights_should_return_400_when_departure_and_destination_are_same() throws Exception {
        // Arrange
        String invalidSearchRequest = """
                {
                    "departure": "Seoul",
                    "destination": "Seoul",
                    "departureDate": "2023-12-25T10:00:00",
                    "numberOfPassengers": 1
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidSearchRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("항공권 검색 - 조건에 맞는 데이터가 없는 경우")
    public void search_flights_should_return_200_with_empty_list_when_no_matching_data() throws Exception {
        // Arrange
        String noMatchSearchRequest = """
                {
                    "departure": "Seoul",
                    "destination": "Mars",
                    "departureDate": "2023-12-25T10:00:00",
                    "numberOfPassengers": 1
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(noMatchSearchRequest))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    @DisplayName("항공권 검색 - 탑승 인원 유효하지 않은 경우")
    public void search_flights_should_return_400_when_passenger_count_is_invalid() throws Exception {
        // Arrange
        String invalidPassengerRequest = """
                {
                    "departure": "Seoul",
                    "destination": "Tokyo",
                    "departureDate": "2023-12-25T10:00:00",
                    "numberOfPassengers": -1
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/flights/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPassengerRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("항공편 상세 조회 - 경계값: 매우 긴 flightId")
    public void get_flight_details_should_return_400_when_flight_id_is_too_long() throws Exception {
        // Arrange
        String longFlightId = "FL".repeat(200); // 매우 긴 ID 생성

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/flights/{flightId}", longFlightId))
                .andExpect(status().isBadRequest()); // 상태 코드 400 확인
    }

}
