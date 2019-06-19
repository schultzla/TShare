import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSheetSearch {

    private String token;
    private List<User> users = new ArrayList<>();

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
        int i = 1;
        while(true) {
            String result = call("supplemental_data=no&active=yes&page=" + i);
            if (i > 1) {
                StringBuffer temp = new StringBuffer(result);
                result = temp.substring(0, temp.length() - 1);
            }
            Root root = new Gson().fromJson(result, Root.class);

            users.addAll(root.getUsers());

            if (root.getUsers().size() % 50 == 0) {
                i++;
                continue;
            } else {
                break;
            }
        }

        return users;
    }
}
