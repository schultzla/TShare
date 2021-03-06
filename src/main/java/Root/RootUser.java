package Root;

import Data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RootUser {
    private Map<String, User> users;

    public List<User> getUsers() {
        List<User> userList = new ArrayList();

        for (String key : users.keySet()) {
            userList.add(users.get(key));
        }

        return userList;
    }
}
