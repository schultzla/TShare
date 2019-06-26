package MicrosoftGraph;

import Data.User;
import Drivers.TSheetSearch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PublicClient {

    private Scanner mScanner;

    public PublicClient() throws IOException {
        DebugLogger mLogger = DebugLogger.getInstance();
    }

    public void startConnect(TSheetSearch search, ArrayList<User> updatedUsers, String effectiveDate) throws Exception {
        AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
        authenticationManager.connect(search, updatedUsers, effectiveDate);
    }
}