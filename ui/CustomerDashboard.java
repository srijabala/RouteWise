package ui;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import model.BusSchedule;
import model.User;
import service.BusScheduleService;

public class CustomerDashboard extends JFrame {

    private User user;
    private BusScheduleService busScheduleService;

    private JComboBox<String> sourceComboBox;
    private JComboBox<String> destinationComboBox;
    private JComboBox<String> dateComboBox;
    private JPanel resultsPanel;

    public CustomerDashboard(User user) {

        this.user = user;
        this.busScheduleService = new BusScheduleService();

        setTitle("RouteWise - Customer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ---------- Welcome ----------
        JLabel welcomeLabel = new JLabel(
                "Welcome, " + user.getFullName() + "!"
        );

        welcomeLabel.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // ---------- Search Panel ----------
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        sourceComboBox = new JComboBox<>(new String[]{
                "Visakhapatnam",
                "Vijayawada",
                "Hyderabad",
                "Chennai",
                "Bengaluru"
        });

        destinationComboBox = new JComboBox<>(new String[]{
               "Vijayawada",
    "Bhubaneswar",
    "Hyderabad",
    "Chennai",
    "Bengaluru",
    "Visakhapatnam",
    "Tirupati",
    "Srisailam",
    "Shirdi",
    "Varanasi",
    "Madurai",
    "Rameswaram",
    "Ayodhya",
    "Pune"
        });

       dateComboBox = new JComboBox<>();

      dateComboBox.addItem("Loading dates...");

        JButton searchButton = new JButton("Search Buses");

        searchPanel.add(new JLabel("From:"));
        searchPanel.add(sourceComboBox);

        searchPanel.add(new JLabel("To:"));
        searchPanel.add(destinationComboBox);

       searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateComboBox);

      searchPanel.add(searchButton);

JButton historyButton =
        new JButton("Booking History");

historyButton.addActionListener(e -> {
    new BookingHistoryFrame(user);
});

searchPanel.add(historyButton);

mainPanel.add(searchPanel, BorderLayout.CENTER);

        // ---------- Results ----------
        resultsPanel = new JPanel();
        resultsPanel.setLayout(
                new BoxLayout(resultsPanel, BoxLayout.Y_AXIS)
        );

        JScrollPane scrollPane = new JScrollPane(resultsPanel);

        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchBuses());
        loadAvailableDates(searchButton);

