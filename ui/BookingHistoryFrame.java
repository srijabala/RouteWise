package ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import model.Booking;
import model.User;
import service.BookingService;

public class BookingHistoryFrame extends JFrame {

    private User user;
    private BookingService bookingService;

    public BookingHistoryFrame(User user) {

        this.user = user;
        bookingService = new BookingService();

        setTitle("RouteWise - Booking History");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(
                new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titleLabel =
                new JLabel("My Booking History");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(titleLabel);

        mainPanel.add(
                Box.createVerticalStrut(20)
        );

        List<Booking> bookings =
                bookingService.getBookingHistory(
                        user.getUserId()
                );

        if (bookings.isEmpty()) {

            JLabel emptyLabel =
                    new JLabel("No bookings found.");

            emptyLabel.setFont(
                    new Font("Arial", Font.PLAIN, 18)
            );

            mainPanel.add(emptyLabel);

        } else {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, hh:mm a"
                    );

            for (Booking booking : bookings) {

                JPanel bookingPanel =
                        new JPanel();

                bookingPanel.setLayout(
                        new BoxLayout(
                                bookingPanel,
                                BoxLayout.Y_AXIS
                        )
                );

                bookingPanel.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(
                                        Color.GRAY
                                ),
                                BorderFactory.createEmptyBorder(
                                        15, 15, 15, 15
                                )
                        )
                );

                bookingPanel.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                );

                JLabel bookingLabel =
                        new JLabel(
                                "Booking #" +
                                booking.getBookingId()
                        );

                bookingLabel.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                18
                        )
                );

                bookingPanel.add(bookingLabel);

                bookingPanel.add(
                        Box.createVerticalStrut(8)
                );

                bookingPanel.add(
                        new JLabel(
                                "Route: " +
                                booking.getSourceCity() +
                                " → " +
                                booking.getDestinationCity()
                        )
                );

                bookingPanel.add(
                        new JLabel(
                                "Bus: " +
                                booking.getBusNumber() +
                                " (" +
                                booking.getBusType() +
                                ")"
                        )
                );

                bookingPanel.add(
                        new JLabel(
                                "Departure: " +
                                booking
                                        .getDepartureTime()
                                        .format(formatter)
                        )
                ); 


                if ("CONFIRMED".equals(booking.getStatus())) {

    JButton cancelButton =
            new JButton("Cancel Booking");

    cancelButton.addActionListener(e -> {

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to cancel Booking #"
                        + booking.getBookingId()
                        + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success =
                bookingService.cancelBooking(
                        booking.getBookingId(),
                        user.getUserId()
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking cancelled successfully."
            );

            dispose();

            new BookingHistoryFrame(user);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to cancel booking."
            );
        }
    });

    bookingPanel.add(
            Box.createVerticalStrut(10)
    );

    bookingPanel.add(cancelButton);
}

                bookingPanel.add(
                        new JLabel(
                                "Seats: " +
                                booking.getTotalSeats()
                        )
                );

                bookingPanel.add(
                        new JLabel(
                                "Amount: ₹" +
                                booking.getTotalAmount()
                        )
                );

                bookingPanel.add(
                        new JLabel(
                                "Payment: " +
                                booking.getPaymentMethod() +
                                " (" +
                                booking.getPaymentStatus() +
                                ")"
                        )
                );

                bookingPanel.add(
                        new JLabel(
                                "Status: " +
                                booking.getStatus()
                        )
                );

                mainPanel.add(bookingPanel);

                mainPanel.add(
                        Box.createVerticalStrut(15)
                );
            }
        }

        JScrollPane scrollPane =
                new JScrollPane(mainPanel);

        add(scrollPane);
    }
}