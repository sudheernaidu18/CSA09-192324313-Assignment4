# Task 5: Same Screen, Different Event-Handling Styles – Currency Converter

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 7 Marks | **Bloom's Level**: K5 – Evaluate  

---

## 1. Task 5.1 – Four Implementations Summary | 3 Marks

The Currency Converter action was implemented using four architectural paradigms:
1. **Style A (Frame Implements `ActionListener`)**:
   - `class FrameStyleA extends JFrame implements ActionListener`
   - Registered using `button.addActionListener(this)`.
   - Single `actionPerformed(e)` handles all events.
2. **Style B (Named Inner Class)**:
   - `class ConvertHandler implements ActionListener` defined inside the outer frame.
   - Encapsulates handler logic into an identifiable, reusable inner class.
3. **Style C (Anonymous Inner Class)**:
   - `button.addActionListener(new ActionListener() { ... })`.
   - Inlined at the point of component registration; no class name declared.
4. **Style D (Lambda Expression – Style used in `Converter.java`)**:
   - `button.addActionListener(e -> convert())`.
   - Functional interface SAM (Single Abstract Method) implementation introduced in Java 8.

All styles register a `WindowAdapter` (`addWindowListener(new WindowAdapter() { ... })`) to dispose of frame resources on close.

---

## 2. Task 5.2 – Requirement Change Impact Analysis | 2 Marks

### New Requirements:
1. Trigger conversion when pressing **Enter** in the amount text field.
2. Display the converted output in a dialog box (`JOptionPane.showMessageDialog`) instead of or in addition to the label.

### Code Delta Across the Four Styles:

| Event Style | Code Added for Enter Trigger (`amountField`) | Code Modified for Dialog Output | Evaluation of Modification Effort |
|---|---|---|---|
| **Style A (Frame implements)** | `amount.addActionListener(this);` | Updated `actionPerformed` to call `JOptionPane`. | 1 line added, 2 lines edited. Low effort, but muddies global `actionPerformed`. |
| **Style B (Named Inner)** | `ConvertHandler h = new ConvertHandler();`<br>`amount.addActionListener(h);` | Updated `ConvertHandler.actionPerformed` body. | 2 lines added, 2 lines edited. Clean encapsulation. |
| **Style C (Anonymous Inner)** | Cannot reuse anonymous instance without storing reference: `ActionListener a = new ...; amount.addActionListener(a);` | Updated inner class method. | 4 lines refactored. Clunky syntax. |
| **Style D (Lambda)** | `ActionListener l = e -> convert();`<br>`btn.addActionListener(l);`<br>`amount.addActionListener(l);` | Updated single helper method `convert()`. | **Minimal effort (1 line added, 1 method edited). Cleanest and most elegant.** |

---

## 3. Task 5.3 – Multi-Criteria Comparative Evaluation | 2 Marks

| Evaluation Criteria | Style A: Frame Implements | Style B: Named Inner Class | Style C: Anonymous Class | Style D: Lambda Expression |
|---|---|---|---|---|
| **Readability** | Poor (Action logic separated from component declaration) | Moderate (Class definition creates boilerplate) | Verbose (Nested boilerplate obscures layout) | **Excellent** (Concise, intuitive, 1 line) |
| **Access to Frame Fields** | Direct (`this.field`) | Direct (`Outer.this.field`) | Direct (Captures enclosing instance) | **Direct** (Lexical scope capture) |
| **Handler Reusability** | Low (Tied to one Frame instance) | **High** (Can be instantiated across buttons/menus) | Zero (Cannot be referenced elsewhere) | **High** (Assignable to `ActionListener` variable) |
| **Ease of Maintenance** | Low (Large `if-else` cascades grow unwieldy) | High (Isolated testable class) | Low (Duplicated across triggers) | **Highest** (Separates UI wiring from logic) |

### Large Application Architecture Recommendation:
- **Recommendation**: For enterprise applications, deploy **Style D (Lambdas / Method References) delegating to an Application Action / Service Layer**, or **`javax.swing.Action` (`AbstractAction`)**.
- **Justification**: A shared `Action` or lambda delegating to a Controller method allows a single piece of business logic to be bound seamlessly to a Toolbar Button, a Menu Item, and a Keyboard Accelerator (`KeyStroke`) with zero duplicated logic and minimal heap overhead.

---

## 4. Complete the Code Verification
In `Converter.java`:
```java
// TODO 1: Register ONE lambda listener for both the Convert button and the Enter key
ActionListener convertListener = e -> convert();
convertButton.addActionListener(convertListener);
amount.addActionListener(convertListener);

// TODO 2: Parse the amount and show the converted value with 2 decimals, or "Invalid amount"
private void convert() {
    try {
        double inr = Double.parseDouble(amount.getText().trim());
        if (inr < 0) {
            result.setText("Invalid amount");
            return;
        }
        String curr = (String) target.getSelectedItem();
        double val = inr * rate(curr);
        result.setText(String.format("%.2f %s", val, curr));
    } catch (NumberFormatException nfe) {
        result.setText("Invalid amount");
    }
}
```

### Verified Test Outputs:
- **Valid Conversion (INR 10,000 $\rightarrow$ USD)**: `120.00 USD`
- **Valid Conversion (INR 50,000 $\rightarrow$ EUR)**: `550.00 EUR`
- **Invalid Non-Numeric Input (`ABC_INVALID`)**: `Invalid amount`
- **Invalid Negative Value (`-500`)**: `Invalid amount`

---

## 5. Alternative Concept and Complexity Analysis
- **Alternative**: Method references (`this::convert`) or shared `javax.swing.Action` (`AbstractAction`).
- **Space & Memory Overhead**:
  - Anonymous classes generate dedicated synthetic `.class` files (e.g. `Converter$1.class`) taking ~1.5 KB disk/metadata space each.
  - Lambdas use `invokedynamic` (JSR-292) and `LambdaMetafactory`, generating CallSite linkages dynamically without bytecode file bloat.
  - A shared `Action` creates exactly **1 listener object** for $k$ triggers, reducing memory from $O(k)$ to $O(1)$.
