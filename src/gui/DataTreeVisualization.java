package src.gui;

import src.datatree.TimeRange;
import src.datatree.YearMap;
import src.types.Method;
import src.types.Metric;
import src.types.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import static src.types.Metric.*;
import static src.types.Metric.HOUR;

public class DataTreeVisualization extends JFrame {
    public static final String PREF_METRICS = "LP_PLOTWINDOW_METRICS";
    Preferences pref = Preferences.userRoot();
    String sr_str[] ={"YEARS", "MONTHS", "DAYS","HOURS"};
    String tt_str[] = {  "Valid Ranges, e.g. : ALL 1900-2022  2000,2001,2002  2000"
            ,"Valid Ranges, e.g. : ALL 1-13  1,2,3,4 5 "
            ,"Valid Ranges, e.g. : ALL 1-32  1,2,3,4 5"
            ,"Valid Ranges, e.g. : ALL 0-24, 0,1,2,3 4"};

    void configure(JComboBox<String> select_range, JRangeField[] rangeFields, String pref_str, TimeRange tr) {

        String pref_metric = pref.get(pref_str, "MONTHS");
        select_range.setSelectedItem(pref_metric);
        select_range.setEditable(true);
        select_range.setToolTipText("Selecting a western timezone for measurements belonging to " +
                "african regions can result in 'duplicated entries' warnings, since some dates/time combis " +
                "do not exist in Europa due to clock change. However, don't bother with a 100% correct timezone.");
        for(Metric m : Metric.values()){
            if(m == Metric.SIZE) continue;
            int i = m.value();
            rangeFields[i] = new JRangeField(20,tr,m);
            rangeFields[i].setToolTipText(tt_str[i]);
            rangeFields[i].setName("range");
            //  rangeFields[i].setValue("ALL");
        }

    }

    JPanel getPlotStuff(Data pd, JButton plotButton, JButton visButton,JComboBox<String> select_range, JRangeField[] rangeFields){

        //configure panels
        JPanel panelHeader= new JPanel();
        JLabel title_l = new JLabel(String.valueOf(pd)+" - Define valid ranges:");
        title_l.setFont(title_l.getFont().deriveFont(Font.BOLD, 14f));
        panelHeader.add(title_l, BorderLayout.CENTER);

        JPanel panelThresh= new JPanel();
        panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.Y_AXIS));

        JPanel panelMiddle = new JPanel();

        panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
        panelMiddle.add(panelThresh);
        for (int i =0; i < rangeFields.length;++i){

            JPanel range_panel = new JPanel();
            range_panel.setLayout(new BoxLayout(range_panel, BoxLayout.X_AXIS));

            range_panel.add(Box.createRigidArea(new Dimension(20,0)));
            range_panel.add(new JLabel(String.format("%1$7s",Metric.values()[i])));
            range_panel.add(Box.createRigidArea(new Dimension(10,0)));
            range_panel.add(rangeFields[i]);
            range_panel.add(Box.createRigidArea(new Dimension(20,0)));
            panelThresh.add(range_panel);
        }

        JPanel panelTZ = new JPanel();
        //panelTZ.setLayout(new BoxLayout(panelTZ, BoxLayout.X_AXIS));
        panelTZ.add(new JLabel("Select time unit: "));
        //panelTZ.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
        panelTZ.add(select_range);
        panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
        panelMiddle.add(panelTZ);
        panelMiddle.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
        panelMiddle.add(new JSeparator());

        JPanel panelFooter= new JPanel();
        panelFooter.add(plotButton);
        panelFooter.add(visButton);

        //configure frame layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createRigidArea(new Dimension(0,5))); // a spacer
        mainPanel.add(panelHeader);
        mainPanel.add(new JSeparator());
        mainPanel.add(panelMiddle);
        mainPanel.add(Box.createRigidArea(new Dimension(0,10))); // a spacer
        mainPanel.add(panelFooter);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5))); // a spacer

        return mainPanel;

    }

    public DataTreeVisualization(ArrayList<Column> columns) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 1));
        setTitle("Set Data Limits");

        ImageIcon appIcon = IOManager.loadLGIcon("icon");
        if(appIcon != null) {
            setIconImage(appIcon.getImage());
        }


        JTabbedPane tabbedPane = new JTabbedPane();
        int key_events[] = {KeyEvent.VK_1,KeyEvent.VK_2,KeyEvent.VK_3,KeyEvent.VK_4,
                KeyEvent.VK_5,KeyEvent.VK_6,KeyEvent.VK_7,KeyEvent.VK_8,KeyEvent.VK_9 };
        for (int i = 0; i < columns.size(); ++i) {
            Column col = columns.get(i);
            Data data = col.get_data();
            Method method = col.get_method();
            YearMap dataTree = col.get_data_tree();
            TimeRange timeRange = new TimeRange(~0l);
            dataTree.add_years(timeRange);

            ImageIcon icon = IOManager.scale(IOManager.loadLGIcon(data.toString().toLowerCase()), 16,16);

            String pref_str = PREF_METRICS + "_" + data.toString().toUpperCase();
            JButton visTreeButton = new JButton("Show Data Tree");
            JButton plotButton = new JButton("Plot Data");
            JComboBox<String> select_range= new JComboBox<String>(sr_str);
            JRangeField[] rangeFields =new JRangeField[Metric.SIZE.value()];
            configure(select_range, rangeFields, pref_str, timeRange);

            visTreeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    PlotHelper.visualizeDataTree(dataTree, method,timeRange, "LG_DATA_TREE.csv");
                }
            });

            plotButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    pref.put(pref_str,String.valueOf(select_range.getSelectedItem()));
                    if(select_range.getSelectedItem().equals("YEARS"))
                        PlotHelper.plot_stats(dataTree, method, YEAR, "Yearly-Avg", data, timeRange);
                    else if(select_range.getSelectedItem().equals("MONTHS"))
                        PlotHelper.plot_stats(dataTree, method, MONTH, "Monthly-Avg", data, timeRange);
                    else if(select_range.getSelectedItem().equals("DAYS"))
                        PlotHelper.plot_stats(dataTree, method, DAY, "Daily-Avg", data, timeRange);
                    else if(select_range.getSelectedItem().equals("HOURS"))
                        PlotHelper.plot_stats(dataTree, method, HOUR, "Hourly-Avg", data, timeRange);
                }
            });

            JPanel panel = getPlotStuff(col.get_data(),plotButton, visTreeButton, select_range, rangeFields);
            tabbedPane.addTab(col.get_data().toString(), icon, panel, col.get_data().getDescription());
            tabbedPane.setMnemonicAt(0, key_events[i]);
        }

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        pack();
        setVisible(true);
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

}
