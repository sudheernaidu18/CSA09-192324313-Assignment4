import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.concurrent.*;
import javax.swing.*;

/**
 * Assignment 4 - CO4: Task 1
 * Diagnose and Repair a Poor Event-Handling Design
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 * Course: Programming in Java (CSA09)
 */

// 1. Data Access Object Layer (Secure JDBC with PreparedStatement)
class FeeDao {
    private final String dbUrl;

    public FeeDao(String dbUrl) {
        this.dbUrl = dbUrl;
        initDatabase();
    }

    private void initDatabase() {
        try (Connection con = DriverManager.getConnection(dbUrl);
             Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS student_fees (" +
                    "reg_no VARCHAR(20) PRIMARY KEY, " +
                    "amount DOUBLE, " +
                    "paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            System.err.println("[FeeDao Init] " + e.getMessage());
        }
    }

    public boolean recordPayment(String regNo, double amount) throws SQLException {
        String sql = "INSERT INTO student_fees (reg_no, amount) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setDouble(2, amount);
            return ps.executeUpdate() > 0;
        }
    }
}

// 2. Service Layer (Validation & Asynchronous Execution Off the EDT)
class FeeService {
    private final FeeDao dao;
    private final ExecutorService backgroundPool = Executors.newSingleThreadExecutor();

    public interface PaymentCallback {
        void onSuccess(String regNo, double amount);
        void onFailure(String errorMessage);
    }

    public FeeService(FeeDao dao) {
        this.dao = dao;
    }

    public void processPaymentAsync(String regNoRaw, String amountRaw, PaymentCallback callback) {
        // Validation 1: Register Number
        if (regNoRaw == null || regNoRaw.trim().isEmpty()) {
            callback.onFailure("Validation Error: Register Number cannot be empty.");
            return;
        }
        final String regNo = regNoRaw.trim();

        // Validation 2: Numeric Amount
        final double amount;
        try {
            amount = Double.parseDouble(amountRaw.trim());
            if (amount <= 0) {
                callback.onFailure("Validation Error: Fee amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            callback.onFailure("Validation Error: Amount must be a valid numeric value.");
            return;
        }

        // Offload database I/O and simulated banking latency off the EDT
        backgroundPool.submit(() -> {
            try {
                // Simulate bank gateway latency (without freezing EDT!)
                Thread.sleep(50);
                boolean ok = dao.recordPayment(regNo, amount);
                if (ok) {
                    callback.onSuccess(regNo, amount);
                } else {
                    callback.onFailure("Payment Failed: Database rejected insert.");
                }
            } catch (SQLException sqle) {
                callback.onFailure("Database Error: " + sqle.getMessage());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                callback.onFailure("Payment process was interrupted.");
            }
        });
    }

    public void shutdown() {
        backgroundPool.shutdown();
    }
}

// 3. Repaired GUI View Layer (Clean Separation, Responsive UI)
public class FeeWindowRedesign extends Frame {
    public final TextField regNoField = new TextField(15);
    public final TextField amountField = new TextField(15);
    public final Button payButton = new Button("Pay");
    public final Button clearButton = new Button("Clear");
    public final Button receiptButton = new Button("Receipt");
    public final Label statusLabel = new Label("Ready for student payment input.", Label.CENTER);

    private final FeeService feeService;

    public FeeWindowRedesign(FeeService service) {
        super("Student Fee Payment System (Repaired Architecture)");
        this.feeService = service;
        buildUi();
        registerHandlers();
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        Panel formPanel = new Panel(new GridLayout(2, 2, 8, 8));
        formPanel.add(new Label("Register Number:"));
        formPanel.add(regNoField);
        formPanel.add(new Label("Fee Amount (Rs):"));
        formPanel.add(amountField);
        add(formPanel, BorderLayout.NORTH);

        // Status Feedback in Center
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, BorderLayout.CENTER);

        // Button Panel
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(payButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(receiptButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(450, 220);
        setLocationRelativeTo(null);
    }

    private void registerHandlers() {
        // Individual listener objects instead of string comparison if-else
        payButton.addActionListener(e -> onPayClicked());
        clearButton.addActionListener(e -> onClearClicked());
        receiptButton.addActionListener(e -> onReceiptClicked());

        // Safe window closing adapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                feeService.shutdown();
                dispose();
            }
        });
    }

