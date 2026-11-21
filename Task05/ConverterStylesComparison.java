import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Assignment 4 - CO4: Task 5
 * Four Event Handling Styles Comparison & Changing Requirements
 * 
 * Student Name: Korasika Sudheer
 * Register Number: 192324313
 */

public class ConverterStylesComparison {

    // Helper rate
    private static double rate(String curr) { return curr.equals("USD") ? 0.012 : 0.011; }

    // STYLE A: Frame Itself Implements ActionListener
    static class FrameStyleA extends JFrame implements ActionListener {
        final JTextField amount = new JTextField("1000", 8);
        final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
        final JButton btn = new JButton("Convert");

        FrameStyleA() {
            super("Style A: Frame Implements Listener");
            setLayout(new FlowLayout());
            add(amount); add(target); add(btn);
            btn.addActionListener(this);
            amount.addActionListener(this); // Added for Req 5.2
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            pack();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doConvert();
        }

        void doConvert() {
            try {
                double val = Double.parseDouble(amount.getText()) * rate((String) target.getSelectedItem());
                // Req 5.2: Dialog box output
                System.out.println("[Style A Dialog] Converted: " + val);
            } catch (Exception ex) {
                System.out.println("[Style A Dialog] Invalid amount");
            }
        }
    }

    // STYLE B: Named Inner Class
    static class FrameStyleB extends JFrame {
        final JTextField amount = new JTextField("2000", 8);
        final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
        final JButton btn = new JButton("Convert");

        class ConvertActionHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double val = Double.parseDouble(amount.getText()) * rate((String) target.getSelectedItem());
                    System.out.println("[Style B Dialog] Converted: " + val);
                } catch (Exception ex) {
                    System.out.println("[Style B Dialog] Invalid amount");
                }
            }
        }

        FrameStyleB() {
            super("Style B: Named Inner Class");
            setLayout(new FlowLayout());
            add(amount); add(target); add(btn);
            ConvertActionHandler handler = new ConvertActionHandler();
            btn.addActionListener(handler);
            amount.addActionListener(handler); // Added for Req 5.2
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            pack();
        }
    }

    // STYLE C: Anonymous Inner Class
    static class FrameStyleC extends JFrame {
        final JTextField amount = new JTextField("3000", 8);
        final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
        final JButton btn = new JButton("Convert");

        FrameStyleC() {
            super("Style C: Anonymous Inner Class");
            setLayout(new FlowLayout());
            add(amount); add(target); add(btn);

            ActionListener handler = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        double val = Double.parseDouble(amount.getText()) * rate((String) target.getSelectedItem());
                        System.out.println("[Style C Dialog] Converted: " + val);
                    } catch (Exception ex) {
                        System.out.println("[Style C Dialog] Invalid amount");
                    }
                }
            };
            btn.addActionListener(handler);
            amount.addActionListener(handler); // Added for Req 5.2

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            pack();
        }
    }

    // STYLE D: Lambda Expression
    static class FrameStyleD extends JFrame {
        final JTextField amount = new JTextField("4000", 8);
        final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
        final JButton btn = new JButton("Convert");

        FrameStyleD() {
            super("Style D: Lambda Expression");
            setLayout(new FlowLayout());
            add(amount); add(target); add(btn);

            // Reusable lambda listener
            ActionListener handler = e -> {
                try {
                    double val = Double.parseDouble(amount.getText()) * rate((String) target.getSelectedItem());
                    System.out.println("[Style D Dialog] Converted: " + val);
                } catch (Exception ex) {
                    System.out.println("[Style D Dialog] Invalid amount");
                }
            };
            btn.addActionListener(handler);
            amount.addActionListener(handler); // Added for Req 5.2

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { dispose(); }
            });
            pack();
        }
    }

    public static void main(String[] args) {
        System.out.println("===========================================================");
        System.out.println("  TASK 5: EVENT-HANDLING STYLES EVALUATION SUITE        ");
        System.out.println("  Student: Korasika Sudheer | Register No: 192324313        ");
        System.out.println("===========================================================");

        FrameStyleA a = new FrameStyleA();
        FrameStyleB b = new FrameStyleB();
        FrameStyleC c = new FrameStyleC();
        FrameStyleD d = new FrameStyleD();

        System.out.println("\n--- Triggering Style A (Frame Implements ActionListener) ---");
        a.actionPerformed(null);

        System.out.println("\n--- Triggering Style B (Named Inner Class) ---");
        b.new ConvertActionHandler().actionPerformed(null);

        System.out.println("\n--- Triggering Style C (Anonymous Inner Class) ---");
        c.btn.getActionListeners()[0].actionPerformed(null);

        System.out.println("\n--- Triggering Style D (Lambda Expression) ---");
        d.btn.getActionListeners()[0].actionPerformed(null);

        a.dispose(); b.dispose(); c.dispose(); d.dispose();
    }
}
