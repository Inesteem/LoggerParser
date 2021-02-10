package src.gui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class MainMenuButton  extends JButton {
    public MainMenuButton(String msg) {
        super(msg);
        setContentAreaFilled(false);
        setFocusPainted(false); // used for demonstration
        UIManager.put("Button.disabledText",new ColorUIResource(79,110,91));
        //UIManager.put("Button.disabledText",new ColorUIResource(78,97,86));
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(
                new Point(0, 0),
                new ColorUIResource(145,217,174),
                new Point(0, getHeight()),
                new ColorUIResource(0,128,51)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }
}
