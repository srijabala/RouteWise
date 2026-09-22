package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.Booking;
import model.BusSchedule;
import model.Seat;
import model.User;
import util.PDFTicketGenerator;

/**
 * TicketPreviewDialog
 * 
 * Interactive Ticket Preview & Print Dialog built using 100% Core Java (Swing & java.awt.print).
 * Displays a visual boarding pass & receipt with options to Print directly or Save as PDF.
 */
public class TicketPreviewDialog extends JDialog implements Printable {

    private static final Color NAVY = new Color(20, 39, 63);
    private static final Color BLUE = new Color(28, 105, 101);
    private static final Color BG_LIGHT = new Color(246, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 40, 55);
    private static final Color MUTED = new Color(105, 115, 128);
    private static final Color BORDER = new Color(220, 226, 234);
    private static final Color GREEN = new Color(25, 135, 84);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final User user;
    private final BusSchedule schedule;
    private final List<Seat> seats;
    private final Booking booking;
    private final List<String> seatNumbers;
    private final double totalAmount;
    private final String paymentMethod;
    private final int seniorCitizenCount;
    private final int wheelchairCount;
    private final String devotionalPackageName;

    private JPanel printableTicketPanel;

    // Constructor for Immediate Payment Ticket Preview
    public TicketPreviewDialog(
            JFrame owner,
            User user,
            BusSchedule schedule,
            List<Seat> seats,
            double totalAmount,
            String paymentMethod,
            int seniorCitizenCount,
            int wheelchairCount,
            String devotionalPackageName) {

        super(owner, "RouteWise - Official Ticket Preview", true);
        this.user = user;
        this.schedule = schedule;
        this.seats = seats;
        this.booking = null;
        this.seatNumbers = null;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.seniorCitizenCount = seniorCitizenCount;
        this.wheelchairCount = wheelchairCount;
        this.devotionalPackageName = devotionalPackageName;

        initUI();
    }

    // Constructor for Booking History Ticket Preview
    public TicketPreviewDialog(
            JFrame owner,
            User user,
            Booking booking,
            List<String> seatNumbers) {

        super(owner, "RouteWise - Official Ticket Preview", true);
        this.user = user;
        this.schedule = null;
        this.seats = null;
        this.booking = booking;
        this.seatNumbers = seatNumbers;
        this.totalAmount = booking.getTotalAmount();
        this.paymentMethod = booking.getPaymentMethod();
        this.seniorCitizenCount = booking.getSeniorCitizenCount();
        this.wheelchairCount = booking.getWheelchairCount();
        this.devotionalPackageName = booking.getDevotionalPackageName();

        initUI();
    }

