package Drivers;

import Data.Jobcode;
import Data.JobcodeAssignment;
import Data.User;
import Root.RootJobcode;
import Root.RootJobcodeAssignment;
import Root.RootUser;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TSheetSearch {

    private String token;
    private List<User> users = new ArrayList<>();
    private List<Jobcode> jobcodeList = new ArrayList<>();
    private HashMap<Integer, Jobcode> jobcodes = new HashMap<>();

    public TSheetSearch(String token) {
        this.token = token;

        int i = 1;
        while(true) {
            String result = call("jobcodes?supplemental_data=no&page=" + i);

            if (i > 1) {
                StringBuffer temp = new StringBuffer(result);
                result = temp.substring(0, temp.length() - 1);
            }
            RootJobcode root = new Gson().fromJson(result.toString(), RootJobcode.class);

            jobcodeList.addAll(root.getJobcodes());

            if (root.getJobcodes().size() % 50 == 0) {
                i++;
                continue;
            } else {
                break;
            }
        }

        for (Jobcode j : jobcodeList) {
            jobcodes.put(j.getId(), j);
        }


    }

    private String call(String params) {
        String result;
        Response response;
        String url = "https://rest.tsheets.com/api/v1/" + params;


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
            String result = call("users?per_page=50&supplemental_data=no&active=yes&page=" + i);
            if (i > 1) {
                StringBuffer temp = new StringBuffer(result);
                result = temp.substring(0, temp.length() - 1);
            }
            RootUser root = new Gson().fromJson(result, RootUser.class);

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

    public User getUserByName(String name) {
        StringBuffer result = new StringBuffer(call("users?supplemental_data=no&active=yes&first_name=" + name));
        String temp = result.substring(0, result.length() - 1);

        RootUser root = new Gson().fromJson(temp, RootUser.class);

        if (root.getUsers().size() == 0) {
            throw new IllegalArgumentException();
        }

        return root.getUsers().get(0);
    }

    public List<Jobcode> getUserContracts(int userId) {
        StringBuffer result = new StringBuffer(call("jobcode_assignments?supplemental_data=no&user_ids=" + userId));

        RootJobcodeAssignment root = new Gson().fromJson(result.toString(), RootJobcodeAssignment.class);

        List<Jobcode> ans = new ArrayList<>();

        for (JobcodeAssignment j : root.getJobcodeAssignments()) {
            if (j.isActive()) {
                ans.add(jobcodes.get(j.getJobcodeId()));
            }
        }

        return ans;
    }

    public Jobcode getJobcode(int id) {
        return jobcodes.get(id);
    }

    public ArrayList<String> getStringContracts(String name) {
        ArrayList<String> complete = new ArrayList<>();

        for (Jobcode j : getUserContracts(getUserByName(name).getId())) {
            StringBuffer contract = new StringBuffer();

            if (j.getParentId() == 0) {
                continue;
            } else if (j.hasChild()) {
                continue;
            } else if (j.isAssignedToAll()) {
                continue;
            }

            Jobcode temp = j;
            while (temp != null) {
                contract.insert(0, temp.getName() + " -> ");

                temp = getJobcode(temp.getParentId());
            }

            String ans = contract.substring(0, contract.length() - 4);

            complete.add(ans);
        }

        return complete;
    }
}