        add(mainPanel);
    }

     private void loadAvailableDates(JButton searchButton) {

    searchButton.setEnabled(false);

    SwingWorker<List<String>, Void> worker =
            new SwingWorker<List<String>, Void>() {

        @Override
        protected List<String> doInBackground() {

            return busScheduleService
                    .getAvailableTravelDates();
        }

        @Override
        protected void done() {

            try {

                List<String> dates = get();

                dateComboBox.removeAllItems();

                for (String date : dates) {

                    dateComboBox.addItem(date);
                }

                if (dates.isEmpty()) {

                    dateComboBox.addItem(
                            "No dates available"
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();

                dateComboBox.removeAllItems();

                dateComboBox.addItem(
                        "Unable to load dates"
                );
            }

            searchButton.setEnabled(true);
        }
    };

    worker.execute();
}
  
private void searchBuses() {

    System.out.println("1. SEARCH BUTTON CLICKED");

    String source =
            (String) sourceComboBox.getSelectedItem();

    String destination =
            (String) destinationComboBox.getSelectedItem();

    String dateText =
            (String) dateComboBox.getSelectedItem();

    try {

        LocalDate travelDate =
                LocalDate.parse(dateText);

        System.out.println(
                "2. Search: "
                + source
                + " -> "
                + destination
                + " | "
                + dateText
        );

        SwingWorker<List<BusSchedule>, Void> worker =
                new SwingWorker<List<BusSchedule>, Void>() {

            @Override
            protected List<BusSchedule> doInBackground() {

                System.out.println(
                        "3. Database search started..."
                );

                long startTime =
                        System.currentTimeMillis();

                List<BusSchedule> schedules =
                        busScheduleService.searchSchedules(
                                source,
                                destination,
                                travelDate
                        );

                long endTime =
                        System.currentTimeMillis();

                System.out.println(
                        "4. Database call took "
                        + (endTime - startTime)
                        + " ms"
                );

                return schedules;
            }

            @Override
            protected void done() {

                try {

                    List<BusSchedule> schedules =
                            get();

                    System.out.println(
                            "5. Database returned "
                            + schedules.size()
                            + " schedule(s)"
                    );

                    resultsPanel.removeAll();

                    if (schedules.isEmpty()) {

                        resultsPanel.add(
                                new JLabel(
                                        "No buses found."
                                )
                        );

                    } else {

                        for (BusSchedule schedule : schedules) {

                            JPanel busPanel =
                                    new JPanel(
                                            new GridLayout(
                                                    2,
                                                    4,
                                                    10,
                                                    10
                                            )
                                    );

                            busPanel.setBorder(
                                    BorderFactory.createLineBorder(
                                            Color.GRAY
                                    )
                            );

                            busPanel.add(
                                    new JLabel(
                                            "Bus: "
                                            + schedule.getBusNumber()
                                    )
                            );

                            busPanel.add(
                                    new JLabel(
                                            "Operator: "
                                            + schedule.getOperatorName()
                                    )
                            );

                                JPanel typePanel = new JPanel(
        new FlowLayout(FlowLayout.LEFT, 5, 0)
);

typePanel.add(
        new JLabel(
                "Type: "
                + schedule.getBusType()
        )
);

if ("DEVOTIONAL".equals(schedule.getTripCategory())) {

    JLabel devotionalLabel =
            new JLabel("🛕 DEVOTIONAL");

    devotionalLabel.setFont(
            new Font("Arial", Font.BOLD, 12)
    );

    devotionalLabel.setForeground(
            new Color(128, 0, 128)
    );

    typePanel.add(devotionalLabel);
}

busPanel.add(typePanel);

                            busPanel.add(
                                    new JLabel(
                                            "Fare: ₹"
                                            + schedule.getFare()
                                    )
                            );

                            busPanel.add(
                                    new JLabel(
                                            "Departure: "
                                            + schedule.getDepartureTime()
                                    )
                            );

                            busPanel.add(
                                    new JLabel(
                                            "Arrival: "
                                            + schedule.getArrivalTime()
                                    )
                            );

                            busPanel.add(
                                    new JLabel(
                                            "Total Seats: "
                                            + schedule.getTotalSeats()
                                    )
                            );

                            int availableSeats =
                                    schedule.getAvailableSeats();

                            if (availableSeats == 0) {

                                JPanel bookedPanel =
                                        new JPanel();

                                bookedPanel.setBackground(
                                        new Color(255, 220, 220)
                                );

                                bookedPanel.setBorder(
                                        BorderFactory.createLineBorder(
                                                Color.RED
                                        )
                                );

                                JLabel bookedLabel =
                                        new JLabel(
                                                "ALL SEATS ARE BOOKED"
                                        );

                                bookedLabel.setForeground(
                                        Color.RED
                                );

                                bookedLabel.setFont(
                                        new Font(
                                                "Arial",
                                                Font.BOLD,
                                                14
                                        )
                                );

                                bookedPanel.add(
                                        bookedLabel
                                );

                                busPanel.add(
                                        bookedPanel
                                );

                            } else {

                                busPanel.add(
                                        new JLabel(
                                                "Available Seats: "
                                                + availableSeats
                                        )
                                );

                                JButton selectSeatsButton =
                                        new JButton(
                                                "Select Seats"
                                        );

                                selectSeatsButton.addActionListener(
                                        e -> {

                                            new SeatSelectionFrame(
                                                    schedule,
                                                    user
                                            );

                                            dispose();
                                        }
                                );

                                busPanel.add(
                                        selectSeatsButton
                                );
                            }

                            resultsPanel.add(
                                    busPanel
                            );

                            resultsPanel.add(
                                    Box.createVerticalStrut(10)
                            );
                        }
                    }

                    resultsPanel.revalidate();
                    resultsPanel.repaint();

                    System.out.println(
                            "6. Results displayed"
                    );

                } catch (Exception e) {

                    e.printStackTrace();

                    JOptionPane.showMessageDialog(
                            CustomerDashboard.this,
                            "Unable to search buses."
                    );
                }
            }
        };

        worker.execute();

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter the date in YYYY-MM-DD format."
        );
    }
}}

   