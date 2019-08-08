package Data;

import com.poiji.annotation.ExcelCell;

public class BirthdayRecord {

    @ExcelCell(1)
    public String employee;

    @ExcelCell(2)
    public String birthday;

    @ExcelCell(3)
    public String hireDate;
}
