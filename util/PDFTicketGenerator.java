package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Booking;
import model.BusSchedule;
import model.Seat;
import model.User;

public class PDFTicketGenerator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static void generateAndSaveTicket(
            User user,
            BusSchedule schedule,
            List<Seat> seats,
            double totalAmount,
            String paymentMethod,
            int seniorCitizenCount,
            int wheelchairCount,
            String devotionalPackageName) {

        StringBuilder seatNums = new StringBuilder();

        if (seats != null) {
            for (int i = 0; i < seats.size(); i++) {
                seatNums.append(seats.get(i).getSeatNumber());

                if (i < seats.size() - 1) {
                    seatNums.append(", ");
                }
            }
        }

        TicketData data = new TicketData();

        // This method is used immediately after payment. The actual DB booking
        // ID is not passed into this method yet, so keep the existing reference
        // behavior for now.
        data.bookingId = "BK-" + (System.currentTimeMillis() % 100000);

        data.passengerName =
                user != null && user.getFullName() != null
                        ? user.getFullName()
                        : "Customer";

        data.passengerEmail =
                user != null && user.getEmail() != null
                        ? user.getEmail()
                        : "N/A";

        data.route =
                schedule.getSourceCity()
                        + " -> "
                        + schedule.getDestinationCity();

        data.busNumber = safe(schedule.getBusNumber());
        data.busType = safe(schedule.getBusType());
        data.operator = safe(schedule.getOperatorName());

        data.departureTime =
                schedule.getDepartureTime() != null
                        ? schedule.getDepartureTime().format(DATE_FORMATTER)
                        : "N/A";

        data.arrivalTime =
                schedule.getArrivalTime() != null
                        ? schedule.getArrivalTime().format(DATE_FORMATTER)
                        : "N/A";

        data.seats = seatNums.length() > 0 ? seatNums.toString() : "N/A";
        data.totalSeats = seats != null ? String.valueOf(seats.size()) : "0";
        data.farePerSeat =
                "Rs. " + String.format("%.2f", schedule.getFare());

        data.totalAmount =
                "Rs. " + String.format("%.2f", totalAmount);

        data.paymentMethod =
                paymentMethod != null && !paymentMethod.isBlank()
                        ? paymentMethod
                        : "N/A";

        data.paymentStatus = "PAID - CONFIRMED";
        data.seniorCitizenCount = seniorCitizenCount;
        data.wheelchairCount = wheelchairCount;
        data.devotionalPackage = devotionalPackageName;

        saveTicketWithFileChooser(data);
    }

    public static void generateAndSaveTicket(
            Booking booking,
            User user,
            List<String> seatNumbers) {

        StringBuilder seatNums = new StringBuilder();

        if (seatNumbers != null && !seatNumbers.isEmpty()) {
            for (int i = 0; i < seatNumbers.size(); i++) {
                seatNums.append(seatNumbers.get(i));

                if (i < seatNumbers.size() - 1) {
                    seatNums.append(", ");
                }
            }
        } else {
            seatNums.append(booking.getTotalSeats()).append(" Seat(s)");
        }

        TicketData data = new TicketData();

        data.bookingId = "BK-" + booking.getBookingId();

        data.passengerName =
                user != null && user.getFullName() != null
                        ? user.getFullName()
                        : "Customer";

        data.passengerEmail =
                user != null && user.getEmail() != null
                        ? user.getEmail()
                        : "N/A";

        data.route =
                booking.getSourceCity()
                        + " -> "
                        + booking.getDestinationCity();

        data.busNumber = safe(booking.getBusNumber());
        data.busType = safe(booking.getBusType());
        data.operator = "RouteWise Partner";

        data.departureTime =
                booking.getDepartureTime() != null
                        ? booking.getDepartureTime().format(DATE_FORMATTER)
                        : "N/A";

        data.arrivalTime =
                booking.getArrivalTime() != null
                        ? booking.getArrivalTime().format(DATE_FORMATTER)
                        : "N/A";

        data.seats = seatNums.toString();
        data.totalSeats = String.valueOf(booking.getTotalSeats());
        data.farePerSeat = "See booking fare";
        data.totalAmount =
                "Rs. " + String.format("%.2f", booking.getTotalAmount());

        data.paymentMethod =
                booking.getPaymentMethod() != null
                        ? booking.getPaymentMethod()
                        : "N/A";

        data.paymentStatus =
                booking.getPaymentStatus() != null
                        ? booking.getPaymentStatus()
                        : booking.getStatus();

        data.seniorCitizenCount = booking.getSeniorCitizenCount();
        data.wheelchairCount = booking.getWheelchairCount();
        data.devotionalPackage = booking.getDevotionalPackageName();

        saveTicketWithFileChooser(data);
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    private static void saveTicketWithFileChooser(TicketData data) {

        File downloadsFolder =
                new File(System.getProperty("user.home"), "Downloads");

        if (!downloadsFolder.exists()) {
            downloadsFolder = new File(System.getProperty("user.home"));
        }

        JFileChooser fileChooser = new JFileChooser(downloadsFolder);

        fileChooser.setDialogTitle("Save RouteWise Booking Ticket");

        fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                        "PDF Document (*.pdf)",
                        "pdf"
                )
        );

        fileChooser.setSelectedFile(
                new File(
                        downloadsFolder,
                        "RouteWise_Ticket_"
                                + data.bookingId
                                + ".pdf"
                )
        );

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();

        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave =
                    new File(
                            fileToSave.getAbsolutePath() + ".pdf"
                    );
        }

        try {
            byte[] pdfBytes = buildPDFBytes(data);

            try (FileOutputStream fos =
                         new FileOutputStream(fileToSave)) {

                fos.write(pdfBytes);
                fos.flush();
            }

            try {
                if (java.awt.Desktop.isDesktopSupported()
                        && java.awt.Desktop.getDesktop()
                        .isSupported(java.awt.Desktop.Action.OPEN)) {

                    java.awt.Desktop.getDesktop().open(fileToSave);
                }
            } catch (Exception launchEx) {
                System.err.println("Could not auto-open PDF viewer: " + launchEx.getMessage());
            }

            JOptionPane.showMessageDialog(
                    null,
                    "PDF ticket saved successfully.\n\n"
                            + "Saved to:\n"
                            + fileToSave.getAbsolutePath(),
                    "Ticket Exported",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Failed to save PDF ticket:\n"
                            + ex.getMessage(),
                    "PDF Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static byte[] buildPDFBytes(TicketData data)
            throws IOException {

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();

        StringBuilder content = new StringBuilder();

        // Background / header
        content.append("0.078 0.152 0.247 rg\n");
        content.append("30 720 535 90 re f\n");

        text(content, 50, 770, 26, true, "FFFFFF", "RouteWise");
        text(content, 50, 748, 10, false, "D9E0E8",
                "BUS RESERVATION TICKET & PAYMENT RECEIPT");
        text(content, 400, 770, 9, false, "D9E0E8",
                "Ticket Reference");
        text(content, 400, 750, 12, true, "FFFFFF",
                data.bookingId);

        // Passenger / journey
        box(content, 30, 575, 535, 125);

        text(content, 45, 677, 13, true, "1C2635",
                "PASSENGER & JOURNEY DETAILS");

        label(content, 45, 650, "Passenger Name");
        value(content, 45, 634, data.passengerName);

        label(content, 300, 650, "Email");
        value(content, 300, 634, data.passengerEmail);

        label(content, 45, 607, "Route");
        value(content, 45, 591, data.route);

        label(content, 300, 607, "Bus");
        value(content, 300, 591,
                data.busNumber + " - " + data.busType);

        label(content, 45, 565, "Operator");
        value(content, 45, 549, data.operator);

        // Schedule / seats
        box(content, 30, 425, 535, 135);

        text(content, 45, 537, 13, true, "1C2635",
                "SCHEDULE & SEATING");

        label(content, 45, 510, "Departure");
        value(content, 45, 494, data.departureTime);

        label(content, 300, 510, "Arrival");
        value(content, 300, 494, data.arrivalTime);

        label(content, 45, 468, "Assigned Seats");
        value(content, 45, 452, data.seats);

        label(content, 300, 468, "Seats Booked");
        value(content, 300, 452,
                data.totalSeats + " Seat(s)");

        // Payment / extras
        box(content, 30, 245, 535, 165);

        text(content, 45, 387, 13, true, "1C2635",
                "PAYMENT & TRAVEL SUPPORT");

        label(content, 45, 360, "Fare per Seat");
        value(content, 45, 344, data.farePerSeat);

        label(content, 300, 360, "Payment Method");
        value(content, 300, 344, data.paymentMethod);

        label(content, 45, 318, "Payment Status");
        value(content, 45, 302, data.paymentStatus);

        label(content, 300, 318, "Accessibility");
        value(
                content,
                300,
                302,
                "Senior: " + data.seniorCitizenCount
                        + " | Wheelchair: "
                        + data.wheelchairCount
        );

        label(content, 45, 276, "Devotional Package");
        value(
                content,
                45,
                260,
                data.devotionalPackage != null
                        && !data.devotionalPackage.isBlank()
                        ? data.devotionalPackage
                        : "None"
        );

        text(content, 300, 274, 10, true, "52606A",
                "TOTAL AMOUNT PAID");
        text(content, 300, 252, 17, true, "1C6965",
                data.totalAmount);

        // Footer
        content.append("0.86 0.88 0.91 RG\n");
        content.append("30 215 m 565 215 l S\n");

        text(content, 45, 190, 10, true, "14273F",
                "RouteWise Support");

        text(content, 45, 172, 9, false, "64748B",
                "support@routewise.com");

        text(content, 45, 156, 9, false, "64748B",
                "This is a simulated payment receipt for the RouteWise project.");

        text(content, 45, 140, 9, false, "64748B",
                "Please verify your booking details before travel.");

        byte[] streamBytes =
                content.toString()
                        .getBytes(StandardCharsets.US_ASCII);

        writePdfObjects(pdf, streamBytes);

        return pdf.toByteArray();
    }

    private static void box(
            StringBuilder content,
            int x,
            int y,
            int width,
            int height) {

        content.append("0.96 0.97 0.98 rg\n");
        content.append(x).append(" ")
                .append(y).append(" ")
                .append(width).append(" ")
                .append(height)
                .append(" re f\n");

        content.append("0.86 0.88 0.91 RG\n");
        content.append("1 w\n");
        content.append(x).append(" ")
                .append(y).append(" ")
                .append(width).append(" ")
                .append(height)
                .append(" re s\n");
    }

    private static void label(
            StringBuilder content,
            int x,
            int y,
            String value) {

        text(content, x, y, 9, true, "69727E", value + ":");
    }

    private static void value(
            StringBuilder content,
            int x,
            int y,
            String value) {

        text(
                content,
                x,
                y,
                10,
                false,
                "1C2635",
                value
        );
    }

    /*
     * IMPORTANT:
     * Tm sets an absolute text position.
     * The previous generator used repeated Td commands,
     * which caused later fields to move relative to earlier fields.
     */
    private static void text(
            StringBuilder content,
            int x,
            int y,
            int size,
            boolean bold,
            String hexColor,
            String value) {

        String rgb = hexToRgb(hexColor);

        content.append("BT\n");

        content.append(bold ? "/F2 " : "/F1 ")
                .append(size)
                .append(" Tf\n");

        content.append(rgb)
                .append(" rg\n");

        content.append("1 0 0 1 ")
                .append(x)
                .append(" ")
                .append(y)
                .append(" Tm\n");

        content.append("(")
                .append(escapePdf(value))
                .append(") Tj\n");

        content.append("ET\n");
    }

    private static String hexToRgb(String hex) {

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return String.format(
                "%.3f %.3f %.3f",
                r / 255.0,
                g / 255.0,
                b / 255.0
        );
    }

    private static String escapePdf(String value) {

        if (value == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (char c : value.toCharArray()) {

            if (c == '(') {
                result.append("\\(");
            } else if (c == ')') {
                result.append("\\)");
            } else if (c == '\\') {
                result.append("\\\\");
            } else if (c >= 32 && c <= 126) {
                result.append(c);
            } else {
                result.append("?");
            }
        }

        return result.toString();
    }

    private static void writePdfObjects(
            ByteArrayOutputStream pdf,
            byte[] streamBytes)
            throws IOException {

        String header =
                "%PDF-1.4\n"
                        + "%\u00e2\u00e3\u00cf\u00d3\n";

        pdf.write(
                header.getBytes(StandardCharsets.US_ASCII)
        );

        long offset1 = pdf.size();
        write(
                pdf,
                "1 0 obj\n"
                        + "<</Type /Catalog /Pages 2 0 R>>\n"
                        + "endobj\n"
        );

        long offset2 = pdf.size();
        write(
                pdf,
                "2 0 obj\n"
                        + "<</Type /Pages /Kids [3 0 R] /Count 1>>\n"
                        + "endobj\n"
        );

        long offset3 = pdf.size();
        write(
                pdf,
                "3 0 obj\n"
                        + "<</Type /Page /Parent 2 0 R "
                        + "/MediaBox [0 0 595 842] "
                        + "/Resources "
                        + "<</Font "
                        + "<</F1 5 0 R /F2 6 0 R>>>> "
                        + "/Contents 4 0 R>>\n"
                        + "endobj\n"
        );

        long offset4 = pdf.size();

        write(
                pdf,
                "4 0 obj\n"
                        + "<</Length "
                        + streamBytes.length
                        + ">>\n"
                        + "stream\n"
        );

        pdf.write(streamBytes);

        write(
                pdf,
                "\nendstream\n"
                        + "endobj\n"
        );

        long offset5 = pdf.size();

        write(
                pdf,
                "5 0 obj\n"
                        + "<</Type /Font /Subtype /Type1 "
                        + "/BaseFont /Helvetica /Encoding /WinAnsiEncoding>>\n"
                        + "endobj\n"
        );

        long offset6 = pdf.size();

        write(
                pdf,
                "6 0 obj\n"
                        + "<</Type /Font /Subtype /Type1 "
                        + "/BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding>>\n"
                        + "endobj\n"
        );

        long startXref = pdf.size();

        StringBuilder xref = new StringBuilder();

        xref.append("xref\n");
        xref.append("0 7\n");
        xref.append("0000000000 65535 f \n");

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset1
                )
        );

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset2
                )
        );

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset3
                )
        );

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset4
                )
        );

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset5
                )
        );

        xref.append(
                String.format(
                        "%010d 00000 n \n",
                        offset6
                )
        );

        xref.append("trailer\n");
        xref.append("<</Size 7 /Root 1 0 R>>\n");
        xref.append("startxref\n");
        xref.append(startXref).append("\n");
        xref.append("%%EOF\n");

        write(pdf, xref.toString());
    }

    private static void write(
            ByteArrayOutputStream output,
            String text)
            throws IOException {

        output.write(
                text.getBytes(StandardCharsets.US_ASCII)
        );
    }

    private static class TicketData {

        String bookingId;
        String passengerName;
        String passengerEmail;
        String route;
        String busNumber;
        String busType;
        String operator;
        String departureTime;
        String arrivalTime;
        String seats;
        String totalSeats;
        String farePerSeat;
        String totalAmount;
        String paymentMethod;
        String paymentStatus;
        int seniorCitizenCount;
        int wheelchairCount;
        String devotionalPackage;
    }
}
