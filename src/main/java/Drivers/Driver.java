package Drivers;

import Data.Keys;

public class Driver {

    static TSheetSearch search = new TSheetSearch(Keys.TSHEETS_KEY);

    public static void main(String[] args) {
        GUIBuilder gui = new GUIBuilder(search);
    }


}
