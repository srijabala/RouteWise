package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ContactUsDialog extends JDialog {

    // ---------- Colors ----------
    private static final Color NAVY = new Color(20, 39, 63);
    private static final Color BLUE = new Color(28, 105, 101);
    private static final Color BG_LIGHT = new Color(246, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 40, 55);
    private static final Color MUTED = new Color(105, 115, 128);
    private static final Color BORDER = new Color(220, 226, 234);

    // ---------- Fonts ----------
    private static final Font TITLE_FONT =
            new Font("SansSerif", Font.BOLD, 22);

    private static final Font SUBTITLE_FONT =
            new Font("SansSerif", Font.PLAIN, 13);

    private static final Font CARD_TITLE_FONT =
            new Font("SansSerif", Font.BOLD, 14);

    private static final Font CARD_TEXT_FONT =
            new Font("SansSerif", Font.PLAIN, 13);

    private static final Font CARD_SMALL_FONT =
            new Font("SansSerif", Font.PLAIN, 12);

    private static final Font BUTTON_FONT =
            new Font("SansSerif", Font.BOLD, 12);

    public ContactUsDialog(Frame owner) {

        super(owner, "RouteWise - Contact & Support", true);

        setSize(620, 570);
        setLocationRelativeTo(owner);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        // ---------- HEADER ----------

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(NAVY);
        headerPanel.setBorder(
                new EmptyBorder(22, 26, 22, 26));

        JLabel titleLabel =
                new JLabel("RouteWise Support");

        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel =
                new JLabel(
                        "Help with bookings, tickets and travel assistance"
                );

        subLabel.setFont(SUBTITLE_FONT);
        subLabel.setForeground(
                new Color(205, 215, 228));

        JPanel headerText = new JPanel();
        headerText.setLayout(
                new BoxLayout(
                        headerText,
                        BoxLayout.Y_AXIS));

        headerText.setOpaque(false);

        headerText.add(titleLabel);
        headerText.add(Box.createVerticalStrut(7));
        headerText.add(subLabel);

        headerPanel.add(
                headerText,
                BorderLayout.CENTER);

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH);

        // ---------- CONTENT ----------

        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS));

        contentPanel.setBackground(BG_LIGHT);

        contentPanel.setBorder(
                new EmptyBorder(20, 26, 14, 26));

        // ---------- CONTACT CARDS ----------

        JPanel gridPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                14,
                                14));

        gridPanel.setOpaque(false);

        gridPanel.add(
                createContactCard(
                        "CUSTOMER HELPLINE",
                        "+91 1800-123-7688\n"
                                + "+91 0891-2748900",
                        "Booking and travel queries"));

        gridPanel.add(
                createContactCard(
                        "EMAIL SUPPORT",
                        "support@routewise.com\n"
                                + "help@routewise.com",
                        "Questions and feedback"));

        gridPanel.add(
                createContactCard(
                        "ROUTEWISE OFFICE",
                        "Visakhapatnam\n"
                                + "Andhra Pradesh, India",
                        "RouteWise project office"));

        gridPanel.add(
                createContactCard(
                        "SUPPORT HOURS",
                        "Monday - Sunday\n"
                                + "Available throughout the day",
                        "Travel assistance"));

        contentPanel.add(gridPanel);

        contentPanel.add(
                Box.createVerticalStrut(18));

        // ---------- QUICK HELP ----------

        JPanel helpCard =
                new JPanel(
                        new BorderLayout());

        helpCard.setBackground(CARD_BG);

        helpCard.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER),
                        new EmptyBorder(
                                16,
                                18,
                                16,
                                18)));

        JLabel helpTitle =
                new JLabel("QUICK HELP");

        helpTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15));

        helpTitle.setForeground(BLUE);

        JLabel helpText =
                new JLabel(
                        "<html>"
                                + "<b>Booking:</b> Search for a route, "
                                + "select a bus and choose your seats.<br><br>"
                                + "<b>Cancellation:</b> Open My Bookings "
                                + "and select the booking to cancel.<br><br>"
                                + "<b>Special Assistance:</b> Request senior "
                                + "citizen or wheelchair assistance during booking.<br><br>"
                                + "<b>Ticket:</b> Generate your PDF ticket "
                                + "after successful payment or from Booking History."
                                + "</html>");

        helpText.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13));

        helpText.setForeground(TEXT_DARK);

        JPanel helpContent = new JPanel();

        helpContent.setLayout(
                new BoxLayout(
                        helpContent,
                        BoxLayout.Y_AXIS));

        helpContent.setOpaque(false);

        helpContent.add(helpTitle);
        helpContent.add(
                Box.createVerticalStrut(10));
        helpContent.add(helpText);

        helpCard.add(
                helpContent,
                BorderLayout.CENTER);

        contentPanel.add(helpCard);

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER);

        // ---------- FOOTER ----------

        JPanel footerPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        0,
                        0));

        footerPanel.setBackground(BG_LIGHT);

        footerPanel.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        18,
                        0));

        JButton closeButton =
                new JButton("Close");

        closeButton.setFont(BUTTON_FONT);
        closeButton.setBackground(NAVY);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR));

        closeButton.setPreferredSize(
                new Dimension(110, 36));

        closeButton.addActionListener(
                e -> dispose());

        footerPanel.add(closeButton);

        mainPanel.add(
                footerPanel,
                BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createContactCard(
            String title,
            String details,
            String description) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        card.setBackground(CARD_BG);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER),
                        new EmptyBorder(
                                16,
                                16,
                                15,
                                16)));

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(NAVY);

        JLabel detailsLabel =
                new JLabel(
                        "<html>"
                                + details.replace(
                                        "\n",
                                        "<br>")
                                + "</html>");

        detailsLabel.setFont(CARD_TEXT_FONT);
        detailsLabel.setForeground(TEXT_DARK);

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(CARD_SMALL_FONT);
        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);

        card.add(
                Box.createVerticalStrut(9));

        card.add(detailsLabel);

        card.add(
                Box.createVerticalStrut(9));

        card.add(descriptionLabel);

        return card;
    }
}