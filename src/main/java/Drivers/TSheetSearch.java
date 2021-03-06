package Drivers;

import Data.*;
import Root.RootJobcode;
import Root.RootJobcodeAssignment;
import Root.RootTimesheet;
import Root.RootUser;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TSheetSearch {

    private String token;
    private TreeMap<String, User> users = new TreeMap<>();
    private HashMap<Integer, Jobcode> jobcodes = new HashMap<>();

    protected TSheetSearch(String token) {
        this.token = token;

        int i = 1;
        List<Jobcode> jobcodeList = new ArrayList<>();
        while(true) {
            String result = call("jobcodes?type=all&supplemental_data=no&page=" + i);

            if (i > 1) {
                StringBuilder temp = new StringBuilder(result);
                result = temp.substring(0, temp.length() - 1);
            }
            RootJobcode root = new Gson().fromJson(result, RootJobcode.class);

            jobcodeList.addAll(root.getJobcodes());

            if (root.getJobcodes().size() % 50 == 0) {
                i++;
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

            StringBuilder temp = new StringBuilder(response.body().string());
            result = temp.substring(17, temp.length() - 18);
            StringBuilder temp2 = new StringBuilder(result);
            temp2.insert(0, '{');
            temp2.append('}');

            result = temp2.toString();

            return result;

        } catch (IOException e) {
            if (GUIBuilder.debug.isSelected()) {
                GUIBuilder.logMsg(e.getMessage());
            } else {
                e.printStackTrace();
            }
        }

        return "Failed";
    }

    public HashMap<User, Double[]> calcMonthlyHours(String month, String year) {
        HashMap<User, Double[]> empHours = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date start = null;
        try {
            start = sdf.parse(year + "-" + month + "-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startDate = sdf.format(start);
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = sdf.format(cal.getTime());

        OkHttpClient client = new OkHttpClient();

        for (User u : this.getAllUsers().values()) {
            Request request = new Request.Builder()
                    .url("https://rest.tsheets.com/api/v1/timesheets?per_page=50&start_date=" + startDate + "&end_date=" + endDate + "&supplemental_data=no&user_ids=" + u.getId())
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            String result = null;
            try {
                result = client.newCall(request).execute().body().string();
            } catch (Exception e) {
                if (GUIBuilder.debug.isSelected()) {
                    GUIBuilder.logMsg(e.getMessage());
                } else {
                    e.printStackTrace();
                }
                continue;
            }

            Results root = new Gson().fromJson(result, Results.class);

            List<Timesheet> timesheets = new ArrayList<>();
            try {
                timesheets = root.getTimesheets().getTimesheets();
            } catch (NullPointerException e) {
                GUIBuilder.logMsg("Error, unable to update " + u.getName() + " (NullPointerException)");
                continue;
            }

            int page = 2;
            while (root.isMore()) {
                Request moreRequest = new Request.Builder()
                        .url("https://rest.tsheets.com/api/v1/timesheets?per_page=50&start_date=" + startDate + "&end_date=" + endDate + "&supplemental_data=no&user_ids=" + u.getId() + "&page=" + page)
                        .get()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                String moreResult = null;
                try {
                    moreResult = client.newCall(moreRequest).execute().body().string();
                } catch (Exception e) {
                    if (GUIBuilder.debug.isSelected()) {
                        GUIBuilder.logMsg(e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                    continue;
                }
                page++;

                root = new Gson().fromJson(moreResult, Results.class);

                timesheets.addAll(root.getTimesheets().getTimesheets());
            }

            double totalIndirect = 0, paidTimeOff = 0;
            for (Timesheet t : timesheets) {
                double hours = t.getDuration() / 60.0 / 60.0;
                if (getJobcode(t.getJobcode_id()).getType().equals("pto")) {
                    paidTimeOff += hours;
                } else if (getJobcode(t.getJobcode_id()).getName().contains("G&A")
                || getJobcode(t.getJobcode_id()).getName().contains("B&P")
                || getJobcode(t.getJobcode_id()).getName().contains("Overhead")
                || getJobcode(t.getJobcode_id()).getName().contains("IR&D")) {
                    totalIndirect += hours;
                }
            }
            Double[] hrs = new Double[2];
            hrs[0] = paidTimeOff;
            hrs[1] = totalIndirect;
            empHours.put(u, hrs);
        }

        return empHours;
    }

    public TreeMap<String, User> getAllUsers() {
        int i = 1;
        while(true) {
            String result = call("users?per_page=50&supplemental_data=no&active=yes&page=" + i);
            if (i > 1) {
                StringBuilder temp = new StringBuilder(result);
                result = temp.substring(0, temp.length() - 1);
            }
            RootUser root = new Gson().fromJson(result, RootUser.class);

            for (User u : root.getUsers()) {
                users.put(u.getLastName() + ", " + u.getFirstName(), u);
            }

            if (root.getUsers().size() % 50 == 0) {
                i++;
            } else {
                break;
            }
        }

        return users;
    }

    public User getUserByUsername(String username) {
        StringBuilder result = new StringBuilder(call("users?supplemental_data=no&active=yes&usernames=" + username));
        String temp = result.substring(0, result.length() - 1);

        RootUser root = new Gson().fromJson(temp, RootUser.class);

        if (root.getUsers().size() == 0) {
            throw new IllegalArgumentException();
        }

        return root.getUsers().get(0);
    }

    public List<Jobcode> getUserContracts(int userId) {
        String result = call("jobcode_assignments?supplemental_data=no&user_ids=" + userId);

        RootJobcodeAssignment root = new Gson().fromJson(result, RootJobcodeAssignment.class);

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

    public HashSet<String> getUniqueJobcodes() {
        HashSet<String> complete = new HashSet<>();
        for (Jobcode j : jobcodes.values()) {
            StringBuilder contract = new StringBuilder();

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

    public ArrayList<String> getStringContracts(String username) {
        ArrayList<String> complete = new ArrayList<>();

        for (Jobcode j : getUserContracts(getUserByUsername(username).getId())) {
            StringBuilder contract = new StringBuilder();
            if (j == null) {
                continue;
            }
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
