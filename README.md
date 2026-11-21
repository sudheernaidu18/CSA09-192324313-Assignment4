# Programming in Java: Assignment 4 – CO4
## Event-Driven GUI Design, AWT/Swing Architecture & GUI-JDBC Integration

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Subject**: Programming in Java (Course Code: CSA09)  
**Outcome**: CO4 (Event Delegation Model, Layouts, Swing & JDBC, Sessions 49–56)  
**Total Marks**: 50 Marks  
**Java Version**: Java 25.0.1 LTS (`openjdk 25.0.1`)  
**Repository**: [https://github.com/sudheernaidu18/CSA09-192324313-Assignment4](https://github.com/sudheernaidu18/CSA09-192324313-Assignment4)

---

## 1. Project Directory Structure

```
CSA09-192324313-Assignment4/
├── Task01/                            # GUI Design Review - Diagnose & Repair (6 Marks)
│   ├── FeeWindowRedesign.java         # Repaired 3-tier architecture with off-EDT background worker
│   ├── README.md                      # Comprehensive defect diagnosis, defense & complexity analysis
│   └── output/
│       └── screenshot.png             # Execution and validation terminal screenshot
├── Task02/                            # From Requirement to Event-Driven Design (8 Marks)
│   ├── AppointmentDeskApp.java        # Hospital outpatient appointment desk, dynamic doctor combo
│   ├── README.md                      # Event model table, component hierarchy, pseudocode & trace
│   └── output/
│       └── screenshot.png             # Execution output screenshot
├── Task03/                            # From Sketch to Layout (9 Marks)
│   ├── LibrarySearchScreen.java       # Campus library catalog screen with responsive resizing
│   ├── README.md                      # Container nesting diagram, layout justifications, 3 window sizes
│   └── output/
│       └── screenshot.png             # Component resizing audit screenshot
├── Task04/                            # Event-Handling Forensics - DrawPad (7 Marks)
│   ├── DrawPad.java                   # Completed TODO 1 (mouseClicked) & TODO 2 (persistent paint)
│   ├── README.md                      # Defect diagnosis, minimize interaction bug, @Override compiler proof
│   └── output/
│       └── screenshot.png             # State persistence and click audit screenshot
├── Task05/                            # Same Screen, Different Event-Handling Styles (7 Marks)
│   ├── Converter.java                 # Completed TODO 1 & 2 (lambda currency converter)
│   ├── ConverterStylesComparison.java # 4 styles comparison, requirement change & dialog output
│   ├── README.md                      # 4-style evaluation matrix, code delta, method reference analysis
│   └── output/
│       └── screenshot.png             # Valid/invalid conversion and styles execution screenshot
├── Task06/                            # Design Challenge - Campus Event Registration App (13 Marks)
│   ├── CampusEventRegistrationApp.java# Full Swing + JDBC application with SwingWorker & JMenuBar
│   ├── schema.sql                     # Relational MySQL / H2 schema with unique participant constraints
│   ├── README.md                      # 5-stage engineering design, class diagram, duplicate testing
│   └── output/
│       └── screenshot.png             # Normal, invalid and duplicate test verification screenshot
├── lib/                               # Embedded Relational Database Drivers
│   └── h2.jar                         # Pure Java relational SQL engine (MySQL compatibility mode)
├── run_all.sh                         # Master test & execution script
└── README.md                          # Root assignment documentation & submission report
```

---

## 2. Compilation and Execution Instructions

### Prerequisites
- JDK 17, 21, or 25 installed
- Bash/Zsh shell

### Execute All Tasks at Once
```bash
git clone https://github.com/sudheernaidu18/CSA09-192324313-Assignment4.git
cd CSA09-192324313-Assignment4
chmod +x run_all.sh
./run_all.sh
```

### Compile & Run Individual Tasks
```bash
# Task 1: Fee Window Redesign
cd Task01 && javac -cp ".:../lib/h2.jar" FeeWindowRedesign.java && java -cp ".:../lib/h2.jar" FeeWindowRedesign

# Task 2: Hospital Appointment Desk
cd ../Task02 && javac AppointmentDeskApp.java && java AppointmentDeskApp

# Task 3: Campus Library Layout
cd ../Task03 && javac LibrarySearchScreen.java && java LibrarySearchScreen

# Task 4: DrawPad Event Forensics
cd ../Task04 && javac DrawPad.java && java DrawPad

# Task 5: Currency Converter Styles
cd ../Task05 && javac Converter.java ConverterStylesComparison.java && java Converter && java ConverterStylesComparison

# Task 6: Campus Event Registration Desktop App
cd ../Task06 && javac -cp ".:../lib/h2.jar" CampusEventRegistrationApp.java && java -cp ".:../lib/h2.jar" CampusEventRegistrationApp
```

---

## 3. Mark Distribution & Assessment Breakdown

| Task | Competency Assessed | Bloom's Level | Marks | Status |
|---|---|---|---|---|
| **Task 1** | Diagnose and repair a poor event-handling design | K4 – Analyse | 6 | **Verified & Documented** |
| **Task 2** | Design an event-driven interface from a requirement | K4 – Analyse | 8 | **Verified & Documented** |
| **Task 3** | Design and implement responsive nested layouts | K4 – Analyse | 9 | **Verified & Documented** |
| **Task 4** | Debug event handling using failing interactions (DrawPad) | K4 – Analyse | 7 | **Verified & Documented** |
| **Task 5** | Compare event-handling styles under changing requirements | K5 – Evaluate | 7 | **Verified & Documented** |
| **Task 6** | Independently design and build a layered GUI–JDBC application | K5 – Evaluate | 13 | **Verified & Documented** |
| **TOTAL** | | | **50 / 50** | **100% Complete** |
