package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import model.BusSchedule;
import model.Seat;
import model.User;
import service.BookingService;

public class PaymentFrame extends JFrame {

    private User user;
    private BusSchedule schedule;
    private List<Seat> selectedSeats;
    private double totalAmount;

    private JRadioButton upiButton;
    private JRadioButton cardButton;

    private int devotionalPackageId;
    private int seniorCitizenCount;
    private int wheelchairCount;

    private BookingService bookingService;

    public PaymentFrame(
            User user,
            BusSchedule schedule,
            List<Seat> selectedSeats,
            double totalAmount,
            int devotionalPackageId,
            int seniorCitizenCount,
            int wheelchairCount) {

        this.user = user;
        this.schedule = schedule;
        this.selectedSeats = selectedSeats;
        this.totalAmount = totalAmount;
        this.bookingService = new BookingService();
        this.devotionalPackageId = devotionalPackageId;
        this.seniorCitizenCount = seniorCitizenCount;
        this.wheelchairCount = wheelchairCount;

        setTitle("RouteWise - Payment");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 25, 25
                )
        );

        JLabel titleLabel =
                new JLabel("Payment");

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        JPanel detailsPanel =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                10,
                                10
                        )
                );

        detailsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Booking Details"
                )
        );

        detailsPanel.add(
                new JLabel("Route:")
        );

        detailsPanel.add(
                new JLabel(
                        schedule.getSourceCity()
                        + " → "
                        + schedule.getDestinationCity()
                )
        );

        detailsPanel.add(
                new JLabel("Seats:")
        );

        StringBuilder seatNumbers =
                new StringBuilder();

        for (Seat seat : selectedSeats) {

            if (seatNumbers.length() > 0) {
                seatNumbers.append(", ");
            }

            seatNumbers.append(
                    seat.getSeatNumber()
            );
        }

        detailsPanel.add(
                new JLabel(
                        seatNumbers.toString()
                )
        );

        detailsPanel.add(
                new JLabel("Amount:")
        );

        JLabel amountLabel =
                new JLabel(
                        "₹" + totalAmount
                );

        amountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        detailsPanel.add(amountLabel);

        mainPanel.add(
                detailsPanel,
                BorderLayout.CENTER
        );

        JPanel paymentPanel =
                new JPanel();

        paymentPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Payment Method"
                )
        );

        upiButton =
                new JRadioButton("UPI", true);

        cardButton =
                new JRadioButton("Card");

        ButtonGroup paymentGroup =
                new ButtonGroup();

        paymentGroup.add(upiButton);
        paymentGroup.add(cardButton);

        paymentPanel.add(upiButton);
        paymentPanel.add(cardButton);

        JPanel bottomPanel =
                new JPanel();

        JButton cancelButton =
                new JButton("Cancel");

        JButton payButton =
                new JButton("Pay Now");

        bottomPanel.add(cancelButton);
        bottomPanel.add(payButton);

        JPanel southPanel =
                new JPanel(
                        new BorderLayout()
                );

        southPanel.add(
                paymentPanel,
                BorderLayout.CENTER
        );

        southPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                southPanel,
                BorderLayout.SOUTH
        );

        cancelButton.addActionListener(e -> {

            new BookingSummaryFrame(
                    user,
                    schedule,
                    selectedSeats
            );

            dispose();
        });

        payButton.addActionListener(e -> {

            String paymentMethod;

            if (upiButton.isSelected()) {
                paymentMethod = "UPI";
            } else {
                paymentMethod = "CARD";
            }

            boolean success =
                    bookingService.createBooking(
                            user.getUserId(),
                            schedule.getScheduleId(),
                            selectedSeats,
                            totalAmount,
                            paymentMethod,
                            devotionalPackageId,
                            seniorCitizenCount,
                            wheelchairCount
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Payment successful!\n"
                        + "Booking confirmed!\n"
                        + "Method: "
                        + paymentMethod
                        + "\nAmount: ₹"
                        + totalAmount
                );

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Booking could not be completed.\n"
                        + "One or more selected seats may have "
                        + "been booked by another customer."
                );
            }
        });

        add(mainPanel);
    }
}