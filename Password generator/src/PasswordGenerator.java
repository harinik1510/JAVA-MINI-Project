package kh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGeneratorGUI extends JFrame {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

    // GUI components
    private JTextField lengthField;
    private JCheckBox lowerBox, upperBox, digitsBox, symbolsBox;
    private JTextArea outputArea;

    public PasswordGeneratorGUI() {
        setTitle("Password Generator");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center the window
        setLayout(new BorderLayout(10, 10));

        // ==== Top Panel ====
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Password Length:"));
        lengthField = new JTextField("16");
        inputPanel.add(lengthField);

        lowerBox = new JCheckBox("Include Lowercase (a-z)", true);
        upperBox = new JCheckBox("Include Uppercase (A-Z)", true);
        digitsBox = new JCheckBox("Include Digits (0-9)", true);
        symbolsBox = new JCheckBox("Include Symbols (!@#...)", true);

        inputPanel.add(lowerBox);
        inputPanel.add(upperBox);
        inputPanel.add(digitsBox);
        inputPanel.add(symbolsBox);

        JButton generateBtn = new JButton("Generate Password");
        generateBtn.addActionListener(this::generatePasswordAction);

        inputPanel.add(new JLabel()); // Empty space
        inputPanel.add(generateBtn);

        add(inputPanel, BorderLayout.NORTH);

        // ==== Output Area ====
        outputArea = new JTextArea(5, 30);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Password"));
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void generatePasswordAction(ActionEvent e) {
        try {
            int length = Integer.parseInt(lengthField.getText().trim());
            boolean useLower = lowerBox.isSelected();
            boolean useUpper = upperBox.isSelected();
            boolean useDigits = digitsBox.isSelected();
            boolean useSymbols = symbolsBox.isSelected();

            String password = generatePassword(length, useLower, useUpper, useDigits, useSymbols);
            outputArea.setText(password);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for password length.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Core password generation logic
    public static String generatePassword(int length, boolean useLower, boolean useUpper,
                                          boolean useDigits, boolean useSymbols) {

        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be > 0");
        }

        StringBuilder allChars = new StringBuilder();
        List<String> requiredCategories = new ArrayList<>();

        if (useLower) {
            allChars.append(LOWER);
            requiredCategories.add(LOWER);
        }
        if (useUpper) {
            allChars.append(UPPER);
            requiredCategories.add(UPPER);
        }
        if (useDigits) {
            allChars.append(DIGITS);
            requiredCategories.add(DIGITS);
        }
        if (useSymbols) {
            allChars.append(SYMBOLS);
            requiredCategories.add(SYMBOLS);
        }

        if (allChars.length() == 0) {
            throw new IllegalArgumentException("At least one character category must be enabled.");
        }

        if (length < requiredCategories.size()) {
            throw new IllegalArgumentException(
                    "Length too short to include one character from each selected category. " +
                            "Minimum required length: " + requiredCategories.size()
            );
        }

        char[] password = new char[length];

        // Ensure one char from each category
        for (String category : requiredCategories) {
            int pos;
            do {
                pos = secureRandom.nextInt(length);
            } while (password[pos] != '\u0000');
            password[pos] = category.charAt(secureRandom.nextInt(category.length()));
        }

        // Fill remaining positions
        for (int i = 0; i < length; i++) {
            if (password[i] == '\u0000') {
                password[i] = allChars.charAt(secureRandom.nextInt(allChars.length()));
            }
        }

        return new String(password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordGeneratorGUI::new);
    }
}
