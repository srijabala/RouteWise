package ui;

import service.GeminiService;

import javax.swing.*;
import java.awt.*;

public class AITravelAssistantFrame extends JFrame {

    private final GeminiService geminiService;
    private final String routeWiseContext;

    private JTextArea questionArea;
    private JTextArea responseArea;
    private JButton askButton;

    public AITravelAssistantFrame() {
        this(null);
    }

    public AITravelAssistantFrame(String routeWiseContext) {

        geminiService = new GeminiService();
        this.routeWiseContext = routeWiseContext;

        setTitle("RouteWise AI Travel Assistant");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
createUI();
setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                20, 20, 20, 20
        ));

        JLabel titleLabel =
                new JLabel("🤖 RouteWise AI Travel Assistant");

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 22)
        );

        JLabel infoLabel =
                new JLabel(
                        "Ask about destinations, travel planning, or general safety."
                );

        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        questionArea = new JTextArea(4, 40);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        questionArea.setBorder(
                BorderFactory.createTitledBorder(
                        "Your Question"
                )
        );

        askButton = new JButton("Ask Gemini");

        JPanel questionPanel = new JPanel(new BorderLayout(10, 10));
        questionPanel.add(
                new JScrollPane(questionArea),
                BorderLayout.CENTER
        );
        questionPanel.add(
                askButton,
                BorderLayout.SOUTH
        );

        responseArea = new JTextArea();
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setEditable(false);

        responseArea.setBorder(
                BorderFactory.createTitledBorder(
                        "RouteWise AI Response"
                )
        );

        mainPanel.add(questionPanel, BorderLayout.CENTER);

        mainPanel.add(
                new JScrollPane(responseArea),
                BorderLayout.SOUTH
        );

        add(mainPanel);

        askButton.addActionListener(e -> askGemini());
    }

    private void askGemini() {

        String question = questionArea.getText().trim();

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
        responseArea.setText("Thinking...");

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        return geminiService.askGemini(
                                question,
                                routeWiseContext
                        );
                    }

                    @Override
                    protected void done() {

                        try {

                            responseArea.setText(get());

                        } catch (Exception e) {

                            responseArea.setText(
                                    "Unable to get a response from Gemini."
                            );

                            e.printStackTrace();

                        } finally {

                            askButton.setEnabled(true);
                        }
                    }
                };

        worker.execute();
    }
}