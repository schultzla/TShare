package Data;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Record {

    public DateTime dateSubmitted, dateJob;

    @ExcelRow
    public int rowIndex;

    @ExcelCell(0)
    public String submit;

    @ExcelCell(6)
    public String username;

    @ExcelCell(7)
    public String firstName;

    @ExcelCell(8)
    public String lastName;

    @ExcelCell(9)
    public String job;

    @ExcelCell(12)
    public String jobCode;

    @Override
    public String toString() {
        return "Record{" +
                "status='" + this.compareDates() + '\'' +
                ", dateSubmitted='" + dateSubmitted.toString() + '\'' +
                ", username='" + username + '\'' +
                ", jobDate='" + dateJob.toString() + '\'' +
                ", jobCode='" + jobCode + '\'' +
                '}';
    }

    public boolean compareDates() {
        DateTimeFormatter submitSdf = DateTimeFormat.forPattern("MM/dd/yy HH:mm").withZone(DateTimeZone.forID("America/New_York"));
        DateTimeFormatter jobSdf = DateTimeFormat.forPattern("MM/dd/yy").withZone(DateTimeZone.forID("America/New_York"));

        dateSubmitted = new DateTime(submitSdf.parseDateTime(this.submit));
        dateJob = new DateTime(jobSdf.parseDateTime(this.job));

        DateTimeZone zone = DateTimeZone.forID("America/New_York");

        dateJob = dateJob.plusDays(1);

        if (zone.isStandardOffset(dateJob.getMillis())) {
            dateJob = dateJob.plusHours(13);
        } else {
            dateJob = dateJob.plusHours(12);
        }
        dateJob = dateJob.plusMinutes(59);

        return dateSubmitted.isAfter(dateJob);
    }
}
