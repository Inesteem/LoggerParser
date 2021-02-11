package src.gui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;

public class ColoredButton extends JButton {
    ColorUIResource darkButtonColor;
    ColorUIResource lightButtonColor;
    public ColoredButton(String msg, ColorUIResource textColor, ColorUIResource dTextColor,
                         ColorUIResource darkButtonColor, ColorUIResource lightButtonColor) {
        super(msg);
        setContentAreaFilled(false);
        setFocusPainted(false); // used for demonstration
        //UIManager.put("Button.disabledText",disabledTextColor);
        setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return dTextColor;
            }
        });
        setForeground(textColor);
        this.lightButtonColor = lightButtonColor;
        this.darkButtonColor = darkButtonColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(
                new Point(0, 0),
                lightButtonColor,
                new Point(0, getHeight()),
                darkButtonColor));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }
    public static ColoredButton newDeleteButton(String msg) {
        return new ColoredButton(msg,
                new ColorUIResource(226, 210, 210),
                new ColorUIResource(100, 100, 100),
                new ColorUIResource(114, 0, 0),
                new ColorUIResource(234, 94, 94)
        );
    }
    public static ColoredButton newMenuButton(String msg) {
        ColoredButton b = new ColoredButton(msg,
                new ColorUIResource(3, 52, 6),
                new ColorUIResource(79,110,91),
                new ColorUIResource(0,128,51),
                new ColorUIResource(145,217,174)
        );
        Dimension dim = new Dimension(120,60);
        b.setMaximumSize(dim);
        b.setFont(new Font("Arial", Font.BOLD, 20));
        return b;
    }
}
