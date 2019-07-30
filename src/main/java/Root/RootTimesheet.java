package Root;

import Data.Timesheet;
import Data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RootTimesheet {
    private Map<String, Timesheet> timesheets;

    public List<Timesheet> getTimesheets() {
        List<Timesheet> timesheetList = new ArrayList();

        for (String key : timesheets.keySet()) {
            timesheetList.add(timesheets.get(key));
        }

        return timesheetList;
    }
}
