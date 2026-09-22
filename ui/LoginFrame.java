package ui;

import service.UserService;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private UserService userService;

    public LoginFrame() {

        userService = new UserService();

        setTitle("RouteWise - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("RouteWise Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        JButton backButton = new JButton("← Back");

 

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(backButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> handleLogin());
  backButton.addActionListener(e -> {
    dispose();
    new WelcomeFrame();
});  }
private void handleLogin() {

    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (email.isEmpty() || password.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please enter email and password."
        );

        return;
    }

    User user = userService.login(email, password);

    if (user != null) {

        JOptionPane.showMessageDialog(
                this,
                "Login successful!\nWelcome, " + user.getFullName()
        );

        dispose();

        if (user.getRole().equals("CUSTOMER")) {

            new CustomerDashboard(user);

        } else if (user.getRole().equals("ADMIN")) {

            new AdminDashboard(user);
        }

    } else {

        JOptionPane.showMessageDialog(
                this,
                "Invalid email or password."
        );
    }
}
}