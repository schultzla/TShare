import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RootJobcodeAssignment {
    private Map<String, JobcodeAssignment> jobcode_assignments;

    public List<JobcodeAssignment> getJobcodeAssignments() {
        List<JobcodeAssignment> jobcodeAssignments = new ArrayList();

        for (String key : jobcode_assignments.keySet()) {
            jobcodeAssignments.add(jobcode_assignments.get(key));
        }

        return jobcodeAssignments;
    }
}
