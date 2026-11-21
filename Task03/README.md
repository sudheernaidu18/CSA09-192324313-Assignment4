# Task 3: From Sketch to Layout – Campus Library Search Screen

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 9 Marks | **Bloom's Level**: K4 – Analyse  

---

## 1. Problem Context & Layout Requirements
The Campus Library search screen requires:
1. Title bar banner at the top.
2. Search row with keyword `JTextField`, category `JComboBox`, and `Search` button.
3. Central tabular results area displaying books, authors, categories, and availability status.
4. Bottom row with pagination buttons (`Prev`, `Next`), a `Reserve Book` action button, and a status message.
5. **Critical Resizing Requirement**: When the user expands or shrinks the window, the **results table must grow dynamically to fill all available space**, while the search bar and bottom button controls retain their fixed preferred heights.

---

## 2. Task 3.1 – Choose the Layout Managers | 3 Marks

| Container | Layout Manager Chosen | Justification with Respect to Resizing Requirement |
|---|---|---|
| **Root Frame (`JFrame`)** | `BorderLayout` | Fundamental container layout. `BorderLayout.NORTH` and `SOUTH` are granted their preferred heights, while `BorderLayout.CENTER` is allocated 100% of the remaining horizontal and vertical expansion space upon resizing. |
| **Top Container (`topContainer`)** | `BorderLayout` | Stacks the central title label in `NORTH` and the input control panel in `SOUTH`, maintaining compact vertical height. |
| **Search Row Panel (`searchRow`)** | `FlowLayout(CENTER)` | Keeps input controls (label, text field, combo box, search button) horizontally arranged at their natural preferred sizes without stretching text fields vertically. |
| **Results Area (`scrollPane`)** | Placed in `BorderLayout.CENTER` | `JScrollPane` automatically adjusts its viewport dimensions and displays horizontal/vertical scrollbars when table data exceeds viewport bounds. |
| **Bottom Container (`bottomContainer`)**| `BorderLayout` | Stacks the button bar in `NORTH` and the status feedback label in `SOUTH`. |
| **Pagination Button Row (`buttonRow`)** | `FlowLayout(CENTER)` | Keeps navigation buttons grouped cleanly in the center at consistent sizes. |

---

## 3. Task 3.2 – Nested Container Architecture Diagram | 2 Marks

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │ JFrame (Root Container: BorderLayout)                                       │
 │                                                                             │
 │  ┌───────────────────────────────────────────────────────────────────────┐  │
 │  │ topContainer (BorderLayout.NORTH)                                     │  │
 │  │  ├── Title Label: "CAMPUS CENTRAL LIBRARY CATALOG"                    │  │
 │  │  └── searchRow (FlowLayout): [Keywords] [TextField] [Category] [Search]│
 │  └───────────────────────────────────────────────────────────────────────┘  │
 │                                                                             │
 │  ┌───────────────────────────────────────────────────────────────────────┐  │
 │  │ scrollPane wrapping JTable (BorderLayout.CENTER)                      │  │
 │  │                                                                       │  │
 │  │  [Book ID]  |  [Title]             |  [Author]    |  [Status]         │  │
 │  │  CS-101     |  Core Java (25th Ed) |  Horstmann   |  Available        │  │
 │  │  CS-102     |  Effective Java      |  Bloch       |  Available        │  │
 │  │                                                                       │  │
 │  │  ◄────────────── (Expands Vertically & Horizontally) ──────────────►  │  │
 │  └───────────────────────────────────────────────────────────────────────┘  │
 │                                                                             │
 │  ┌───────────────────────────────────────────────────────────────────────┐  │
 │  │ bottomContainer (BorderLayout.SOUTH)                                  │  │
 │  │  ├── buttonRow (FlowLayout): [ ◀ Prev ]  [ Next ▶ ]  [ Reserve Book ] │  │
 │  │  └── statusLabel: "Status: Ready. Showing 5 records."                 │  │
 │  └───────────────────────────────────────────────────────────────────────┘  │
 └─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Task 3.4 – Test the Resizing Behaviour (Three Window Sizes) | 1 Mark

The screen layout was evaluated across three window sizes:
1. **Small Window (520 × 340)**:
   - Compact display; table rows compress to show top 4 records with vertical scrollbar activated.
   - Search row and pagination buttons remain fully visible at normal heights without clipping.
2. **Medium Window (750 × 480 - Default)**:
   - All 5 sample books display with optimal whitespace and clear table columns.
   - Status bar and buttons remain positioned at bottom.
3. **Large Window (1000 × 650)**:
   - The results table expands to occupy the expanded central canvas.
   - Search controls and action buttons maintain exact original heights and remain centered, fulfilling the resizing requirement.

---

## 5. Alternative Concept and Complexity Analysis
- **Alternative**: Using `GridBagLayout` or absolute positioning (`setLayout(null)` with manual `setBounds`).
- **Resizing Resilience**:
  - *Absolute Positioning (`null`)*: Extremely fragile. Resizing the window leaves components stranded at static $(x, y)$ coordinates; when shrunk, components are clipped and hidden.
  - *`GridBagLayout`*: Highly capable but requires configuring verbose `GridBagConstraints` (`weightx`, `weighty`, `gridx`, `gridy`) for every component, increasing code complexity and maintenance risk.
  - *Nested `BorderLayout` + `FlowLayout`*: Achieves the exact responsive behavior cleanly in $O(1)$ layout calculation passes with high readability.
