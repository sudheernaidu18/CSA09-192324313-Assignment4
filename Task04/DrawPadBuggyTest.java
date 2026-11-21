import java.awt.event.*;
public class DrawPadBuggyTest {
    public static void main(String[] args) {
        new MouseAdapter() {
            @Override
            public void mouseclicked(MouseEvent e) {}
        };
    }
}
