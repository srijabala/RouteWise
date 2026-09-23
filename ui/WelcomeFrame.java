package ui;

import java.awt.*;
import javax.swing.*;

public class WelcomeFrame extends JFrame {

    private static final Color BACKGROUND = new Color(15, 23, 42);
    private static final Color CARD = new Color(30, 41, 59);
    private static final Color PRIMARY = new Color(59, 130, 246);
    private static final Color PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color TEXT = new Color(248, 250, 252);
    private static final Color MUTED = new Color(148, 163, 184);

    public WelcomeFrame() {

        setTitle("RouteWise");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(40, 55, 40, 55)
        );

        // ---------- LEFT SIDE ----------

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND);

        JLabel logoLabel = new JLabel("ROUTEWISE");
        logoLabel.setFont(
                new Font("SansSerif", Font.BOLD, 34)
        );
        logoLabel.setForeground(TEXT);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel taglineLabel = new JLabel(
                "Travel smarter. Book with confidence."
        );
        taglineLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 18)
        );
        taglineLabel.setForeground(MUTED);
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(
                "<html>Search routes, choose your seats,<br>" +
                "and manage your bus journeys with ease.</html>"
        );
        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(taglineLabel);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(descriptionLabel);

        // Simple route visual
        leftPanel.add(Box.createVerticalStrut(45));

        JPanel routePanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                0,
                0
        ));
        routePanel.setBackground(BACKGROUND);
        routePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel start = new JLabel("●");
        start.setFont(new Font("SansSerif", Font.BOLD, 22));
        start.setForeground(PRIMARY);

        JLabel line = new JLabel("  ─────────────  ");
        line.setFont(new Font("SansSerif", Font.BOLD, 18));
        line.setForeground(MUTED);

        JLabel destination = new JLabel("●");
        destination.setFont(new Font("SansSerif", Font.BOLD, 22));
        destination.setForeground(PRIMARY);

        routePanel.add(start);
        routePanel.add(line);
        routePanel.add(destination);

        leftPanel.add(routePanel);

        JLabel routeText = new JLabel(
                "Your journey starts here."
        );
        routeText.setFont(
                new Font("SansSerif", Font.ITALIC, 14)
        );
        routeText.setForeground(MUTED);
        routeText.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(routeText);

        // ---------- RIGHT SIDE CARD ----------

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(51, 65, 85)
                        ),
                        BorderFactory.createEmptyBorder(
                                35, 40, 35, 40
                        )
                )
        );

        JLabel welcomeLabel = new JLabel(
                "Welcome to RouteWise"
        );
        welcomeLabel.setFont(
                new Font("SansSerif", Font.BOLD, 24)
        );
        welcomeLabel.setForeground(TEXT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel accountLabel = new JLabel(
                "Get started with your journey"
        );
        accountLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        accountLabel.setForeground(MUTED);
        accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = createButton(
                "LOGIN",
                PRIMARY
        );

        JButton registerButton = createButton(
                "REGISTER",
                new Color(71, 85, 105)
        );

        loginButton.addActionListener(e -> {

            dispose();

            new LoginFrame();
        });

        registerButton.addActionListener(e -> {

             dispose();
              new RegisterFrame();
        });

        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(accountLabel);
        card.add(Box.createVerticalStrut(35));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(15));
        card.add(registerButton);

        // ---------- COMBINE ----------

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(card, BorderLayout.EAST);

        add(mainPanel);
    }

    private JButton createButton(
            String text,
            Color background
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(background);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setPreferredSize(
                new Dimension(220, 48)
        );

        button.setMaximumSize(
                new Dimension(220, 48)
        );

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }
}