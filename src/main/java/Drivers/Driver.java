package Drivers;

import MicrosoftGraph.Authentication;
import MicrosoftGraph.Token;
import javax.swing.*;

public class Driver {

    static TSheetSearch search;
    public static JWindow window;

    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.loadingScreen();

        Token token = new Authentication().authorize();
        String key = new Authentication().authorizeTSheets(token);
        search = new TSheetSearch(key);

        GUIBuilder gui = new GUIBuilder(search, token.getToken());
    }

    public void loadingScreen() {
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
