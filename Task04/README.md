# Task 4: Event-Handling Forensics – Debugging DrawPad

**Student Name**: Korasika Sudheer  
**Register Number**: 192324313  
**Course**: Programming in Java (CSA09) – CO4  
**Marks**: 7 Marks | **Bloom's Level**: K4 – Analyse  

---

## 1. Task 4.1 – Locate the Defects | 2 Marks

### Defect 1: Why the click is never handled
In the original code:
```java
public void mouseclicked(MouseEvent e) { ... }
```
- Java is strictly case-sensitive. The callback defined in `java.awt.event.MouseListener` and adapted by `java.awt.event.MouseAdapter` is named `mouseClicked(MouseEvent e)` with a capital `'C'`.
- The misspelled method `mouseclicked` was treated by the compiler as an arbitrary new helper method belonging to the anonymous class. It did **not** override `MouseAdapter.mouseClicked`.
- Consequently, whenever the operating system dispatched a mouse click, AWT invoked the default empty method `MouseAdapter.mouseClicked(e)`, and the developer's block was never executed.

### Defect 2: Why the click label never changes
- Inside the uncalled method, `count++` occurred, but `status.setText("Clicks: " + count)` was completely missing. Even if invoked, the GUI label component was never notified of the state update.

---

## 2. Task 4.2 – Construct a Failing Interaction (The Minimize Defect) | 2 Marks

Even after correcting the method spelling to `mouseClicked`, a critical architectural defect remains:
```java
Graphics g = getGraphics(); 
g.fillOval(e.getX(), e.getY(), 6, 6);
```
- **The Failing Interaction**:
  1. The user clicks 5 times on the canvas; 5 dots appear.
  2. The user minimizes the window to the taskbar, then restores it (or drags another window across it).
  3. **Result**: All 5 dots vanish completely, leaving a blank canvas!
- **Why this happens**:
  `getGraphics()` obtains a temporary, non-persistent hardware graphics context direct to the native video framebuffer. Whenever the OS minimizes or obscures a window, the framebuffer contents are discarded to conserve memory. Upon restoration, the OS requests a complete surface repaint by sending a `PaintEvent` to AWT, calling `paint(Graphics g)`. Because the coordinates were drawn imperatively on the fly without storing state, `paint()` redraws the default blank background, wiping every dot.

---

## 3. Task 4.3 & 4.4 – Repair Program & Prevent Recurrence | 3 Marks

### 3.1 Repair Strategy
1. **Model Persistence**: Store click coordinates in an in-memory list: `List<Point> points = new ArrayList<>()`.
2. **Event Callback**: In `mouseClicked`, add `new Point(e.getX(), e.getY())`, update `status.setText("Clicks: " + points.size())`, and request a system redraw via `repaint()`.
3. **Declarative Rendering**: Override `paint(Graphics g)` to iterate through `points` and draw each dot centered: `g.fillOval(p.x - 3, p.y - 3, 6, 6)`.

### 3.2 How `@Override` Prevents Recurrence
Annotating the method with `@Override` instructs the Java compiler to enforce supertype validation:
```java
@Override
public void mouseclicked(MouseEvent e) { ... }
```
**Compiler Output**:
```text
DrawPad.java:23: error: method does not override or implement a method from a supertype
            @Override
            ^
1 error
```
The typo is caught immediately at compile time, eliminating silent runtime bugs.

---

## 4. Complete the Code Implementation
In `DrawPad.java`:
```java
// TODO 1: Override mouseClicked correctly: store the point, update the label and request a repaint
@Override
public void mouseClicked(MouseEvent e) {
    points.add(new Point(e.getX(), e.getY()));
    status.setText("Clicks: " + points.size());
    repaint();
}

// TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
@Override
public void paint(Graphics g) {
    super.paint(g);
    g.setColor(new Color(220, 20, 60));
    for (Point p : points) {
        g.fillOval(p.x - 3, p.y - 3, 6, 6);
    }
}
```

---

## 5. Alternative Concept and Complexity Analysis
- **Alternative**: Off-screen `BufferedImage` (Double Buffering) vs. Point List Traversal.
- **Time Complexity per Repaint**:
  - *Point List*: $O(n)$ time to iterate and draw $n$ points. For $n > 50,000$, repainting stutters.
  - *`BufferedImage`*: Draws each dot onto an off-screen bitmap once upon click ($O(1)$). During `paint()`, a single `g.drawImage()` call copies the entire bitmap via DMA in **$O(1)$ constant time** regardless of how many dots exist.
- **Space Complexity**:
  - *Point List*: $O(n)$ heap memory ($24\text{ bytes} \times n$).
  - *`BufferedImage`*: Fixed space of $\text{width} \times \text{height} \times 4\text{ bytes}$ (e.g., $400 \times 300 \times 4 \approx 480\text{ KB}$) independent of click count.
