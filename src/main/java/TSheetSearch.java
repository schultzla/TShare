import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class TSheetSearch {

    private String token;

    public TSheetSearch(String token) {
        this.token = token;
    }

    public String call(String params) {
        String result;
        Response response;
        String url = "https://rest.tsheets.com/api/v1/users?per_page=50&" + params;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try {
            response = client.newCall(request).execute();

            StringBuffer temp = new StringBuffer(response.body().string());
            result = temp.substring(17, temp.length() - 18);
            StringBuffer temp2 = new StringBuffer(result);
            temp2.insert(0, '{');
            temp2.append('}');

            result = temp2.toString();

            return result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Failed";
    }

    public List<User> getAllUsers() {
        Root root = new Gson().fromJson(call("supplemental_data=no&active=yes"), Root.class);

        return root.getUsers();
    }
}
