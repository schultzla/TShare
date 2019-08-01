# TShare

TShare is an application containing three automation processes designed for DESI (Disk Enterprise Solutions, Inc.)

  - Contracts Exporter
  - Hours Updater
  - Late Entries Calculator

## Contracts Exporter
![Contracts](https://github.com/schultzla/TSheetsAPI/blob/master/main.PNG?raw=true)
Updates numerous SharePoint lists to consolidate contracts data for use in the Timesheet Reporting Criteria PowerApp

### Usage
All steps below will notify you with a desktop notification when they are completed, so you can minimize TShare and have it run in the background!
- Update TSheets Customers
-- Retrieves any new TSheets customers and adds them to the customers reference list in SharePoint
-- Deletes any non-active TSheets customers that are in the SharePoint list already
- Manually Update Customers Info
-- This is a manual step, clicking this button will open the customer reference list
-- Here you will update various information such as a contracts PoP and comments/notes
- Update Customers Assigned to Employees
-- Will bring up a menu to select any/all employees to update
-- Updates the customers that are assigned to the selected employees in the employee customers SharePoint list

## Hours Updater
![Contracts](https://github.com/schultzla/TSheetsAPI/blob/master/hours.PNG?raw=true)
Retrieves an employees submitted hours for selected month(s) and updates the employee indirect annual hours plan SharePoint list with their actual hours

### Usage
All steps below will notify you with a desktop notification when they are completed, so you can minimize TShare and have it run in the background! A little more information is that TSheets saves the submitted hours for every day each employee submits time for. This application retrieves that information and then updates a SharePoint list used to track non-direct hours. Managers add planned hours to help plan for the entire year, and this goes through to update those planned hours with the actuals
- Update
-- Updates using only the selected month and year
- Update from Selected to Current Month
-- Updates starting from the selected month and year, all the way up to the month prior to the current month

## Late Entries Analyzer
![Contracts](https://github.com/schultzla/TSheetsAPI/blob/master/late.PNG?raw=true)
Takes a spreadsheet from TSheets that gives all of the 'late entries' for saving time. Employees must save their daily time prior to 9 AM the next day. TSheets gives an excel report of all entries that were saved after this time. This tool is to calculate how may late entries each employee has for a given spreadsheet and gives a report at the end to summarize the information.

### Usage
A properly formatted file must be uplaoded first before you can run and configure the calculator

What is a properly formatted file? Take the .csv file that is given and convert the values to a table, then save it as an .xlsx or .xlx file

- Calculate
-- This will calculate each employees late entries for the spreadsheet and print out a report with this information
- Configure
-- Set Minimum Lates: set the minimum amount of late entries an employee must have to be included in the final report
-- Jobcodes Filter: set which jobcodes can be ignored as a late entry. By default, "Vacation", "Holiday", "Sick", "Leave without Pay", "Ownership Vacation", "Jury Duty", "Bereavement" are all ignored

### Tech

TShare uses a number of technologies to accomplish it's tasks

* Java
* Gradle
* Microsoft Graph API
* GSON
* OkHTTP3
* Poiji
* TSheets API

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.


License
----

MIT
