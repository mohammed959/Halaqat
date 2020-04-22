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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RecitationsManagement extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TeacherPanelViewUtils.printHeaderWithMenu(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/teacher-panel/index?action=today");
            return;
        }

        try {
            switch (action) {
                case "save":
                    saveTodayRecitations(request, response);
                    break;

                case "edit":
                    editRecitation(request, response);
                    break;
                case "delete":
                    deleteRecitation(request, response);
                    break;
                default:
                    response.sendRedirect("/teacher-panel/index?action=today");
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
            response.sendRedirect("/teacher-panel/index?action=today");
            return;
        }
        try {
            switch (action) {
                case "today":
                    printTodayRecitationsPage(request, response);
                    break;

                case "student":
                    printStudentRecitations(request, response);
                    break;
                case "edit":
                    printEditForm(request, response);
                    break;
                case "delete":
                    printDeleteConfirmation(request, response);
                    break;

                default:
                    response.sendRedirect("/teacher-panel/index?action=today");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());
    }

    private void printDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        writer.print("<h3 id='section-title'>حذف تسميع طالب</h3>\n");

        Pair<Student, Recitation> validatedData = validateRecitation(request, writer);
        if (validatedData == null)
            return;

        Student student = validatedData.getFirst();
        String dateParam = validatedData.getSecond().getDate();
        int type = validatedData.getSecond().gettype();

        RecitationDao recitationDao = new RecitationDatabaseAccess();

        Recitation recitation = recitationDao.studentRecitation(student.getNationalID(), type, dateParam);
        if (recitation == null) {
            writer.print(ViewUtils.formatErrorMessage("التسميع غير موجود!"));
            return;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormatter.parse(dateParam);
        SimpleDateFormat displayDateFormatter = new SimpleDateFormat("EEEE dd/MM", new Locale("ar", "sa"));

        writer.print("<center><br><br><h5>هل أنت متأكد أنك تريد حذف التسميع: </h5>\n" +
                "الطالب:" + student.getFirstName() + " (" + (type == Recitation.TYPE_NEW ? "حفظ" : "مراجعة") + ") بيوم : " + displayDateFormatter.format(date) +
                "<form action='/teacher-panel/index' method='post'>\n" +
                "\n" +
                "<input type='hidden' name='national-id' value='" + student.getNationalID() + "' />\n" +
                "<input type='hidden' name='type' value='" + type + "' />\n" +
                "<input type='hidden' name='date' value='" + dateParam + "' />\n" +
                "<input type='hidden' name='action' value='delete' />\n" +
                "\n<br><br>" +
                "<input type='submit' class='button-red' value='نعم' />\n" +
                "<button type='button' class='button-green' onclick='window.history.back()'>لا</button>\n" +
                "\n" +
                "</form></center>");


    }

    private Pair<Student, Recitation> validateRecitation(HttpServletRequest request, PrintWriter writer) throws SQLException, ClassNotFoundException, ParseException {
        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح"));
            return null;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        Student student = studentDao.get(nationalId);

        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));


        if (!student.getHalaqaName().equals(halaqa.getname())) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك صلاحية التعديل على الطالب!"));
            return null;
        }

        String dateParam = request.getParameter("date");
        if (dateParam == null) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return null;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormatter.parse(dateParam);
        } catch (ParseException e) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            writer.print(ViewUtils.formatErrorMessage("التاريخ المختار يوافق إجازة نهاية الأسبوع!"));
            return null;
        }

        SemesterDao semesterDao = new SemesterDatabaseAccess();
        Semester currentSemester = semesterDao.getLast();
        Date semesterStarting = dateFormatter.parse(currentSemester.getStartingDate());

        if (!date.after(semesterStarting)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن التعديل على الفصول الدراسية السابقة!"));
            return null;
        }
        Date today = new Date();

        if (date.after(today)) {
            writer.print(ViewUtils.formatErrorMessage("التاريخ المحدد في المستقبل!"));
            return null;
        }

        String typeParam = request.getParameter("type");
        if (typeParam == null) {
            writer.print(ViewUtils.formatErrorMessage("نوع التسميع غير صالح"));
            return null;
        }
        int type;
        try {
            type = Integer.parseInt(typeParam);
        } catch (NumberFormatException e) {
            writer.print(ViewUtils.formatErrorMessage("نوع التسميع غير صالح"));
            return null;
        }
        if (type != Recitation.TYPE_NEW && type != Recitation.TYPE_REVISION) {
            writer.print(ViewUtils.formatErrorMessage("نوع التسميع غير صالح"));
            return null;
        }

        return new Pair<>(student, new Recitation(0, dateParam, type, 0, 0, null, null, nationalId));
    }

    private void printEditForm(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {
        PrintWriter writer = response.getWriter();

        Pair<Student, Recitation> validatedData = validateRecitation(request, writer);
        if (validatedData == null)
            return;

        Student student = validatedData.getFirst();
        String dateParam = validatedData.getSecond().getDate();
        int type = validatedData.getSecond().gettype();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormatter.parse(dateParam);


        SimpleDateFormat displayDateFormatter = new SimpleDateFormat("EEEE dd/MM", new Locale("ar", "sa"));

        writer.print("<h3 id='section-title'>تعديل تسميع طالب</h3>\n" +
                "  <span>الطالب:</span>\n" +
                "  <span>" + student.getFirstName() + "</span>\n" +
                "  <br><br>\n");
        writer.print("     <form method='post' action='/teacher-panel/index'>");

        writer.print("        <table id='recitations-edit-table'>\n" +
                "            <thead>\n" +
                "            <tr>\n" +
                "\n" +
                "                <th rowspan='2'> اليوم</th>\n" +
                "                <th rowspan='2'> النوع</th>\n" +
                "                <th colspan='2'> من</th>\n" +
                "                <th colspan='2'> إلى</th>\n" +
                "                <th rowspan='2'> التقدير</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "\n" +
                "            <tbody><tr>");


        RecitationDao recitationDao = new RecitationDatabaseAccess();

        Recitation recitation = recitationDao.studentRecitation(student.getNationalID(), type, dateParam);

        writer.print("                  <td>" + displayDateFormatter.format(date) + "</td>\n");
        writer.print("                  <td>" + (type == Recitation.TYPE_NEW ? "حفظ" : "مراجعة") + "</td>\n");
        writer.print("                  <td><input type='text' class='text-input' value='" + (recitation != null ? recitation.getstartSorah() : "") + "' name='start-sorah' /></td>\n");
        writer.print("                  <td><input type='text' class='text-input' value='" + (recitation != null ? (recitation.getstartAyah() == -1 ? "هـ" : recitation.getstartAyah()) : "") + "' name='start-ayah' /></td>\n");
        writer.print("                  <td><input type='text' class='text-input' value='" + (recitation != null ? recitation.getendSorah() : "") + "' name='end-sorah' /></td>\n");
        writer.print("                  <td><input type='text' class='text-input' value='" + (recitation != null ? (recitation.getendAyah() == -1 ? "هـ" : recitation.getendAyah()) : "") + "' name='end-ayah' /></td>\n");


        writer.print("<td>\n" +
                "                    <select class='select-input' name='grade' >\n" +
                "                        <option value=-1 " + (recitation != null && recitation.getgrade() == -1 ? "selected" : "") + ">-</option>\n" +
                "                        <option value=" + Recitation.GRADE_EXCELLENT + (recitation != null && recitation.getgrade() == Recitation.GRADE_EXCELLENT ? " selected" : "") + "> ممتاز</option>\n" +
                "                        <option value=" + Recitation.GRADE_VERY_GOOD + (recitation != null && recitation.getgrade() == Recitation.GRADE_VERY_GOOD ? " selected" : "") + "> جيد جدا</option>\n" +
                "                        <option value=" + Recitation.GRADE_GOOD + (recitation != null && recitation.getgrade() == Recitation.GRADE_GOOD ? " selected" : "") + ">جيد</option>\n" +
                "                        <option value=" + Recitation.GRADE_NOT_MEMORIZED + (recitation != null && recitation.getgrade() == Recitation.GRADE_NOT_MEMORIZED ? " selected" : "") + "> لم يحفظ</option>\n" +
                "                    </select>\n" +
                "                </td>");
        writer.print("          </tr></tbody></table>");

        writer.print("        <input type='hidden' name='action' value='edit' />" +
                "        <input type='hidden' name='national-id' value='" + student.getNationalID() + "' />" +
                "        <input type='hidden' name='date' value='" + dateParam + "' />" +
                "        <input type='hidden' name='type' value='" + type + "' />" +
                "        <br><br><center><input class='button-primary save' type='submit' value='حفظ'/></center></form>");


    }

    private void printStudentRecitations(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {
        PrintWriter writer = response.getWriter();
        String teacherId = (String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY);
        StudentDao studentDao = new StudentDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();


        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }

        // Allow only the student's teacher to view his recitations.
        Halaqa halaqa = halaqaDao.get(studentDao.get(nationalId).getHalaqaName(), false);
        if (!halaqa.getTeacherId().equals(teacherId)) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك الصلاحية لعرض تسميع الطالب!"));
            return;
        }

        List<Student> students = studentDao.byTeacher(teacherId);
        writer.print("      <form action='/teacher-panel/index' id='student-selection-form' method='get'>\n" +
                "        الطالب :\n" +
                "        <select onchange=\"document.getElementById('student-selection-form').submit()\" name='national-id' class='select-input'>\n");

        for (Student s : students) {
            writer.print("            <option " + (nationalId.equals(s.getNationalID()) ? "selected" : "") + " value='" + s.getNationalID() + "'>" + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</option>\n");
        }

        writer.print("        </select>\n" +
                "<input type='hidden' name='action' value='student' />" +
                "</form><br>");


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
        writer.print("      <form id='recitation-options' action='/teacher-panel/index' method='get'>\n");
        Calendar calendar = Calendar.getInstance();
        if (!(calendar.get(Calendar.MONTH) == month - 1 && calendar.get(Calendar.YEAR) == year)) { // Print next only if the selected month is not the current month..
            int nextMonth, nextYear;

            nextMonth = month + 1;
            nextYear = year;


            if (nextMonth == 13) {
                nextMonth = 1;
                nextYear = year + 1;
            }
            writer.print("<a style='float:left' class='fa fa-chevron-left button-green' href='/teacher-panel/index?action=student&national-id=" + nationalId + "&month=" + nextMonth + "&year=" + nextYear + "'></a>");
        }
        int prevMonth, prevYear;
        prevMonth = month - 1;
        prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        writer.print("<a style='float:right; clear=both' class='fa fa-chevron-right button-green' href='/teacher-panel/index?action=student&national-id=" + nationalId + "&month=" + prevMonth + "&year=" + prevYear + "'></a>");

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
                "        <input type='hidden' name='action' value='student' />" + // Keep the action parameter
                "        <input type='hidden' name='national-id' value='" + nationalId + "' />" +
                "        <input type='submit' class='button-primary' value='عرض' />" +
                "      </form><br><br>");

        ViewUtils.printStudentRecitationsTable(writer, nationalId, month, year, true);
        writer.print("</center>");

    }


    private void printTodayRecitationsPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        StudentDao studentDao = new StudentDatabaseAccess();
        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        List<Student> students = studentDao.byHalaqa(halaqa.getname());

        writer.print("    <h3 id='section-title'> حلقة " + halaqa.getname() + "</h3><br><br>\n");

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            writer.print(ViewUtils.formatInfoMessage("إجازة نهاية الأسبوع!"));
            if (students.size() > 0)
                writer.print("<a class='button-primary' href='/teacher-panel/index?action=student&national-id=" + students.get(0).getNationalID() + "'>سجل التسميع</a>");
            return;
        }


        if (students.size() == 0) {
            writer.print(ViewUtils.formatInfoMessage("لا يوجد طلاب بهذه الحلقة!"));
            return;
        }
        // Check if already submitted and saved successfully (Using success message parameter)
        String message = request.getParameter("message");
        if (message != null && message.equals("saved"))
            writer.print(ViewUtils.formatSuccessMessage("تم الحفظ بنجاح") + "<br><br><br>");

        RecitationDao recitationDao = new RecitationDatabaseAccess();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Date());

        writer.print("    <form method='post' action='/teacher-panel/index' id='halaqa-students-form'>\n" +
                "        <table id='recitations-edit-table'>\n" +
                "            <thead>\n" +
                "            <tr>\n" +
                "\n" +
                "                <th rowspan='2'> الطالب</th>\n" +
                "                <th rowspan='2'> النوع</th>\n" +
                "                <th colspan='2'> من</th>\n" +
                "                <th colspan='2'> إلى</th>\n" +
                "                <th rowspan='2'> التقدير</th>\n" +
                "                <th rowspan='2'> الحضور</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "\n" +
                "            <tbody>\n");


        Recitation newRecitation, revision;
        for (Student s :
                students) {
            writer.print("  <tr>\n" +
                    "                <td rowspan='2'><a href='/teacher-panel/index?action=student&national-id=" + s.getNationalID() + "'> " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</a></td>\n" +
                    "                <td>حفظ</td>\n");

            newRecitation = recitationDao.studentRecitation(s.getNationalID(), Recitation.TYPE_NEW, today);
            revision = recitationDao.studentRecitation(s.getNationalID(), Recitation.TYPE_REVISION, today);

            writer.print("                  <td><input type='text' class='text-input' value='" + (newRecitation != null ? newRecitation.getstartSorah() : "") + "' name='new-start-sorah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (newRecitation != null ? (newRecitation.getstartAyah() == -1 ? "هـ" : newRecitation.getstartAyah()) : "") + "' name='new-start-ayah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (newRecitation != null ? newRecitation.getendSorah() : "") + "' name='new-end-sorah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (newRecitation != null ? (newRecitation.getendAyah() == -1 ? "هـ" : newRecitation.getendAyah()) : "") + "' name='new-end-ayah-" + s.getNationalID() + "' /></td>\n");


            writer.print("<td>\n" +
                    "                    <select class='select-input' name='new-grade-" + s.getNationalID() + "' >\n" +
                    "                        <option value=-1 " + (newRecitation != null && newRecitation.getgrade() == -1 ? "selected" : "") + ">-</option>\n" +
                    "                        <option value=" + Recitation.GRADE_EXCELLENT + (newRecitation != null && newRecitation.getgrade() == Recitation.GRADE_EXCELLENT ? " selected" : "") + "> ممتاز</option>\n" +
                    "                        <option value=" + Recitation.GRADE_VERY_GOOD + (newRecitation != null && newRecitation.getgrade() == Recitation.GRADE_VERY_GOOD ? " selected" : "") + "> جيد جدا</option>\n" +
                    "                        <option value=" + Recitation.GRADE_GOOD + (newRecitation != null && newRecitation.getgrade() == Recitation.GRADE_GOOD ? " selected" : "") + ">جيد</option>\n" +
                    "                        <option value=" + Recitation.GRADE_NOT_MEMORIZED + (newRecitation != null && newRecitation.getgrade() == Recitation.GRADE_NOT_MEMORIZED ? " selected" : "") + "> لم يحفظ</option>\n" +
                    "                    </select>\n" +
                    "                </td>");


            Attendance attendance = attendanceDao.get(s.getNationalID(), today);
            writer.print("                <td rowspan='2'><input type='checkbox' name='attendance-" + s.getNationalID() + "' " + (attendance != null && attendance.isPresent() ? "checked" : "") + " /></td>" +
                    "</tr>");


            writer.print("<tr>" +
                    "<td>مراجعة</td>");
            writer.print("                  <td><input type='text' class='text-input' value='" + (revision != null ? revision.getstartSorah() : "") + "' name='revision-start-sorah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (revision != null ? (revision.getstartAyah() == -1 ? "هـ" : revision.getstartAyah()) : "") + "' name='revision-start-ayah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (revision != null ? revision.getendSorah() : "") + "' name='revision-end-sorah-" + s.getNationalID() + "' /></td>\n");
            writer.print("                  <td><input type='text' class='text-input' value='" + (revision != null ? (revision.getendAyah() == -1 ? "هـ" : revision.getendAyah()) : "") + "' name='revision-end-ayah-" + s.getNationalID() + "' /></td>\n");

            writer.print("<td>\n" +
                    "                    <select class='select-input' name='revision-grade-" + s.getNationalID() + "' >\n" +
                    "                        <option value=-1 " + (revision != null && revision.getgrade() == -1 ? "selected" : "") + ">-</option>\n" +
                    "                        <option value=" + Recitation.GRADE_EXCELLENT + (revision != null && revision.getgrade() == Recitation.GRADE_EXCELLENT ? " selected" : "") + "> ممتاز</option>\n" +
                    "                        <option value=" + Recitation.GRADE_VERY_GOOD + (revision != null && revision.getgrade() == Recitation.GRADE_VERY_GOOD ? " selected" : "") + "> جيد جدا</option>\n" +
                    "                        <option value=" + Recitation.GRADE_GOOD + (revision != null && revision.getgrade() == Recitation.GRADE_GOOD ? " selected" : "") + ">جيد</option>\n" +
                    "                        <option value=" + Recitation.GRADE_NOT_MEMORIZED + (revision != null && revision.getgrade() == Recitation.GRADE_NOT_MEMORIZED ? " selected" : "") + "> لم يحفظ</option>\n" +
                    "                    </select>\n" +
                    "                </td>" +
                    "               </tr>");


        }

        writer.print("            </tbody>\n" +
                "        </table>");
        writer.print("<input type='hidden' name='action' value='save' />");
        writer.print("<br><p class='blue-color'>*ادخل ه أو هـ للدلالة على نهاية السورة.</p>");
        writer.print("        <input class='button-primary save' type='submit' value='حفظ' name='save'/></form>");


    }

    private void saveTodayRecitations(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            response.getWriter().print(ViewUtils.formatErrorMessage("إجازة نهاية الأسبوع!"));
            return;
        }

        StudentDao studentDao = new StudentDatabaseAccess();
        RecitationDao recitationDao = new RecitationDatabaseAccess();
        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();


        List<Student> students = studentDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date()); // Format the current date.

        Recitation newRecitation, revision; // Allocate temporary objects once instead of allocating it in each iteration
        for (Student s :
                students) {
            String newStartSorah = request.getParameter("new-start-sorah-" + s.getNationalID());
            String newStartAyah = request.getParameter("new-start-ayah-" + s.getNationalID());

            String newEndSorah = request.getParameter("new-end-sorah-" + s.getNationalID());
            String newEndAyah = request.getParameter("new-end-ayah-" + s.getNationalID());

            String newGrade = request.getParameter("new-grade-" + s.getNationalID());

            // Check if the teacher submitted a new recitation for current s (Student)
            if (newStartSorah != null && newStartSorah.length() > 0
                    && newStartAyah != null && newStartAyah.length() > 0
                    && newEndSorah != null && newEndSorah.length() > 0
                    && newEndAyah != null && newEndAyah.length() > 0
                    && newGrade != null && newGrade.length() > 0) {

                int nStartAyah = 0, nEndAyah = 0;
                boolean correctNumbers = true;
                try {
                    if (newStartAyah.equals("ه") || newStartAyah.equals("هـ"))
                        nStartAyah = -1;
                    else {
                        nStartAyah = Integer.parseInt(newStartAyah);
                        if (nStartAyah < 1)
                            correctNumbers = false;
                    }
                    if (newEndAyah.equals("ه") || newEndAyah.equals("هـ"))
                        nEndAyah = -1;
                    else {
                        nEndAyah = Integer.parseInt(newEndAyah);
                        if (nEndAyah < 1)
                            correctNumbers = false;
                    }

                } catch (NumberFormatException e) {
                    correctNumbers = false;
                }


                if (correctNumbers) {
                    newRecitation = new Recitation(Integer.valueOf(newGrade), date, Recitation.TYPE_NEW, nStartAyah, nEndAyah, newStartSorah, newEndSorah, s.getNationalID());

                    // Check if there is exists a new recitation for today date, if so, update it, otherwise create a new (new recitation)
                    if (recitationDao.studentRecitation(s.getNationalID(), Recitation.TYPE_NEW, date) != null)
                        recitationDao.update(newRecitation);
                    else
                        recitationDao.insert(newRecitation);
                }
            }
            String revisionStartSorah = request.getParameter("revision-start-sorah-" + s.getNationalID());
            String revisionStartAyah = request.getParameter("revision-start-ayah-" + s.getNationalID());

            String revisionEndSorah = request.getParameter("revision-end-sorah-" + s.getNationalID());
            String revisionEndAyah = request.getParameter("revision-end-ayah-" + s.getNationalID());

            String revisionGrade = request.getParameter("revision-grade-" + s.getNationalID());

            // Check if the teacher submitted a revision recitation for current s (Student)
            if (revisionStartSorah != null && revisionStartSorah.length() > 0
                    && revisionStartAyah != null && revisionStartAyah.length() > 0
                    && revisionEndSorah != null && revisionEndSorah.length() > 0
                    && revisionEndAyah != null && revisionEndAyah.length() > 0
                    && revisionGrade != null && revisionGrade.length() > 0) {

                int rStartAyah = 0, rEndAyah = 0;
                boolean correctNumbers = true;
                try {
                    if (revisionStartAyah.equals("ه") || revisionStartAyah.equals("هـ"))
                        rStartAyah = -1;
                    else {
                        rStartAyah = Integer.parseInt(revisionStartAyah);
                        if (rStartAyah < 1)
                            correctNumbers = false;
                    }
                    if (revisionEndAyah.equals("ه") || revisionEndAyah.equals("هـ"))
                        rEndAyah = -1;
                    else {
                        rEndAyah = Integer.parseInt(revisionEndAyah);
                        if (rStartAyah < 1)
                            correctNumbers = false;
                    }

                } catch (NumberFormatException e) {
                    correctNumbers = false;
                }

                if (correctNumbers) {
                    revision = new Recitation(Integer.valueOf(revisionGrade), date, Recitation.TYPE_REVISION, rStartAyah, rEndAyah, revisionStartSorah, revisionEndSorah, s.getNationalID());

                    // Check if there is exists a revision recitation for today date, if so, update it, otherwise create a new (revision recitation)
                    if (recitationDao.studentRecitation(s.getNationalID(), Recitation.TYPE_REVISION, date) != null)
                        recitationDao.update(revision);
                    else
                        recitationDao.insert(revision);
                } else {
                    response.getWriter().print(ViewUtils.formatErrorMessage("بيانات تسميع غير صالحة للطالب " + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName()));
                    return;
                }
            }


            // Saving attendance:
            String presentParam = request.getParameter("attendance-" + s.getNationalID());
            boolean present = presentParam != null && presentParam.equals("on");
            Attendance attendance = new Attendance(s.getNationalID(), date, present);
            if (attendanceDao.get(s.getNationalID(), date) == null) {
                attendanceDao.insert(attendance);
            } else {
                attendanceDao.update(attendance);
            }


        }
        response.sendRedirect("/teacher-panel/index?action=today&message=saved");
    }

    private void editRecitation(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {
        PrintWriter writer = response.getWriter();

        Pair<Student, Recitation> validatedData = validateRecitation(request, writer);
        if (validatedData == null)
            return;

        Student student = validatedData.getFirst();
        String dateParam = validatedData.getSecond().getDate();
        int type = validatedData.getSecond().gettype();


        String startSorah = request.getParameter("start-sorah");
        if (startSorah == null || startSorah.length() == 0) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إخال سورة البداية"));
            return;
        }


        String startAyah = request.getParameter("start-ayah");
        if (startAyah == null || startAyah.length() == 0) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إخال آية البداية"));
            return;
        }

        String endSorah = request.getParameter("end-sorah");
        if (endSorah == null || endSorah.length() == 0) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إخال سورة النهاية"));
            return;
        }

        String endAyah = request.getParameter("end-ayah");
        if (endAyah == null || endAyah.length() == 0) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إخال آية النهاية"));
            return;
        }

        String newGrade = request.getParameter("grade");
        if (newGrade == null || newGrade.length() == 0) {
            writer.print(ViewUtils.formatErrorMessage("يرجى اختيار التقدير"));
            return;
        }

        int nStartAyah = 0, nEndAyah = 0;
        boolean correctNumbers = true;
        try {
            if (startAyah.equals("ه") || startAyah.equals("هـ"))
                nStartAyah = -1;
            else {
                nStartAyah = Integer.parseInt(startAyah);
                if (nStartAyah < 1)
                    correctNumbers = false;
            }
            if (endAyah.equals("ه") || endAyah.equals("هـ"))
                nEndAyah = -1;
            else {
                nEndAyah = Integer.parseInt(endAyah);
                if (nEndAyah < 1)
                    correctNumbers = false;
            }

        } catch (NumberFormatException e) {
            correctNumbers = false;
        }


        if (correctNumbers) {
            Recitation recitation = new Recitation(Integer.valueOf(newGrade), dateParam, type,
                    nStartAyah, nEndAyah, startSorah, endSorah, student.getNationalID());
            RecitationDao recitationDao = new RecitationDatabaseAccess();

            // Check if there is exists a new recitation, if so, update it, otherwise create a new (new recitation)
            if (recitationDao.studentRecitation(student.getNationalID(), type, dateParam) != null)
                recitationDao.update(recitation);
            else
                recitationDao.insert(recitation);

            writer.print(ViewUtils.formatSuccessMessage("تم حفظ التغييرات بنجاح"));
            writer.print("<meta http-equiv='Refresh' content='2;url=/teacher-panel/index?action=student&national-id="+student.getNationalID()+"'>");
        } else {
            writer.print(ViewUtils.formatErrorMessage("أحد أرقام الآيات غير صحيح!"));
        }
    }

    private void deleteRecitation(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        Pair<Student, Recitation> validatedData = validateRecitation(request, writer);
        if (validatedData == null)
            return;

        Student student = validatedData.getFirst();
        String dateParam = validatedData.getSecond().getDate();
        int type = validatedData.getSecond().gettype();

        RecitationDao recitationDao = new RecitationDatabaseAccess();

        Recitation recitation = recitationDao.studentRecitation(student.getNationalID(), type, dateParam);
        if (recitation == null) {
            writer.print(ViewUtils.formatErrorMessage("التسميع غير موجود!"));
            return;
        }

        recitationDao.delete(student.getNationalID(), dateParam, type);

        writer.print(ViewUtils.formatSuccessMessage("تم حذف التسميع بنجاح"));
        writer.print("<meta http-equiv='Refresh' content='2;url=/teacher-panel/index?action=student&national-id="+student.getNationalID()+"'>");
    }
}
