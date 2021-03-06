package Drivers;

import MicrosoftGraph.Authentication;
import MicrosoftGraph.Graph;
import MicrosoftGraph.Token;
import javax.swing.*;

public class Driver {

    static TSheetSearch search;
    public static JWindow window;

    public static void main(String[] args) {
        Graph.getMonthForInt(1);
        Driver driver = new Driver();
        try {
            driver.loadingScreen();
        } catch (NullPointerException e) {
            window.setVisible(false);
        }

        Token token = new Authentication().authorize();
        String key = new Authentication().authorizeTSheets(token);
        search = new TSheetSearch(key);
        try {
            GUIBuilder gui = new GUIBuilder(search, token.getToken());
        } catch (Exception e) {
            if (GUIBuilder.debug.isSelected()) {
                GUIBuilder.logMsg("There was an error! Below is the stack trace");
                GUIBuilder.logMsg(e.getMessage());
            }
        }
    }

    public void loadingScreen() throws NullPointerException {
        window = new JWindow();

        String pathToImage = "loading.gif";
        ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource(pathToImage));

        window.getContentPane().add(
                new JLabel("", image, SwingConstants.CENTER));
        window.setBounds(500, 150, 400, 300);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }





}
