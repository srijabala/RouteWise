package ui;

import dao.DevotionalPackageDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.BusSchedule;
import model.DevotionalPackage;
import model.Seat;
import model.User;
import service.BookingService;

public class BookingSummaryFrame extends JFrame {

    private final User user;
    private final BusSchedule schedule;
    private final List<Seat> selectedSeats;
    private final BookingService bookingService;

    private DevotionalPackage devotionalPackage;
    private JLabel totalLabel;
    private JButton packageButton;
    private boolean packageSelected = false;
    private JSpinner seniorCitizenSpinner;
    private JSpinner wheelchairSpinner;

    // Theme Colors
    private final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Modern Blue
    private final Color ACCENT_GREEN = new Color(22, 163, 74);     // Green for selection
    private final Color BG_COLOR = new Color(248, 250, 252);        // Soft Light Gray
    private final Color CARD_BG = Color.WHITE;

    public BookingSummaryFrame(User user, BusSchedule schedule, List<Seat> selectedSeats) {
        this.user = user;
        this.schedule = schedule;
        this.selectedSeats = selectedSeats;
        this.bookingService = new BookingService();

        if ("DEVOTIONAL".equalsIgnoreCase(schedule.getTripCategory())) {
            DevotionalPackageDAO packageDAO = new DevotionalPackageDAO();
            devotionalPackage = packageDAO.getPackageByScheduleId(schedule.getScheduleId());
        }

        setTitle("RouteWise - Booking Summary");
        setSize(650, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        createUI();
        setVisible(true);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- TITLE ---
        JLabel titleLabel = new JLabel("Booking Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- TRIP DETAILS CARD ---
        JPanel detailsPanel = createCardPanel("Trip Details");
        detailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(detailsPanel, gbc, row++, "Passenger:", user.getFullName());
        addDetailRow(detailsPanel, gbc, row++, "Route:", schedule.getSourceCity() + " → " + schedule.getDestinationCity());
        addDetailRow(detailsPanel, gbc, row++, "Bus Details:", schedule.getBusNumber() + " (" + schedule.getBusType() + ")");
        addDetailRow(detailsPanel, gbc, row++, "Operator:", schedule.getOperatorName());
        addDetailRow(detailsPanel, gbc, row++, "Departure:", schedule.getDepartureTime().toString());
        addDetailRow(detailsPanel, gbc, row++, "Arrival:", schedule.getArrivalTime().toString());

        StringBuilder seatNumbers = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            seatNumbers.append(selectedSeats.get(i).getSeatNumber());
            if (i < selectedSeats.size() - 1) seatNumbers.append(", ");
        }
        addDetailRow(detailsPanel, gbc, row++, "Selected Seats:", seatNumbers.toString() + " (" + selectedSeats.size() + " seat(s))");
        addDetailRow(detailsPanel, gbc, row++, "Fare per Seat:", "₹" + schedule.getFare());

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- OPTIONAL DEVOTIONAL PACKAGE CARD ---
        if (devotionalPackage != null) {
            JPanel packagePanel = createCardPanel("Devotional Add-on Package");
            packagePanel.setLayout(new BorderLayout(15, 0));

            JPanel packageInfo = new JPanel();
            packageInfo.setLayout(new BoxLayout(packageInfo, BoxLayout.Y_AXIS));
            packageInfo.setBackground(CARD_BG);

            JLabel packageNameLabel = new JLabel(devotionalPackage.getPackageName());
            packageNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel packagePriceLabel = new JLabel("₹" + devotionalPackage.getPricePerPerson() + " per person");
            packagePriceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            packagePriceLabel.setForeground(Color.GRAY);

            packageInfo.add(packageNameLabel);
            packageInfo.add(Box.createVerticalStrut(3));
            packageInfo.add(packagePriceLabel);

            packageButton = new JButton("Add Package");
            packageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            packageButton.setFocusPainted(false);
            packageButton.addActionListener(e -> togglePackage());

            packagePanel.add(packageInfo, BorderLayout.CENTER);
            packagePanel.add(packageButton, BorderLayout.EAST);

            mainPanel.add(packagePanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        // --- TOTAL AMOUNT & ACTIONS ---
        JPanel totalCard = createCardPanel(null);
        totalCard.setLayout(new BorderLayout());

        JLabel totalTitle = new JLabel("Total Payable:");
        totalTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(PRIMARY_COLOR);
        updateTotal();

        totalCard.add(totalTitle, BorderLayout.WEST);
        totalCard.add(totalLabel, BorderLayout.EAST);
        // --- ACCESSIBILITY ASSISTANCE ---

JPanel accessibilityPanel =
        createCardPanel("Accessibility Assistance");

accessibilityPanel.setLayout(
        new GridLayout(2, 2, 10, 10)
);

JLabel seniorLabel =
        new JLabel("Senior citizens requiring assistance:");

seniorCitizenSpinner =
        new JSpinner(
                new SpinnerNumberModel(
                        0,
                        0,
                        selectedSeats.size(),
                        1
                )
        );

JLabel wheelchairLabel =
        new JLabel("Wheelchair users requiring assistance:");

wheelchairSpinner =
        new JSpinner(
                new SpinnerNumberModel(
                        0,
                        0,
                        selectedSeats.size(),
                        1
                )
        );

accessibilityPanel.add(seniorLabel);
accessibilityPanel.add(seniorCitizenSpinner);

accessibilityPanel.add(wheelchairLabel);
accessibilityPanel.add(wheelchairSpinner);

mainPanel.add(accessibilityPanel);
mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(totalCard);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton confirmButton = new JButton("Proceed to Payment");
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);

        buttonPanel.add(backButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);

        // --- ACTION LISTENERS ---
        backButton.addActionListener(e -> {
            new SeatSelectionFrame(schedule, user);
            dispose();
        });

     confirmButton.addActionListener(e -> {
    new PaymentFrame(
            user,
            schedule,
            selectedSeats,
            calculateTotal(),
            packageSelected && devotionalPackage != null
                    ? devotionalPackage.getPackageId()
                    : 0,
            (Integer) seniorCitizenSpinner.getValue(),
            (Integer) wheelchairSpinner.getValue()
    );
    dispose();
});
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane);
    }

    private void togglePackage() {
        packageSelected = !packageSelected;
        if (packageSelected) {
            packageButton.setText("✓ Package Added");
            packageButton.setBackground(ACCENT_GREEN);
            packageButton.setForeground(Color.WHITE);
        } else {
            packageButton.setText("Add Package");
            packageButton.setBackground(UIManager.getColor("Button.background"));
            packageButton.setForeground(Color.BLACK);
        }
        updateTotal();
    }

    private double calculateTotal() {
        double total = selectedSeats.size() * schedule.getFare();
        if (packageSelected && devotionalPackage != null) {
            total += selectedSeats.size() * devotionalPackage.getPricePerPerson();
        }
        return total;
    }

    private void updateTotal() {
        totalLabel.setText("₹" + String.format("%.2f", calculateTotal()));
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        if (title != null) {
            panel.setBorder(BorderFactory.createTitledBorder(panel.getBorder(), title,
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new Font("Segoe UI", Font.BOLD, 13), new Color(100, 116, 139)));
        }
        return panel;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setForeground(new Color(100, 116, 139));
        panel.add(lblLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valLabel.setForeground(new Color(30, 41, 59));
        panel.add(valLabel, gbc);
    }
}