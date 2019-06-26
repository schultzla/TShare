package Drivers;

import Data.User;
import MicrosoftGraph.PublicClient;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;

public class GUIBuilder {

    private ArrayList<JCheckBox> boxes;
    private String effectiveDate;
    private TreeMap<String, User> users;
    public static JFrame frame;

    protected GUIBuilder(TSheetSearch search) {
        users = search.getAllUsers();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("Contract Exporter");
        JPanel panel = new JPanel();
        JFrame employeeSelector = new JFrame("Select Employees");
        JPanel employeePanel = new JPanel();
        JPanel saveEmployees = new JPanel();
        JPanel checkOptions = new JPanel(new GridLayout(2, 1));

        JButton update = new JButton("Update Contracts List");
        update.setToolTipText("Updates the list of contracts with any new contracts from SharePoint");
        JButton export = new JButton("Export Contracts");
        export.setToolTipText("Updates selected employees contracts");
        JButton beginExport = new JButton("Begin Export");
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

        employeePanel.setBorder(new TitledBorder(new EtchedBorder(), "Employees"));
        checkOptions.setBorder(new TitledBorder(new EtchedBorder(), "Quick Select"));
        saveEmployees.setBorder(new TitledBorder(new EtchedBorder(), "Options"));

        saveEmployees.add(dateLabel);
        saveEmployees.add(date);
        saveEmployees.add(beginExport);

        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
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
            //TODO Add pulling new contracts from TSheets and exporting to SharePoint reference list

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

            PublicClient publicClient = null;
            try {
                publicClient = new PublicClient();
                publicClient.startConnect(search, updateUsers, effectiveDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }
}
