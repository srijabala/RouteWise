package ui;

import service.GeminiService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AITravelAssistantFrame extends JFrame {

    private final GeminiService geminiService;
    private final String routeWiseContext;

    private JTextField questionField;
    private JTextArea responseArea;
    private JButton askButton;

    public AITravelAssistantFrame() {
        this(null);
    }

    public AITravelAssistantFrame(String routeWiseContext) {
        this.geminiService = new GeminiService();
        this.routeWiseContext = routeWiseContext;

        setTitle("RouteWise AI Assistant");
        setSize(580, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        setVisible(true);
    }


    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("✨ RouteWise AI Assistant");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));

        JLabel subTitleLabel = new JLabel("Ask anything about your destination, route, food, or travel safety.");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitleLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subTitleLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Response Display (Center)
        responseArea = new JTextArea("Hello! 👋 I am your RouteWise AI Assistant.\nAsk me anything about your trip, rest stops, itinerary, or local attractions!");
        responseArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setEditable(false);
        responseArea.setBackground(Color.WHITE);
        responseArea.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(responseArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);

        questionField = new JTextField();
        questionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        questionField.setPreferredSize(new Dimension(0, 38));
        questionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        askButton = new JButton("Ask AI");
        askButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        askButton.setBackground(new Color(79, 70, 229));
        askButton.setForeground(Color.WHITE);
        askButton.setFocusPainted(false);
        askButton.setPreferredSize(new Dimension(85, 38));
        askButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputPanel.add(questionField, BorderLayout.CENTER);
        inputPanel.add(askButton, BorderLayout.EAST);

        // Quick suggestions panel
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        quickPanel.setOpaque(false);

        String[] suggestions = {"📍 Top Attractions", "🍱 Food Spots", "🛡️ Travel Safety"};
        for (String sug : suggestions) {
            JButton chip = new JButton(sug);
            chip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            chip.setBackground(new Color(241, 245, 249));
            chip.setForeground(new Color(51, 65, 85));
            chip.setFocusPainted(false);
            chip.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
            chip.addActionListener(e -> {
                questionField.setText("Tell me about " + sug.replaceAll("[^a-zA-Z ]", "").trim() + " for this route");
                askGemini();
            });
            quickPanel.add(chip);
        }

        JPanel southPanel = new JPanel(new BorderLayout(0, 8));
        southPanel.setOpaque(false);
        southPanel.add(quickPanel, BorderLayout.NORTH);
        southPanel.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(southPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Listeners
        askButton.addActionListener(e -> askGemini());
        questionField.addActionListener(e -> askGemini());
    }

    private void askGemini() {
        String question = questionField.getText().trim();

        if (question.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a question.",
                    "Question Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        askButton.setEnabled(false);
        questionField.setEnabled(false);
        responseArea.setText("🤖 Thinking... Please wait.");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return geminiService.askGemini(question, routeWiseContext);
            }

            @Override
            protected void done() {
                try {
                    responseArea.setText(get());
                } catch (Exception e) {
                    responseArea.setText("⚠️ Unable to get a response from Gemini.");
                } finally {
                    askButton.setEnabled(true);
                    questionField.setEnabled(true);
                    questionField.requestFocus();
                }
            }
        };

        worker.execute();
    }
}