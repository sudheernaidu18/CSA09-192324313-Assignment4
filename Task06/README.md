# Task 6: Design Challenge – Campus Event Registration Desktop Application

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 13 Marks | **Bloom's Level**: K5 – Evaluate  

---

## 1. Stage 1 – Interpret the Problem | 2 Marks

### 1.1 Stakeholders & Users
1. **Students**: Register for cultural, technical, and sports events by submitting their university register number, name, department, target event, and phone number.
2. **Cultural Committee Organizers**: View all participant registrations in an interactive table, filter registrations by event, and export participant rosters to CSV for logistics and attendance.

### 1.2 Screens & Components
- **Primary Window (`JFrame`)**:
  - `JMenuBar`: `File` (Export to CSV, Exit), `Help` (About).
  - Registration Form (`JPanel` with `GridLayout`): Fields for Register No, Student Name, Department, Event Selection, Contact Number, Submit & Clear buttons.
  - Interactive Table (`JTable` in `JScrollPane`): Displays live participant registrations with dynamic filtering.
  - Status Bar (`JLabel`): Color-coded feedback (Green for success, Red for errors).

### 1.3 Testable Requirements & Business Rules
1. **Mandatory Fields & Format**: Register number must be exactly 9 digits; phone number must be 10 digits starting with 6-9.
2. **Duplicate Prevention Rule**: A student **cannot register twice for the same event**. However, the same student **can** register for different events.
3. **Responsive UI**: Database operations (inserts, queries, filtering) must run off the Event Dispatch Thread via `SwingWorker` so the window never freezes.

---

## 2. Stage 2 – Screen and Event Flow | 2 Marks

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │  File   Help                                                                │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │ [Student Event Registration Form]                                           │
 │  Register No: [ 192324313 ]        Student Name: [ Korasika Sudheer ]       │
 │  Department:  [ CSE       ▼]       Event Name:   [ EV-101 (HackSIMATS)   ▼] │
 │  Phone:       [ 9876543210]        [ Submit Registration ]  [ Clear ]       │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │ [Registered Participants Catalog]                                           │
 │  Filter by Event: [ All Events                 ▼]                           │
 │  ┌────────────┬──────────────────┬────────────┬─────────────────┬─────────┐ │
 │  │ Reg No     │ Student Name     │ Department │ Event Name      │ Phone   │ │
 │  ├────────────┼──────────────────┼────────────┼─────────────────┼─────────┤ │
 │  │ 192324313  │ Korasika Sudheer │ CSE        │ HackSIMATS 2026 │ 9876... │ │
 │  │ 192324314  │ Rohan Verma      │ IT         │ HackSIMATS 2026 │ 9876... │ │
 │  └────────────┴──────────────────┴────────────┴─────────────────┴─────────┘ │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │ Status: Registration Successful for Korasika Sudheer (192324313)            │
 └─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Stage 3 – Critical Design Decisions | 2 Marks

1. **Where Input Validation Occurs**:
   - Client-side validation in `EventRegistrationService` before any JDBC network connection is opened. Rejects malformed input in microseconds.
2. **Where Duplicate Checking Occurs**:
   - Enforced at both the Service layer and the Database schema layer using a composite unique key:
     `CONSTRAINT uk_student_event UNIQUE (reg_no, event_id)`.
   - Guarantees data integrity even under concurrent multi-user submissions.
3. **Asynchronous Non-Blocking Execution**:
   - Submissions and table queries execute inside `SwingWorker<T, V>`. `doInBackground()` runs on a background worker thread; `done()` runs on the EDT to update UI elements safely.

---

## 4. Stage 4 – Class Architecture & Handler Pseudocode | 4 Marks

### 4.1 3-Tier Layered Architecture Diagram

```
 ┌────────────────────────────────────────────────────────┐
 │ Presentation Layer: CampusEventRegistrationApp (Swing) │
 └───────────────────────────┬────────────────────────────┘
                             │ invokes via SwingWorker
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │ Service Layer: EventRegistrationService (Validation)   │
 └───────────────────────────┬────────────────────────────┘
                             │ calls
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │ Data Access Layer: EventRegistrationDao (JDBC)         │
 └───────────────────────────┬────────────────────────────┘
                             │ executes SQL
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │ Relational Database (MySQL / H2 InnoDB)                │
 └────────────────────────────────────────────────────────┘
```

### 4.2 Registration Action Pseudocode
```text
ALGORITHM HandleRegistration(formInputs)
BEGIN
    registration = new EventRegistration(regNo, name, dept, eventId, phone)
    DISABLE registerBtn
    statusLabel.setText("Submitting registration...")

    EXECUTE IN BACKGROUND (SwingWorker):
        TRY:
            service.register(registration)
            RETURN SUCCESS
        CATCH IllegalArgumentException iae:
            RETURN ERROR(iae.message)
        CATCH SQLException sqle:
            IF sqle IS UNIQUE_VIOLATION THEN
                RETURN ERROR("Duplicate registration detected for student " + regNo)
            ELSE
                RETURN ERROR("Database error: " + sqle.message)

    WHEN FINISHED ON EDT:
        ENABLE registerBtn
        IF SUCCESS THEN
            statusLabel.setText("Registration Successful!")
            clearForm()
            reloadTableAsync()
        ELSE
            statusLabel.setText(ERROR)
END
```

---

## 5. Stage 5 – Implementation Verification & Test Results | 3 Marks

| Test ID | Test Scenario Description | Expected Outcome | Actual System Response | Pass/Fail |
|---|---|---|---|---|
| **TC-01** | Invalid Reg No: `123` (< 9 digits) | Client validation exception | Rejected: *"Register Number must be exactly 9 digits"* | **PASSED** |
| **TC-02** | Invalid Phone: `12345` (< 10 digits) | Client validation exception | Rejected: *"Contact number must be a valid 10-digit number"* | **PASSED** |
| **TC-03** | Valid Registration: `192324313` for `EV-101` | Inserted into DB & table updated | Success: `true`, row added to database | **PASSED** |
| **TC-04** | Duplicate: `192324313` registering for `EV-101` again | SQL Unique constraint violation | Rejected: *"Unique index or primary key violation: uk_student_event"* | **PASSED** |
| **TC-05** | Different Event: `192324313` for `EV-102` | Permitted | Success: `true`, allowed multi-event participation | **PASSED** |
| **TC-06** | Export to CSV via Menu | File generated on disk | Exported CSV with full participant details | **PASSED** |

---

## 6. Alternative Concept and Complexity Analysis
- **Alternative**: Loading all 100,000 registrations at once into `DefaultTableModel` vs. Paginated cursor loading (`LIMIT 50 OFFSET ?`) via `SwingWorker`.
- **Loading Time & Latency**:
  - *All at once*: Fetching 100,000 records over network JDBC consumes ~4.5 seconds and causes an EDT freeze if run synchronously.
  - *Paginated (`LIMIT 50`)*: Loads in $< 15\text{ ms}$, ensuring instant screen responsiveness.
- **Memory Footprint**:
  - *All at once*: 100,000 rows $\times$ 5 strings $\approx 500,000$ String objects on the Java heap (~45 MB RAM).
  - *Paginated*: Fixed memory for 50 displayed rows (~25 KB RAM), independent of database size.
