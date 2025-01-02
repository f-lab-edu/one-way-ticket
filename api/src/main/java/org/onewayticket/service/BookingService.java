package org.onewayticket.service;

import lombok.RequiredArgsConstructor;
import org.onewayticket.domain.Booking;
import org.onewayticket.domain.BookingDetail;
import org.onewayticket.domain.Flight;
import org.onewayticket.domain.Member;
import org.onewayticket.domain.Passenger;
import org.onewayticket.enums.BookingStatus;
import org.onewayticket.repository.BookingRepository;
import org.onewayticket.util.ReferenceCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final MemberService memberService;
    @Qualifier("asyncExecutor")
    @Autowired
    private Executor asyncExecutor;

    public Booking createBooking(String email, List<Passenger> passengers, String paymentKey, Long flightId) {
        Booking booking = Booking.builder().referenceCode(ReferenceCodeGenerator.generateReferenceCode()).bookingEmail(email).flightId(flightId).paymentKey(paymentKey).passengers(passengers).status(BookingStatus.COMPLETED).build();
        try {
            bookingRepository.save(booking);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("Booking 저장 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
        return booking;
    }

    public BookingDetail getBookingDetailsByReferenceCode(String referenceCode) {
        // TODO: Index 추가 여부 고민
        Booking booking = bookingRepository.findByReferenceCode(referenceCode).orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));

        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        return new BookingDetail(booking, flight);
    }

    public List<BookingDetail> getBookingDetailsList(String username) {
        Member member = memberService.getMemberByUsername(username);
        List<Booking> bookings = bookingRepository.findAllByMemberId(member.getId());
        return bookings.stream().map(booking -> {
            Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());
            return new BookingDetail(booking, flight);
        }).toList();
    }

    public CompletableFuture<BookingDetail> getBookingDetailsForUserAsyncWithCustomExecutor(Long bookingId, String username) {
        System.out.println("Async method start. Thread: " + Thread.currentThread().getName());
        System.out.println("asyncExecutor = " + asyncExecutor);
        CompletableFuture<Booking> bookingFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching booking. Thread: " + Thread.currentThread().getName());
            return bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));
        }, asyncExecutor); // 명시적으로 Executor 지정

        CompletableFuture<Flight> flightFuture = bookingFuture.thenApplyAsync(booking -> {
            System.out.println("Fetching flight details. Thread: " + Thread.currentThread().getName());
            return flightService.getFlightDetails(booking.getFlightId().toString());
        }, asyncExecutor); // 명시적으로 Executor 지정

        return bookingFuture.thenCombineAsync(flightFuture, (booking, flight) -> {
            System.out.println("Combining results. Thread: " + Thread.currentThread().getName());
            String usernameInBooking = booking.getBookingEmail();
            if (!usernameInBooking.equals(username)) {
                throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
            }
            return new BookingDetail(booking, flight);
        }, asyncExecutor); // 명시적으로 Executor 지정
    }

//    public CompletableFuture<BookingDetail> getBookingDetailsForUserAsyncWithCustomExecutor(Long bookingId, String username) {
//        System.out.println("ASYNC Service. Thread: " + Thread.currentThread().getName());
//
//        CompletableFuture<Booking> bookingFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("Fetching booking. Thread: " + Thread.currentThread().getName());
//            return bookingRepository.findById(bookingId)
//                    .orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));
//        });
//
//        CompletableFuture<Flight> flightFuture = bookingFuture.thenApplyAsync(booking -> {
//            System.out.println("Fetching flight details. Thread: " + Thread.currentThread().getName());
//            return flightService.getFlightDetails(booking.getFlightId().toString());
//        });
//
//        return bookingFuture.thenCombine(flightFuture, (booking, flight) -> {
//            System.out.println("Combining results. Thread: " + Thread.currentThread().getName());
//            String usernameInBooking = booking.getBookingEmail();
//            if (!usernameInBooking.equals(username)) {
//                throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
//            }
//            return new BookingDetail(booking, flight);
//        });
//    }

    public BookingDetail getBookingDetailsForUser(Long bookingId, String username) {
        System.out.println("CALLBLE SERVICE = " + Thread.currentThread().getName());
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("예약 정보를 찾을 수 없습니다."));
        Flight flight = flightService.getFlightDetails(booking.getFlightId().toString());

        String usernameInBooking = booking.getBookingEmail();
        if (!usernameInBooking.equals(username)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        return new BookingDetail(booking, flight);
    }

    @Transactional
    public void cancelBooking(String id, String username) {
        Booking booking = bookingRepository.findById(Long.valueOf(id)).orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다."));

        String usernameInBooking = booking.getBookingEmail();

        if (!username.equals(usernameInBooking)) {
            throw new IllegalArgumentException("로그인한 사용자와 예약자가 일치하지 않습니다.");
        }

        updateBookingStatusToCancelled(booking);
    }

    private void updateBookingStatusToCancelled(Booking booking) {
        booking.updateStatus(BookingStatus.CANCELLED);

        try {
            bookingRepository.save(booking);
        } catch (DataAccessException e) {
            throw new DataIntegrityViolationException("Booking 취소 중 예외가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
