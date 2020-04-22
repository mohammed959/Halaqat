package halaqat.view.admin_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.EmployeeDao;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.EmployeeDatabaseAccess;
import halaqat.data.dao.imp.HalaqaDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Employee;
import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Student;
import halaqat.view.ViewUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class AdminPanelIndex extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        AdminViewUtils.printHeaderWithMenu(request, response);

        try {
            printStatistics(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع"));
        }

        AdminViewUtils.printFooter(response.getWriter());

    }

    private void printStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter printWriter = response.getWriter();

        EmployeeDao employeeDao = new EmployeeDatabaseAccess();

        HashMap<Integer, Integer> employeesCounts = employeeDao.numberOfEmployees();

        Integer teachers = employeesCounts.get(Employee.TYPE_TEACHER);
        int numberOfTeachers = teachers == null ? 0 : teachers;
        Integer administrators = employeesCounts.get(Employee.TYPE_ADMINISTRATOR);
        int numberOfAdministrators = administrators == null ? 0 : administrators;

        printWriter.print("    <div id=\"greeting\"> أهلاً <span>" + request.getSession().getAttribute(AppConstants.FNAME_KEY) + "</span>..</div>\n" +
                "\n" +
                "    <div id=\"statistics\">\n" +
                "        <span class=\"primary-color\">إحصائيات:</span>\n" +
                "        <br>\n" +
                "        عدد الموظفين: <span>" + (numberOfAdministrators + numberOfTeachers) + "</span> موظف.\n" +
                "        <br>\n" +
                "\n" +
                "        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;منهم ");
        if (numberOfTeachers == 1)
            printWriter.print(" أساتذ واحد" +
                    "        <br>\n");
        else if (numberOfTeachers == 2)
            printWriter.print(" أستاذَين" +
                    "        <br>\n");
        else
            printWriter.print(numberOfTeachers + " أساتذة" +
                    "        <br>\n");


        printWriter.print("        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;و ");
        if (numberOfAdministrators == 1)
            printWriter.print(" إداري واحد" +
                    "        <br>\n");
        else if(numberOfAdministrators == 2)
            printWriter.print(" إداريَّين" +
                    "        <br>\n");
        else
            printWriter.print(numberOfAdministrators + " إداريين" +
                    "        <br>\n");

        printWriter.print("<center><h3>عدد الطلاب حسب الحلقات<h3></center><br>");

        printWriter.print("        <table id=\"halqat-students\">\n" +
                "            <thead>\n" +
                "\n" +
                "            <tr>\n" +
                "                <th>\n" +
                "                    الحلقة\n" +
                "                </th>\n" +
                "                <th>\n" +
                "                    عدد الطلاب\n" +
                "                </th>\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "            <tbody>\n");

        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        List<Halaqa> halaqat = halaqaDao.getAll(false);

        StudentDao studentDao = new StudentDatabaseAccess();
        HashMap<String, Integer> studentsNumbers = studentDao.numberOfStudentsByHalaqat();

        int sum = 0;

        for (Halaqa halaqa : halaqat) {
            Integer numberOfStudents = studentsNumbers.get(halaqa.getname());
            if (numberOfStudents != null)
                sum += numberOfStudents;
            printWriter.print("            <tr>\n" +
                    "                <td>" + halaqa.getname() + "</td>\n" +
                    "                <td>" + (numberOfStudents == null ? 0 : numberOfStudents) + "</td>\n" +
                    "            </tr>\n");

        }
        printWriter.print("</tbody><tfoot>" +
                "<tr>" +
                "<td>العدد الكلي:</td>" +
                "<td>" + sum + "</td>" +
                "</tr>" +
                "</tfoot></table>");

    }
}
