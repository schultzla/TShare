package MicrosoftGraph;

import Data.*;
import Drivers.GUIBuilder;
import Drivers.TSheetSearch;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Graph {

    private String token;
    private TSheetSearch search;
    private OkHttpClient client;

    public Graph(String token, TSheetSearch search) {
        this.token = token;
        this.search = search;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        client = builder.build();
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
                        "\"Name\": \"" + u.getName() + "\", " +
                        "}}";;

                RequestBody requestBody = new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                        bufferedSink.writeUtf8(json);
                    }
                };

                Request request = new Request.Builder()
                        .url(Keys.ADD_RECORD_URL)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json;charset=UTF-8")
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
        GUIBuilder.logMsg("=== New contracts exported to SharePoint in " + minutes + " minutes and " + seconds + " seconds. ===");
    }

    public void clearList(ArrayList<User> users) throws IOException {
        Stopwatch watch = Stopwatch.createStarted();
        GUIBuilder.logMsg("=== Deleting Records ===");

        Request request = new Request.Builder()
                .url(Keys.GET_ALL_ITEMS_URL)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Authorization", "Bearer " + token)
                .build();


        Response response = client.newCall(request).execute();

        StringBuilder trimmedResponse = new StringBuilder(response.body().string());
        String trimmed = trimmedResponse.substring(249, trimmedResponse.length() - 1);
        ArrayList<SharepointItem> items = new Gson().fromJson(trimmed, new TypeToken<ArrayList<SharepointItem>>(){}.getType());

        for (SharepointItem s : items) {
            Request getItem = new Request.Builder()
                    .url(Keys.GET_ITEM_URL + s.getId())
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
                            .url(Keys.DELETE_RECORD_URL + s.getId())
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

        response.close();
        watch.stop();
        long minutes = watch.elapsed(TimeUnit.MINUTES);
        long seconds = watch.elapsed(TimeUnit.SECONDS) - (minutes * 60);
        GUIBuilder.logMsg("=== Old items deleted from list in " + minutes + " minutes and " + seconds + " seconds. ===");
    }
}
