package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.BusSchedule;
import model.User;
import service.AIContextService;
import service.BusScheduleService;

public class CustomerDashboard extends JFrame {

    // ---------- Theme ----------
    private static final Color NAVY = new Color(20, 39, 63);
    private static final Color BLUE = new Color(28, 105, 101);
    private static final Color BG = new Color(246, 248, 251);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(30, 40, 55);
    private static final Color MUTED = new Color(105, 115, 128);
    private static final Color BORDER = new Color(220, 226, 234);
    private static final Color GREEN = new Color(25, 135, 84);
    private static final Color RED = new Color(190, 55, 55);
    private static final Color PINK = new Color(190, 65, 125);
    private static final Color PURPLE = new Color(112, 72, 158);

    // ---------- Logic ----------
    private final User user;
    private final BusScheduleService busScheduleService;
    private final AIContextService aiContextService;

    private List<BusSchedule> currentSchedules = new ArrayList<>();
    private List<BusSchedule> todaysSchedules = new ArrayList<>();

    // ---------- UI ----------
    private JComboBox<String> sourceBox;
    private JComboBox<String> destinationBox;
    private JComboBox<String> dateBox;
    private JButton searchButton;
    private JPanel todayPanel;
    private JPanel resultsPanel;

    // ---------- Smart Filter UI ----------
    private JComboBox<String> busTypeFilterCombo;
    private JComboBox<String> timeFilterCombo;
    private JComboBox<String> minSeatsFilterCombo;
    private JComboBox<String> sortCombo;
    private JLabel resultsCountLabel;
    private boolean isUpdatingFilters = false;

