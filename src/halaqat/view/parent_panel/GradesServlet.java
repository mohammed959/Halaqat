/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package halaqat.view.parent_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.MonthlyExamDao;
import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.dao.abs.SemesterGradesDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.*;
import halaqat.data.data_structures.Pair;
import halaqat.data.pojos.MonthlyExam;
import halaqat.data.pojos.Semester;
import halaqat.data.pojos.SemesterGrades;
import halaqat.data.pojos.Student;
import halaqat.view.ViewUtils;
import halaqat.view.teacher_panel.TeacherPanelViewUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mohammed
 */
public class GradesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ParentPanelViewUtils.printHeaderWithMenu(request, response);


        try {
            printGrades(request, response);
        } catch (Exception e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
            e.printStackTrace();
        }

        ParentPanelViewUtils.printFooter(response.getWriter());
    }

    private void printGrades(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {

        PrintWriter writer = response.getWriter();
        String parentId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);

        SemesterGradesDao semesterGradesDao = new SemesterGradesDatabaseAccess();
        MonthlyExamDao monthlyExamDao = new MonthlyExamDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester currentSemester = semesterDao.getLast();

        if (currentSemester == null) {
            writer.print(ViewUtils.formatInfoMessage("لاتوجد أي درجات !"));
            return;
        }

        List<Semester> semesters = semesterDao.getAll();

        int semesterId;
        try {
            semesterId = Integer.parseInt(request.getParameter("semester-id"));
        } catch (NumberFormatException ex) {
            semesterId = currentSemester.getId();
        }

        writer.print("   <form action='/parent-panel/grades' method='get' id='grades-selection-form'>\n"
                + "        <label for='semester-selection'>الفصل الدراسي:</label>\n"
                + "        <select name='semester-id' id='semester-selection' class='select-input'>\n");
        Semester semester;
        for (int i = 1; i < semesters.size(); i++) {
            semester = semesters.get(i);
            writer.print("            <option value=" + semester.getId() + (semester.getId() == semesterId ? " selected" : "") + ">" + semester.getName() + "</option>\n");
        }

        writer.print("        </select>\n"
                + "        <input type='submit' value='عرض' class='button-primary' />\n"
                + "    </form><br>\n");

        List<Pair<Student, SemesterGrades>> studentsGrades = semesterGradesDao.dependentsGrades(currentSemester.getId(), parentId);

        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br><br>");
        writer.print("  <div id='section-to-print'>");

        writer.print(
                "        <center><table id='students-grades-table'>\n"
                        + "            <thead>\n"
                        + "            <tr>\n"
                        + "                <th>اسم الطالب</th>\n"
                        + "                <th>الشهري الأول</th>\n"
                        + "                <th>الشهري الثاني</th>\n"
                        + "                <th>المواظبة</th>\n"
                        + "                <th>السلوك</th>\n"
                        + "                <th>الإختبار النهائي</th>\n"
                        + "                <th>المجموع</th>\n"
                        + "            </tr>\n"
                        + "            </thead>\n"
                        + "            <tbody>");

        Student s;
        SemesterGrades sg;
        MonthlyExam exam1, exam2;
        int sum;
        for (Pair<Student, SemesterGrades> studentGrades : studentsGrades) {
            s = studentGrades.getFirst();
            sg = studentGrades.getSecond();

            exam1 = monthlyExamDao.get(semesterId, s.getNationalID(), 1);
            exam2 = monthlyExamDao.get(semesterId, s.getNationalID(), 2);

            writer.print("            <tr>\n"
                    + "                <td>" + s.getFirstName() + " " + s.getMiddleName() + "</td>\n"
                    + "                <td>" + (exam1 != null ? exam1.getGrade() : "") + "</td>\n"
                    + "                <td>" + (exam2 != null ? exam2.getGrade() : "") + "</td>\n"
                    + "                <td>" + (sg != null && sg.getAttendance() != -1 ? +sg.getAttendance() : "") + "</td>\n"
                    + "                <td>" + (sg != null && sg.getBehavior() != -1 ? +sg.getBehavior() : "") + "</td>\n"
                    + "                <td>" + (sg != null && sg.getFinalG() != -1 ? +sg.getFinalG() : "") + "</td>\n"
                    + "\n");
            sum = 0;
            if (exam1 != null) {
                sum += exam1.getGrade();
            }
            if (exam2 != null) {
                sum += exam2.getGrade();
            }
            if (sg != null && sg.getAttendance() != -1) {
                sum += sg.getAttendance();
            }
            if (sg != null && sg.getBehavior() != -1) {
                sum += sg.getBehavior();
            }
            if (sg != null && sg.getFinalG() != -1) {
                sum += sg.getFinalG();
            }
            writer.print("                <td>" + (sum != 0 ? sum : "") + "</td>\n"
                    + "            </tr>\n");
        }

        writer.print("            </tbody>\n"
                + "        </table></center></div>\n");

    }


}