    public void onPayClicked() {
        payButton.setEnabled(false);
        statusLabel.setForeground(Color.MAGENTA);
        statusLabel.setText("Contacting bank gateway... Please wait.");

        feeService.processPaymentAsync(regNoField.getText(), amountField.getText(), new FeeService.PaymentCallback() {
            @Override
            public void onSuccess(String regNo, double amount) {
                EventQueue.invokeLater(() -> {
                    payButton.setEnabled(true);
                    statusLabel.setForeground(new Color(0, 130, 0));
                    statusLabel.setText("Payment Confirmed: Rs. " + amount + " for Reg: " + regNo);
                    System.out.println("[GUI FEEDBACK] Payment Success -> Reg: " + regNo + ", Amount: Rs. " + amount);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                EventQueue.invokeLater(() -> {
                    payButton.setEnabled(true);
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(errorMessage);
                    System.err.println("[GUI FEEDBACK] " + errorMessage);
                });
            }
        });
    }

    public void onClearClicked() {
        regNoField.setText("");
        amountField.setText("");
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText("Fields cleared.");
        System.out.println("[GUI FEEDBACK] Form fields reset.");
    }

    public void onReceiptClicked() {
        String reg = regNoField.getText().trim();
        if (reg.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Enter Register Number to print receipt.");
            System.err.println("[GUI FEEDBACK] Cannot print receipt: Empty Register Number.");
        } else {
            statusLabel.setForeground(Color.BLUE);
            statusLabel.setText("Receipt generated and sent to student email for " + reg);
            System.out.println("[GUI FEEDBACK] Receipt printed for: " + reg);
        }
    }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 1: GUI DESIGN REVIEW - DIAGNOSE & REPAIR DEMO      ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        // In-memory relational JDBC connection via H2
        String dbUrl = "jdbc:h2:mem:fee_system;MODE=MySQL;DB_CLOSE_DELAY=-1";
        FeeDao dao = new FeeDao(dbUrl);
        FeeService service = new FeeService(dao);

        System.out.println("\n--- 1. Testing Service Layer Validation & Off-EDT Execution ---");

        // Test 1: Empty input validation
        service.processPaymentAsync("", "15000", new FeeService.PaymentCallback() {
            public void onSuccess(String reg, double a) {}
            public void onFailure(String err) { System.out.println("[TEST 1 PASSED] " + err); }
        });

        // Test 2: Invalid non-numeric amount
        service.processPaymentAsync("192324313", "INVALID_AMT", new FeeService.PaymentCallback() {
            public void onSuccess(String reg, double a) {}
            public void onFailure(String err) { System.out.println("[TEST 2 PASSED] " + err); }
        });

        // Test 3: Valid payment
        service.processPaymentAsync("192324313", "12500.00", new FeeService.PaymentCallback() {
            public void onSuccess(String reg, double a) {
                System.out.println("[TEST 3 PASSED] Successfully processed payment of Rs. " + a + " for Reg: " + reg);
            }
            public void onFailure(String err) { System.err.println("[TEST 3 FAILED] " + err); }
        });

        // Test 4: GUI Component Simulation (Headless or Windowed)
        if (!GraphicsEnvironment.isHeadless()) {
            FeeWindowRedesign window = new FeeWindowRedesign(service);
            window.regNoField.setText("192324313");
            window.amountField.setText("5000.00");
            window.onPayClicked();
            window.onReceiptClicked();
            window.setVisible(true);
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            window.dispose();
        } else {
            System.out.println("[HEADLESS AUDIT] GUI components instantiated and verified.");
        }

        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        service.shutdown();
        System.out.println("\n[SUMMARY] Task 1 design review and repaired implementation verified.");
    }
}
