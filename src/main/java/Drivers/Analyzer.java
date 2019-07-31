package Drivers;

import Data.Record;
import com.poiji.bind.Poiji;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Analyzer {

    static HashMap<String, HashSet<String>> employeeSubmissions = new HashMap<>();
    static Map<String, Integer> employeeLateCounts = new TreeMap<>();
    static HashMap<String, Record> records = new HashMap<>();
    static HashMap<String, Double> totalAttempts = new HashMap<>();
    static TreeSet<String> jobCodes = new TreeSet<>();

    List<Record> data;

    public Analyzer(String fileName) {
        data = Poiji.fromExcel(new File(fileName), Record.class);
        for (Record r : data) {
            jobCodes.add(r.jobCode);
        }
    }

    public void analyze(HashSet<String> exempt) {
        employeeSubmissions.clear();
        employeeLateCounts.clear();
        records.clear();
        totalAttempts.clear();

        for (Record r : data) {
            if (totalAttempts.containsKey(r.username)) {
                totalAttempts.put(r.username, totalAttempts.get(r.username) + 1.0);
            } else {
                totalAttempts.put(r.username, 1.0);
            }

            if (exempt.contains(r.jobCode)) {
                continue;
            } else {
                if (r.compareDates()) {
                    records.put(r.username, r);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String simpleJobDate = sdf.format(r.dateJob.toDate());

                    if (employeeSubmissions.containsKey(r.username)) {
                        if (!employeeSubmissions.get(r.username).contains(simpleJobDate)) {
                            employeeSubmissions.get(r.username).add(simpleJobDate);
                            if (employeeLateCounts.containsKey(r.username)) {
                                employeeLateCounts.put(r.username, employeeLateCounts.get(r.username) + 1);
                            } else {
                                employeeLateCounts.put(r.username, 1);
                            }
                        }
                    } else {
                        HashSet<String> temp = new HashSet<>();
                        temp.add(simpleJobDate);
                        employeeSubmissions.put(r.username, temp);

                        if (employeeLateCounts.containsKey(r.username)) {
                            employeeLateCounts.put(r.username, employeeLateCounts.get(r.username) + 1);
                        } else {
                            employeeLateCounts.put(r.username, 1);
                        }
                    }
                }
            }
        }
    }

    public Record getRecord(String username) {
        return records.get(username);
    }

    public TreeSet<String> getCodes() {
        return jobCodes;
    }

}
