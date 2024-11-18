package org.onewayticket.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- 예약 생성 테스트 ---
    @Test
    @DisplayName("예약 생성 - 결제 정보 누락 시 실패")
    public void createBooking_should_Return_400_whenPaymentInfoIsMissing() throws Exception {
        // Arrange
        String bookingRequestWithoutPaymentId = """
                {
                    "bookingName": "John Doe",
                    "bookingEmail": "john.doe@example.com",
                    "bookingPhoneNumber": "123456789",
                    "birthDate": "1990-01-01",
                    "flightId": "FL123"
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestWithoutPaymentId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Payment information is missing"));
    }

    @Test
    @DisplayName("예약 생성 - 성공")
    public void createBooking_should_return_200_whenValidRequest() throws Exception {
        // Arrange
        String validBookingRequest = """
                {
                    "bookingName": "John Doe",
                    "bookingEmail": "john.doe@example.com",
                    "bookingPhoneNumber": "123456789",
                    "birthDate": "1990-01-01",
                    "flightId": "FL123",
                    "paymentId": "PAY123"
                }
                """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookingRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking created"));
    }

    // --- 예약 취소 테스트 ---
    @Test
    @DisplayName("예약 취소 - 사용자 토큰 정보 불일치 시 실패")
    public void cancelBooking_should_return_403_whenUserTokenIsInvalid() throws Exception {
        // Arrange
        String bookingId = "BK123";
        String invalidUserToken = "INVALID_TOKEN";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", bookingId)
                        .header("Authorization", "Bearer " + invalidUserToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized user"));
    }

    @Test
    @DisplayName("예약 취소 - 성공")
    public void cancelBooking_should_return_200_whenUserTokenIsValid() throws Exception {
        // Arrange
        String bookingId = "BK123";
        String validUserToken = "VALID_TOKEN";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bookings/{id}", bookingId)
                        .header("Authorization", "Bearer " + validUserToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking canceled"));
    }

    @Test
    @DisplayName("비회원 예약 목록 조회 - 사용자 정보와 알맞는 예약 정보가 없을 때")
    public void getMyBookings_shouldReturn404_whenNoMatchingBooking() throws Exception {
        // Arrange
        String reservationId = "UNKNOWN";
        String name = "Nonexistent User";
        String birthDate = "2000-01-01";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bookings/my")
                        .param("bookingId", reservationId)
                        .param("name", name)
                        .param("birthDate", birthDate))
                .andExpect(status().isNotFound()); // 404 확인
    }

    @Test
    @DisplayName("비회원 예약 목록 조회 - 사용자 정보와 일치하는 예약 정보 반환")
    public void getMyBookings_shouldReturnBookingList_whenMatchingBookingExists() throws Exception {
        // Arrange
        String bookingId = "BK001";
        String name = "John Doe";
        String birthDate = "1990-01-01";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bookings/my")
                        .param("bookingId", bookingId)
                        .param("name", name)
                        .param("birthDate", birthDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value("BK001"))
                .andExpect(jsonPath("$[0].reservationName").value("John Doe"));
    }
}
