package halaqat.view.student_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.*;
import halaqat.data.dao.imp.*;
import halaqat.data.data_structures.Pair;
import halaqat.data.pojos.*;
import halaqat.view.ViewUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentPanel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        printHeader(request, response);
        String action = request.getParameter("action");
        try {
            if (action != null && action.equals("grades")) {
                printGrades(request, response);
            } else {
                printRecitationsTable(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع"));
        }

        printFooter(response.getWriter());

    }

    private void printGrades(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {

        PrintWriter writer = response.getWriter();

        SemesterDao semesterDao = new SemesterDatabaseAccess();


        List<Semester> semesters = semesterDao.getAll();


        int semesterId;
        try {
            semesterId = Integer.parseInt(request.getParameter("semester-id"));
        } catch (NumberFormatException ex) {
            if (semesters.size() == 0) {
                writer.print(ViewUtils.formatInfoMessage("لا يوجد فصول لعرض درجاتها!"));
                return;
            }
            semesterId = semesters.get(0).getId(); // If no semester-id sent or invalid id, Show the previous semester..
        }
        writer.print("   <form action='/student' method='get' id='grades-selection-form'>\n" +
                "        <label for='semester-selection'>الفصل الدراسي:</label>\n" +
                "        <select name='semester-id' id='semester-selection' class='select-input'>\n");
        for (Semester semester : semesters) {
            writer.print("            <option value=" + semester.getId() + (semester.getId() == semesterId ? " selected" : "") + ">" + semester.getName() + "</option>\n");
        }


        writer.print("        </select>\n" +
                "        <input type='hidden' name='action' value='grades' />" +
                "        <input type='submit' value='عرض' class='button-primary' />\n" +
                "    </form><br><br>\n");


        writer.print(

                "        <center><table id='students-grades-table'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th>الشهري الأول</th>\n" +
                        "                <th>الشهري الثاني</th>\n" +
                        "                <th>المواظبة</th>\n" +
                        "                <th>السلوك</th>\n" +
                        "                <th>الإختبار النهائي</th>\n" +
                        "                <th>المجموع</th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>");

        SemesterGradesDao semesterGradesDao = new SemesterGradesDatabaseAccess();
        MonthlyExamDao monthlyExamDao = new MonthlyExamDatabaseAccess();

        String nationalId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);
        SemesterGrades sg = semesterGradesDao.get(semesterId, nationalId);
        if (sg == null) {
            writer.print("<tr><td class='disabled-cell' colspan='6'>لا يوجد درجات لعرضها</td><tr>" +
                    "</tbody>\n" +
                    "</table></center>\n");
            return;
        }
        MonthlyExam exam1, exam2;
        int sum = 0;
        exam1 = monthlyExamDao.get(semesterId, nationalId, 1);
        exam2 = monthlyExamDao.get(semesterId, nationalId, 2);


        writer.print("            <tr>\n" +
                "                <td>" + (exam1 != null ? exam1.getGrade() : "") + "</td>\n" +
                "                <td>" + (exam2 != null ? exam2.getGrade() : "") + "</td>\n" +
                "                <td>" + (sg.getAttendance() != -1 ? +sg.getAttendance() : "") + "</td>\n" +
                "                <td>" + (sg.getBehavior() != -1 ? +sg.getBehavior() : "") + "</td>\n" +
                "                <td>" + (sg.getFinalG() != -1 ? +sg.getFinalG() : "") + "</td>\n" +
                "\n");
        if (exam1 != null)
            sum += exam1.getGrade();
        if (exam2 != null)
            sum += exam2.getGrade();
        if (sg.getAttendance() != -1)
            sum += sg.getAttendance();
        if (sg.getBehavior() != -1)
            sum += sg.getBehavior();
        if (sg.getFinalG() != -1)
            sum += sg.getFinalG();
        writer.print("                <td>" + sum + "</td>\n" +
                "            </tr>\n");


        writer.print("            </tbody>\n" +
                "        </table></center>\n");


    }


    private void printRecitationsTable(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException, ParseException {
        PrintWriter writer = response.getWriter();

        String nationalId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);

        writer.print("<center>");

        Date today = new Date();
        String yearParam = request.getParameter("year");
        int year;
        if (yearParam == null || yearParam.length() == 0) {
            year = today.getYear() + 1900; // getYear() returns years after 1900
        } else {
            year = Integer.parseInt(yearParam);
        }

        String monthParam = request.getParameter("month");
        int month;
        if (monthParam == null || monthParam.length() == 0) {
            month = today.getMonth() + 1; // getMonth range is 0-11
        } else {
            month = Integer.parseInt(monthParam);
        }


        RecitationDao recitationDao = new RecitationDatabaseAccess();
        List<Integer> years = recitationDao.distinctYears(nationalId);
        writer.print("      <form id='recitation-options' action='' method='get'>\n");
        Calendar calendar = Calendar.getInstance();
        if (!(calendar.get(Calendar.MONTH) == month - 1 && calendar.get(Calendar.YEAR) == year)) { // Print next only if the selected month is not the current month..
            int nextMonth, nextYear;

            nextMonth = month + 1;
            nextYear = year;


            if (nextMonth == 13) {
                nextMonth = 1;
                nextYear = year + 1;
            }
            writer.print("<a style='float:left' class='fa fa-chevron-left button-green' href='/student?month=" + nextMonth + "&year=" + nextYear + "'></a>");
        }
        int prevMonth, prevYear;
        prevMonth = month - 1;
        prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        writer.print("<a style='float:right;' class='fa fa-chevron-right button-green' href='/student?month=" + prevMonth + "&year=" + prevYear + "'></a>");

        writer.print("        <span>العام :</span>\n" +
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

        writer.print("        </select>\n" +
                "        <input type='submit' class='button-primary' value='عرض' />" +
                "      </form><br><br>");

        ViewUtils.printStudentRecitationsTable(writer, nationalId, month, year, false);
        writer.print("</center>");
    }


    private void printHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        String studentName = request.getSession().getAttribute(AppConstants.FNAME_KEY) + " "
                + request.getSession().getAttribute(AppConstants.LNAME_KEY);

        response.getWriter().print("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\n" +
                "    <meta charset='utf-8'>\n" +
                "    <title> حساب الطالب </title>\n" +
                "    <link rel='stylesheet' href='/css/student-panel.css'>\n" +
                "    <link rel='stylesheet' href='/css/font-awesome.min.css'>\n" +
                "    <link rel='stylesheet' href='/css/style.css'>\n" +
                "    <link href='https://fonts.googleapis.com/css?family=Cairo:600' rel='stylesheet'>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id='header-container'>\n" +
                "<div id='header-content'>" +
                "    <h1>نظام رتِّل لإدارة الحلقات</h1>\n" +
                "\n" +
                "  <div id='user'>\n" +
                "      <div class='fa fa-user' id='user-name'><span>" + studentName + "</span></div>\n" +
                "      <div id='user-options'>\n" +
                "        <a href='/user?action=change-password'>تغيير كلمة المرور</a>\n" +
                "        <a href='/user?action=logout'>تسجيل الخروج</a>\n" +
                "      </div>\n" +
                "  </div>" +
                "</div>\n" +
                "<div class='navbar-container'>\n" +
                "    <div class='navbar'>\n" +
                "\n" +
                "        <ul>\n" +
                "            <li>\n" +
                "\n" +
                "                <a href='/student'>التسميع</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "\n" +
                "                <a href='/student?action=grades'>الدرجات</a>\n" +
                "\n" +
                "            </li>\n" +
                "\n" +
                "        </ul>\n" +
                "\n" +
                "    </div>\n" +
                "    <br>\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "<div id='content-container'>");

    }

    public static void printFooter(PrintWriter printWriter) {
        printWriter.write("</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}
