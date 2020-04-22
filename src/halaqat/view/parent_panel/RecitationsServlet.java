
package halaqat.view.parent_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.AttendanceDao;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.ParentDao;
import halaqat.data.dao.abs.RecitationDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.AttendanceDatabaseAccess;
import halaqat.data.dao.imp.HalaqaDatabaseAccess;
import halaqat.data.dao.imp.ParentDatabaseAccess;
import halaqat.data.dao.imp.RecitationDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;
import halaqat.view.ViewUtils;
import halaqat.view.teacher_panel.TeacherPanelViewUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RecitationsServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ParentPanelViewUtils.printHeaderWithMenu(request, response);
        try {
            printStudentsRecitations(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());

    }

    private void printStudentsRecitations(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {

        PrintWriter writer = response.getWriter();
        String parentId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);
        StudentDao studentDao = new StudentDatabaseAccess();
        String nationalId = request.getParameter("national-id");


        List<Student> students = studentDao.byParent(parentId);

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
            month = today.get(Calendar.MONTH) + 1; // getMonth range is 0-11
        } else {
            month = Integer.parseInt(monthParam);
        }

        writer.print("      <form action='/parent-panel/index' id='student-selection-form' method='get'>\n");

        writer.print("        الطالب :\n" +
                "        <select onchange=\"document.getElementById('student-selection-form').submit()\" name='national-id' class='select-input'>\n");


        for (Student student : students) {
            writer.print("<option " + (student.getNationalID().equals(nationalId) ? "selected" : "") + " value='" + student.getNationalID() + "'>" + student.getFirstName() + " " + student.getMiddleName() + "</option>\n");
        }
        writer.print("        </select>\n" +
                "<input type='hidden' name='action' value='student' />" +
                "</form><br>");
        writer.print("<center>");

        if (nationalId == null) {
            if (students.size() == 0) {
                return;
            }
            nationalId = students.get(0).getNationalID();
        }
        RecitationDatabaseAccess recitationDao = new RecitationDatabaseAccess();
        List<Integer> years = recitationDao.distinctYears(nationalId);
        writer.print("      <form id='recitation-options' action='" + request.getRequestURL() + "' method='get'>\n" + // Keep the action parameter
                "        <span>العام :</span>\n" +
                "\n" +
                "        <select name='year' class='select-input'>\n");

        for (int i : years)
            writer.print("            <option " + (year == i ? "selected" : "") + " value=" + i + ">" + i + "</option>\n");

        writer.print("        </select>\n" +
                "\n" +
                "\n" +
                "        <span> الشهر :</span>\n" +
                "\n" +
                "        <select name='month' class='select-input'>\n");

        for (int i = 1; i <= 12; i++)
            writer.print("            <option " + (month == i ? "selected" : "") + " value=" + i + ">" + i + "</option>\n");

        writer.print("        </select>\n");
        if (!(today.get(Calendar.MONTH) == month - 1 && today.get(Calendar.YEAR) == year)) { // Print next only if the selected month is not the current month..
            int nextMonth, nextYear;

            nextMonth = month + 1;
            nextYear = year;


            if (nextMonth == 13) {
                nextMonth = 1;
                nextYear = year + 1;
            }
            writer.print("<a style='float:left' class='fa fa-chevron-left button-green' href='/parent-panel/index?national-id=" + nationalId + "&month=" + nextMonth + "&year=" + nextYear + "'></a>");
        }
        int prevMonth, prevYear;
        prevMonth = month - 1;
        prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        writer.print("<a style='float:right;' class='fa fa-chevron-right button-green' href='/parent-panel/index?national-id=" + nationalId + "&month=" + prevMonth + "&year=" + prevYear + "'></a>");

        writer.print("        <input type='hidden' name='national-id' value='" + nationalId + "' />" +
                "        <input type='submit' class='button-primary' value='عرض' />" +
                "      </form><br><br>");


        ViewUtils.printStudentRecitationsTable(writer, nationalId, month, year, false);
        writer.print("</center>");

    }


}
