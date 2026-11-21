# Task 2: From Requirement to Event-Driven Design – Hospital Outpatient Appointment Desk

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 8 Marks | **Bloom's Level**: K4 – Analyse  

---

## 1. Problem Context & Functional Scope
The hospital outpatient registration desk manages incoming patient appointments across medical departments. The receptionist:
1. Inputs a patient identifier (UHID).
2. Selects a clinical department from a dropdown, which **dynamically updates the dependent doctor list**.
3. Selects an attending specialist doctor and an available appointment time slot.
4. Clicks "Book Appointment" to validate input, prevent incomplete bookings, and show visual confirmation.
5. If the user attempts to close the window while form entry is in progress, the window prompts for confirmation before closing.

---

## 2. Task 2.1 – Model the Events Table | 2 Marks

| Event Source Component | Event Object Generated | Listener Interface | Triggering User Action | Handler Method & Action Taken |
|---|---|---|---|---|
| `deptCombo` (`JComboBox`) | `ActionEvent` / `ItemEvent` | `ActionListener` | Selecting a new department item | `refreshDoctorList(dept)`: Clears doctor combo and populates doctors for the selected department. |
| `bookButton` (`JButton`) | `ActionEvent` | `ActionListener` | Clicking "Book Appointment" | `validateAndBook()`: Checks UHID format, verifies doctor selection, records booking, and updates status label. |
| `resetButton` (`JButton`) | `ActionEvent` | `ActionListener` | Clicking "Reset Form" | `resetForm()`: Clears fields, resets combos, and restores default prompt. |
| `AppointmentDeskApp` (`JFrame`)| `WindowEvent` | `WindowListener` (`WindowAdapter`)| Clicking the title bar close box (`X`) | `windowClosing(e)`: Prompts confirmation dialog if form contains unsaved patient data; disposes frame on 'Yes'. |

---

## 3. Task 2.2 – Design the Component Hierarchy | 2 Marks

```
 JFrame (AppointmentDeskApp) - BorderLayout(12, 12)
   ├── NORTH: JPanel (headerPanel) - FlowLayout(CENTER)
   │     └── JLabel ("SIMATS HEALTHCARE - OUTPATIENT REGISTRATION")
   │
   ├── CENTER: JPanel (formPanel) - GridLayout(4, 2, 10, 10)
   │     ├── JLabel ("Patient ID (UHID):")
   │     ├── JTextField (patientIdField)
   │     ├── JLabel ("Department:")
   │     ├── JComboBox (deptCombo)
   │     ├── JLabel ("Attending Doctor:")
   │     ├── JComboBox (doctorCombo)
   │     ├── JLabel ("Preferred Time Slot:")
   │     └── JComboBox (slotCombo)
   │
   └── SOUTH: JPanel (southPanel) - BorderLayout(8, 8)
         ├── NORTH: JPanel (buttonRow) - FlowLayout(CENTER, 15, 8)
         │     ├── JButton (bookButton)
         │     └── JButton (resetButton)
         └── SOUTH: JLabel (statusLabel) - Feedback display
```

---

## 4. Task 2.3 – Handler Logic (Algorithmic Pseudocode) | 3 Marks

### (a) Refresh Doctor List on Department Change
```text
ALGORITHM RefreshDoctorList(selectedDepartment)
INPUT: selectedDepartment (String)
OUTPUT: Updates doctorCombo items
BEGIN
    doctorCombo.removeAllItems()
    doctorList = doctorCache.get(selectedDepartment)
    IF doctorList IS NOT NULL THEN
        FOR EACH doctor IN doctorList DO
            doctorCombo.addItem(doctor)
        END FOR
    END IF
    statusLabel.setText("Department switched to " + selectedDepartment)
END
```

### (b) Validate and Book Appointment
```text
ALGORITHM ValidateAndBook(patientId, department, doctor, slot)
INPUT: Form fields (patientId, department, doctor, slot)
OUTPUT: Boolean (True if booked, False otherwise)
BEGIN
    IF patientId IS EMPTY OR TRIM(patientId) == "" THEN
        statusLabel.setText("Validation Error: Patient ID is required.")
        RETURN FALSE
    END IF
    IF NOT MATCHES(patientId, "^[A-Z0-9-]{4,15}$") THEN
        statusLabel.setText("Validation Error: Invalid UHID format.")
        RETURN FALSE
    END IF
    IF doctor IS NULL OR doctor == "" THEN
        statusLabel.setText("Validation Error: Please select an attending doctor.")
        RETURN FALSE
    END IF
    
    SET bookingInProgress = TRUE
    // Persist to booking registry
    statusLabel.setText("CONFIRMED: " + patientId + " booked with " + doctor + " at " + slot)
    SET bookingInProgress = FALSE
    RETURN TRUE
END
```

---

## 5. Task 2.4 – Event Sequence Trace vs. Applet Lifecycle | 1 Mark

### Lifecycle Event Trace of the Desktop Window
1. **Instantiation**: `new AppointmentDeskApp()` calls constructors, sets layout managers, and builds component tree.
2. **Display**: `setVisible(true)` triggers peer creation, peer notification, and schedules the initial `paint()`/`update()` on the EDT.
3. **Active Loop**: The AWT EventQueue loops continuously dispatching `MouseEvents`, `KeyEvents`, and `ActionEvents`.
4. **Closing Event**: User clicks `X` $\rightarrow$ OS sends window message $\rightarrow$ AWT dispatches `windowClosing(e)` to `WindowAdapter`.
5. **Disposal**: `dispose()` releases native OS window peers and frame resources.

### Comparison with Applet Lifecycle Methods
- **`init()`**: Comparable to constructor and GUI build (`buildLayout()`); runs once to initialize components.
- **`start()`**: Comparable to `setVisible(true)`; activates background worker threads or animations.
- **`paint(g)`**: Directly equivalent to Swing's `paintComponent(g)` for rendering visuals.
- **`stop()`**: Suspends active background tasks when minimized or navigated away.
- **`destroy()`**: Directly equivalent to `dispose()`; deallocates native graphical peer handles.

---

## 6. Alternative Concept and Complexity Analysis
- **Alternative**: Fetching doctor lists directly from the database upon every dropdown selection change vs. caching in `Map<String, List<Doctor>>`.
- **Latency & Time Complexity**:
  - *In-Memory Cache*: Hash map lookup is $O(1)$ constant time ($\approx 0.05\text{ ms}$). Immediate UI response.
  - *Database Query*: Network round-trip, query parsing, and cursor fetch takes $O(m)$ query time plus 10–50 ms network I/O per dropdown change, causing perceptible UI stutter.
- **Space Complexity**:
  - For $d$ departments and $m$ doctors, the in-memory cache uses $O(d + m)$ references, occupying less than 15 KB of heap RAM, making in-memory caching vastly superior for static hospital master data.
