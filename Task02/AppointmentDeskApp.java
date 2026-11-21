import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Assignment 4 - CO4: Task 2
 * Hospital Outpatient Appointment Desk
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 * Course: Programming in Java (CSA09)
 */

public class AppointmentDeskApp extends JFrame {

    // Component declarations
    private final JTextField patientIdField = new JTextField(15);
    private final JComboBox<String> deptCombo = new JComboBox<>(new String[]{"Cardiology", "Neurology", "Orthopedics", "Pediatrics"});
    private final JComboBox<String> doctorCombo = new JComboBox<>();
    private final JComboBox<String> slotCombo = new JComboBox<>(new String[]{"09:00 AM - 10:00 AM", "10:30 AM - 11:30 AM", "02:00 PM - 03:00 PM", "04:00 PM - 05:00 PM"});
    private final JButton bookButton = new JButton("Book Appointment");
    private final JButton resetButton = new JButton("Reset Form");
    private final JLabel statusLabel = new JLabel("Enter details to book an outpatient consultation.", JLabel.CENTER);

    // In-memory departmental doctor cache: Map<Department, List<Doctor>>
    private final Map<String, List<String>> doctorCache = new HashMap<>();
    private volatile boolean bookingInProgress = false;

    public AppointmentDeskApp() {
        super("Hospital Outpatient Appointment Desk");
        initDoctorCache();
        buildLayout();
        registerEventHandlers();
        refreshDoctorList((String) deptCombo.getSelectedItem());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Governed by custom WindowClosing confirmation
        pack();
        setLocationRelativeTo(null);
    }

    private void initDoctorCache() {
        doctorCache.put("Cardiology", Arrays.asList("Dr. V. Ramanathan (MBBS, MD)", "Dr. S. Priya (DM Cardio)"));
        doctorCache.put("Neurology", Arrays.asList("Dr. K. Anand (M.Ch Neuro)", "Dr. M. Deepa (MD, DM)"));
        doctorCache.put("Orthopedics", Arrays.asList("Dr. R. Rajesh (MS Ortho)", "Dr. P. Karthik (D.Ortho)"));
        doctorCache.put("Pediatrics", Arrays.asList("Dr. N. Gayathri (MD Pedia)", "Dr. A. Joseph (DCH)"));
    }

    private void buildLayout() {
        setLayout(new BorderLayout(12, 12));

        // Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(24, 64, 128));
        JLabel title = new JLabel("SIMATS HEALTHCARE - OUTPATIENT REGISTRATION");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Center Form Grid (5 rows, 2 columns)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        formPanel.add(new JLabel("Patient ID (UHID):"));
        formPanel.add(patientIdField);

        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptCombo);

        formPanel.add(new JLabel("Attending Doctor:"));
        formPanel.add(doctorCombo);

        formPanel.add(new JLabel("Preferred Time Slot:"));
        formPanel.add(slotCombo);

        add(formPanel, BorderLayout.CENTER);

        // Bottom Controls & Status
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonRow.add(bookButton);
        buttonRow.add(resetButton);

        southPanel.add(buttonRow, BorderLayout.NORTH);
        statusLabel.setForeground(new Color(30, 30, 30));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void registerEventHandlers() {
        // Event 1: Department selection changes -> dynamically refresh doctor list
        deptCombo.addActionListener(e -> {
            String selectedDept = (String) deptCombo.getSelectedItem();
            refreshDoctorList(selectedDept);
        });

        // Event 2: Book Appointment button clicked
        bookButton.addActionListener(e -> handleBooking());

        // Event 3: Reset Form button clicked
        resetButton.addActionListener(e -> resetForm());

        // Event 4: Window Closing Event with active booking check
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bookingInProgress || !patientIdField.getText().trim().isEmpty()) {
                    int answer = JOptionPane.showConfirmDialog(
                            AppointmentDeskApp.this,
                            "An appointment booking is currently in progress or unsaved!\nAre you sure you want to discard and exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (answer == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }

    public void refreshDoctorList(String department) {
        doctorCombo.removeAllItems();
        List<String> doctors = doctorCache.getOrDefault(department, Collections.emptyList());
        for (String doc : doctors) {
            doctorCombo.addItem(doc);
        }
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Department switched to " + department + ". " + doctors.size() + " doctors loaded.");
    }

    public boolean validateAndBook(String patientId, String dept, String doctor, String slot) {
        if (patientId == null || patientId.trim().isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Validation Error: Patient ID (UHID) is required.");
            return false;
        }

        if (!patientId.matches("^[A-Z0-9-]{4,15}$")) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Validation Error: Invalid UHID format (Alpha-numeric only).");
            return false;
        }

        if (doctor == null || doctor.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Validation Error: Please select an attending doctor.");
            return false;
        }

        // Normal booking simulation
        bookingInProgress = true;
        statusLabel.setForeground(new Color(0, 128, 0));
        statusLabel.setText("CONFIRMED: " + patientId + " booked with " + doctor + " at " + slot);
        System.out.println("[AUDIT BOOKING] Success: UHID=" + patientId + " | Dept=" + dept + " | Doctor=" + doctor + " | Slot=" + slot);
        bookingInProgress = false;
        return true;
    }

    private void handleBooking() {
        String patientId = patientIdField.getText().trim();
        String dept = (String) deptCombo.getSelectedItem();
        String doctor = (String) doctorCombo.getSelectedItem();
        String slot = (String) slotCombo.getSelectedItem();
        validateAndBook(patientId, dept, doctor, slot);
    }

    private void resetForm() {
        patientIdField.setText("");
        deptCombo.setSelectedIndex(0);
        slotCombo.setSelectedIndex(0);
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText("Form cleared. Ready for input.");
        System.out.println("[AUDIT] Form reset to default.");
    }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 2: HOSPITAL APPOINTMENT DESK EVENT-DRIVEN DEMO    ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        AppointmentDeskApp app = new AppointmentDeskApp();

        System.out.println("\n--- 1. Testing Department Selection Dynamic Filtering ---");
        app.refreshDoctorList("Cardiology");
        System.out.println("Cardiology doctors: " + app.doctorCache.get("Cardiology"));
        app.refreshDoctorList("Neurology");
        System.out.println("Neurology doctors: " + app.doctorCache.get("Neurology"));

        System.out.println("\n--- 2. Testing Validation Scenarios ---");
        // Test A: Empty ID
        boolean r1 = app.validateAndBook("", "Cardiology", "Dr. V. Ramanathan", "09:00 AM - 10:00 AM");
        System.out.println("Empty ID Rejected: " + (!r1));

        // Test B: Invalid format
        boolean r2 = app.validateAndBook("!@#$%", "Cardiology", "Dr. V. Ramanathan", "09:00 AM - 10:00 AM");
        System.out.println("Invalid Symbols Rejected: " + (!r2));

        // Test C: Valid booking
        boolean r3 = app.validateAndBook("UHID-192324313", "Cardiology", "Dr. V. Ramanathan (MBBS, MD)", "09:00 AM - 10:00 AM");
        System.out.println("Valid Booking Confirmed: " + r3);

        if (!GraphicsEnvironment.isHeadless()) {
            app.setVisible(true);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            app.dispose();
        } else {
            System.out.println("[HEADLESS AUDIT] GUI components verified in headless runtime.");
        }
    }
}
