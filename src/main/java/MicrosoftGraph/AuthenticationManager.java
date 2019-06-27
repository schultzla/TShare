/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package MicrosoftGraph;

import Data.DetailedSharepointItem;
import Data.Fields;
import Data.SharepointItem;
import Data.User;
import Drivers.GUIBuilder;
import Drivers.TSheetSearch;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.graph.logger.LoggerLevel;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;

public class AuthenticationManager {

    private static AuthenticationManager INSTANCE;
    private OAuth20Service mOAuthService = null;
    private OAuth2AccessToken mAccessToken;
    private String effectiveDate;

    {
        if (Debug.DebugLevel == LoggerLevel.DEBUG) {
            DebugLogger.getInstance().writeLog(Level.INFO, "AuthenticationManager initialization block called");

            try (OAuth20Service service = new ServiceBuilder(Constants.CLIENT_ID)
                    .callback(Constants.REDIRECT_URL)
                    .scope(Constants.SCOPES)
                    .apiKey(Constants.CLIENT_ID)
                    .debugStream(System.out)
                    .debug()
                    .build(MicrosoftAzureAD20Api.instance())
            ) {
                mOAuthService = service;
            } catch (java.io.IOException | IllegalArgumentException ex) {
                try {
                    throw ex;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try (OAuth20Service service = new ServiceBuilder(Constants.CLIENT_ID)
                    .callback(Constants.REDIRECT_URL)
                    .scope(Constants.SCOPES)
                    .apiKey(Constants.CLIENT_ID)
                    .build(MicrosoftAzureAD20Api.instance())
            ) {
                mOAuthService = service;
            } catch (java.io.IOException | IllegalArgumentException ex) {
                try {
                    throw ex;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private AuthenticationManager() throws IOException {
        DebugLogger.getInstance().writeLog(Level.INFO, "AuthenticationManager constructor called");
    }

    public static synchronized AuthenticationManager getInstance() throws java.io.IOException {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationManager();
        }
        return INSTANCE;
    }

    public void connect(TSheetSearch search, ArrayList<User> updatedUsers, String effectiveDate) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
        this.effectiveDate = effectiveDate;
        String token = getAuthorizationCode();
        if (token == null) {
            return;
        } else {
            mAccessToken = mOAuthService.getAccessToken(token);
            GUIBuilder.logMsg("=== Beginning process ===");
            clearList(updatedUsers);
            makeAuthenticatedMeRequest(search, updatedUsers);
            GUIBuilder.logMsg("=== Completed ===");
        }

    }

    private void makeAuthenticatedMeRequest(TSheetSearch search, ArrayList<User> updatedUsers) throws InterruptedException, ExecutionException, IOException {
        Stopwatch watch = Stopwatch.createStarted();
        for (User u : updatedUsers) {
            String suffix;
            if (u.getName().endsWith("s")) {
                suffix = "'";
            } else {
                suffix = "'s";
            }
            GUIBuilder.logMsg("Adding " + u.getName() + suffix + " contracts");

            final OAuthRequest request = new OAuthRequest(Verb.POST, Constants.ADD_RECORD_URL);
            request.addHeader("Content-Type", "application/json;charset=UTF-8");
            String json = "";
            for (String s : search.getStringContracts(u.getFirstName())) {
                json = "{\"fields\": { " +
                        "\"Email\": \"" + u.getEmail() + "\", " +
                        "\"Contract\": \"" + s + "\", " +
                        "\"Date\": \"" + effectiveDate + "\", " +
                        "\"Name\": \"" + u.getFirstName() + " " + u.getLastName() + "\", " +
                        "}}";
                request.setPayload(json);
                mOAuthService.signRequest(mAccessToken, request);
                final Response response = mOAuthService.execute(request);
            }
        }
        watch.stop();
        long minutes = watch.elapsed(TimeUnit.MINUTES);
        long seconds = watch.elapsed(TimeUnit.SECONDS) - (minutes * 60);
        GUIBuilder.logMsg("New contracts exported to SharePoint in " + minutes + " minutes and " + seconds + " seconds.");
    }

    private void clearList(ArrayList<User> users) throws InterruptedException, ExecutionException, IOException {
        Stopwatch watch = Stopwatch.createStarted();
        final OAuthRequest request = new OAuthRequest(Verb.GET, Constants.GET_ALL_ITEMS_URL);
        mOAuthService.signRequest(mAccessToken, request);
        request.addHeader("Accept", "application/json, text/plain, */*");
        final Response response = mOAuthService.execute(request);
        StringBuilder trimmedResponse = new StringBuilder(response.getBody());
        String trimmed = trimmedResponse.substring(249, trimmedResponse.length() - 1);
        ArrayList<SharepointItem> items = new Gson().fromJson(trimmed, new
                TypeToken<ArrayList<SharepointItem>>(){}.getType());

        for (SharepointItem s : items) {
            final OAuthRequest getItem = new OAuthRequest(Verb.GET, Constants.GET_ITEM_URL + s.getId());
            mOAuthService.signRequest(mAccessToken, getItem);
            getItem.addHeader("Accept", "application/json, text/plain, */*");
            final Response getResponse = mOAuthService.execute(getItem);
            String body = getResponse.getBody();
            Fields field = new Gson().fromJson(body, Fields.class);

            DetailedSharepointItem newItem = field.getFields();

            for (User u : users) {
                if (u.getEmail().equals(newItem.getEmail())) {
                    String suffix = "";
                    if (u.getName().endsWith("s")) {
                        suffix = "'";
                    } else {
                        suffix = "'s";
                    }
                    GUIBuilder.logMsg("Deleting " + u.getFirstName() + " " + u.getLastName() + suffix + " record");
                    final OAuthRequest deleteRequest = new OAuthRequest(Verb.DELETE, Constants.DELETE_RECORD_URL + s.getId());
                    mOAuthService.signRequest(mAccessToken, deleteRequest);
                    final Response deleteResponse = mOAuthService.execute(deleteRequest);
                }
            }
        }
        watch.stop();
        long minutes = watch.elapsed(TimeUnit.MINUTES);
        long seconds = watch.elapsed(TimeUnit.SECONDS) - (minutes * 60);
        GUIBuilder.logMsg("Old items deleted from list in " + minutes + " minutes and " + seconds + " seconds.");
    }

    private String getAuthorizationCode() throws IOException, URISyntaxException {
        // Obtain the Authorization URL
        final String authorizationUrl = mOAuthService.getAuthorizationUrl();
        if (isDesktopSupported()) {
            getDesktop().browse(new URI(authorizationUrl));
        }
        else {
            System.out.println("Now go and authorize Java-Native-Console-Connect here:");
            System.out.println(authorizationUrl);
        }
        String code;

        String input = JOptionPane.showInputDialog(GUIBuilder.frame, "Paste the entire URL of the page after signing in");

        if (input == null || input.isEmpty()) {
            return null;
        } else {
            String regex = "code=([a-zA-Z0-9_-]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            matcher.find();
            code = matcher.group(1);
        }



        return code;
    }

}