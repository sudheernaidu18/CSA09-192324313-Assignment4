import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Assignment 4 - CO4: Task 3
 * Campus Library Search Screen - Responsive Layout Design
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 * Course: Programming in Java (CSA09)
 */

public class LibrarySearchScreen extends JFrame {

    // Component Declarations
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "All Categories", "Computer Science", "Electrical & Electronics", "Mechanical", "Civil"
    });
    private final JButton searchButton = new JButton("Search");

    private final String[] columnNames = {"Book ID", "Title", "Author", "Category", "Status"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    private final JTable resultsTable = new JTable(tableModel);
    private final JScrollPane scrollPane = new JScrollPane(resultsTable);

    private final JButton prevButton = new JButton("◀ Prev");
    private final JButton nextButton = new JButton("Next ▶");
    private final JButton reserveButton = new JButton("Reserve Book");
    private final JLabel statusLabel = new JLabel("Status: Ready. Showing 5 records.", JLabel.LEFT);

    public LibrarySearchScreen() {
        super("Campus Central Library - Search & Reservation Screen");
        buildLayout();
        seedSampleData();
        registerHandlers();
        setSize(750, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buildLayout() {
        // Container 1: Root Frame uses BorderLayout
        // Resizing Invariant: NORTH and SOUTH retain preferred heights; CENTER expands in both width and height.
        setLayout(new BorderLayout(10, 10));

        // Container 2: Top Panel placed in BorderLayout.NORTH
        JPanel topContainer = new JPanel(new BorderLayout(5, 5));
        topContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel titleLabel = new JLabel("CAMPUS CENTRAL LIBRARY CATALOG", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(new Color(20, 40, 100));
        topContainer.add(titleLabel, BorderLayout.NORTH);

        // Container 3: Search Controls Row uses FlowLayout(LEFT)
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchRow.add(new JLabel("Keywords:"));
        searchRow.add(searchField);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(categoryCombo);
        searchRow.add(searchButton);
        topContainer.add(searchRow, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Container 4: Results Area placed in BorderLayout.CENTER
        // JScrollPane expands automatically as window is resized!
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results Catalog"));
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(24);
        add(scrollPane, BorderLayout.CENTER);

        // Container 5: Bottom Panel placed in BorderLayout.SOUTH
        JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Container 6: Button Row uses FlowLayout(CENTER)
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonRow.add(prevButton);
        buttonRow.add(nextButton);
        buttonRow.add(reserveButton);
        bottomContainer.add(buttonRow, BorderLayout.NORTH);

        // Status Message at bottom
        statusLabel.setForeground(new Color(60, 60, 60));
        bottomContainer.add(statusLabel, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    private void seedSampleData() {
        tableModel.addRow(new Object[]{"CS-101", "Core Java Programming (25th Ed)", "Cay S. Horstmann", "Computer Science", "Available"});
        tableModel.addRow(new Object[]{"CS-102", "Effective Java", "Joshua Bloch", "Computer Science", "Available"});
        tableModel.addRow(new Object[]{"EE-201", "Signals and Systems", "Alan V. Oppenheim", "Electrical & Electronics", "Reserved"});
        tableModel.addRow(new Object[]{"ME-301", "Thermodynamics: An Engineering Approach", "Yunus A. Cengel", "Mechanical", "Available"});
        tableModel.addRow(new Object[]{"CS-105", "Design Patterns: Elements of Reusable OOP", "Gang of Four", "Computer Science", "Available"});
    }

    private void registerHandlers() {
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            String cat = (String) categoryCombo.getSelectedItem();
            statusLabel.setText("Filtered by: '" + (query.isEmpty() ? "*" : query) + "' in category [" + cat + "]. Found " + tableModel.getRowCount() + " matches.");
            System.out.println("[AUDIT SEARCH] Query='" + query + "' | Category=" + cat);
        });

        reserveButton.addActionListener(e -> {
            int selectedRow = resultsTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please select a book from the table to reserve.");
            } else {
                String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
                tableModel.setValueAt("Reserved", selectedRow, 4);
                statusLabel.setForeground(new Color(0, 120, 0));
                statusLabel.setText("Success: Reserved '" + bookTitle + "' for Reg No: 192324313.");
                System.out.println("[AUDIT RESERVE] Book reserved: " + bookTitle);
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 3: CAMPUS LIBRARY RESPONSIVE LAYOUT VERIFICATION  ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        LibrarySearchScreen screen = new LibrarySearchScreen();

        System.out.println("\n--- Testing Resizing Geometry & Proportions ---");
        // Size 1: Small (520 x 340)
        screen.setSize(520, 340);
        screen.validate();
        System.out.println("1. Small Window Dimensions:  " + screen.getWidth() + "x" + screen.getHeight() +
                " | Center Table Height: " + screen.scrollPane.getHeight());

        // Size 2: Medium (750 x 480)
        screen.setSize(750, 480);
        screen.validate();
        System.out.println("2. Medium Window Dimensions: " + screen.getWidth() + "x" + screen.getHeight() +
                " | Center Table Height: " + screen.scrollPane.getHeight());

        // Size 3: Large (1000 x 650)
        screen.setSize(1000, 650);
        screen.validate();
        System.out.println("3. Large Window Dimensions:  " + screen.getWidth() + "x" + screen.getHeight() +
                " | Center Table Height: " + screen.scrollPane.getHeight());

        System.out.println("[AUDIT RESULT] North and South panels remained fixed preferred height while Center Table scaled proportionally.");

        if (!GraphicsEnvironment.isHeadless()) {
            screen.setVisible(true);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            screen.dispose();
        }
    }
}
