package Drivers;

import Data.Keys;
import MicrosoftGraph.Authentication;
import MicrosoftGraph.Token;

public class Driver {

    static TSheetSearch search = new TSheetSearch(Keys.TSHEETS_KEY);

    public static void main(String[] args) {
        Token token = new Authentication().authorize();

        GUIBuilder gui = new GUIBuilder(search, token.getToken());

    }





}
