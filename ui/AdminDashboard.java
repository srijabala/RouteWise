package ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Booking;
import model.User;
import service.BookingService;

public class AdminDashboard extends JFrame {

    private User user;
    private BookingService bookingService;

    private JPanel bookingsPanel;
    private JLabel totalBookingsLabel;
    private JLabel confirmedBookingsLabel;
    private JLabel cancelledBookingsLabel;

    public AdminDashboard(User user) {

        this.user = user;
        bookingService = new BookingService();

        setTitle("RouteWise - Admin Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(20, 20));

        mainPanel.setBackground(
                new Color(245, 247, 250)
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        25, 30, 25, 30
                )
        );

        // ---------- HEADER ----------

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setOpaque(false);

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setOpaque(false);

        JLabel titleLabel =
                new JLabel("RouteWise");

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        JLabel subtitleLabel =
                new JLabel("Administration Dashboard");

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        subtitleLabel.setForeground(
                new Color(90, 90, 90)
        );

        titlePanel.add(titleLabel);
        titlePanel.add(
                Box.createVerticalStrut(4)
        );
        titlePanel.add(subtitleLabel);

        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        JPanel adminPanel =
                new JPanel();

        adminPanel.setLayout(
                new BoxLayout(
                        adminPanel,
                        BoxLayout.Y_AXIS
                )
        );

        adminPanel.setOpaque(false);

        JLabel adminLabel =
                new JLabel("ADMIN");

        adminLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        JLabel emailLabel =
                new JLabel(user.getEmail());

        emailLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        adminPanel.add(adminLabel);
        adminPanel.add(emailLabel);

        headerPanel.add(
                adminPanel,
                BorderLayout.EAST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // ---------- CENTER ----------

        JPanel centerPanel =
                new JPanel();

        centerPanel.setLayout(
                new BoxLayout(
                        centerPanel,
                        BoxLayout.Y_AXIS
                )
        );

        centerPanel.setOpaque(false);

        // ---------- STAT CARDS ----------

        JPanel statsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                15,
                                0
                        )
                );

        statsPanel.setOpaque(false);

        totalBookingsLabel =
                new JLabel("0");

        confirmedBookingsLabel =
                new JLabel("0");

        cancelledBookingsLabel =
                new JLabel("0");

        statsPanel.add(
                createStatCard(
                        "TOTAL BOOKINGS",
                        totalBookingsLabel
                )
        );

        statsPanel.add(
                createStatCard(
                        "CONFIRMED",
                        confirmedBookingsLabel
                )
        );

        statsPanel.add(
                createStatCard(
                        "CANCELLED",
                        cancelledBookingsLabel
                )
        );

        centerPanel.add(statsPanel);

        centerPanel.add(
                Box.createVerticalStrut(25)
        );

        // ---------- BOOKINGS HEADER ----------

        JPanel bookingsHeader =
                new JPanel(new BorderLayout());

        bookingsHeader.setOpaque(false);

        JLabel bookingsTitle =
                new JLabel("Recent Bookings");

        bookingsTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        21
                )
        );

        bookingsHeader.add(
                bookingsTitle,
                BorderLayout.WEST
        );

        JButton refreshButton =
                new JButton("Refresh");

        refreshButton.addActionListener(
                e -> loadBookings()
        );

        bookingsHeader.add(
                refreshButton,
                BorderLayout.EAST
        );

        centerPanel.add(bookingsHeader);

        centerPanel.add(
                Box.createVerticalStrut(10)
        );

        // ---------- BOOKINGS ----------

        bookingsPanel =
                new JPanel();

        bookingsPanel.setLayout(
                new BoxLayout(
                        bookingsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        bookingsPanel.setBackground(
                Color.WHITE
        );

        bookingsPanel.setBorder(
                new EmptyBorder(
                        15, 15, 15, 15
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(bookingsPanel);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 214, 220)
                )
        );

        centerPanel.add(scrollPane);

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);

        loadBookings();
    }

    private JPanel createStatCard(
            String title,
            JLabel valueLabel) {

        JPanel card =
                new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        card.setBackground(
                Color.WHITE
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 224, 230)
                        ),
                        new EmptyBorder(
                                15, 18, 15, 18
                        )
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        titleLabel.setForeground(
                new Color(100, 100, 100)
        );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        valueLabel.setForeground(
                new Color(30, 40, 55)
        );

        card.add(titleLabel);

        card.add(
                Box.createVerticalStrut(8)
        );

        card.add(valueLabel);

        return card;
    }

    private void loadBookings() {

        bookingsPanel.removeAll();

        List<Booking> bookings =
                bookingService.getAllBookings();

        int totalBookings =
                bookings.size();

        int confirmedBookings = 0;
        int cancelledBookings = 0;

        for (Booking booking : bookings) {

            if ("CONFIRMED".equals(
                    booking.getStatus())) {

                confirmedBookings++;

            } else if ("CANCELLED".equals(
                    booking.getStatus())) {

                cancelledBookings++;
            }
        }

        totalBookingsLabel.setText(
                String.valueOf(totalBookings)
        );

        confirmedBookingsLabel.setText(
                String.valueOf(confirmedBookings)
        );

        cancelledBookingsLabel.setText(
                String.valueOf(cancelledBookings)
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy, hh:mm a"
                );

        if (bookings.isEmpty()) {

            bookingsPanel.add(
                    new JLabel(
                            "No bookings found."
                    )
            );

        } else {

            for (Booking booking : bookings) {

                bookingsPanel.add(
                        createBookingCard(
                                booking,
                                formatter
                        )
                );

                bookingsPanel.add(
                        Box.createVerticalStrut(12)
                );
            }
        }

        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }

    private JPanel createBookingCard(
            Booking booking,
            DateTimeFormatter formatter) {

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                15,
                                5
                        )
                );

        card.setBackground(
                new Color(250, 251, 253)
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 224, 230)
                        ),
                        new EmptyBorder(
                                15, 18, 15, 18
                        )
                )
        );

        // ---------- LEFT ----------

        JPanel detailsPanel =
                new JPanel();

        detailsPanel.setLayout(
                new BoxLayout(
                        detailsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        detailsPanel.setOpaque(false);

        JLabel bookingLabel =
                new JLabel(
                        "Booking #"
                        + booking.getBookingId()
                );

        bookingLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        JLabel routeLabel =
                new JLabel(
                        booking.getSourceCity()
                        + " → "
                        + booking.getDestinationCity()
                );

        routeLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        JLabel busLabel =
                new JLabel(
                        "Bus: "
                        + booking.getBusNumber()
                        + "  |  "
                        + booking.getBusType()
                );

        JLabel userLabel =
                new JLabel(
                        "User ID: "
                        + booking.getUserId()
                );

        JLabel departureLabel =
                new JLabel(
                        "Departure: "
                        + booking
                                .getDepartureTime()
                                .format(formatter)
                );

        JLabel seatsLabel =
                new JLabel(
                        "Seats: "
                        + booking.getTotalSeats()
                );

        detailsPanel.add(bookingLabel);

        detailsPanel.add(
                Box.createVerticalStrut(5)
        );

        detailsPanel.add(routeLabel);

        detailsPanel.add(
                Box.createVerticalStrut(5)
        );

           detailsPanel.add(busLabel);
    detailsPanel.add(userLabel);
    detailsPanel.add(departureLabel);
    detailsPanel.add(seatsLabel);

JLabel seniorLabel =
        new JLabel(
                "Senior Assistance: "
                        + booking.getSeniorCitizenCount()
        );

    JLabel wheelchairLabel =
        new JLabel(
                "Wheelchair Assistance: "
                        + booking.getWheelchairCount()
        );

             detailsPanel.add(seniorLabel);
            detailsPanel.add(wheelchairLabel);

        // ---------- RIGHT ----------

        JPanel statusPanel =
                new JPanel();

        statusPanel.setLayout(
                new BoxLayout(
                        statusPanel,
                        BoxLayout.Y_AXIS
                )
        );

        statusPanel.setOpaque(false);

        JLabel amountLabel =
                new JLabel(
                        "₹"
                        + booking.getTotalAmount()
                );

        amountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        JLabel paymentLabel =
                new JLabel(
                        "Payment: "
                        + booking.getPaymentMethod()
                );

        JLabel paymentStatusLabel =
                new JLabel(
                        booking.getPaymentStatus()
                );

        JLabel bookingStatusLabel =
                new JLabel(
                        booking.getStatus()
                );

        bookingStatusLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        statusPanel.add(amountLabel);

        statusPanel.add(
                Box.createVerticalStrut(5)
        );

        statusPanel.add(paymentLabel);
        statusPanel.add(paymentStatusLabel);

        statusPanel.add(
                Box.createVerticalStrut(8)
        );

        statusPanel.add(
                bookingStatusLabel
        );

        card.add(
                detailsPanel,
                BorderLayout.CENTER
        );

        card.add(
                statusPanel,
                BorderLayout.EAST
        );

        return card;
    }
}