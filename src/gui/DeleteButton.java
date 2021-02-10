package src.gui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class DeleteButton extends JButton {
    public DeleteButton(String msg) {
        super(msg);
        setContentAreaFilled(false);
        setFocusPainted(false); // used for demonstration
        setForeground(new ColorUIResource(226, 210, 210));
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(
                new Point(0, 0),
                new ColorUIResource(234, 94, 94),
                new Point(0, getHeight()),
                new ColorUIResource(114, 0, 0)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }
}
