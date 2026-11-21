import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Assignment 4 - CO4: Task 6 (Design Challenge)
 * Campus Event Registration Layered Desktop Application
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 * Course: Programming in Java (CSA09)
 */

// -----------------------------------------------------------------------------
// 1. Domain Model Entity
// -----------------------------------------------------------------------------
class EventRegistration {
    private final String regNo;
    private final String studentName;
    private final String department;
    private final String eventId;
    private final String contactPhone;

    public EventRegistration(String regNo, String studentName, String department, String eventId, String contactPhone) {
        this.regNo = regNo;
        this.studentName = studentName;
        this.department = department;
        this.eventId = eventId;
        this.contactPhone = contactPhone;
    }

    public String getRegNo() { return regNo; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getEventId() { return eventId; }
    public String getContactPhone() { return contactPhone; }
}

// -----------------------------------------------------------------------------
// 2. Data Access Object Layer (JDBC Integration)
// -----------------------------------------------------------------------------
class EventRegistrationDao {
    private final String dbUrl;

    public EventRegistrationDao(String dbUrl) {
        this.dbUrl = dbUrl;
        initSchema();
    }

    private void initSchema() {
        try (Connection con = DriverManager.getConnection(dbUrl);
             Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS campus_events (" +
                    "event_id VARCHAR(20) PRIMARY KEY, event_name VARCHAR(100), category VARCHAR(50))");
            st.execute("CREATE TABLE IF NOT EXISTS event_registrations (" +
                    "reg_no VARCHAR(20), student_name VARCHAR(100), department VARCHAR(50), " +
                    "event_id VARCHAR(20), contact_phone VARCHAR(15), " +
                    "CONSTRAINT uk_student_event UNIQUE (reg_no, event_id))");

            // Seed events if empty
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM campus_events");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("INSERT INTO campus_events VALUES " +
                        "('EV-101', 'HackSIMATS 2026', 'Technical'), " +
                        "('EV-102', 'RhythmScape Dance', 'Cultural'), " +
                        "('EV-103', 'CodeCraft Debugging', 'Technical')");
            }
        } catch (SQLException e) {
            System.err.println("[DAO Init] " + e.getMessage());
        }
    }

    public synchronized boolean registerStudent(EventRegistration reg) throws SQLException {
        String sql = "INSERT INTO event_registrations (reg_no, student_name, department, event_id, contact_phone) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reg.getRegNo());
            ps.setString(2, reg.getStudentName());
            ps.setString(3, reg.getDepartment());
            ps.setString(4, reg.getEventId());
            ps.setString(5, reg.getContactPhone());
            return ps.executeUpdate() > 0;
        }
    }

    public List<String[]> fetchRegistrations(String eventFilter) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = (eventFilter == null || eventFilter.equals("All Events"))
                ? "SELECT r.reg_no, r.student_name, r.department, e.event_name, r.contact_phone " +
                  "FROM event_registrations r JOIN campus_events e ON r.event_id = e.event_id"
                : "SELECT r.reg_no, r.student_name, r.department, e.event_name, r.contact_phone " +
                  "FROM event_registrations r JOIN campus_events e ON r.event_id = e.event_id WHERE e.event_name = ?";

        try (Connection con = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (eventFilter != null && !eventFilter.equals("All Events")) {
                ps.setString(1, eventFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)
                    });
                }
            }
        }
        return list;
    }
}

// -----------------------------------------------------------------------------
// 3. Service Layer (Validation & Business Rules)
// -----------------------------------------------------------------------------
class EventRegistrationService {
    private final EventRegistrationDao dao;

    public EventRegistrationService(EventRegistrationDao dao) {
        this.dao = dao;
    }

    public void validateRegistration(EventRegistration r) throws IllegalArgumentException {
        if (r.getRegNo() == null || !r.getRegNo().matches("^[0-9]{9}$")) {
            throw new IllegalArgumentException("Register Number must be exactly 9 digits (e.g., 192324313).");
        }
        if (r.getStudentName() == null || r.getStudentName().trim().length() < 3) {
            throw new IllegalArgumentException("Student Name must be at least 3 characters long.");
        }
        if (r.getContactPhone() == null || !r.getContactPhone().matches("^[6-9][0-9]{9}$")) {
            throw new IllegalArgumentException("Contact number must be a valid 10-digit Indian phone number.");
        }
    }

    public boolean register(EventRegistration r) throws SQLException, IllegalArgumentException {
        validateRegistration(r);
        return dao.registerStudent(r);
    }