    private void initUI() {
        setSize(700, 750);
        setMinimumSize(new Dimension(680, 650));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        // Printable Ticket Canvas
        printableTicketPanel = createTicketCanvas();

        JScrollPane scrollPane = new JScrollPane(printableTicketPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Control Action Buttons Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        footer.setBackground(NAVY);

        JButton printBtn = new JButton("Print Ticket");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        printBtn.setBackground(GREEN);
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GREEN),
                new EmptyBorder(7, 14, 7, 14)));
        printBtn.addActionListener(e -> printTicket());

        JButton pdfBtn = new JButton("Save PDF File");
        pdfBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pdfBtn.setBackground(BLUE);
        pdfBtn.setForeground(Color.WHITE);
        pdfBtn.setFocusPainted(false);
        pdfBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pdfBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BLUE),
                new EmptyBorder(7, 14, 7, 14)));
        pdfBtn.addActionListener(e -> exportPDF());

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setForeground(TEXT_DARK);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(7, 14, 7, 14)));
        closeBtn.addActionListener(e -> dispose());

        footer.add(printBtn);
        footer.add(pdfBtn);
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createTicketCanvas() {
        JPanel canvas = new JPanel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.Y_AXIS));
        canvas.setBackground(CARD_BG);
        canvas.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(20, 24, 20, 24)));

        // Header Banner
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(NAVY);
        banner.setBorder(new EmptyBorder(14, 18, 14, 18));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLbl = new JLabel("RouteWise");
        logoLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("PASSENGER TICKET & RECEIPT");
        subLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(new Color(190, 204, 222));

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);
        brandText.add(logoLbl);
        brandText.add(subLbl);

        String bId = booking != null ? "BK-" + booking.getBookingId() : "BK-" + (System.currentTimeMillis() % 100000);
        JLabel refLbl = new JLabel("Ticket Ref: " + bId);
        refLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refLbl.setForeground(Color.WHITE);
        refLbl.setOpaque(false);
        refLbl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 120), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        banner.add(brandText, BorderLayout.WEST);
        banner.add(refLbl, BorderLayout.EAST);
        canvas.add(banner);

        canvas.add(Box.createVerticalStrut(16));

        // 1. Journey Details Card Box
        JPanel routeBox = createCardBox("Journey Details");
        JPanel routeGrid = new JPanel(new GridLayout(2, 2, 16, 10));
        routeGrid.setOpaque(false);

        String pName = user.getFullName() != null ? user.getFullName() : "Customer";
        String pEmail = user.getEmail() != null ? user.getEmail() : "N/A";
        String routeStr = schedule != null ? schedule.getSourceCity() + " -> " + schedule.getDestinationCity() : booking.getSourceCity() + " -> " + booking.getDestinationCity();
        String busInfo = schedule != null ? schedule.getBusNumber() + " (" + schedule.getBusType() + ")" : booking.getBusNumber() + " (" + booking.getBusType() + ")";

        addDetailField(routeGrid, "PASSENGER NAME", pName);
        addDetailField(routeGrid, "PASSENGER EMAIL", pEmail);
        addDetailField(routeGrid, "ROUTE", routeStr);
        addDetailField(routeGrid, "BUS DETAILS", busInfo);

        routeBox.add(routeGrid, BorderLayout.CENTER);
        canvas.add(routeBox);
        canvas.add(Box.createVerticalStrut(14));

        // 2. Schedule & Seating Card Box
        JPanel scheduleBox = createCardBox("Schedule & Seating");
        JPanel scheduleGrid = new JPanel(new GridLayout(2, 2, 16, 10));
        scheduleGrid.setOpaque(false);

        String depTime = schedule != null ? (schedule.getDepartureTime() != null ? schedule.getDepartureTime().format(DATE_FORMATTER) : "N/A") : (booking.getDepartureTime() != null ? booking.getDepartureTime().format(DATE_FORMATTER) : "N/A");
        String arrTime = schedule != null ? (schedule.getArrivalTime() != null ? schedule.getArrivalTime().format(DATE_FORMATTER) : "N/A") : (booking.getArrivalTime() != null ? booking.getArrivalTime().format(DATE_FORMATTER) : "N/A");

        StringBuilder seatStr = new StringBuilder();
        if (seats != null && !seats.isEmpty()) {
            for (int i = 0; i < seats.size(); i++) {
                seatStr.append(seats.get(i).getSeatNumber());
                if (i < seats.size() - 1) seatStr.append(", ");
            }
        } else if (seatNumbers != null && !seatNumbers.isEmpty()) {
            for (int i = 0; i < seatNumbers.size(); i++) {
                seatStr.append(seatNumbers.get(i));
                if (i < seatNumbers.size() - 1) seatStr.append(", ");
            }
        } else if (booking != null) {
            seatStr.append(booking.getTotalSeats()).append(" Seat(s)");
        }

        int seatCount = seats != null ? seats.size() : (seatNumbers != null ? seatNumbers.size() : (booking != null ? booking.getTotalSeats() : 1));

        addDetailField(scheduleGrid, "DEPARTURE TIME", depTime);
        addDetailField(scheduleGrid, "ARRIVAL TIME", arrTime);
        addDetailField(scheduleGrid, "ASSIGNED SEATS", seatStr.toString());
        addDetailField(scheduleGrid, "TOTAL SEATS", seatCount + " Seat(s)");

        scheduleBox.add(scheduleGrid, BorderLayout.CENTER);
        canvas.add(scheduleBox);
        canvas.add(Box.createVerticalStrut(14));

        // 3. Payment & Inclusions Card Box
        JPanel paymentBox = createCardBox("Payment & Inclusions");
        JPanel paymentGrid = new JPanel(new GridLayout(3, 2, 16, 10));
        paymentGrid.setOpaque(false);

        String payMethod = paymentMethod != null ? paymentMethod : "ONLINE";
        String assistStr = "Senior: " + seniorCitizenCount + " | Wheelchair: " + wheelchairCount;
        String devoStr = (devotionalPackageName != null && !devotionalPackageName.isBlank()) ? devotionalPackageName : "None";

        addDetailField(paymentGrid, "PAYMENT METHOD", payMethod);
        addDetailField(paymentGrid, "PAYMENT STATUS", "CONFIRMED (PAID)");
        addDetailField(paymentGrid, "SPECIAL ASSISTANCE", assistStr);
        addDetailField(paymentGrid, "DEVOTIONAL ADD-ON", devoStr);
        addDetailField(paymentGrid, "TOTAL FARE PAID", "₹" + String.format("%.2f", totalAmount));

        paymentBox.add(paymentGrid, BorderLayout.CENTER);
        canvas.add(paymentBox);
        canvas.add(Box.createVerticalStrut(16));

        // Visual Barcode Component
        JPanel barcodePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NAVY);

                int x = 30;
                int y = 4;
                int height = 32;
                int[] pattern = {3, 1, 2, 4, 1, 3, 2, 1, 4, 2, 1, 3, 4, 1, 2, 3, 1, 4, 2, 1, 3, 2, 4, 1, 2, 3, 1, 4, 2, 1, 3, 4, 1, 2, 3};
                for (int p : pattern) {
                    g2.fillRect(x, y, p, height);
                    x += p + 3;
                }
            }
        };
        barcodePanel.setOpaque(false);
        barcodePanel.setPreferredSize(new Dimension(480, 40));
        barcodePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        canvas.add(barcodePanel);
        canvas.add(Box.createVerticalStrut(8));

        JLabel noteLbl = new JLabel("Please show this ticket & a valid Photo ID during boarding. Customer Care: 1800-123-7688");
        noteLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        noteLbl.setForeground(MUTED);
        noteLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        canvas.add(noteLbl);

        return canvas;
    }

    private JPanel createCardBox(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(12, 16, 12, 16)));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        header.setOpaque(false);

        JPanel accentPill = new JPanel();
        accentPill.setPreferredSize(new Dimension(4, 15));
        accentPill.setBackground(BLUE);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(BLUE);

        header.add(accentPill);
        header.add(titleLbl);

        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private void addDetailField(JPanel container, String labelText, String valText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(MUTED);

        JLabel val = new JLabel(valText);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if ("TOTAL FARE PAID".equals(labelText)) {
            val.setForeground(BLUE);
            val.setFont(new Font("Segoe UI", Font.BOLD, 15));
        } else if ("CONFIRMED (PAID)".equals(valText)) {
            val.setForeground(GREEN);
        } else {
            val.setForeground(TEXT_DARK);
        }

        fieldPanel.add(lbl);
        fieldPanel.add(Box.createVerticalStrut(2));
        fieldPanel.add(val);

        container.add(fieldPanel);
    }

    private void printTicket() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPDF() {
        if (schedule != null && seats != null) {
            PDFTicketGenerator.generateAndSaveTicket(
                    user, schedule, seats, totalAmount, paymentMethod, seniorCitizenCount, wheelchairCount, devotionalPackageName);
        } else if (booking != null) {
            PDFTicketGenerator.generateAndSaveTicket(booking, user, seatNumbers);
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        double scaleX = pageFormat.getImageableWidth() / printableTicketPanel.getWidth();
        double scaleY = pageFormat.getImageableHeight() / printableTicketPanel.getHeight();
        double scale = Math.min(scaleX, scaleY);
        g2d.scale(scale, scale);

        printableTicketPanel.printAll(g2d);

        return PAGE_EXISTS;
    }
}
