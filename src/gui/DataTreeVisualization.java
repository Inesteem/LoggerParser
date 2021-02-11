package src.gui;

import src.datatree.TimeRange;
import src.datatree.DataTree;
import src.plotting.PlotHelper;
import src.types.Condition;
import src.types.Method;
import src.types.Metric;
import src.types.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import static src.types.Metric.*;
import static src.types.Metric.HOUR;

public class DataTreeVisualization extends Thread {
    private final Object closeWindow = new Object();
    boolean finished = false;
    Window window;
    class Window extends JFrame {
        ConditionRemove conditionRemove;
        class ConditionRemove extends JFrame {
            JFrame frame = this;
            JButton removeButton = new JButton("Remove");
            JButton cancelButton = new JButton("Cancel");
            String sr_str[] = {"years", "months", "days", "hours"};
            JComboBox<String> selectMetric = new JComboBox<String>(sr_str);
            JComboBox<String> selectCond = new JComboBox<String>(Condition.condNames);
            JDoubleField valField = new JDoubleField(10);
            public void close() {
                setVisible(false);
                dispose();
            }
            public ConditionRemove(DataTree dataTree, TimeRange timeRange, Point pos) {
                valField.setValue(0.0);
                removeButton.setBackground(Color.RED);
                removeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int opt = IOManager.askTwoOptions(frame ,"Remove selected entries", "Remove Data", "Cancel", "Removed Data can only restored by reparsing!");
                        if (opt == 0) {
                            String str = (String)selectMetric.getSelectedItem();
                            int numRemoved = dataTree.remove(timeRange,
                                    Metric.getEnum(str.substring(0, str.length()-1)),
                                    Condition.getEnum((String)selectCond.getSelectedItem()),
                                    valField.getValue());
                            dataTree.reset();
                            IOManager.asMessage(numRemoved + " entries removed.");
                        }
                        //close();

                    }
                });

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        close();
                    }
                });

                setIconImage(appIcon.getImage());
                setLocation(pos);
                //setLayout(new GridBagLayout());
                getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
                //c.anchor = GridBagConstraints.WEST;
                //setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                add(Box.createRigidArea(new Dimension(0,10))); // a spacer

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel.add(new JLabel("Remove all"));
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel.add(selectMetric);
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel.add(new JLabel(" that have "));
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel.add(selectCond);
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel.add(valField);
                JLabel label = new JLabel(" subUnits.");
                label.setToolTipText("For example, the subUnit of a year is >month<.");
                panel.add(label);
                panel.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                add(panel);
                add(Box.createRigidArea(new Dimension(0,10))); // a spacer

                JPanel panel2 = new JPanel();
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
                panel2.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel2.add(removeButton);
                panel2.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                panel2.add(cancelButton);
                panel2.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
                add(panel2);

                add(Box.createRigidArea(new Dimension(0,10))); // a spacer

                pack();
                setVisible(true);
            }
        }
        public void close() {
            setVisible(false);
            dispose();
            if(conditionRemove != null) conditionRemove.close();
        }


        ImageIcon appIcon = IOManager.loadLGIcon("icon.png");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        public static final String PREF_METRICS = "LP_PLOTWINDOW_METRICS";
        Preferences pref = Preferences.userRoot();
        String sr_str[] = {"YEAR", "MONTH", "DAY", "HOUR"};
        String tt_str[] = {"Valid Ranges, e.g. : ALL 1900-2022  2000,2001,2002  2000"
                , "Valid Ranges, e.g. : ALL 1-13  1,2,3,4 5 "
                , "Valid Ranges, e.g. : ALL 1-32  1,2,3,4 5"
                , "Valid Ranges, e.g. : ALL 0-24, 0,1,2,3 4"};


        void configure(JComboBox<String> select_range, JRangeField[] rangeFields, String pref_str, TimeRange tr) {

            String pref_metric = pref.get(pref_str, "MONTHS");
            select_range.setSelectedItem(pref_metric);
            select_range.setEditable(true);
            select_range.setToolTipText("Selecting a western timezone for measurements belonging to " +
                    "african regions can result in 'duplicated entries' warnings, since some dates/time combis " +
                    "do not exist in Europa due to clock change. However, don't bother with a 100% correct timezone.");
            for (Metric m : Metric.values()) {
                if (m == Metric.SIZE) continue;
                int i = m.value();
                rangeFields[i] = new JRangeField(20, tr, m);
                rangeFields[i].setToolTipText(tt_str[i]);
                rangeFields[i].setName("range");
                //  rangeFields[i].setValue("ALL");
            }

        }

        JPanel getPlotStuff(Data pd, JButton plotButton, JButton visButton, ColoredButton remButton, ColoredButton remCondButton, JComboBox<String> select_range, JRangeField[] rangeFields) {

            //configure panels
            JPanel panelHeader = new JPanel();
            JLabel title_l = new JLabel("Filter / Remove Data:");
            title_l.setFont(title_l.getFont().deriveFont(Font.BOLD, 14f));
            panelHeader.add(title_l, BorderLayout.CENTER);

            JPanel panelThresh = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            //c.gridwidth = 5;
            //panelThresh.setLayout(new BoxLayout(panelThresh, BoxLayout.Y_AXIS));

            JPanel panelMiddle = new JPanel();

            panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.Y_AXIS));
            panelMiddle.add(panelThresh);
            for (int i = 0; i < rangeFields.length; ++i) {
                c.gridx = 1;
                panelThresh.add(Box.createRigidArea(new Dimension(20, 0)),c);
                c.gridx = 2;
                panelThresh.add(new JLabel(String.format("%1s", Metric.values()[i])+":", SwingConstants.LEFT), c);
                c.gridx = 3;
                panelThresh.add(Box.createRigidArea(new Dimension(10, 0)),c);
                c.gridx = 4;
                panelThresh.add(rangeFields[i],c);
                c.gridx = 5;
                panelThresh.add(Box.createRigidArea(new Dimension(20, 0)),c);
            }

            JPanel panelLeftFooter = new JPanel(new GridBagLayout());
            c = new GridBagConstraints();

            c.gridy = 1;
            panelLeftFooter.add(new JLabel("Time unit: "),c);
            panelLeftFooter.add(Box.createRigidArea(new Dimension(5,0)),c); // a spacer
            panelLeftFooter.add(select_range, c);

            c.gridy = 2;
            panelLeftFooter.add(Box.createRigidArea(new Dimension(0, 5)),c); // a spacer

            c.gridy = 3;
            panelLeftFooter.add(plotButton,c);
            panelLeftFooter.add(Box.createRigidArea(new Dimension(5,0)),c); // a spacer
            panelLeftFooter.add(visButton,c);


            JPanel panelRightFooter = new JPanel();
            panelRightFooter.setLayout(new BoxLayout(panelRightFooter, BoxLayout.Y_AXIS));
            panelRightFooter.add(remButton);
            panelRightFooter.add(Box.createRigidArea(new Dimension(0,5))); // a spacer
            panelRightFooter.add(remCondButton);

            JPanel panelFooter = new JPanel();
            panelFooter.setLayout(new BoxLayout(panelFooter, BoxLayout.X_AXIS));
            panelFooter.add(panelLeftFooter);
            panelFooter.add(Box.createRigidArea(new Dimension(5,0))); // a spacer
            panelFooter.add(new JSeparator(SwingConstants.VERTICAL));
            panelFooter.add(Box.createRigidArea(new Dimension(5, 0))); // a spacer
            panelFooter.add(panelRightFooter);

            //configure frame layout
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // a spacer
            mainPanel.add(panelHeader);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // a spacer
            mainPanel.add(new JSeparator());
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // a spacer
            mainPanel.add(panelMiddle);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // a spacer
            mainPanel.add(new JSeparator());
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // a spacer
            mainPanel.add(panelFooter);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 5))); // a spacer

            return mainPanel;

        }

        public Window(ArrayList<Column> columns) {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setLayout(new GridLayout(1, 1));
            setTitle("Set Data Limits");
            setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);

            if (appIcon != null) {
                setIconImage(appIcon.getImage());
            }

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    synchronized (closeWindow) {
                        finished = true;
                        closeWindow.notify();
                    }
                }
            });

            JFrame mainFrame = this;
            JTabbedPane tabbedPane = new JTabbedPane();
            int key_events[] = {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
                    KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9};
            for (int i = 0; i < columns.size(); ++i) {
                Column col = columns.get(i);
                Data data = col.getData();
                Method method = col.getMethod();
                DataTree dataTree = col.getDataTree();
                TimeRange timeRange = col.getTimeRange();
                dataTree.add_years(timeRange);

                ImageIcon icon = IOManager.scale(IOManager.loadLGIcon(data.icon), 16, 16);

                String pref_str = PREF_METRICS + "_" + col.data.name;
                JButton visTreeButton = new JButton("Show Data Tree");
                JButton plotButton = new JButton("Plot Data");
                ColoredButton remButton = ColoredButton.newDeleteButton("Remove");
                remButton.setToolTipText("Removes the Dates specified by the RangeFields completely from the Data Tree.");
                ColoredButton remCondButton = ColoredButton.newDeleteButton("Remove Cond...");
                remCondButton.setToolTipText("Removes the Dates specified by the RangeFields and matching a condition completely from the Data Tree.");

                JComboBox<String> select_range = new JComboBox<String>(sr_str);
                JRangeField[] rangeFields = new JRangeField[Metric.SIZE.value()];
                configure(select_range, rangeFields, pref_str, timeRange);

                remButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pref.put(pref_str, String.valueOf(select_range.getSelectedItem()));
                        int opt = IOManager.askTwoOptions(mainFrame ,"Remove selected entries", "Remove Data", "Cancel", "Removed Data can only restored by reparsing!");
                        if (opt == 0) {
                            int numRemoved = dataTree.remove(timeRange,null, Condition.ALL, 0);
                            dataTree.reset();
                            IOManager.asMessage(numRemoved + " entries removed.");
                        }
                    }
                });
                JFrame frame = this;
                remCondButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(conditionRemove != null) {
                            conditionRemove.close();
                        }
                        conditionRemove = new ConditionRemove(dataTree, timeRange, frame.getLocation());
                    }
                });

                visTreeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pref.put(pref_str, String.valueOf(select_range.getSelectedItem()));
                        PlotHelper.visualizeDataTree(dataTree, method, timeRange, Metric.getEnum((String)select_range.getSelectedItem()),data, "LG_DATA_TREE.csv");
                    }
                });

                plotButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pref.put(pref_str, String.valueOf(select_range.getSelectedItem()));
                        if (select_range.getSelectedItem().equals("YEAR"))
                            PlotHelper.plot_stats(dataTree, method, YEAR, "Yearly-Avg", data, timeRange);
                        else if (select_range.getSelectedItem().equals("MONTH"))
                            PlotHelper.plot_stats(dataTree, method, MONTH, "Monthly-Avg", data, timeRange);
                        else if (select_range.getSelectedItem().equals("DAY"))
                            PlotHelper.plot_stats(dataTree, method, DAY, "Daily-Avg", data, timeRange);
                        else if (select_range.getSelectedItem().equals("HOUR"))
                            PlotHelper.plot_stats(dataTree, method, HOUR, "Hourly-Avg", data, timeRange);
                    }
                });

                JPanel panel = getPlotStuff(col.getData(), plotButton, visTreeButton, remButton, remCondButton, select_range, rangeFields);
                tabbedPane.addTab(col.getData().toString(), icon, panel, col.getData().description);
                tabbedPane.setMnemonicAt(0, key_events[i]);
            }


            //Add the tabbed pane to this panel.
            add(tabbedPane);

            //The following line enables to use scrolling tabs.
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

            pack();
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

    public DataTreeVisualization(ArrayList<Column> columns) {
        window = new Window(columns);
    }


    @Override
    public void run() {
        window.setVisible(true);
        synchronized (closeWindow) {
            try {
                while(!finished) {
                    closeWindow.wait();
                    window.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                //TODO: what todo?
            }
        }
    }

    public void update(ArrayList<Column> columns) {
        Point tmp = window.getLocation();
        window.close();
        window = new Window(columns);
        window.setLocation(tmp);
        window.setVisible(true);
    }
}
