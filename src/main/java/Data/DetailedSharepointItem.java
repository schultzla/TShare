package Data;

public class DetailedSharepointItem {

    /*
    Title is the contract for the ContractsRef list due to SharePoints terrible renaming system
     */

    private String Email, id, Value, Title, PlanFY;

    public String getContract() {
        return Title;
    }

    public String getEmail() {
        return Email;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return Value;
    }

    public String getPlanFY() {
        return PlanFY;
    }


}
