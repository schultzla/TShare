package Data;

public class Jobcode {

    private int id, parent_id;
    private boolean has_children, assigned_to_all;
    private String type, name;

    public boolean hasChild() {
        return has_children;
    }

    public boolean isAssignedToAll() {
        return assigned_to_all;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parent_id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
