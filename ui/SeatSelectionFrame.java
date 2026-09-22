package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import model.BusSchedule;
import model.RestStop;
import model.Seat;
import model.User;
import service.RestStopService;
import service.SeatService;

public class SeatSelectionFrame extends JFrame {

    private final User user;
    private final BusSchedule schedule;
    private final SeatService seatService;
    private final RestStopService restStopService;

    private final List<Seat> selectedSeats = new ArrayList<>();

    private final List<Integer> femaleOccupiedSeatIds;
    private final Set<Integer> womenPreferredSeatIds = new HashSet<>();

    private JLabel selectedLabel;

    // Font specifically supporting Windows Emojis
    private final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 12);
    private final Font EMOJI_FONT_BOLD = new Font("Segoe UI Emoji", Font.BOLD, 14);

    public SeatSelectionFrame(BusSchedule schedule, User user) {
        this.schedule = schedule;
        this.user = user;
        this.restStopService = new RestStopService();
        this.seatService = new SeatService();

        this.femaleOccupiedSeatIds =
                seatService.getFemaleOccupiedSeatIds(schedule.getScheduleId());

        setTitle("RouteWise - Select Seats");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---------------- HEADER ----------------
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel =
                new JLabel(schedule.getBusNumber() + " - Select Your Seats");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel detailsLabel = new JLabel(
                schedule.getOperatorName() + "  |  " +
                schedule.getBusType() + "  |  ₹" +
                schedule.getFare() + " per seat"
        );

        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsLabel.setForeground(new Color(70, 80, 95));

        headerPanel.add(titleLabel);
        headerPanel.add(detailsLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ---------------- SEATS PANEL ----------------
        JPanel seatsContainer = new JPanel();
        seatsContainer.setLayout(new BoxLayout(seatsContainer, BoxLayout.Y_AXIS));
        seatsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Seat> seats =
                seatService.getSeatsByScheduleId(schedule.getScheduleId());

        if (seats.isEmpty()) {

            seatsContainer.add(
                    new JLabel("No seats found for this bus.")
            );

        } else {

            // Women Safety preference is calculated only for female passengers.
            if ("FEMALE".equalsIgnoreCase(user.getGender())) {
                calculateWomenPreferredSeats(seats);

                JLabel womenSafetyLabel =
                        new JLabel("🩷 Women Preferred seats are highlighted");

                womenSafetyLabel.setFont(EMOJI_FONT_BOLD);
                womenSafetyLabel.setForeground(new Color(190, 24, 93));

                womenSafetyLabel.setBorder(
                        BorderFactory.createEmptyBorder(0, 0, 10, 0)
                );

                seatsContainer.add(womenSafetyLabel);
            }

            String busType = schedule.getBusType();

            if (busType.contains("Sleeper")) {
                createSleeperLayout(seats, seatsContainer);
            } else {
                createNormalSeatLayout(seats, seatsContainer);
            }
        }

        JScrollPane seatsScrollPane = new JScrollPane(seatsContainer);
        seatsScrollPane.setBorder(
                BorderFactory.createTitledBorder("Seat Map")
        );

        mainPanel.add(seatsScrollPane, BorderLayout.CENTER);

        // ---------------- REST STOPS SIDEBAR ----------------
        JPanel restStopsPanel = new JPanel();
        restStopsPanel.setLayout(
                new BoxLayout(restStopsPanel, BoxLayout.Y_AXIS)
        );
        restStopsPanel.setBackground(Color.WHITE);
        restStopsPanel.setPreferredSize(new Dimension(320, 0));

        List<RestStop> restStops =
                restStopService.getRestStopsByRouteId(schedule.getRouteId());

        if (restStops.isEmpty()) {

            JLabel emptyLabel =
                    new JLabel("No rest-stop information available.");

            emptyLabel.setBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            );

            restStopsPanel.add(emptyLabel);

        } else {

            for (RestStop stop : restStops) {

                JPanel stopCard = new JPanel();
                stopCard.setLayout(
                        new BoxLayout(stopCard, BoxLayout.Y_AXIS)
                );
                stopCard.setBackground(Color.WHITE);

                stopCard.setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(
                                        0, 0, 1, 0,
                                        new Color(230, 230, 230)
                                ),
                                BorderFactory.createEmptyBorder(
                                        10, 12, 10, 12
                                )
                        )
                );

                // Stop title
                JLabel stopNameLabel = new JLabel(
                        "📍 Stop " + stop.getStopOrder()
                        + ": " + stop.getStopName()
                );

                stopNameLabel.setFont(EMOJI_FONT_BOLD);
                stopNameLabel.setForeground(
                        new Color(30, 41, 59)
                );

                stopCard.add(stopNameLabel);
                stopCard.add(Box.createVerticalStrut(4));

                // Facilities
                StringBuilder facilities = new StringBuilder();

                if (stop.isFoodAvailable()) {
                    facilities.append("🍴 Food  ");
                }

                if (stop.isRestroomAvailable()) {
                    facilities.append("🚻 Restroom  ");
                }

                if (stop.isWheelchairAccessible()) {
                    facilities.append("♿ Accessible  ");
                }

                if (stop.isParkingAvailable()) {
                    facilities.append("🅿 Parking");
                }

                JLabel facilitiesLabel =
                        new JLabel(facilities.toString());

                facilitiesLabel.setFont(EMOJI_FONT);
                facilitiesLabel.setForeground(
                        new Color(16, 185, 129)
                );

                stopCard.add(facilitiesLabel);
                stopCard.add(Box.createVerticalStrut(4));

                // Description
                if (stop.getDescription() != null
                        && !stop.getDescription().isEmpty()) {

                    JLabel descriptionLabel = new JLabel(
                            "<html><body style='width:220px; color:#64748B;'>"
                            + stop.getDescription()
                            + "</body></html>"
                    );

                    descriptionLabel.setFont(EMOJI_FONT);
                    stopCard.add(descriptionLabel);
                }

                restStopsPanel.add(stopCard);
            }
        }

        JScrollPane restStopsScrollPane =
                new JScrollPane(restStopsPanel);

        restStopsScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Travel & Rest Stops"
                )
        );

        mainPanel.add(
                restStopsScrollPane,
                BorderLayout.EAST
        );

        // ---------------- BOTTOM CONTROL BAR ----------------
        JPanel bottomPanel =
                new JPanel(new BorderLayout(10, 10));

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 5, 0, 5)
        );

        selectedLabel =
                new JLabel("Selected Seats: None");

        selectedLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        JButton backButton = new JButton("Back");
        JButton continueButton = new JButton("Continue");

        continueButton.setBackground(
                new Color(37, 99, 235)
        );

        continueButton.setForeground(Color.WHITE);

        continueButton.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);

        bottomPanel.add(
                selectedLabel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.EAST
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ---------------- ACTION LISTENERS ----------------

        backButton.addActionListener(e -> {
            new CustomerDashboard(user);
            dispose();
        });

        continueButton.addActionListener(e -> {

            if (selectedSeats.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select at least one seat."
                );

                return;
            }

            new BookingSummaryFrame(
                    user,
                    schedule,
                    selectedSeats
            );

            dispose();
        });

        add(mainPanel);
    }

    // =========================================================
    // WOMEN SAFETY
    // =========================================================

    private void calculateWomenPreferredSeats(List<Seat> seats) {

        if (femaleOccupiedSeatIds.isEmpty()) {
            return;
        }

        String busType = schedule.getBusType();

        for (Seat occupiedSeat : seats) {

            if (!femaleOccupiedSeatIds.contains(occupiedSeat.getSeatId())) {
                continue;
            }

            int seatNumber;

            try {
                seatNumber =
                        Integer.parseInt(occupiedSeat.getSeatNumber());
            } catch (NumberFormatException e) {
                continue;
            }

            // ---------------- SLEEPER ----------------
            //
            // Actual RouteWise sleeper pattern:
            // 1 LOWER <-> 2 UPPER
            // 3 LOWER <-> 4 UPPER
            // 5 LOWER <-> 6 UPPER
            // ...
            if (busType.contains("Sleeper")) {

                int preferredSeatNumber;

                if (seatNumber % 2 == 1) {
                    preferredSeatNumber = seatNumber + 1;
                } else {
                    preferredSeatNumber = seatNumber - 1;
                }

                addPreferredSeatIfAvailable(
                        seats,
                        preferredSeatNumber
                );
            }

            // ---------------- VOLVO 2+1 ----------------
            //
            // Current UI layout:
            // 1  2 | 3
            // 4  5 | 6
            // ...
            else if (busType.contains("Volvo")) {

                int positionInRow = (seatNumber - 1) % 3;

                if (positionInRow == 0) {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber + 1
                    );

                } else if (positionInRow == 1) {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber - 1
                    );
                }

                // position 2 is the single seat.
            }

            // ---------------- NORMAL 2+2 ----------------
            //
            // Current UI layout:
            // 1  2 | 3  4
            // 5  6 | 7  8
            // ...
            else {

                int positionInRow = (seatNumber - 1) % 4;

                if (positionInRow == 0) {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber + 1
                    );

                } else if (positionInRow == 1) {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber - 1
                    );

                } else if (positionInRow == 2) {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber + 1
                    );

                } else {

                    addPreferredSeatIfAvailable(
                            seats,
                            seatNumber - 1
                    );
                }
            }
        }
    }

    private void addPreferredSeatIfAvailable(
            List<Seat> seats,
            int preferredSeatNumber) {

        for (Seat seat : seats) {

            if (!seat.getSeatNumber().equals(
                    String.valueOf(preferredSeatNumber))) {
                continue;
            }

            if ("AVAILABLE".equalsIgnoreCase(seat.getStatus())) {
                womenPreferredSeatIds.add(seat.getSeatId());
            }

            return;
        }
    }

    // =========================================================
    // SLEEPER LAYOUT
    // =========================================================

    private void createSleeperLayout(
            List<Seat> seats,
            JPanel seatsPanel) {

        JLabel upperLabel = new JLabel("UPPER BERTH");
        upperLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        seatsPanel.add(upperLabel);
        seatsPanel.add(Box.createVerticalStrut(8));

        JPanel upperPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                10
                        )
                );

        JPanel lowerPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                10
                        )
                );

        for (Seat seat : seats) {

            JButton button = createSeatButton(seat);

            if ("UPPER".equalsIgnoreCase(
                    seat.getBerthType())) {

                upperPanel.add(button);

            } else {

                lowerPanel.add(button);
            }
        }

        seatsPanel.add(upperPanel);
        seatsPanel.add(Box.createVerticalStrut(15));

        JLabel lowerLabel =
                new JLabel("LOWER BERTH");

        lowerLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        seatsPanel.add(lowerLabel);
        seatsPanel.add(Box.createVerticalStrut(8));

        seatsPanel.add(lowerPanel);
    }

    // =========================================================
    // SEATER LAYOUT
    // =========================================================

    private void createNormalSeatLayout(
            List<Seat> seats,
            JPanel seatsPanel) {

        JLabel driverLabel =
                new JLabel("🚌 Front / Driver");

        driverLabel.setFont(EMOJI_FONT_BOLD);

        seatsPanel.add(driverLabel);
        seatsPanel.add(Box.createVerticalStrut(10));

        JPanel busLayout = new JPanel();

        busLayout.setLayout(
                new BoxLayout(
                        busLayout,
                        BoxLayout.Y_AXIS
                )
        );

        int seatsPerRow =
                schedule.getBusType().contains("Volvo")
                        ? 3
                        : 4;

        for (int i = 0; i < seats.size(); i += seatsPerRow) {

            JPanel row =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT,
                                    10,
                                    5
                            )
                    );

            for (
                    int j = i;
                    j < i + seatsPerRow && j < seats.size();
                    j++
            ) {

                Seat seat = seats.get(j);

                JButton button =
                        createSeatButton(seat);

                row.add(button);

                if (j == i + 1) {
                    row.add(
                            Box.createRigidArea(
                                    new Dimension(30, 0)
                            )
                    );
                }
            }

            busLayout.add(row);
        }

        seatsPanel.add(busLayout);
    }

    // =========================================================
    // SEAT BUTTON BUILDER
    // =========================================================

    private JButton createSeatButton(Seat seat) {

        JButton button =
                new JButton(getSeatButtonText(seat));

        button.setPreferredSize(
                new Dimension(100, 45)
        );

        button.setFont(EMOJI_FONT);

        if ("BOOKED".equalsIgnoreCase(
                seat.getStatus())) {

            button.setEnabled(false);
            button.setBackground(
                    new Color(220, 220, 220)
            );

            return button;
        }

        boolean womenPreferred =
                womenPreferredSeatIds.contains(
                        seat.getSeatId()
                );

        if (womenPreferred) {

            button.setBackground(
                    new Color(252, 231, 243)
            );

            button.setToolTipText(
                    "🩷 Women Preferred"
            );
        }

        button.addActionListener(e -> {

            if (selectedSeats.contains(seat)) {

                selectedSeats.remove(seat);

                button.setText(
                        getSeatButtonText(seat)
                );

                if (womenPreferred) {

                    button.setBackground(
                            new Color(252, 231, 243)
                    );

                } else {

                    button.setBackground(null);
                }

            } else {

                selectedSeats.add(seat);

                button.setText(
                        "✓ " + getSeatButtonText(seat)
                );

                button.setBackground(
                        new Color(187, 247, 208)
                );
            }

            updateSelectedLabel();
        });

        return button;
    }

    private String getSeatButtonText(Seat seat) {

        String text = seat.getSeatNumber();

        if (seat.getBerthType() != null
                && !seat.getBerthType().isEmpty()) {

            text +=
                    " (" + seat.getBerthType() + ")";
        }

        if (womenPreferredSeatIds.contains(
                seat.getSeatId())) {

            text = "🩷 " + text;
        }

        return text;
    }

    private void updateSelectedLabel() {

        if (selectedSeats.isEmpty()) {

            selectedLabel.setText(
                    "Selected Seats: None"
            );

            return;
        }

        StringBuilder text =
                new StringBuilder(
                        "Selected Seats: "
                );

        for (
                int i = 0;
                i < selectedSeats.size();
                i++
        ) {

            text.append(
                    selectedSeats.get(i).getSeatNumber()
            );

            if (i < selectedSeats.size() - 1) {
                text.append(", ");
            }
        }

        selectedLabel.setText(
                text.toString()
        );
    }
}
