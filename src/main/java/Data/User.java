package Data;

public class User {

    private int id;
    private String first_name, last_name, username, email;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() { return email; }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }
}
