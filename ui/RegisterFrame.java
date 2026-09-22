package ui;

import java.awt.*;
import javax.swing.*;
import service.UserService;

public class RegisterFrame extends JFrame {

    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> genderComboBox;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JButton registerButton;
    private JButton backButton;

    private UserService userService;

    public RegisterFrame() {

        setTitle("RouteWise - Create Account");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
     userService = new UserService();  

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        );

        // ---------- TITLE ----------

        JLabel titleLabel = new JLabel("Create Your RouteWise Account");
        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 24)
        );
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ---------- FORM ----------

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBackground(new Color(30, 41, 59));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fullNameLabel = new JLabel("Full Name");
        JLabel emailLabel = new JLabel("Email");
        JLabel phoneLabel = new JLabel("Phone");
        JLabel genderLabel = new JLabel("Gender");
        JLabel passwordLabel = new JLabel("Password");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");

        JLabel[] labels = {
                fullNameLabel,
                emailLabel,
                phoneLabel,
                genderLabel,
                passwordLabel,
                confirmPasswordLabel
        };

        for (JLabel label : labels) {
            label.setForeground(new Color(226, 232, 240));
            label.setFont(
                    new Font("SansSerif", Font.PLAIN, 14)
            );
        }

        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);

        genderComboBox = new JComboBox<>(
                new String[]{"Select Gender", "MALE", "FEMALE", "OTHER"}
        );

        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(genderComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ---------- BUTTONS ----------

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        buttonPanel.setBackground(new Color(15, 23, 42));

        registerButton = new JButton("REGISTER");
        backButton = new JButton("← Back");

        registerButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        backButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(
                new Color(59, 130, 246)
        );

        backButton.setForeground(Color.WHITE);
        backButton.setBackground(
                new Color(71, 85, 105)
        );

        registerButton.setFocusPainted(false);
        backButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ---------- ACTIONS ----------

        registerButton.addActionListener(
                e -> handleRegistration()
        );

        backButton.addActionListener(e -> {
            dispose();
            new WelcomeFrame();
        });
    }

    private void handleRegistration() {

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();

       if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

    JOptionPane.showMessageDialog(
            this,
            "Please enter a valid email address."
    );

    return;
}

        String phone = phoneField.getText().trim();

        if (!phone.matches("\\d{10}")) {

    JOptionPane.showMessageDialog(
            this,
            "Phone number must contain exactly 10 digits."
    );

    return;
}

        String gender =
                (String) genderComboBox.getSelectedItem();

        String password =
                new String(passwordField.getPassword());

        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (fullName.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()
                || gender.equals("Select Gender")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
            );

            return;
        }
       
       boolean registered = userService.registerUser(
        fullName,
        email,
        password,
        phone,
        gender
);

if (registered) {

    JOptionPane.showMessageDialog(
            this,
            "Registration successful! You can now login."
    );

    dispose();
    new LoginFrame();

} else {

    JOptionPane.showMessageDialog(
            this,
            "Registration failed. The email may already be registered."
    );
}
    
    }
}