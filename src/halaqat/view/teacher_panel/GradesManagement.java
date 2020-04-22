package halaqat.view.teacher_panel;

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
import java.sql.SQLException;
import java.util.List;

public class GradesManagement extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TeacherPanelViewUtils.printHeaderWithMenu(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/teacher-panel/index");
            return;
        }

        try {
            switch (action) {
                case "save":
                    saveGrades(request, response);
                    break;
                default:
                    response.sendRedirect("/teacher-panel/index");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        TeacherPanelViewUtils.printHeaderWithMenu(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/teacher-panel/grades?action=edit");
            return;
        }
        try {
            switch (action) {
                case "edit":
                    printEditGradesPage(request, response);
                    break;

                case "previous":
                    printStudentsGrades(request, response);
                    break;

                default:
                    response.sendRedirect("/teacher-panel/grades?action=edit");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());
    }

    private void printEditGradesPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        String teacherId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);

        SemesterGradesDao semesterGradesDao = new SemesterGradesDatabaseAccess();
        MonthlyExamDao monthlyExamDao = new MonthlyExamDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester currentSemester = semesterDao.getLast();

        if (currentSemester == null) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إضافة فصل دراسي!"));
            return;
        }

        List<halaqat.data.data_structures.Pair<Student, SemesterGrades>> studentsGrades = semesterGradesDao.halaqaGrades(currentSemester.getId(), halaqaDao.byTeacher(teacherId).getname());

        String message = request.getParameter("message");
        if (message != null && message.equals("saved"))
            writer.print(ViewUtils.formatSuccessMessage("تم الحفظ بنجاح") + "<br><br>");

        writer.print("<a href='/teacher-panel/grades?action=previous' id='previous-grades' class='button-green'>عرض الدرجات السابقة</a>\n");

        writer.print("<br><br>");
        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br><br>");
        writer.print("  <div id='section-to-print'>");


        writer.print("    <form action='/teacher-panel/grades' method='post' name='students-grades'>\n" +
                "\n" +
                "        <center><table id='students-grades-table'>\n" +
                "            <caption>درجات الفصل الحالي</caption>\n" +
                "            <thead>\n" +
                "            <tr>\n" +
                "                <th>اسم الطالب</th>\n" +
                "                <th>الشهري الأول</th>\n" +
                "                <th>الشهري الثاني</th>\n" +
                "                <th>المواظبة</th>\n" +
                "                <th>السلوك</th>\n" +
                "                <th>الإختبار النهائي</th>\n" +
                "                <th>المجموع</th>\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "            <tbody>");


        Student s;
        SemesterGrades sg;
        MonthlyExam exam1, exam2;
        int sum;
        for (Pair<Student, SemesterGrades> studentGrades : studentsGrades) {
            s = studentGrades.getFirst();
            sg = studentGrades.getSecond();

            exam1 = monthlyExamDao.get(currentSemester.getId(), s.getNationalID(), 1);
            exam2 = monthlyExamDao.get(currentSemester.getId(), s.getNationalID(), 2);


            writer.print("            <tr>\n" +
                    "                <td>" + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</td>\n" +
                    "                <td><input name='first-monthly-" + s.getNationalID() + "' class='text-input grade-input' "
                    + (exam1 != null ? "value='" + exam1.getGrade() + "'" : "") + " type='number' max='20' min='0'/></td>\n" +

                    "                <td><input name='second-monthly-" + s.getNationalID() + "' class='text-input grade-input' " +
                    (exam2 != null ? "value='" + exam2.getGrade() + "'" : "") + " type='number' max='20' min='0'/></td>\n" +
                    "                <td><input name='attendance-" + s.getNationalID() + "' class='text-input grade-input' " +
                    (sg != null && sg.getAttendance() != -1 ? "value='" + sg.getAttendance() + "'" : "") + " type='number' max='10' min='0'/></td>\n" + // -1 is the value of empty grades
                    "                <td><input name='behaviour-" + s.getNationalID() + "' class='text-input grade-input' " +
                    (sg != null && sg.getBehavior() != -1 ? "value='" + sg.getBehavior() + "'" : "") + " type='number' max='10' min='0'/></td>\n" +
                    "                <td><input name='final-" + s.getNationalID() + "' class='text-input grade-input' " +
                    (sg != null && sg.getFinalG() != -1 ? "value='" + sg.getFinalG() + "'" : "") + " type='number' max='40' min='0'/></td>\n" +
                    "\n");
            sum = 0;
            if (exam1 != null)
                sum += exam1.getGrade();
            if (exam2 != null)
                sum += exam2.getGrade();
            if (sg != null && sg.getAttendance() != -1)
                sum += sg.getAttendance();
            if (sg != null && sg.getBehavior() != -1)
                sum += sg.getBehavior();
            if (sg != null && sg.getFinalG() != -1)
                sum += sg.getFinalG();

            writer.print("                <td>" + (sum != 0 ? sum : "") + "</td>\n" +
                    "            </tr>\n");
        }

        writer.print("            </tbody>\n" +
                "        </table></center>\n" +
                "        <input type='hidden' name='action' value='save' />" +
                "        <input type='submit' id='save-grades' value='حفظ' class='not-print button-primary' />\n" +
                "\n" +
                "    </form>\n");
        writer.print("  </div>");
    }


    private void printStudentsGrades(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        String teacherId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);

        SemesterGradesDao semesterGradesDao = new SemesterGradesDatabaseAccess();
        MonthlyExamDao monthlyExamDao = new MonthlyExamDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        SemesterDao semesterDao = new SemesterDatabaseAccess();


        List<Semester> semesters = semesterDao.getAll();


        int semesterId;
        try {
            semesterId = Integer.parseInt(request.getParameter("semester-id"));
        } catch (NumberFormatException ex) {
            if (semesters.size() < 2) {
                writer.print(ViewUtils.formatInfoMessage("لا يوجد فصول سابقة لعرض درجاتها!"));
                return;
            }
            semesterId = semesters.get(1).getId(); // If no semester-id sent or invalid id, Show the previous semester..
        }

        writer.print("<h3 id='section-title'>عرض الدرجات السابقة</h3>\n");

        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br>");
        writer.print("  <div id='section-to-print'>");

        writer.print("   <form action='/teacher-panel/grades' method='get' id='grades-selection-form'>\n" +
                "        <label for='semester-selection'>الفصل الدراسي:</label>\n" +
                "        <select name='semester-id' id='semester-selection' class='select-input'>\n");
        Semester semester;
        for (int i = 1; i < semesters.size(); i++) {
            semester = semesters.get(i);
            writer.print("            <option value=" + semester.getId() + (semester.getId() == semesterId ? " selected" : "") + ">" + semester.getName() + "</option>\n");
        }


        writer.print("        </select>\n" +
                "        <input type='hidden' name='action' value='previous' />" +
                "        <input type='submit' value='عرض' class='not-print button-primary' />\n" +
                "    </form><br><br>\n");


        List<Pair<Student, SemesterGrades>> studentsGrades = semesterGradesDao.halaqaGrades(semesterId, halaqaDao.byTeacher(teacherId).getname());
        writer.print(

                "        <center><table id='students-grades-table'>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th>اسم الطالب</th>\n" +
                        "                <th>الشهري الأول</th>\n" +
                        "                <th>الشهري الثاني</th>\n" +
                        "                <th>المواظبة</th>\n" +
                        "                <th>السلوك</th>\n" +
                        "                <th>الإختبار النهائي</th>\n" +
                        "                <th>المجموع</th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "            <tbody>");


        Student s;
        SemesterGrades sg;
        MonthlyExam exam1, exam2;
        int sum;
        for (Pair<Student, SemesterGrades> studentGrades : studentsGrades) {
            s = studentGrades.getFirst();
            sg = studentGrades.getSecond();

            exam1 = monthlyExamDao.get(semesterId, s.getNationalID(), 1);
            exam2 = monthlyExamDao.get(semesterId, s.getNationalID(), 2);


            writer.print("            <tr>\n" +
                    "                <td>" + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</td>\n" +
                    "                <td>" + (exam1 != null ? exam1.getGrade() : "") + "</td>\n" +
                    "                <td>" + (exam2 != null ? exam2.getGrade() : "") + "</td>\n" +
                    "                <td>" + (sg != null && sg.getAttendance() != -1 ? +sg.getAttendance() : "") + "</td>\n" +
                    "                <td>" + (sg != null && sg.getBehavior() != -1 ? +sg.getBehavior() : "") + "</td>\n" +
                    "                <td>" + (sg != null && sg.getFinalG() != -1 ? +sg.getFinalG() : "") + "</td>\n" +
                    "\n");
            sum = 0;
            if (exam1 != null)
                sum += exam1.getGrade();
            if (exam2 != null)
                sum += exam2.getGrade();
            if (sg != null && sg.getAttendance() != -1)
                sum += sg.getAttendance();
            if (sg != null && sg.getBehavior() != -1)
                sum += sg.getBehavior();
            if (sg != null && sg.getFinalG() != -1)
                sum += sg.getFinalG();
            writer.print("                <td>" + (sum != 0 ? sum : "") + "</td>\n" +
                    "            </tr>\n");
        }

        writer.print("            </tbody>\n" +
                "        </table></center>\n");
        writer.print("</div>");

    }

    private void saveGrades(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        StudentDao studentDao = new StudentDatabaseAccess();
        SemesterGradesDao semesterGradesDao = new SemesterGradesDatabaseAccess();
        MonthlyExamDao monthlyExamDao = new MonthlyExamDatabaseAccess();

        List<Student> students = studentDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        Semester currentSemester = new SemesterDatabaseAccess().getLast();

        if (currentSemester == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إضافة فصل دراسي!"));
            return;
        }

        for (Student s :
                students) {
            String firstMonthly = request.getParameter("first-monthly-" + s.getNationalID());
            String secondMonthly = request.getParameter("second-monthly-" + s.getNationalID());

            String attendance = request.getParameter("attendance-" + s.getNationalID());
            String behaviour = request.getParameter("behaviour-" + s.getNationalID());

            String finalG = request.getParameter("final-" + s.getNationalID());

            int attendanceGrade = 0, behaviourGrade = 0, finalGrade = 0;
            if (attendance != null && attendance.length() > 0) {
                try {
                    attendanceGrade = Integer.parseInt(attendance);
                } catch (NumberFormatException e) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة حضور غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
                if (attendanceGrade < 0 || attendanceGrade > 10) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة حضور غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
            } else
                attendanceGrade = -1;

            if (behaviour != null && behaviour.length() > 0) {
                try {
                    behaviourGrade = Integer.parseInt(behaviour);
                } catch (NumberFormatException e) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة سلوك غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
                if (behaviourGrade < 0 || attendanceGrade > 10) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة سلوك غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
            } else
                behaviourGrade = -1;

            if (finalG != null && finalG.length() > 0) {

                try {
                    finalGrade = Integer.parseInt(finalG);
                } catch (NumberFormatException e) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة اختبار نهائي غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
                if (finalGrade < 0 || finalGrade > 10) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة اختبار نهائي غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                }
            } else
                finalGrade = -1;

            SemesterGrades grades = new SemesterGrades(currentSemester.getId(), finalGrade, attendanceGrade, behaviourGrade, s.getNationalID());
            if (semesterGradesDao.get(currentSemester.getId(), s.getNationalID()) == null) {
                semesterGradesDao.insert(grades);
            } else {
                semesterGradesDao.update(grades);
            }

            if (firstMonthly != null && firstMonthly.length() > 0) {
                int fMonthly = 0;
                try {
                    fMonthly = Integer.parseInt(firstMonthly);
                } catch (NumberFormatException e) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة الاختبار الشهري الأول للطالب " + s.getFirstName() + " " + s.getLastName()) + " غير صالحة.");
                }
                if (fMonthly < 0 || fMonthly > 20) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة الاختبار الشهري الأول للطالب " + s.getFirstName() + " " + s.getLastName()) + " غير صالحة.");
                } else {
                    MonthlyExam monthlyExam = new MonthlyExam(currentSemester.getId(), s.getNationalID(), 1, fMonthly);
                    if (monthlyExamDao.get(currentSemester.getId(), s.getNationalID(), 1) == null) {
                        monthlyExamDao.insert(monthlyExam);
                    } else {
                        monthlyExamDao.update(monthlyExam);
                    }
                }
            }
            if (secondMonthly != null && secondMonthly.length() > 0) {
                int sMonthly = 0;
                try {
                    sMonthly = Integer.parseInt(secondMonthly);
                } catch (NumberFormatException e) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة الاختبار الشهري الأول للطالب " + s.getFirstName() + " " + s.getLastName()) + " غير صالحة.");
                }
                if (sMonthly < 0 || sMonthly > 20) {
                    response.getWriter().print(ViewUtils.formatErrorMessage("درجة الاختبار الشهري الثاني للطالب " + s.getFirstName() + " " + s.getLastName()) + " غير صالحة.");
                } else {
                    MonthlyExam monthlyExam = new MonthlyExam(currentSemester.getId(), s.getNationalID(), 2, sMonthly);
                    if (monthlyExamDao.get(currentSemester.getId(), s.getNationalID(), 2) == null) {
                        monthlyExamDao.insert(monthlyExam);
                    } else {
                        monthlyExamDao.update(monthlyExam);
                    }
                }


            }
        }
        response.sendRedirect("/teacher-panel/grades?action=edit&message=saved");

    }
}
