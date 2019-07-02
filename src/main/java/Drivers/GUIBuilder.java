package Drivers;

import Data.User;
import MicrosoftGraph.Graph;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
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
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("TShare");
        JPanel panel = new JPanel();
        JPanel progressPanel = new JPanel();
        JFrame employeeSelector = new JFrame("Select Employees");
        JPanel employeePanel = new JPanel();
        JPanel saveEmployees = new JPanel();
        JPanel checkOptions = new JPanel(new GridLayout(2, 1));

        JProgressBar progressBar = new JProgressBar();

        log = new JTextArea(8, 80);
        log.setEditable(false);
        log.setFont(log.getFont().deriveFont(12f));
        log.setForeground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton update = new JButton("Update Contracts");
        update.setToolTipText("Updates the contract reference list with new contracts from TSheets");
        JButton export = new JButton("Export Contracts");
        export.setToolTipText("Updates selected employees contracts");
        update.setPreferredSize(new Dimension(120, 23));
        export.setPreferredSize(new Dimension(120, 23));
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

        panel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        panel.add(update);
        panel.add(export);
        progressPanel.setBorder(new TitledBorder(new EtchedBorder(), "Progress"));
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(scroll);
        progressPanel.add(progressBar);

        employeePanel.setBorder(new TitledBorder(new EtchedBorder(), "Employees"));
        checkOptions.setBorder(new TitledBorder(new EtchedBorder(), "Quick Select"));
        saveEmployees.setBorder(new TitledBorder(new EtchedBorder(), "Options"));

        saveEmployees.add(dateLabel);
        saveEmployees.add(date);
        saveEmployees.add(beginExport);
        saveEmployees.add(cancelExport);

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
            boxes.add(new JCheckBox(u.getFirstName() + " " + u.getLastName()));
        }

        for(JCheckBox b : boxes) {
            employeePanel.add(b);
        }
        checkOptions.add(checkAll);
        checkOptions.add(uncheckAll);
        employeePanel.setLayout(new GridLayout(0, 6));
        employeeSelector.pack();

        update.addActionListener((ActionEvent e) -> {
            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                export.setEnabled(false);
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
                });
            });

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


