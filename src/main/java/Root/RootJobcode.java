package Root;

import Data.Jobcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RootJobcode {
    private Map<String, Jobcode> jobcodes;

    public List<Jobcode> getJobcodes() {
        List<Jobcode> jobcode = new ArrayList();

        for (String key : jobcodes.keySet()) {
            jobcode.add(jobcodes.get(key));
        }

        return jobcode;
    }
}
