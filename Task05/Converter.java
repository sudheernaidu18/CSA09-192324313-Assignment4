import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Assignment 4 - CO4: Task 5 (Complete the Code - Lambda Style)
 * Same Screen, Different Event-Handling Styles: Currency Converter
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 * Course: Programming in Java (CSA09)
 */

public class Converter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target =
            new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter() {
        super("Currency Converter");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        // TODO 1: Register ONE lambda listener for both the Convert button and the
        // Enter key in the amount field
        ActionListener convertListener = e -> convert();
        convertButton.addActionListener(convertListener);
        amount.addActionListener(convertListener);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void convert() {
        // TODO 2: Parse the amount and show the converted value with 2 decimals, or
        // "Invalid amount" if parsing fails
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (inr < 0) {
                result.setText("Invalid amount");
                System.out.println("[CONVERT RESULT] Negative value -> Invalid amount");
                return;
            }
            String curr = (String) target.getSelectedItem();
            double val = inr * rate(curr);
            String formatted = String.format("%.2f %s", val, curr);
            result.setText(formatted);
            System.out.println("[CONVERT RESULT] Converted Rs. " + inr + " to " + formatted);
        } catch (NumberFormatException nfe) {
            result.setText("Invalid amount");
            System.out.println("[CONVERT RESULT] Malformed input -> Invalid amount");
        }
    }

    public JTextField getAmountField() { return amount; }
    public JComboBox<String> getTargetCombo() { return target; }
    public JLabel getResultLabel() { return result; }
    public void triggerConvert() { convert(); }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 5: CURRENCY CONVERTER (TODO 1 & 2 VERIFICATION)   ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        Converter c = new Converter();

        // Test 1: Valid Conversion (INR 10,000 to USD)
        System.out.println("\n--- Test 1: Valid Conversion ---");
        c.getAmountField().setText("10000");
        c.getTargetCombo().setSelectedItem("USD");
        c.triggerConvert();
        System.out.println("Displayed Label: " + c.getResultLabel().getText() + " (Expected: 120.00 USD)");

        // Test 2: Valid Conversion (INR 50,000 to EUR)
        System.out.println("\n--- Test 2: Valid Conversion EUR ---");
        c.getAmountField().setText("50000");
        c.getTargetCombo().setSelectedItem("EUR");
        c.triggerConvert();
        System.out.println("Displayed Label: " + c.getResultLabel().getText() + " (Expected: 550.00 EUR)");

        // Test 3: Invalid Non-Numeric Amount
        System.out.println("\n--- Test 3: Invalid Non-Numeric Input ---");
        c.getAmountField().setText("ABC_INVALID");
        c.triggerConvert();
        System.out.println("Displayed Label: " + c.getResultLabel().getText() + " (Expected: Invalid amount)");

        // Test 4: Invalid Negative Amount
        System.out.println("\n--- Test 4: Invalid Negative Amount ---");
        c.getAmountField().setText("-500");
        c.triggerConvert();
        System.out.println("Displayed Label: " + c.getResultLabel().getText() + " (Expected: Invalid amount)");

        c.dispose();
    }
}
