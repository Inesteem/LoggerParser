package src.gui;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JBackgroundPanel extends JPanel {
    ImageIcon background;
    JPanel bgPanel;
    public JBackgroundPanel(ImageIcon background, ColorUIResource bgColor){
        setLayout(new OverlayLayout(this));
        this.background = background;
        bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(bgColor);
        JLabel jLabel= new JLabel("", JLabel.CENTER);
        jLabel.setAlignmentX(0.5f);
        jLabel.setAlignmentY(0.5f);
        bgPanel.add(jLabel);
        bgPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                jLabel.setIcon(IOManager.scale(background,bgPanel.getWidth(),bgPanel.getHeight()));
            }
        });
        add(bgPanel);


    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
    public Component add(JComponent p) {
        remove(bgPanel);
        p.setOpaque(false);
        Component c = super.add(p);
        super.add(bgPanel);
        return c;
    }

    public static JBackgroundPanel newMainMenuPanel(){
        ImageIcon bgImg = IOManager.crop(IOManager.loadLGIcon("test.jpg"), 0,300,(int)(500*1.375),500);
        JBackgroundPanel ret =  new JBackgroundPanel(bgImg, new ColorUIResource(0,30,30));
        //ret.resize( new Dimension(220,160));
        return ret;
    }
}
