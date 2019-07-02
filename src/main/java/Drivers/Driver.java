package Drivers;

import Data.Keys;
import MicrosoftGraph.Authentication;
import MicrosoftGraph.Token;

public class Driver {

    static TSheetSearch search;

    public static void main(String[] args) {
        Token token = new Authentication().authorize();
        String key = new Authentication().authorizeTSheets(token);

        search = new TSheetSearch(key);

        GUIBuilder gui = new GUIBuilder(search, token.getToken());
    }





}
