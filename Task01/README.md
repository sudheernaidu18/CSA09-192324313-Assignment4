# Task 1: GUI Design Review – Diagnose and Repair a Poor Event-Handling Design

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 6 Marks | **Bloom's Level**: K4 – Analyse  

---

## 1. Task 1.1 – Diagnose the Design (Deficiencies Analysis) | 2 Marks

The original `FeeWindow` implementation exhibits severe architectural and event-handling defects:

| Defect Area | Code Violation | Consequence & User Experience Impact |
|---|---|---|
| **1. UI / Logic Coupling** | Database connection, SQL execution, and banking delay are coded directly inside `actionPerformed`. | Violates Separation of Concerns (SoC). Testing logic requires launching the GUI; impossible to reuse business rules. |
| **2. String Identity Comparison** | `e.getActionCommand() == "Pay"` uses reference equality (`==`) instead of value equality (`.equals("Pay")`). | In Java, strings returned by event dispatchers are not guaranteed to be interned. The check can silently evaluate to `false`, causing clicks to fail or trigger the `else` branch. |
| **3. Lack of Input Validation** | `Double.parseDouble(amount.getText())` without try-catch; `regNo.getText()` never checked for emptiness. | Empty or alphabetic input crashes the Event Dispatch Thread (EDT) with an uncaught `NumberFormatException`. |
| **4. Long-Running Work on the EDT** | `Thread.sleep(3000)` and database I/O run synchronously on the EDT. | **Window Freezes Completely**: The EDT cannot process window repaints, clicks, or OS events. The OS displays the "Application Not Responding" spinner/beachball. |
| **5. SQL Injection Vulnerability** | String concatenation: `"INSERT INTO fees VALUES('" + regNo.getText() + "'," + a + ")"`. | Security flaw (CWE-89). Malicious inputs (e.g. `' OR '1'='1`) can alter database state or inject arbitrary commands. |
| **6. User Feedback Deficit** | Prints `System.out.println("Paid")` to the console. | The user sitting at the GUI sees no on-screen confirmation, visual cue, or progress indicator. |
| **7. Dead UI Components** | The `receipt` button is instantiated and added, but has no branch in `actionPerformed`. | Clicking "Receipt" triggers the `else` branch, inadvertently wiping the user's input fields! |
| **8. Unclosed DB Resources** | `Connection` and `Statement` are never closed. | Leaks database socket connections and server cursor handles. |
| **9. Unclosable Window** | No `WindowListener` or `WindowAdapter` registered. | Clicking the window close button (`X`) does nothing; the application cannot be dismissed cleanly. |

---

## 2. Task 1.2 – Repair the Design (Layered Architecture) | 3 Marks

The system was redesigned into three cleanly separated tiers:
1. **View Layer (`FeeWindowRedesign`)**:
   - Only responsible for UI component layout (`BorderLayout`, `GridLayout`, `FlowLayout`), user feedback (`Label`), and event dispatching.
   - Separate lambda listener attached to each button (`payButton`, `clearButton`, `receiptButton`).
   - `WindowAdapter` disposes of the frame and releases background thread pools on close.
2. **Service Layer (`FeeService`)**:
   - Performs client-side validation (non-empty register number, positive double amount).
   - Offloads long-running banking confirmation and database operations to an `ExecutorService` background pool, keeping the GUI responsive.
   - Dispatches UI updates back to the EDT via `EventQueue.invokeLater()`.
3. **Data Access Layer (`FeeDao`)**:
   - Uses parameterized `PreparedStatement` to eliminate SQL injection.
   - Manages connections safely with `try-with-resources`.

---

## 3. Task 1.3 – Design Defence | 1 Mark
*Our redesign separates presentation from domain logic and persistence. By isolating validation and banking workflows into `FeeService` and database queries into `FeeDao`, business logic can be unit-tested without launching AWT components. Executing database operations on a background thread guarantees that the Event Dispatch Thread never freezes during network latency. Parameterized `PreparedStatement` prevents SQL injection attacks, and individual lambda listeners eliminate fragile string-command comparisons, drastically lowering maintenance overhead.*

---

## 4. Alternative Concept and Complexity Analysis
- **Alternative**: Using individual listener objects (or `javax.swing.Action`) per button vs. one central listener with `if-else` string matching.
- **Dispatch Time Complexity**:
  - *Central Listener with `if-else`*: Requires up to $k$ sequential string comparisons ($O(k)$ time) for $k$ buttons.
  - *Individual Listeners / Swing Action*: Direct invocation of the registered callback ($O(1)$ constant dispatch time).
- **Space Complexity**:
  - Requires allocating $k$ small listener objects on the heap ($O(k)$ space), which is negligible (~32 bytes per instance) and well worth the isolation and speed benefits.
