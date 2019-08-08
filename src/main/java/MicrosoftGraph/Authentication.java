package MicrosoftGraph;

import Data.DetailedSharepointItem;
import Data.Fields;
import Data.Keys;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

import static Drivers.GUIBuilder.infoBox;

public class Authentication {

    public Authentication() {
    }

    public Token authorize() {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client_id", Keys.CLIENT_ID)
                .addFormDataPart("scope", "https://graph.microsoft.com/.default")
                .addFormDataPart("client_secret", Keys.CLIENT_SECRET)
                .addFormDataPart("grant_type", "client_credentials")
                .build();


        Request request = new Request.Builder()
                .url("https://login.microsoftonline.com/e42aec74-d7bb-491d-a0a3-617a9d197a96/oauth2/v2.0/token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Token token = null;
        try {
            token = new Gson().fromJson(response.body().string(), Token.class);
        } catch (IOException e) {
            infoBox("Unable to authenticate Microsoft Graph, try launching TShare again", "Error");
            System.exit(0);
        }

        return token;
    }

    public String authorizeTSheets(Token token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Keys.TSHEETS_KEY_URL)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Authorization", "Bearer " + token.getToken())
                .build();

        String response = "";
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            infoBox("Unable to authenticate TSheets, try launching TShare again", "Error");
            System.exit(0);
        }

        Fields field = new Gson().fromJson(response, Fields.class);
        DetailedSharepointItem item = field.getFields();

        return item.getKey();
    }
}
