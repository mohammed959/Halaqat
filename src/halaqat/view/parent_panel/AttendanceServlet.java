/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package halaqat.view.parent_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.AttendanceDao;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.AttendanceDatabaseAccess;
import halaqat.data.dao.imp.HalaqaDatabaseAccess;
import halaqat.data.dao.imp.SemesterDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Attendance;
import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Semester;
import halaqat.data.pojos.Student;
import halaqat.view.ViewUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mohammed
 */
public class AttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ParentPanelViewUtils.printHeaderWithMenu(req, resp);

        try {
            printAttendanceTable(req, resp);
        } catch (Exception e) {
            resp.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع"));
        }

        ParentPanelViewUtils.printFooter(resp.getWriter());
    }

    private void printAttendanceTable(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {
        PrintWriter writer = response.getWriter();

        String parentId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);

        writer.print("<h3 id='section-title'>سجل الحضور</h3><br>");

        writer.print("<center>");

        Calendar today = Calendar.getInstance();
        String yearParam = request.getParameter("year");
        int year;
        if (yearParam == null || yearParam.length() == 0) {
            year = today.get(Calendar.YEAR);
        } else {
            year = Integer.parseInt(yearParam);
        }

        String monthParam = request.getParameter("month");
        int month;
        if (monthParam == null || monthParam.length() == 0) {
            month = today.get(Calendar.MONTH) + 1; // Range of get(Calendar.MONTH) is [0,11]
        } else {
            month = Integer.parseInt(monthParam);
        }

        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        List<Integer> years = attendanceDao.distinctYearsForParent(parentId);
        writer.print("      <form id='recitation-options' action='/parent-panel/attendance' method='get'>\n");

        if (!(today.get(Calendar.MONTH) == month - 1 && today.get(Calendar.YEAR) == year)) { // Print next only if the selected month is not the current month..
            int nextMonth, nextYear;

            nextMonth = month + 1;
            nextYear = year;


            if (nextMonth == 13) {
                nextMonth = 1;
                nextYear = year + 1;
            }
            writer.print("<a style='float:left' class='fa fa-chevron-left button-green' href='/parent-panel/attendance?month=" + nextMonth + "&year=" + nextYear + "'></a>");
        }
        int prevMonth, prevYear;
        prevMonth = month - 1;
        prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        writer.print("<a style='float:right;' class='fa fa-chevron-right button-green' href='/parent-panel/attendance?month=" + prevMonth + "&year=" + prevYear + "'></a>");


        writer.print("        <span>العام :</span>\n"
                + "\n"
                + "        <select name='year' class='select-input'>\n");

        for (int i : years) {
            writer.print("            <option " + (year == i ? "selected" : "") + " value=" + i + ">" + i + "</option>\n");
        }

        writer.print("        </select>\n"
                + "\n"
                + "\n"
                + "        <span> الشهر :</span>\n"
                + "\n"
                + "        <select name='month' class='select-input'>\n");

        for (int i = 1; i <= 12; i++) {
            writer.print("            <option " + (month == i ? "selected" : "") + " value=" + i + ">" + i + "</option>\n");
        }

        writer.print("        </select>\n"
                + "        <input type='submit' class='button-primary' value='عرض' />"
                + "      </form><br>");

        Calendar selectedDate = new GregorianCalendar(year, month - 1, 1);
        int maxDaysOfMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br><br>");
        writer.print("  <div id='section-to-print'>");

        writer.print("<table id='attendance-table'>\n"
                + "<thead>\n"
                + "   <tr>\n");

        writer.print("      <th>اسم الطالب</th>\n");

        SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEEE dd/MM", new Locale("ar", "sa"));

        for (int i = 1; i <= maxDaysOfMonth; i++) {
            selectedDate.set(Calendar.DAY_OF_MONTH, i);
            boolean isFriday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
            boolean isSaturday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            if (isFriday || isSaturday) {
                response.getWriter().print("        <th></th>\n");
                if (isFriday) // Skip Saturday if Friday is printed as blank <td>
                {
                    i++;
                }

                continue;
            }
            response.getWriter().print("        <th><span>" + dayNameFormatter.format(selectedDate.getTime()) + "</span></th>\n");
        }
        writer.print("  </tr>\n"
                + "</thead>"
                + "<tbody>");

        StudentDao studentDao = new StudentDatabaseAccess();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        for (Student s : studentDao.byParent(parentId)) {
            writer.print("<tr><td>" + s.getFirstName() + " " + s.getMiddleName() + "</td>");

            List<Attendance> attendanceList = attendanceDao.monthlyAttendance(s.getNationalID(), year, month);
            int attendanceIndex = 0;

            for (int i = 1; i <= maxDaysOfMonth; i++) {
                selectedDate.set(Calendar.DAY_OF_MONTH, i);
                boolean isFriday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
                boolean isSaturday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
                if (isFriday || isSaturday) {
                    response.getWriter().print("<td></td>");
                    if (isFriday) { // Skip Saturday if Friday is printed as blank <td>
                        i++;
                    }
                    continue;
                }

                if (attendanceIndex < attendanceList.size()) {
                    Attendance attendance = attendanceList.get(attendanceIndex);
                    Calendar calendar = Calendar.getInstance();
                    Date attendanceDate = dateFormatter.parse(attendance.getDate());
                    calendar.setTime(attendanceDate);
                    // Check if the next item in attendance list is the day in this iteration
                    if (calendar.get(Calendar.DAY_OF_MONTH) == i) {
                        // Check if the day in this iteration in the current semester,
                        // only allow the teacher to edit the current semester attendance.

                        writer.print("<td><span class='fa fa-1x fa-" + (attendance.isPresent() ? "check green-color" : "times red-color") + "'></span></td>");
                        attendanceIndex++;
                        continue;
                    }
                }

                writer.print("<td></td>");

            }

            writer.print("</tr>");
        }
        writer.print("</tbody></table></div><br><br>");

    }

}
