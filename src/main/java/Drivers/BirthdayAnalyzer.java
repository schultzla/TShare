package Drivers;

import Data.BirthdayRecord;
import MicrosoftGraph.Graph;
import com.poiji.bind.Poiji;
import java.io.File;
import java.util.Calendar;
import java.util.List;

public class BirthdayAnalyzer {

    private List<BirthdayRecord> records;

    public BirthdayAnalyzer(String fileName) {
        records = Poiji.fromExcel(new File(fileName), BirthdayRecord.class);
    }

    public void analyze(int selectedMonth) {
        analyzeBirthdays(selectedMonth);
        GUIBuilder.logMsg("");
        analyzeAnniversary(selectedMonth);
    }

    public void analyzeBirthdays(int selectedMonth) {
        GUIBuilder.logMsg("=== Birthdays for " + Graph.getMonthForInt(selectedMonth - 1) + "===");
        for (BirthdayRecord r : records) {
            String[] birth = r.birthday.split("/");
            int birthMonth = Integer.parseInt(birth[0]);
            if (selectedMonth == birthMonth) {
                GUIBuilder.logMsg(r.employee + " : " + r.birthday);
            }
        }
    }

    public void analyzeAnniversary(int selectedMonth) {
        GUIBuilder.logMsg("=== Anniversaries for " + Graph.getMonthForInt(selectedMonth - 1) + "===");
        for (BirthdayRecord r : records) {
            String[] hire = r.hireDate.split("/");
            int hireMonth = Integer.parseInt(hire[0]);
            int hireYear = Integer.parseInt(hire[2]);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            if (selectedMonth == hireMonth && (currentYear - hireYear) > 0) {
                GUIBuilder.logMsg(r.employee + " : " + r.hireDate + ", Years: " + (currentYear - hireYear));
            }
        }
    }


}
