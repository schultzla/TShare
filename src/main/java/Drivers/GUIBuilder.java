package Drivers;

import Data.User;
import MicrosoftGraph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GUIBuilder {

    private ArrayList<JCheckBox> boxes;
    public static String effectiveDate;
    private TreeMap<String, User> users;
    public static JFrame frame;
    public static JTextArea log;
    private Graph graph;

    protected GUIBuilder(TSheetSearch search, String token) {
        users = search.getAllUsers();
        graph = new Graph(token, search);

        Driver.window.setVisible(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.net.URL url = ClassLoader.getSystemResource("tsheets.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("TSheets Time Sheet Criteria Generator");
        frame.setIconImage(img);
        JPanel panel = new JPanel();
        JPanel progressPanel = new JPanel();
        JFrame employeeSelector = new JFrame("Select Employees");
        JFrame updateHoursFrame = new JFrame();
        employeeSelector.setIconImage(img);
        updateHoursFrame.setIconImage(img);
        JPanel employeePanel = new JPanel();
        JPanel saveEmployees = new JPanel();
        JPanel checkOptions = new JPanel(new GridLayout(2, 1));
        JPanel updateHoursOptions = new JPanel();
        JProgressBar progressBar = new JProgressBar();

        log = new JTextArea(15, 80);
        log.setEditable(false);
        log.setFont(log.getFont().deriveFont(12f));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton update = new JButton("Update TSheets Customers");
        update.setToolTipText("Updates the contract reference list with new contracts from TSheets");
        JButton export = new JButton("Update Customers Assigned to Employees");
        export.setToolTipText("Updates selected employees contracts");
        JButton manual = new JButton("Manually Update Customers Info");
        manual.setToolTipText("Opens the SharePoint list to update notes, PoP, etc.");
        JButton updateHours = new JButton("Update Annual Work Plan Hours");
        JButton beginExport = new JButton("Begin Export");
        JButton cancelExport = new JButton("Cancel");
        cancelExport.setPreferredSize(beginExport.getPreferredSize());
        JButton checkAll = new JButton("Check All");
        JButton uncheckAll = new JButton("Uncheck All");

        JTextField date = new JTextField("", 20);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        date.setText(sdf.format(today));
        JLabel dateLabel = new JLabel("Effective Date:");

        panel.setLayout(new GridLayout(0, 2));
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        panel.add(update);
        panel.add(manual);
        panel.add(export);
        panel.add(updateHours);
        progressPanel.setBorder(new TitledBorder(new EtchedBorder(), "Progress"));
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(scroll);
        progressPanel.add(progressBar);

        employeePanel.setBorder(new TitledBorder(new EtchedBorder(), "Employees"));
        checkOptions.setBorder(new TitledBorder(new EtchedBorder(), "Quick Select"));
        saveEmployees.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        updateHoursOptions.setBorder(new TitledBorder(new EtchedBorder(), "Options"));

        saveEmployees.add(dateLabel);
        saveEmployees.add(date);
        saveEmployees.add(beginExport);
        saveEmployees.add(cancelExport);

        String[] mnth = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};
        String[] yr = {"2019", "2020", "2021", "2022", "2023"};
        JComboBox months = new JComboBox(mnth);
        JComboBox years = new JComboBox(yr);
        JButton confirmUpdate = new JButton("Update");
        JCheckBox updateToCurrent = new JCheckBox("Update from Selected to Current Month");

        updateHoursOptions.add(months);
        updateHoursOptions.add(years);
        updateHoursOptions.add(confirmUpdate);
        updateHoursOptions.add(updateToCurrent);

        updateHoursFrame.setResizable(false);
        updateHoursFrame.setLayout(new BorderLayout());
        updateHoursFrame.add(updateHoursOptions);
        updateHoursFrame.setLocationRelativeTo(frame);
        updateHoursFrame.pack();

        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(progressPanel, BorderLayout.SOUTH);
        frame.pack();

        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        employeeSelector.setResizable(false);
        employeeSelector.setLayout(new BorderLayout());
        employeeSelector.add(employeePanel, BorderLayout.CENTER);
        employeeSelector.add(saveEmployees, BorderLayout.SOUTH);
        employeeSelector.add(checkOptions, BorderLayout.EAST);


        boxes = new ArrayList<>();

        for (User u : users.values()) {
            boxes.add(new JCheckBox(u.getLastName() + ", " + u.getFirstName()));
        }

        for(JCheckBox b : boxes) {
            employeePanel.add(b);
        }
        checkOptions.add(checkAll);
        checkOptions.add(uncheckAll);
        employeePanel.setLayout(new GridLayout(0, 6));
        employeeSelector.pack();

        updateHours.addActionListener((ActionEvent e) -> {
            updateHoursFrame.setVisible(true);
        });

        confirmUpdate.addActionListener((ActionEvent e) -> {
           StringBuilder month = new StringBuilder(String.valueOf(months.getSelectedIndex() + 1));
           if (month.length() == 1) {
               month.insert(0, "0");
           }
           String year = years.getSelectedItem().toString();

           updateHoursFrame.setVisible(false);

            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                export.setEnabled(false);
                updateHours.setEnabled(false);
                manual.setEnabled(false);
                try {
                    try {
                        graph.updateActualHours(month.toString(), year, months.getSelectedItem().toString(), updateToCurrent.isSelected());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    export.setEnabled(true);
                    manual.setEnabled(true);
                    updateHours.setEnabled(true);
                });
            });
        });

        update.addActionListener((ActionEvent e) -> {
            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                export.setEnabled(false);
                updateHours.setEnabled(false);
                manual.setEnabled(false);
                try {
                    try {
                        graph.updateContractReferences();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    export.setEnabled(true);
                    manual.setEnabled(true);
                    updateHours.setEnabled(true);
                });
            });

        });

        manual.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URL("https://diskenterprisesolutions.sharepoint.com/sites/CorpOp/ReqForm/Lists/ContractReference/AllItems.aspx").toURI());
            } catch (Exception ex) {}
        });

        cancelExport.addActionListener((ActionEvent e) -> {
            employeeSelector.setVisible(false);
        });

        export.addActionListener((ActionEvent e) -> {
            employeeSelector.setLocation(dim.width/2-employeeSelector.getSize().width/2, dim.height/2-employeeSelector.getSize().height/2);
            employeeSelector.setVisible(true);
        });

        checkAll.addActionListener((ActionEvent e) -> {
            for (Component comp : employeePanel.getComponents()) {
                JCheckBox b = (JCheckBox) comp;
                b.setSelected(true);
            }
        });

        uncheckAll.addActionListener((ActionEvent e) -> {
            for (Component comp : employeePanel.getComponents()) {
                JCheckBox b = (JCheckBox) comp;
                b.setSelected(false);
            }
        });

        beginExport.addActionListener((ActionEvent e) -> {
            effectiveDate = date.getText();
            ArrayList<User> updateUsers = new ArrayList<>();
            for (JCheckBox box : boxes) {
                if (box.isSelected()) {
                    updateUsers.add(users.get(box.getText()));
                }
            }
            employeeSelector.setVisible(false);

            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                export.setEnabled(false);
                manual.setEnabled(false);
                updateHours.setEnabled(false);
                try {
                    graph.clearList(updateUsers);
                    graph.exportRecords(updateUsers);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    export.setEnabled(true);
                    manual.setEnabled(true);
                    updateHours.setEnabled(true);
                });
            });

        });
    }

    public static void logMsg(String message) {
        log.append(message);
        log.append("\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    public static void notif(String message, TrayIcon.MessageType type){
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

            TrayIcon trayIcon = new TrayIcon(image, "TSheets Contract Exporter");
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            trayIcon.displayMessage("TShare", message, type);
        }
    }
}