    public CustomerDashboard(User user) {
        this.user = user;
        busScheduleService = new BusScheduleService();
        aiContextService = new AIContextService();

        setTitle("RouteWise - Bus Reservation");
        setSize(1120, 780);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);
    }

    // =========================================================
    // MAIN
    // =========================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(header(), BorderLayout.NORTH);

        ScrollablePanel content = new ScrollablePanel();
        content.setBorder(new EmptyBorder(24, 34, 32, 34));

        content.add(searchCard());
        content.add(Box.createVerticalStrut(22));

        content.add(todaySection());
        content.add(Box.createVerticalStrut(22));

        content.add(supportSection());
        content.add(Box.createVerticalStrut(24));

        content.add(availableBusesHeaderCard());
        content.add(Box.createVerticalStrut(14));

        resultsPanel = verticalPanel();
        resultsPanel.add(
                infoCard(
                        "Search for a route and travel date to view buses."
                )
        );
        content.add(resultsPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);
        add(root);

        searchButton.addActionListener(e -> searchBuses());
        loadDates();
        loadToday();
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NAVY);
        p.setBorder(new EmptyBorder(15, 34, 15, 34));

        JPanel brand = verticalPanel();
        JLabel name = label("RouteWise", 24, Font.BOLD, WHITE);
        JLabel sub = label("Bus reservation • travel assistance", 12,
                Font.PLAIN, new Color(190, 204, 222));
        brand.add(name);
        brand.add(sub);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel welcome = label("Welcome, " + (user.getFullName() != null ? user.getFullName() : "Customer"), 15, Font.BOLD, WHITE);
        rightPanel.add(welcome);

        JButton myBookingsBtn = secondaryButton("My Bookings");
        myBookingsBtn.addActionListener(e -> new BookingHistoryFrame(user));
        rightPanel.add(myBookingsBtn);

        JButton contactBtn = secondaryButton("Contact Us");
        contactBtn.addActionListener(e -> openContactUs());
        rightPanel.add(contactBtn);

        JButton logoutBtn = secondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new WelcomeFrame();
        });
        rightPanel.add(logoutBtn);

        p.add(brand, BorderLayout.WEST);
        p.add(rightPanel, BorderLayout.EAST);
        return p;
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private JPanel searchCard() {
    JPanel card = card();

    JPanel heading = verticalPanel();
    heading.add(label("Where are you travelling?", 24, Font.BOLD, TEXT));
    heading.add(label("Search routes, compare buses and choose your seats.",
            13, Font.PLAIN, MUTED));

    sourceBox = new JComboBox<>(new String[] {
            "Visakhapatnam", "Vijayawada", "Hyderabad",
            "Chennai", "Bengaluru"
    });

    destinationBox = new JComboBox<>(new String[] {
            "Vijayawada", "Bhubaneswar", "Hyderabad", "Chennai",
            "Bengaluru", "Visakhapatnam", "Tirupati", "Srisailam",
            "Shirdi", "Varanasi", "Madurai", "Rameswaram",
            "Ayodhya", "Pune"
    });

    dateBox = new JComboBox<>();
    dateBox.addItem("Loading dates...");
    styleCombo(sourceBox);
    styleCombo(destinationBox);
    styleCombo(dateBox);

    // REPLACE THE OLD GRIDLAYOUT BLOCK HERE
    JPanel form = new JPanel(new GridBagLayout());
form.setOpaque(false);

GridBagConstraints gbc = new GridBagConstraints();
gbc.gridy = 0;
gbc.fill = GridBagConstraints.HORIZONTAL;
gbc.insets = new Insets(0, 0, 0, 12);

gbc.gridx = 0;
gbc.weightx = 1;
form.add(field("FROM", sourceBox), gbc);

gbc.gridx = 1;
form.add(field("TO", destinationBox), gbc);

gbc.gridx = 2;
form.add(field("TRAVEL DATE", dateBox), gbc);

searchButton = primaryButton("SEARCH");
searchButton.setPreferredSize(new Dimension(120, 38));

gbc.gridx = 3;
gbc.weightx = 0;
gbc.insets = new Insets(0, 0, 0, 0);
form.add(searchButton, gbc);

    card.add(heading, BorderLayout.NORTH);
    card.add(form, BorderLayout.SOUTH);
    return card;
}

    private JPanel field(String title, JComboBox<String> box) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(label(title, 10, Font.BOLD, MUTED), BorderLayout.NORTH);
        p.add(box, BorderLayout.CENTER);
        return p;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setBackground(WHITE);
        box.setPreferredSize(new Dimension(150, 42));
    }

    // =========================================================
    // TODAY
    // =========================================================

    private JPanel todaySection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(sectionTitle("Departing Today"), BorderLayout.WEST);
        heading.add(label(
                LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                12, Font.PLAIN, MUTED), BorderLayout.EAST);

        todayPanel = verticalPanel();
        todayPanel.add(infoCard("Loading today's departures..."));

        section.add(heading, BorderLayout.NORTH);
        section.add(todayPanel, BorderLayout.CENTER);
        return section;
    }

    private void loadToday() {
        new SwingWorker<List<BusSchedule>, Void>() {
            protected List<BusSchedule> doInBackground() {
                return busScheduleService.getTodaysSchedules();
            }

            protected void done() {
                try {
                    todaysSchedules = get();
                    todayPanel.removeAll();

                    if (todaysSchedules.isEmpty()) {
                        JPanel empty = infoCard("No departures are scheduled for today.");
                        empty.add(Box.createVerticalStrut(3));
                        empty.add(label("Use Search Buses above for upcoming departures.",
                                12, Font.PLAIN, MUTED));
                        todayPanel.add(empty);
                    } else {
                        for (BusSchedule s : todaysSchedules) {
                            todayPanel.add(todayBusCard(s));
                            todayPanel.add(Box.createVerticalStrut(8));
                        }
                    }
                    refresh(todayPanel);
                } catch (Exception e) {
                    e.printStackTrace();
                    todayPanel.removeAll();
                    todayPanel.add(infoCard("Today's bus information could not be loaded."));
                    refresh(todayPanel);
                }
            }
        }.execute();
    }

    private JPanel todayBusCard(BusSchedule s) {
        JPanel card = busCard();

        JPanel route = verticalPanel();
        route.add(label(s.getSourceCity() + "  →  " + s.getDestinationCity(),
                15, Font.BOLD, TEXT));
        route.add(Box.createVerticalStrut(4));
        route.add(label(time(s.getDepartureTime()) + "  →  " +
                time(s.getArrivalTime()), 12, Font.PLAIN, MUTED));

        JPanel details = verticalPanel();
        details.add(label(s.getBusNumber() + "  •  " + s.getBusType(),
                12, Font.PLAIN, TEXT));
        details.add(Box.createVerticalStrut(4));
        details.add(label(s.getAvailableSeats() + " seats available",
                12, Font.BOLD, s.getAvailableSeats() > 0 ? GREEN : RED));

        JPanel right = verticalPanel();
        right.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel fare = label("₹" + (int) s.getFare(), 18, Font.BOLD, BLUE);
        fare.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(fare);

        JButton select = primaryButton("SELECT SEATS");
        select.setAlignmentX(Component.RIGHT_ALIGNMENT);
        select.addActionListener(e -> openSeats(s));
        right.add(Box.createVerticalStrut(7));
        right.add(select);

        if (devotional(s)) {
            JButton info = secondaryButton("Package details");
            info.setAlignmentX(Component.RIGHT_ALIGNMENT);
            info.addActionListener(e -> devotionalInfo());
            right.add(Box.createVerticalStrut(6));
            right.add(info);
        }

        card.add(details, BorderLayout.WEST);
        card.add(route, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // =========================================================
    // SUPPORT
    // =========================================================

    private JPanel supportSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(sectionTitle("Travel Support"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 5, 10, 0));
        grid.setOpaque(false);

        grid.add(supportCard("AI TRAVEL ASSISTANT",
                "Destinations, planning and travel questions.",
                BLUE, e -> openAI()));

        grid.add(supportCard("WOMEN SAFETY",
                "Women-preferred seating, emergency numbers and safety tips.",
                PINK, e -> womenSafety()));

        grid.add(supportCard("ACCESSIBILITY",
                "Senior citizen and wheelchair assistance details.",
                BLUE, e -> accessibility()));

        grid.add(supportCard("DEVOTIONAL TRIPS",
                "Travel-support packages and what they include.",
                PURPLE, e -> devotionalInfo()));

        grid.add(supportCard("CONTACT US",
                "Helplines, support email and 24/7 customer care.",
                NAVY, e -> openContactUs()));

        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel supportCard(String title, String text, Color accent,
            java.awt.event.ActionListener action) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(14, 15, 12, 15)));

        card.add(label(title, 10, Font.BOLD, accent));
        card.add(Box.createVerticalStrut(7));
        card.add(label("<html><div style='width:190px'>" + text +
                "</div></html>", 12, Font.PLAIN, MUTED));
        card.add(Box.createVerticalStrut(8));

        JButton details = textButton("VIEW DETAILS", accent);
        details.addActionListener(action);
        card.add(details);

        return card;
    }

    // =========================================================
    // SEARCH RESULTS
    // =========================================================

    private void loadDates() {
        searchButton.setEnabled(false);

        new SwingWorker<List<String>, Void>() {
            protected List<String> doInBackground() {
                return busScheduleService.getAvailableTravelDates();
            }

            protected void done() {
                try {
                    List<String> dates = get();
                    dateBox.removeAllItems();

                    for (String d : dates) dateBox.addItem(d);
                    if (dates.isEmpty()) dateBox.addItem("No dates available");

                } catch (Exception e) {
                    e.printStackTrace();
                    dateBox.removeAllItems();
                    dateBox.addItem("Unable to load dates");
                } finally {
                    searchButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void searchBuses() {
        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();
        String date = (String) dateBox.getSelectedItem();

        if (source == null || destination == null || date == null) {
            warning("Please select a source, destination and date.");
            return;
        }

        if (source.equals(destination)) {
            warning("Source and destination cannot be the same.");
            return;
        }

        try {
            LocalDate travelDate = LocalDate.parse(date);
            searchButton.setEnabled(false);
            searchButton.setText("SEARCHING...");

            new SwingWorker<List<BusSchedule>, Void>() {
                protected List<BusSchedule> doInBackground() {
                    return busScheduleService.searchSchedules(
                            source, destination, travelDate);
                }

                protected void done() {
                    try {
                        currentSchedules = get();
                        isUpdatingFilters = true;
                        if (busTypeFilterCombo != null) busTypeFilterCombo.setSelectedIndex(0);
                        if (timeFilterCombo != null) timeFilterCombo.setSelectedIndex(0);
                        if (minSeatsFilterCombo != null) minSeatsFilterCombo.setSelectedIndex(0);
                        if (sortCombo != null) sortCombo.setSelectedIndex(0);
                        isUpdatingFilters = false;
                        displayResults(currentSchedules);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                                CustomerDashboard.this,
                                "Unable to search buses.",
                                "Search Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        searchButton.setEnabled(true);
                        searchButton.setText("SEARCH BUSES");
                    }
                }
            }.execute();

        } catch (Exception e) {
            warning("Please select a valid travel date.");
        }
    }

    private void displayResults(List<BusSchedule> schedules) {
        resultsPanel.removeAll();

        int totalCount = currentSchedules != null ? currentSchedules.size() : 0;
        int filteredCount = schedules != null ? schedules.size() : 0;

        if (resultsCountLabel != null) {
            if (currentSchedules == null || currentSchedules.isEmpty()) {
                resultsCountLabel.setText("0 buses found");
            } else if (filteredCount == totalCount) {
                resultsCountLabel.setText(totalCount + (totalCount == 1 ? " bus available" : " buses available"));
            } else {
                resultsCountLabel.setText(filteredCount + " of " + totalCount + " buses match filters");
            }
        }

        if (currentSchedules == null || currentSchedules.isEmpty()) {
            resultsPanel.add(infoCard(
                    "Search for a route and travel date to view buses."));
        } else if (schedules == null || schedules.isEmpty()) {
            resultsPanel.add(infoCard(
                    "No buses match the selected filters."));
        } else {
            for (BusSchedule s : schedules) {
                resultsPanel.add(resultCard(s));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        refresh(resultsPanel);
    }

    private JPanel resultCard(BusSchedule s) {
        JPanel card = busCard();

        JPanel left = verticalPanel();
        left.add(label(s.getSourceCity() + "  →  " + s.getDestinationCity(),
                16, Font.BOLD, TEXT));
        left.add(Box.createVerticalStrut(5));
        left.add(label(s.getOperatorName() + "  •  " + s.getBusNumber(),
                12, Font.PLAIN, MUTED));
        left.add(Box.createVerticalStrut(3));
        left.add(label(s.getBusType(), 12, Font.PLAIN, TEXT));

        if (devotional(s)) {
            left.add(Box.createVerticalStrut(6));
            left.add(label("DEVOTIONAL TRIP", 10, Font.BOLD, PURPLE));
        }

        JPanel times = new JPanel(new GridLayout(1, 2, 25, 0));
        times.setOpaque(false);
        times.add(timeGroup("DEPARTURE", dateTime(s.getDepartureTime())));
        times.add(timeGroup("ARRIVAL", dateTime(s.getArrivalTime())));

        JPanel right = verticalPanel();
        JLabel fare = label("₹" + (int) s.getFare(), 21, Font.BOLD, BLUE);
        fare.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(fare);

        JLabel seats = label(s.getAvailableSeats() + " seats available",
                12, Font.BOLD, s.getAvailableSeats() > 0 ? GREEN : RED);
        seats.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(seats);

        if (s.getAvailableSeats() > 0) {
            JButton select = primaryButton("SELECT SEATS");
            select.setAlignmentX(Component.RIGHT_ALIGNMENT);
            select.addActionListener(e -> openSeats(s));
            right.add(Box.createVerticalStrut(9));
            right.add(select);
        } else {
            JLabel sold = label("SOLD OUT", 11, Font.BOLD, RED);
            sold.setAlignmentX(Component.RIGHT_ALIGNMENT);
            right.add(Box.createVerticalStrut(9));
            right.add(sold);
        }

        if (devotional(s)) {
            JButton packageInfo = secondaryButton("Package details");
            packageInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            packageInfo.addActionListener(e -> devotionalInfo());
            right.add(Box.createVerticalStrut(6));
            right.add(packageInfo);
        }

        card.add(left, BorderLayout.WEST);
        card.add(times, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // =========================================================
    // FEATURE DETAILS
    // =========================================================

    private void womenSafety() {
        JPanel p = dialogPanel();
        dialogTitle(p, "Women Safety & Emergency", PINK);

        dialogSection(p, "Women-preferred seating",
                "RouteWise identifies available seats near currently occupied "
                + "seats of female passengers and highlights them as Women Preferred.\n\n"
                + "This is a seating preference, not a guarantee of women-only seating. "
                + "Gender information is self-declared in the current system.");

        dialogSection(p, "Emergency numbers",
                "Emergency: 112\n"
                + "Women Helpline: 181\n"
                + "Police: 100\n"
                + "Ambulance: 102\n"
                + "Cyber Crime: 1930\n"
                + "Senior Citizens: 14567");

        dialogSection(p, "Travel safety tips",
                "• Keep your phone charged.\n"
                + "• Keep emergency contacts accessible.\n"
                + "• Avoid sharing sensitive personal information.\n"
                + "• Inform a trusted person about your journey.\n"
                + "• Keep booking/ticket information accessible.\n"
                + "• Report harassment or immediate danger to the appropriate service.");

        showDialog(p, "Women Safety");
    }

    private void accessibility() {
        JPanel p = dialogPanel();
        dialogTitle(p, "Accessibility Assistance", BLUE);

        dialogSection(p, "Senior Citizen Assistance",
                "• Boarding assistance\n"
                + "• Getting-off assistance\n"
                + "• Help locating the assigned seat\n"
                + "• Luggage assistance during boarding\n"
                + "• Support around designated rest stops\n\n"
                + "Maximum: 4 assistance requests per bus.");

        dialogSection(p, "Wheelchair Assistance",
                "• Boarding assistance\n"
                + "• Getting-off assistance\n"
                + "• Accessibility support during travel\n"
                + "• Help locating the assigned seat\n"
                + "• Support during boarding/rest-stop transitions\n\n"
                + "Maximum: 2 assistance requests per bus.");

        dialogSection(p, "Important",
                "RouteWise assistance does not represent medical care, "
                + "medical transportation, guaranteed specialized equipment, "
                + "or automatic staff dispatch.");

        showDialog(p, "Accessibility");
    }

    private void devotionalInfo() {
        JPanel p = dialogPanel();
        dialogTitle(p, "RouteWise Devotional Travel Package", PURPLE);

        dialogSection(p, "What the package provides",
                "• Devotional travel assistance and information\n"
                + "• Basic trip guidance\n"
                + "• Route and travel information\n"
                + "• Suggested temple visit information\n"
                + "• Suggested sightseeing and places around the destination\n"
                + "• Basic travel-planning information");

        dialogSection(p, "Before payment",
                "The package is optional. Package-specific inclusions "
                + "and the additional price per passenger are shown before payment.");

        dialogSection(p, "Important clarification",
                "RouteWise devotional packages are travel-support packages. "
                + "They do not include official temple entry tickets, darshan tickets, "
                + "accommodation, or third-party services unless explicitly stated.");

        showDialog(p, "Devotional Package");
    }

    private void openAI() {
        String context = aiContextService.buildScheduleContext(currentSchedules);
        new AITravelAssistantFrame(context);
    }

    private void openSeats(BusSchedule schedule) {
        new SeatSelectionFrame(schedule, user);
        dispose();
    }

    private void openContactUs() {
        new ContactUsDialog(this).setVisible(true);
    }

    private JPanel availableBusesHeaderCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 14));

        // Top Bar: Heading + Results Count Badge (Left) & Sort + Reset (Right)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel leftHeading = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftHeading.setOpaque(false);

        JLabel titleLabel = label("Available Buses", 20, Font.BOLD, TEXT);
        resultsCountLabel = label("0 buses found", 12, Font.BOLD, BLUE);
        resultsCountLabel.setOpaque(true);
        resultsCountLabel.setBackground(new Color(230, 242, 242));
        resultsCountLabel.setForeground(BLUE);
        resultsCountLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 215, 213), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        leftHeading.add(titleLabel);
        leftHeading.add(resultsCountLabel);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        JLabel sortLabel = label("Sort by:", 12, Font.BOLD, MUTED);
        sortCombo = new JComboBox<>(new String[] {
                "Departure Time", "Fare: Low to High", "Available Seats: High to Low"
        });
        styleCombo(sortCombo);
        sortCombo.setPreferredSize(new Dimension(180, 36));
        sortCombo.addActionListener(e -> applyInMemoryFilters());

        JButton resetBtn = secondaryButton("Reset Filters");
        resetBtn.setPreferredSize(new Dimension(105, 36));
        resetBtn.addActionListener(e -> {
            isUpdatingFilters = true;
            if (busTypeFilterCombo != null) busTypeFilterCombo.setSelectedIndex(0);
            if (timeFilterCombo != null) timeFilterCombo.setSelectedIndex(0);
            if (minSeatsFilterCombo != null) minSeatsFilterCombo.setSelectedIndex(0);
            if (sortCombo != null) sortCombo.setSelectedIndex(0);
            isUpdatingFilters = false;
            if (currentSchedules != null && !currentSchedules.isEmpty()) {
                displayResults(currentSchedules);
            }
        });

        rightControls.add(sortLabel);
        rightControls.add(sortCombo);
        rightControls.add(resetBtn);

        topBar.add(leftHeading, BorderLayout.WEST);
        topBar.add(rightControls, BorderLayout.EAST);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBackground(BORDER);

        // Bottom Row: Filter Controls
        busTypeFilterCombo = new JComboBox<>(new String[] {
                "All Bus Types",
                "AC Sleeper",
                "AC Seater",
                "Non-AC Sleeper",
                "Non-AC Seater",
                "Volvo Multi-Axle"
        });

        timeFilterCombo = new JComboBox<>(new String[] {
                "Any Time", "Morning (6 AM - 12 PM)", "Afternoon (12 PM - 6 PM)", "Night (6 PM - 6 AM)"
        });

        minSeatsFilterCombo = new JComboBox<>(new String[] {
                "Any Seats", "1+ Seat", "2+ Seats", "3+ Seats", "4+ Seats"
        });

        styleCombo(busTypeFilterCombo);
        styleCombo(timeFilterCombo);
        styleCombo(minSeatsFilterCombo);

        busTypeFilterCombo.setPreferredSize(new Dimension(180, 38));
        timeFilterCombo.setPreferredSize(new Dimension(210, 38));
        minSeatsFilterCombo.setPreferredSize(new Dimension(160, 38));

        busTypeFilterCombo.addActionListener(e -> applyInMemoryFilters());
        timeFilterCombo.addActionListener(e -> applyInMemoryFilters());
        minSeatsFilterCombo.addActionListener(e -> applyInMemoryFilters());

        JPanel filterRow = new JPanel(new GridBagLayout());
        filterRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);

        gbc.gridx = 0; gbc.weightx = 1.0;
        filterRow.add(field("BUS TYPE", busTypeFilterCombo), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        filterRow.add(field("DEPARTURE TIME", timeFilterCombo), gbc);

        gbc.gridx = 2; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        filterRow.add(field("SEATS AVAILABLE", minSeatsFilterCombo), gbc);

        // Assembly
        JPanel cardContent = verticalPanel();
        cardContent.add(topBar);
        cardContent.add(Box.createVerticalStrut(14));
        cardContent.add(sep);
        cardContent.add(Box.createVerticalStrut(14));
        cardContent.add(filterRow);

        card.add(cardContent, BorderLayout.CENTER);
        return card;
    }

    private void applyInMemoryFilters() {
        if (isUpdatingFilters) return;
        if (currentSchedules == null || currentSchedules.isEmpty()) {
            displayResults(new ArrayList<>());
            return;
        }

        String selectedType = (String) busTypeFilterCombo.getSelectedItem();
        String selectedTime = (String) timeFilterCombo.getSelectedItem();
        String selectedMinSeats = (String) minSeatsFilterCombo.getSelectedItem();
        String selectedSort = (String) sortCombo.getSelectedItem();

        List<BusSchedule> filtered = new ArrayList<>();

        for (BusSchedule s : currentSchedules) {
            // 1. Bus Type Filter
            // Filter against the exact bus type stored in the CURRENT
            // search results. Do not use a loose contains() match because
            // "AC" would also match "Non-AC".
            if (selectedType != null && !selectedType.equals("All Bus Types")) {
                String actualType = s.getBusType() == null
                        ? ""
                        : s.getBusType().trim();

                if (!actualType.equalsIgnoreCase(selectedType)) {
                    continue;
                }
            }

            // 2. Departure Time Filter
            if (selectedTime != null && !selectedTime.equals("Any Time")) {
                int hour = s.getDepartureTime() != null ? s.getDepartureTime().getHour() : -1;
                if (hour != -1) {
                    if (selectedTime.startsWith("Morning") && (hour < 6 || hour >= 12)) continue;
                    if (selectedTime.startsWith("Afternoon") && (hour < 12 || hour >= 18)) continue;
                    if (selectedTime.startsWith("Night") && (hour >= 6 && hour < 18)) continue;
                }
            }

            // 3. Minimum Available Seats Filter
            if (selectedMinSeats != null && !selectedMinSeats.equals("Any Seats")) {
                int minSeats = 0;
                if (selectedMinSeats.startsWith("1+")) minSeats = 1;
                else if (selectedMinSeats.startsWith("2+")) minSeats = 2;
                else if (selectedMinSeats.startsWith("3+")) minSeats = 3;
                else if (selectedMinSeats.startsWith("4+")) minSeats = 4;

                if (s.getAvailableSeats() < minSeats) {
                    continue;
                }
            }

            filtered.add(s);
        }

        // Apply Sorting
        if (selectedSort != null) {
            if (selectedSort.contains("Fare")) {
                filtered.sort((a, b) -> Double.compare(a.getFare(), b.getFare()));
            } else if (selectedSort.contains("Available Seats")) {
                filtered.sort((a, b) -> Integer.compare(b.getAvailableSeats(), a.getAvailableSeats()));
            } else {
                // Default: Departure Time
                filtered.sort((a, b) -> {
                    if (a.getDepartureTime() == null || b.getDepartureTime() == null) return 0;
                    return a.getDepartureTime().compareTo(b.getDepartureTime());
                });
            }
        }

        displayResults(filtered);
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

   private JPanel sectionTitle(String text) {
    JPanel p = new JPanel(
            new FlowLayout(
                    FlowLayout.LEFT,
                    0,
                    0
            )
    );

    p.setOpaque(false);
    p.setAlignmentX(Component.LEFT_ALIGNMENT);

    p.add(label(
            text,
            19,
            Font.BOLD,
            TEXT
    ));

    return p;
}

    private JPanel card() {
        JPanel p = new JPanel(new BorderLayout(0, 17));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(20, 22, 20, 22)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JPanel busCard() {
        JPanel p = new JPanel(new BorderLayout(28, 0));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(15, 18, 15, 18)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JPanel verticalPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private JPanel infoCard(String text) {
        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(text, 13, Font.PLAIN, MUTED));
        return p;
    }

    private JPanel timeGroup(String title, String time) {
        JPanel p = verticalPanel();
        p.add(label(title, 10, Font.BOLD, MUTED));
        p.add(Box.createVerticalStrut(3));
        p.add(label(time, 14, Font.BOLD, TEXT));
        return p;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(BLUE);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 13, 8, 13));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(WHITE);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(7, 10, 7, 10)));
        return b;
    }

    private JButton textButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(color);
        b.setBackground(WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(5, 0, 5, 0));
        return b;
    }

    private JPanel dialogPanel() {
        JPanel p = verticalPanel();
        p.setBorder(new EmptyBorder(7, 7, 7, 7));
        return p;
    }

    private void dialogTitle(JPanel p, String title, Color color) {
        p.add(label(title, 18, Font.BOLD, color));
        p.add(Box.createVerticalStrut(12));
    }

    private void dialogSection(JPanel p, String title, String text) {
        p.add(label(title, 13, Font.BOLD, TEXT));
        p.add(Box.createVerticalStrut(4));
        p.add(label("<html>" + text.replace("\n", "<br>") + "</html>",
                12, Font.PLAIN, MUTED));
        p.add(Box.createVerticalStrut(11));
    }

    private void showDialog(JPanel panel, String title) {
        JOptionPane.showMessageDialog(
                this, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void warning(String text) {
        JOptionPane.showMessageDialog(
                this, text, "Check Search Details",
                JOptionPane.WARNING_MESSAGE);
    }

    private void refresh(JPanel p) {
        p.revalidate();
        p.repaint();
    }

    private boolean devotional(BusSchedule s) {
        return "DEVOTIONAL".equals(s.getTripCategory());
    }

    private String time(java.time.LocalDateTime value) {
        if (value == null) return "N/A";
        return value.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private String dateTime(java.time.LocalDateTime value) {
        if (value == null) return "N/A";
        return value.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"));
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BG);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
