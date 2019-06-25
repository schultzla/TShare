package MicrosoftGraph;

import Drivers.TSheetSearch;
import java.io.IOException;
import java.util.Scanner;

public class PublicClient {

    protected static AuthenticationManager authenticationManager = null;
    public DebugLogger mLogger;
    public Scanner mScanner;

    public PublicClient() throws IOException {
        mLogger = DebugLogger.getInstance();
        mScanner = new Scanner(System.in, "UTF-8");
    }

    public void startConnect(TSheetSearch search) throws Exception {
        authenticationManager = AuthenticationManager.getInstance();
        authenticationManager.connect(mScanner, search);
    }

    public static AuthenticationManager getAuthenticationManager() throws IOException {

        try {
            return AuthenticationManager.getInstance();
        } finally {

        }
    }

    public String getUserInput(String prompt) throws Exception {
        System.out.println(prompt);
        final String code = mScanner.nextLine();
        return code;
    }
}
