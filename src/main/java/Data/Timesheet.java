package Data;

public class Timesheet {

    private int id;
    private int user_id;
    private int jobcode_id;

    public int getDuration() {
        return duration;
    }

    private int duration;

    private String date;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getJobcode_id() {
        return jobcode_id;
    }

    public String getDate() {
        return date;
    }
}
