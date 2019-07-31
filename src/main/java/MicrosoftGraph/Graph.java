package MicrosoftGraph;

import Data.*;
import Drivers.GUIBuilder;
import Drivers.TSheetSearch;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import okhttp3.*;
import java.awt.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {

    private String token;
    private TSheetSearch search;
    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public Graph(String token, TSheetSearch search) {
        this.token = token;
        this.search = search;
        client = new OkHttpClient();
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        StringBuilder monthAct = new StringBuilder(month);
        month = monthAct.substring(0, 3);
        return month.toUpperCase();
    }


    public void updateActualHours(String month, String year, String monthName, boolean updateToCurrent) throws IOException {
        GUIBuilder.logMsg("=== Updating Indirect Annual Work Plan Actual Hours ===");
        if (updateToCurrent) {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int monthInt = Integer.valueOf(month);

            while (monthInt < currentMonth) {
                GUIBuilder.logMsg("=== Updating " + monthName + " Actual Hours ===");
                HashMap<User, Double[]> empHours = search.calcMonthlyHours(String.valueOf(monthInt), year);

                Request request = new Request.Builder()
                        .url(Keys.ANNUAL_PLAN_URL)
                        .addHeader("Accept", "application/json, text/plain, */*")
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                String response = "";
                try {
                    response = client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Value val = new Gson().fromJson(response, Value.class);
                ArrayList<SharepointItem> items = val.getItems();

                for (SharepointItem item : items) {
                    Request getItem = new Request.Builder()
                            .url(Keys.ANNUAL_PLAN_URL + item.getId())
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Accept", "application/json, text/plain, */*")
                            .build();

                    String body = "";
                    body = client.newCall(getItem).execute().body().string();

                    Fields field = new Gson().fromJson(body, Fields.class);
                    DetailedSharepointItem newItem = field.getFields();

                    for (User u : empHours.keySet()) {
                        String suffix = u.getName().endsWith("s") ? "'" : "'s";

                        if (newItem.getEmail().toLowerCase().equals(u.getEmail().toLowerCase()) && newItem.getPlanFY().equals(year)) {
                            GUIBuilder.logMsg("Updating " + u.getName() + suffix + " actual hours (PTO: " + empHours.get(u)[0] + ", Indirect: " + empHours.get(u)[1] + ")");
                            String monthInd = "\"" + monthName + "_IND\": \"", monthPto = "\"" + monthName + "_PTO\": \"", planAct = "\"" + monthName + "_PLN_ACT\": \"";
                            String monthTot = "\"" + monthName + "_TOT0\": \"";

                            String json = "{\"fields\": { " +
                                    monthInd + empHours.get(u)[1] + "\", " +
                                    monthPto + empHours.get(u)[0] + "\", " +
                                    monthTot + (empHours.get(u)[0] + empHours.get(u)[1]) + "\", " +
                                    planAct + "Actual" + "\"" +
                                    "}}";

                            RequestBody requestBody = RequestBody.create(json, JSON);

                            Request addRequest = new Request.Builder()
                                    .url(Keys.ANNUAL_PLAN_URL + item.getId())
                                    .addHeader("Authorization", "Bearer " + token)
                                    .addHeader("Content-Type", "application/json")
                                    .patch(requestBody)
                                    .build();

                            Response res = null;
                            try {
                                res = client.newCall(addRequest).execute();
                            } catch (SocketTimeoutException e) {
                                continue;
                            }
                            res.close();
                        }
                    }
                }
                GUIBuilder.logMsg("=== Finished Updating " + monthName + " Actual Hours ===");
                monthInt++;
                monthName = getMonthForInt(monthInt - 1);
            }
        } else {
            HashMap<User, Double[]> empHours = search.calcMonthlyHours(month, year);

            Request request = new Request.Builder()
                    .url(Keys.ANNUAL_PLAN_URL)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            String response = "";
            try {
                response = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Value val = new Gson().fromJson(response, Value.class);
            ArrayList<SharepointItem> items = val.getItems();

            for (SharepointItem item : items) {
                Request getItem = new Request.Builder()
                        .url(Keys.ANNUAL_PLAN_URL + item.getId())
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Accept", "application/json, text/plain, */*")
                        .build();

                String body = "";
                body = client.newCall(getItem).execute().body().string();

                Fields field = new Gson().fromJson(body, Fields.class);
                DetailedSharepointItem newItem = field.getFields();

                for (User u : empHours.keySet()) {
                    String suffix = u.getName().endsWith("s") ? "'" : "'s";

                    if (newItem.getEmail().toLowerCase().equals(u.getEmail().toLowerCase()) && newItem.getPlanFY().equals(year)) {
                        GUIBuilder.logMsg("Updating " + u.getName() + suffix + " actual hours (PTO: " + empHours.get(u)[0] + ", Indirect: " + empHours.get(u)[1] + ")");
                        String monthInd = "\"" + monthName + "_IND\": \"", monthPto = "\"" + monthName + "_PTO\": \"", planAct = "\"" + monthName + "_PLN_ACT\": \"";
                        String monthTot = "\"" + monthName + "_TOT0\": \"";

                        String json = "{\"fields\": { " +
                                monthInd + empHours.get(u)[1] + "\", " +
                                monthPto + empHours.get(u)[0] + "\", " +
                                monthTot + (empHours.get(u)[0] + empHours.get(u)[1]) + "\", " +
                                planAct + "Actual" + "\"" +
                                "}}";

                        RequestBody requestBody = RequestBody.create(json, JSON);

                        Request addRequest = new Request.Builder()
                                .url(Keys.ANNUAL_PLAN_URL + item.getId())
                                .addHeader("Authorization", "Bearer " + token)
                                .addHeader("Content-Type", "application/json")
                                .patch(requestBody)
                                .build();

                        Response res = null;
                        try {
                            res = client.newCall(addRequest).execute();
                        } catch (SocketTimeoutException e) {
                            continue;
                        }
                        res.close();
                    }
                }
            }
        }
        GUIBuilder.notif("Finished updating indirect annual work plan hours", TrayIcon.MessageType.INFO);
        GUIBuilder.logMsg("=== Finished Updating Indirect Annual Work Plan Actual Hours ===");
    }

    public void updateContractReferences() throws IOException {
        GUIBuilder.logMsg("=== Updating Contracts Reference List ===");
        HashSet<String> contracts = search.getUniqueJobcodes();

        Request request = new Request.Builder()
                .url(Keys.CONTRACTS_REF_URL)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        String response = "";
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Value val = new Gson().fromJson(response, Value.class);
        ArrayList<SharepointItem> items = val.getItems();

        if (!items.isEmpty()) {
            for (SharepointItem item : val.getItems()) {
                Request getItem = new Request.Builder()
                        .url(Keys.CONTRACTS_REF_URL + item.getId())
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Accept", "application/json, text/plain, */*")
                        .build();

                String body = "";
                body = client.newCall(getItem).execute().body().string();

                Fields field = new Gson().fromJson(body, Fields.class);
                DetailedSharepointItem newItem = field.getFields();

                if (contracts.contains(newItem.getContract())) {
                    contracts.remove(newItem.getContract());
                } else {
                    if (!newItem.getContract().equals("Indirect")) {
                        Request deleteItem = new Request.Builder()
                                .url(Keys.CONTRACTS_URL + newItem.getId())
                                .addHeader("Authorization", "Bearer " + token)
                                .delete()
                                .build();

                        try {
                            client.newCall(deleteItem).execute();
                        } catch (SocketTimeoutException e) {
                            continue;
                        }
                    }
                }
            }
        }

        if (contracts.isEmpty()) {
            GUIBuilder.logMsg("No new contracts");
        }
        for (String s : contracts) {
            GUIBuilder.logMsg("Adding " + s);
            String json = "{\"fields\": { " +
                    "\"Title\": \"" + s + "\"" +
                    "}}";

            RequestBody requestBody = RequestBody.create(json, JSON);

            Request addRequest = new Request.Builder()
                    .url(Keys.CONTRACTS_REF_URL)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try {
                client.newCall(addRequest).execute();
            } catch (SocketTimeoutException e) {
                continue;
            }
        }

        GUIBuilder.notif("Finished updating contracts reference list", TrayIcon.MessageType.INFO);
        GUIBuilder.logMsg("=== Finished Updating Contracts Reference List ===");
    }

    public void exportRecords(ArrayList<User> users) throws IOException {
        Stopwatch watch = Stopwatch.createStarted();
        GUIBuilder.logMsg("=== Export New Records ===");

        for (User u : users) {
            String suffix = u.getName().endsWith("s") ? "'" : "'s";

            GUIBuilder.logMsg("Adding " + u.getName() + suffix + " contracts");

            for (String s : search.getStringContracts(u.getUsername())) {
                String json = "{\"fields\": { " +
                        "\"Email\": \"" + u.getEmail() + "\", " +
                        "\"Contract\": \"" + s + "\", " +
                        "\"Date\": \"" + GUIBuilder.effectiveDate + "\", " +
                        "\"Name\": \"" + u.getName() + "\"" +
                        "}}";

                RequestBody requestBody = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(Keys.CONTRACTS_URL)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .post(requestBody)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (SocketTimeoutException e) {
                    continue;
                }
            }
        }

        watch.stop();
        long minutes = watch.elapsed(TimeUnit.MINUTES);
        long seconds = watch.elapsed(TimeUnit.SECONDS) - (minutes * 60);
        GUIBuilder.notif("Finished exporting contracts to SharePoint", TrayIcon.MessageType.INFO);
        GUIBuilder.logMsg("=== New contracts exported to SharePoint in " + minutes + " minutes and " + seconds + " seconds. ===");
    }

    public void clearList(ArrayList<User> users) throws IOException {
        Stopwatch watch = Stopwatch.createStarted();
        GUIBuilder.logMsg("=== Deleting Records ===");

        Request request = new Request.Builder()
                .url(Keys.CONTRACTS_URL)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Authorization", "Bearer " + token)
                .build();


        Response response = client.newCall(request).execute();

        Value val = new Gson().fromJson(response.body().string(), Value.class);
        ArrayList<SharepointItem> items = val.getItems();

        for (SharepointItem s : items) {
            Request getItem = new Request.Builder()
                    .url(Keys.CONTRACTS_URL + s.getId())
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .build();

            String body = "";
            body = client.newCall(getItem).execute().body().string();

            Fields field = new Gson().fromJson(body, Fields.class);
            DetailedSharepointItem newItem = field.getFields();

            for (User u : users) {
                if (u.getEmail().equals(newItem.getEmail())) {
                    String suffix = u.getName().endsWith("s") ? "'" : "'s";

                    GUIBuilder.logMsg("Deleting " + u.getName() + suffix + " record");

                    Request deleteItem = new Request.Builder()
                            .url(Keys.CONTRACTS_URL + s.getId())
                            .addHeader("Authorization", "Bearer " + token)
                            .delete()
                            .build();

                    try {
                        client.newCall(deleteItem).execute();
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                }
            }
        }

        response.body().close();
        watch.stop();
        long minutes = watch.elapsed(TimeUnit.MINUTES);
        long seconds = watch.elapsed(TimeUnit.SECONDS) - (minutes * 60);
        GUIBuilder.logMsg("=== Old items deleted from list in " + minutes + " minutes and " + seconds + " seconds. ===");
    }
}
