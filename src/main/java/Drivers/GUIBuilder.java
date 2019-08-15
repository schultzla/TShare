package Drivers;

import Data.Record;
import Data.User;
import MicrosoftGraph.Graph;
import com.poiji.exception.InvalidExcelFileExtension;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GUIBuilder {

    private ArrayList<JCheckBox> employeeBoxes;
    public static JCheckBoxMenuItem debug;
    public static String effectiveDate;
    private TreeMap<String, User> users;
    public static JFrame frame;
    public static JTextArea log;
    private Graph graph;
    static String path;
    static String fileName;
    static int minLates = 0;
    static Analyzer analyzer;
    static BirthdayAnalyzer birthdayAnalyzer;
    static JCheckBox[] boxes;
    static HashSet<String> defaultCodes = new HashSet<>(), codes = new HashSet<>();

    protected GUIBuilder(TSheetSearch search, String token) {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Settings");

        debug = new JCheckBoxMenuItem("Debug Mode");

        menu.add(debug);
        menuBar.add(menu);

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

        defaultCodes = new HashSet<>(Arrays.asList(new String[]{"Vacation", "Holiday", "Sick", "Leave without Pay", "Ownership Vacation", "Jury Duty", "Bereavement"}));

        /*
         * Buttons
         */
        JButton calculateLateDays = new JButton("Calculate");
        JButton configure = new JButton("Configure");
        JButton setMinLates = new JButton("Set Min Lates");
        JButton addFilters = new JButton("Jobcode Filter");
        JButton saveFilters = new JButton("Save");
        JButton copyClip = new JButton("Copy to Clipboard");
        JButton upload = new JButton("Upload File");

        JPanel analyzerPnl = new JPanel();
        analyzerPnl.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        analyzerPnl.setLayout(new GridLayout());

        analyzerPnl.add(calculateLateDays);
        analyzerPnl.add(configure);
        analyzerPnl.add(copyClip);
        analyzerPnl.add(upload);

        calculateLateDays.setEnabled(false);
        configure.setEnabled(false);

        JFrame configureFrame = new JFrame("Config");
        JPanel analyzerConfig = new JPanel();
        analyzerConfig.setBorder(new TitledBorder(new EtchedBorder(), "Configurations"));

        configureFrame.add(analyzerConfig);
        configureFrame.setResizable(false);

        analyzerConfig.add(setMinLates);
        configureFrame.setLocationRelativeTo(frame);
        analyzerConfig.add(addFilters);

        configureFrame.pack();

        /*
        Build filters frame/panel
         */
        JPanel filterPanel = new JPanel();
        JFrame filters = new JFrame();
        filterPanel.setBorder(new TitledBorder(new EtchedBorder(), "Filters"));
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

        filters.setLocationRelativeTo(frame);



        filters.setResizable(false);
        filters.add(filterPanel);
        filters.pack();

        /*
         * Button actions
         */

        copyClip.addActionListener((ActionEvent e) -> {
            StringSelection selection = new StringSelection(log.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });

        setMinLates.addActionListener((ActionEvent e) -> {
            try {
                minLates = Integer.valueOf(JOptionPane.showInputDialog("Enter the minimum amount of late entries someone needs to be displayed"));
            } catch (Exception ex) {
                minLates = 0;
            }
        });

        configure.addActionListener((ActionEvent e) -> {
            configureFrame.setVisible(true);

        });

        addFilters.addActionListener((ActionEvent e) -> {
            codes.clear();
            filters.setVisible(true);

            saveFilters.addActionListener((ActionEvent ev) -> {
                /*
                 * Build exemption filters for jobcodes
                 */
                for (JCheckBox b : boxes) {
                    if (b.isSelected()) {
                        codes.add(b.getText());
                    }
                }

                filters.dispose();
            });

        });

        calculateLateDays.addActionListener((ActionEvent e) -> {
            log.setText("");
            analyzer.analyze(codes);

            log.append("File Name: " + fileName + "\n");
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date();
            log.append("Date Ran: " + df.format(date) + "\n");
            log.append("Minimum Late: " + minLates + "\n");
            log.append("==========\n");

            for (String s : analyzer.employeeLateCounts.keySet()) {
                if (analyzer.employeeLateCounts.get(s) >= minLates) {
                    Record temp = analyzer.getRecord(s);
                    String name = temp.firstName + " " + temp.lastName;

                    log.append("Employee: " + name + ", Late Entries: " + analyzer.employeeLateCounts.get(s));
                    log.append("\n");
                }
            }
        });


        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("TShare");
        frame.setIconImage(img);
        frame.setJMenuBar(menuBar);
        JPanel panel = new JPanel();
        JPanel progressPanel = new JPanel();
        JFrame employeeSelector = new JFrame("Select Employees");
        employeeSelector.setIconImage(img);
        JPanel employeePanel = new JPanel();
        JPanel saveEmployees = new JPanel();
        JPanel checkOptions = new JPanel(new GridLayout(2, 1));
        JPanel updateHoursOptions = new JPanel();
        JProgressBar progressBar = new JProgressBar();

        configureFrame.setIconImage(img);
        filters.setIconImage(img);

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

        panel.setLayout(new GridLayout(0, 3));
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        panel.add(update);
        panel.add(manual);
        panel.add(export);
        progressPanel.setBorder(new TitledBorder(new EtchedBorder(), "Progress"));
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(scroll);
        progressPanel.add(progressBar);

        employeePanel.setBorder(new TitledBorder(new EtchedBorder(), "Employees"));
        checkOptions.setBorder(new TitledBorder(new EtchedBorder(), "Quick Select"));
        saveEmployees.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        updateHoursOptions.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        updateHoursOptions.setLayout(new GridLayout(0, 4));
        JButton updateToCurrent = new JButton("Update from Selected to Current Month");

        saveEmployees.add(dateLabel);
        saveEmployees.add(date);
        saveEmployees.add(beginExport);
        saveEmployees.add(cancelExport);

        String[] mnth = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] yr = new String[6];
        yr[0] = String.valueOf(currentYear - 1);
        for (int i = 0; i < 5; i++) {
            yr[i + 1] = String.valueOf(currentYear + i);
        }
        JComboBox months = new JComboBox(mnth);
        JComboBox years = new JComboBox(yr);
        years.setSelectedIndex(1);
        JButton confirmUpdate = new JButton("Update");

        updateHoursOptions.add(months);
        updateHoursOptions.add(years);
        updateHoursOptions.add(confirmUpdate);
        updateHoursOptions.add(updateToCurrent);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Contract Exporter", panel);
        tabbedPane.addTab("Hours Updater", updateHoursOptions);
        tabbedPane.addTab("Late Entries Analyzer", analyzerPnl);

        JPanel birthdayCalcPnl = new JPanel();
        birthdayCalcPnl.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        birthdayCalcPnl.setLayout(new GridLayout());
        JButton birthdayUpload = new JButton("Upload File");
        JButton birthdayCopy = new JButton("Copy to Clipboard");
        JButton birthdayCalc = new JButton("Calculate");

        birthdayCalc.setEnabled(false);

        JComboBox birthdayMonths = new JComboBox(mnth);
        birthdayMonths.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));

        birthdayCalcPnl.add(birthdayMonths);
        birthdayCalcPnl.add(birthdayCalc);
        birthdayCalcPnl.add(birthdayCopy);
        birthdayCalcPnl.add(birthdayUpload);

        tabbedPane.addTab("Birthday/Anniversary Analyzer", birthdayCalcPnl);


        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(tabbedPane, BorderLayout.CENTER);
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


        employeeBoxes = new ArrayList<>();

        for (User u : users.values()) {
            employeeBoxes.add(new JCheckBox(u.getLastName() + ", " + u.getFirstName()));
        }

        for(JCheckBox b : employeeBoxes) {
            employeePanel.add(b);
        }
        checkOptions.add(checkAll);
        checkOptions.add(uncheckAll);
        employeePanel.setLayout(new GridLayout(0, 6));
        employeeSelector.pack();

        birthdayCalc.addActionListener((ActionEvent e) -> {
            log.setText("");
            birthdayAnalyzer.analyze(birthdayMonths.getSelectedIndex() + 1);
        });

        birthdayCopy.addActionListener((ActionEvent e) -> {
            StringSelection selection = new StringSelection(log.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });

        birthdayUpload.addActionListener((ActionEvent e) -> {
            if(fileOpener(calculateLateDays, configure, path != null, true)) {
                birthdayCalc.setEnabled(true);
            }
        });

        confirmUpdate.addActionListener((ActionEvent e) -> {
            log.setText("");
           StringBuilder month = new StringBuilder(String.valueOf(months.getSelectedIndex() + 1));
           if (month.length() == 1) {
               month.insert(0, "0");
           }
           String year = years.getSelectedItem().toString();


            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                confirmUpdate.setEnabled(false);
                months.setEnabled(false);
                years.setEnabled(false);
                export.setEnabled(false);
                updateToCurrent.setEnabled(false);
                manual.setEnabled(false);
                tabbedPane.setEnabled(false);

                graph.updateActualHours(month.toString(), year, months.getSelectedItem().toString(), false);

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    export.setEnabled(true);
                    manual.setEnabled(true);
                    confirmUpdate.setEnabled(true);
                    updateToCurrent.setEnabled(true);
                    months.setEnabled(true);
                    years.setEnabled(true);
                    tabbedPane.setEnabled(true);
                });
            });
        });

        updateToCurrent.addActionListener((ActionEvent e) -> {
            log.setText("");
            StringBuilder month = new StringBuilder(String.valueOf(months.getSelectedIndex() + 1));
            if (month.length() == 1) {
                month.insert(0, "0");
            }
            String year = years.getSelectedItem().toString();


            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                confirmUpdate.setEnabled(false);
                export.setEnabled(false);
                updateToCurrent.setEnabled(false);
                tabbedPane.setEnabled(false);
                manual.setEnabled(false);

                graph.updateActualHours(month.toString(), year, months.getSelectedItem().toString(), true);

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    confirmUpdate.setEnabled(true);
                    export.setEnabled(true);
                    updateToCurrent.setEnabled(true);
                    tabbedPane.setEnabled(true);
                    manual.setEnabled(true);
                });
            });
        });

        update.addActionListener((ActionEvent e) -> {
            log.setText("");
            progressBar.setIndeterminate(true);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                update.setEnabled(false);
                export.setEnabled(false);
                manual.setEnabled(false);
                tabbedPane.setEnabled(false);

                graph.updateContractReferences();

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    update.setEnabled(true);
                    export.setEnabled(true);
                    manual.setEnabled(true);
                    tabbedPane.setEnabled(true);

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

        upload.addActionListener((ActionEvent e) -> {
            if (fileOpener(calculateLateDays, configure, path != null, false)) {
                boxes = new JCheckBox[analyzer.getCodes().size()];
                String[] arrCodes = new String[analyzer.getCodes().size()];

                int j = 0;
                for (String s : analyzer.getCodes()) {
                    arrCodes[j] = s;
                    j++;
                }

                for(int i = 0; i < boxes.length; i++) {
                    boxes[i] = new JCheckBox(arrCodes[i]);
                }

                for(JCheckBox b : boxes) {
                    if (defaultCodes.contains(b.getText())) {
                        b.setSelected(true);
                    }
                    filterPanel.add(b);
                }

                filterPanel.add(saveFilters);
                filters.pack();
            }
        });

        beginExport.addActionListener((ActionEvent e) -> {
            log.setText("");
            effectiveDate = date.getText();
            ArrayList<User> updateUsers = new ArrayList<>();
            for (JCheckBox box : employeeBoxes) {
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
                tabbedPane.setEnabled(false);
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
                    tabbedPane.setEnabled(true);

                });
            });

        });
    }

    public static void logMsg(String message) {
        log.append(message);
        log.append("\n");

        if (message.contains("Exception")) {
            Highlighter highlighter = log.getHighlighter();
            Highlighter.HighlightPainter painter =
                    new DefaultHighlighter.DefaultHighlightPainter(Color.red);

            int p0 = log.getText().indexOf(message);
            int p1 = p0 + message.length();
            try {
                highlighter.addHighlight(p0, p1, painter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
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

    public static boolean fileOpener(JButton calculateLate, JButton config, boolean newFile, boolean birthday) {
        while (path == null || newFile) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx", "xlx");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileName = selectedFile.getName();
                path = selectedFile.getAbsolutePath();
                if (birthday) {
                    try {
                        birthdayAnalyzer = new BirthdayAnalyzer(path);
                    } catch (Exception e) {
                        infoBox("Error, file isn't properly formatted", "Error");
                        path = null;
                        return false;
                    }
                } else {
                    try {
                        analyzer = new Analyzer(path);
                        calculateLate.setEnabled(true);
                        config.setEnabled(true);
                    } catch (NullPointerException e) {
                        infoBox("Error, file isn't properly formatted", "Error");
                        path = null;
                        return false;
                    }
                }
                return true;
            } else if (returnValue == JFileChooser.CANCEL_OPTION) {
                break;
            }
        }

        return false;
    }

    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}