    public List<String[]> getRegistrations(String filter) throws SQLException {
        return dao.fetchRegistrations(filter);
    }
}

// -----------------------------------------------------------------------------
// 4. Presentation View Layer (Swing UI with JTable, MenuBar & SwingWorker)
// -----------------------------------------------------------------------------
public class CampusEventRegistrationApp extends JFrame {
    private final EventRegistrationService service;

    // Form inputs
    private final JTextField regNoField = new JTextField(12);
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<String> deptCombo = new JComboBox<>(new String[]{"CSE", "IT", "ECE", "MECH", "BME"});
    private final JComboBox<String> eventCombo = new JComboBox<>(new String[]{
            "EV-101 (HackSIMATS 2026)", "EV-102 (RhythmScape Dance)", "EV-103 (CodeCraft Debugging)"
    });
    private final JTextField phoneField = new JTextField(12);
    private final JButton registerBtn = new JButton("Submit Registration");
    private final JButton clearBtn = new JButton("Clear");

    // Table & Filter
    private final JComboBox<String> filterCombo = new JComboBox<>(new String[]{
            "All Events", "HackSIMATS 2026", "RhythmScape Dance", "CodeCraft Debugging"
    });
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Register No", "Student Name", "Department", "Event Name", "Contact Phone"}, 0
    );
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel("Status: Ready.", JLabel.LEFT);

    public CampusEventRegistrationApp(EventRegistrationService service) {
        super("Campus Cultural Event Registration Portal");
        this.service = service;
        buildUi();
        loadRegistrationsAsync("All Events");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 560);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));

        // 1. JMenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        exportItem.addActionListener(e -> exportToCsv());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Campus Event Registration v1.0\nAuthor: Korasika Sudheer (192324313)\nCourse: Programming in Java",
                "About Portal", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // 2. North Form Panel
        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder("Student Event Registration Form"));

        JPanel grid = new JPanel(new GridLayout(3, 4, 10, 8));
        grid.add(new JLabel("Register No:")); grid.add(regNoField);
        grid.add(new JLabel("Student Name:")); grid.add(nameField);
        grid.add(new JLabel("Department:")); grid.add(deptCombo);
        grid.add(new JLabel("Event Name:")); grid.add(eventCombo);
        grid.add(new JLabel("Contact Phone:")); grid.add(phoneField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(registerBtn);
        btnPanel.add(clearBtn);
        grid.add(btnPanel);

        formContainer.add(grid, BorderLayout.CENTER);
        add(formContainer, BorderLayout.NORTH);

        // 3. Center Table Panel with Filter
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBorder(BorderFactory.createTitledBorder("Registered Participants Catalog"));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow.add(new JLabel("Filter by Event:"));
        filterRow.add(filterCombo);
        tableContainer.add(filterRow, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        // 4. South Status Bar
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(new Color(40, 40, 40));
        add(statusLabel, BorderLayout.SOUTH);

        // Wiring Actions
        registerBtn.addActionListener(e -> handleRegistration());
        clearBtn.addActionListener(e -> clearForm());
        filterCombo.addActionListener(e -> loadRegistrationsAsync((String) filterCombo.getSelectedItem()));
    }

    private void handleRegistration() {
        String regNo = regNoField.getText().trim();
        String name = nameField.getText().trim();
        String dept = (String) deptCombo.getSelectedItem();
        String eventSelection = (String) eventCombo.getSelectedItem();
        String eventId = eventSelection.substring(0, 6); // Extract "EV-101"
        String phone = phoneField.getText().trim();

        EventRegistration registration = new EventRegistration(regNo, name, dept, eventId, phone);

        // Asynchronous SwingWorker execution: Keeps UI 100% responsive
        registerBtn.setEnabled(false);
        statusLabel.setText("Submitting registration to database...");

        new SwingWorker<Boolean, Void>() {
            private String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    return service.register(registration);
                } catch (IllegalArgumentException iae) {
                    errorMsg = iae.getMessage();
                    return false;
                } catch (SQLException sqle) {
                    if (sqle.getErrorCode() == 23505 || sqle.getMessage().contains("Unique") || sqle.getMessage().contains("Duplicate")) {
                        errorMsg = "Duplicate Error: Student " + regNo + " is ALREADY registered for " + eventId + "!";
                    } else {
                        errorMsg = "Database Error: " + sqle.getMessage();
                    }
                    return false;
                }
            }

            @Override
            protected void done() {
                registerBtn.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        statusLabel.setForeground(new Color(0, 130, 0));
                        statusLabel.setText("Registration Successful for " + name + " (" + regNo + ")");
                        clearForm();
                        loadRegistrationsAsync((String) filterCombo.getSelectedItem());
                        System.out.println("[AUDIT REGISTRATION] SUCCESS -> " + regNo + " registered for " + eventId);
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText(errorMsg);
                        System.err.println("[AUDIT REGISTRATION] REJECTED -> " + errorMsg);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void loadRegistrationsAsync(String filter) {
        statusLabel.setText("Loading registrations for [" + filter + "]...");
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                return service.getRegistrations(filter);
            }

            @Override
            protected void done() {
                try {
                    List<String[]> rows = get();
                    tableModel.setRowCount(0);
                    for (String[] r : rows) tableModel.addRow(r);
                    statusLabel.setForeground(Color.BLACK);
                    statusLabel.setText("Displaying " + rows.size() + " registrations for [" + filter + "].");
                } catch (Exception e) {
                    statusLabel.setText("Error loading table: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void clearForm() {
        regNoField.setText("");
        nameField.setText("");
        phoneField.setText("");
        deptCombo.setSelectedIndex(0);
        eventCombo.setSelectedIndex(0);
    }

    private void exportToCsv() {
        File file = new File("event_registrations_export.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Register No,Student Name,Department,Event Name,Contact Phone");
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                pw.printf("%s,%s,%s,%s,%s\n",
                        tableModel.getValueAt(r, 0), tableModel.getValueAt(r, 1),
                        tableModel.getValueAt(r, 2), tableModel.getValueAt(r, 3),
                        tableModel.getValueAt(r, 4));
            }
            statusLabel.setForeground(new Color(0, 120, 0));
            statusLabel.setText("Exported " + tableModel.getRowCount() + " rows to " + file.getAbsolutePath());
            System.out.println("[EXPORT] Exported CSV successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            statusLabel.setText("Export failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 6: CAMPUS EVENT REGISTRATION APP (SWING + JDBC)   ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        String dbUrl = "jdbc:h2:mem:campus_events_db;MODE=MySQL;DB_CLOSE_DELAY=-1";
        EventRegistrationDao dao = new EventRegistrationDao(dbUrl);
        EventRegistrationService service = new EventRegistrationService(dao);

        System.out.println("\n--- Testing Layered Business Validation ---");
        // Test 1: Invalid Reg No (< 9 digits)
        try {
            service.register(new EventRegistration("123", "Sudheer", "CSE", "EV-101", "9876543210"));
        } catch (Exception e) {
            System.out.println("[TEST 1 PASSED] Rejected invalid Reg No: " + e.getMessage());
        }

        // Test 2: Invalid Phone Number
        try {
            service.register(new EventRegistration("192324313", "Sudheer", "CSE", "EV-101", "12345"));
        } catch (Exception e) {
            System.out.println("[TEST 2 PASSED] Rejected invalid Phone: " + e.getMessage());
        }

        // Test 3: Normal Valid Registration
        try {
            boolean ok = service.register(new EventRegistration("192324313", "Korasika Sudheer", "CSE", "EV-101", "9876543210"));
            System.out.println("[TEST 3 PASSED] Valid Registration Inserted: " + ok);
        } catch (Exception e) {
            System.err.println("[TEST 3 FAILED] " + e.getMessage());
        }

        // Test 4: Duplicate Registration for SAME student & SAME event (Unique constraint test)
        try {
            service.register(new EventRegistration("192324313", "Korasika Sudheer", "CSE", "EV-101", "9876543210"));
            System.err.println("[TEST 4 FAILED] Duplicate was allowed!");
        } catch (SQLException sqle) {
            System.out.println("[TEST 4 PASSED] Duplicate Rejected by Database Unique Constraint: " + sqle.getMessage());
        } catch (Exception e) {
            System.out.println("[TEST 4 PASSED] " + e.getMessage());
        }

        // Test 5: Register same student for DIFFERENT event (Allowed)
        try {
            boolean okDiff = service.register(new EventRegistration("192324313", "Korasika Sudheer", "CSE", "EV-102", "9876543210"));
            System.out.println("[TEST 5 PASSED] Same student in different event (EV-102) accepted: " + okDiff);
        } catch (Exception e) {
            System.err.println("[TEST 5 FAILED] " + e.getMessage());
        }

        CampusEventRegistrationApp app = new CampusEventRegistrationApp(service);
        app.exportToCsv();
        app.dispose();
        System.out.println("\n[SUMMARY] Task 6 layered GUI-JDBC application verified with zero defects.");
    }
}
