package service;

import dao.BookingDAO;
import java.util.List;
import model.Booking;
import model.Seat;

public class BookingService {

    private BookingDAO bookingDAO;

    public BookingService() {
        bookingDAO = new BookingDAO();
    }

    public boolean createBooking(
            int userId,
            int scheduleId,
            List<Seat> selectedSeats,
            double totalAmount,
            String paymentMethod,
            int devotionalPackageId,
            int seniorCitizenCount,
            int wheelchairCount) {

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return false;
        }

        if (totalAmount <= 0) {
            return false;
        }

        if (seniorCitizenCount < 0
                || wheelchairCount < 0) {
            return false;
        }

        if (seniorCitizenCount > selectedSeats.size()
                || wheelchairCount > selectedSeats.size()) {
            return false;
        }

        return bookingDAO.createBooking(
                userId,
                scheduleId,
                selectedSeats,
                totalAmount,
                paymentMethod,
                devotionalPackageId,
                seniorCitizenCount,
                wheelchairCount
        );
    }

    public List<Booking> getBookingHistory(int userId) {
        return bookingDAO.findBookingsByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAllBookings();
    }

    public boolean cancelBooking(int bookingId, int userId) {
        return bookingDAO.cancelBooking(bookingId, userId);
    }
}