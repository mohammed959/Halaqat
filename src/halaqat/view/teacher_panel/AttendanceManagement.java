package halaqat.view.teacher_panel;

import halaqat.AppConstants;
import halaqat.data.dao.abs.AttendanceDao;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.*;
import halaqat.data.pojos.Attendance;
import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Semester;
import halaqat.data.pojos.Student;
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

public class AttendanceManagement extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TeacherPanelViewUtils.printHeaderWithMenu(request, response);
        try {
            String action = request.getParameter("action");
            if (action == null) {
                response.sendRedirect("/teacher-panel");
                return;
            } else {
                switch (action) {
                    case "create":
                        create(request, response);
                        break;
                    case "edit":
                        edit(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TeacherPanelViewUtils.printHeaderWithMenu(request, response);
        try {
            String action = request.getParameter("action");
            if (action == null)
                printAttendanceTable(request, response);
            else {
                switch (action) {
                    case "create":
                        printCreate(request, response);
                        break;
                    case "edit":
                        printEdit(request, response);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        TeacherPanelViewUtils.printFooter(response.getWriter());
    }

    private void printEdit(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ParseException, IOException {
        PrintWriter writer = response.getWriter();
        String nationalId = request.getParameter("sid");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();

        Student student = studentDao.get(nationalId);
        if (student == null) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير مسجل!"));
            return;
        }
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        if (!halaqa.getname().equals(student.getHalaqaName())) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك صلاحية التعديل على الطالب"));
        }


        String date = request.getParameter("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date attendanceDate;
        try {
            attendanceDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return;
        }

        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        Attendance attendance = attendanceDao.get(nationalId, date);
        if (attendance == null) {
            writer.print(ViewUtils.formatErrorMessage("لا يوجد سجل للطالب في التاريخ المحدد!"));
            return;
        }

        writer.print("<center><h3 id='section-title'>تعديل سجل تحضير طالب</h3></center>");
        printAttendanceForm(writer, student, attendanceDate, true, attendance.isPresent());

    }

    private void printCreate(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException, ParseException {
        PrintWriter writer = response.getWriter();
        String nationalId = request.getParameter("sid");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();

        Student student = studentDao.get(nationalId);
        if (student == null) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير مسجل!"));
            return;
        }
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        if (!halaqa.getname().equals(student.getHalaqaName())) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك صلاحية التعديل على الطالب"));
        }


        String date = request.getParameter("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date attendanceDate;
        try {
            attendanceDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return;
        }

        Date today = new Date();
        Date registrationDate = simpleDateFormat.parse(student.getRegistrationDate());
        if (attendanceDate.before(registrationDate)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن تحضير طالب قبل تاريخ تسجيله!"));
            return;
        } else if (attendanceDate.after(today)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن تحضير طالب في المستقبل!"));
            return;
        }


        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        Attendance attendance = attendanceDao.get(nationalId, date);
        if (attendance != null) {
            writer.print(ViewUtils.formatInfoMessage("يوجد سجل للطالب في هذا اليوم، يمكنك تعديله من خلال النموذج التالي:") + "<br><br>");
            printAttendanceForm(writer, student, attendanceDate, true, attendance.isPresent());
            return;
        }

        writer.print("<center><h3 id='section-title'>تحضير طالب</h3></center>");
        printAttendanceForm(writer, student, attendanceDate, false, true);
    }

    private void printAttendanceForm(PrintWriter writer, Student student, Date date, boolean editing, boolean check) {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("ar", "sa"));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar", "sa"));
        writer.print("    <form method='post' action='/teacher-panel/attendance'>\n" +
                "      <label>الطالب: </label><span>" + student.getFirstName() + " " + student.getMiddleName() + " " + student.getLastName() + "</span><br><br>\n" +
                "      <label>التاريخ: </label><span>" + displayDateFormat.format(date) + "</span><br><br>\n" +
                "      <span>حضور الطالب: </span><input type='checkbox'" + (check ? " checked" : "") + " name='present' /><br><br>\n" +
                "      <input type='hidden' name='date' value='" + dateFormatter.format(date) + "' />\n" +
                "      <input type='hidden' name='sid' value='" + student.getNationalID() + "' />\n" +
                "      <input type='hidden' name='action' value='" + (editing ? "edit" : "create") + "' />\n" +
                "\n" +
                "      <input type='submit' class='button-primary' value='حفظ' />\n" +
                "    </form>");
    }


    private void printAttendanceTable(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException, ParseException {
        PrintWriter writer = response.getWriter();
        SemesterDao semesterDao = new SemesterDatabaseAccess();
        Semester currentSemester = semesterDao.getLast();
        if (currentSemester == null) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إضافة فصل دراسي جديد!"));
            return;
        }


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
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        List<Integer> years = attendanceDao.distinctYearsForHalaqa(halaqa.getname());
        writer.print("      <form id='recitation-options' action='/parent-panel/attendance' method='get'>\n");

        if (!(today.get(Calendar.MONTH) == month - 1 && today.get(Calendar.YEAR) == year)) { // Print next only if the selected month is not the current month..
            int nextMonth, nextYear;

            nextMonth = month + 1;
            nextYear = year;


            if (nextMonth == 13) {
                nextMonth = 1;
                nextYear = year + 1;
            }
            writer.print("<a style='float:left' class='fa fa-chevron-left button-green' href='/teacher-panel/attendance?month=" + nextMonth + "&year=" + nextYear + "'></a>");
        }
        int prevMonth, prevYear;
        prevMonth = month - 1;
        prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        writer.print("<a style='float:right;' class='fa fa-chevron-right button-green' href='/teacher-panel/attendance?month=" + prevMonth + "&year=" + prevYear + "'></a>");

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
                "      </form><br>");

        Calendar selectedDate = new GregorianCalendar(year, month - 1, 1);
        int maxDaysOfMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br><br>");
        writer.print("  <div id='section-to-print'>");


        writer.print("<table id='attendance-table'>\n" +
                "<thead>\n" +
                "   <tr>\n");

        writer.print("      <th>اسم الطالب</th>\n");

        SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEEE dd/MM", new Locale("ar", "sa"));

        for (int i = 1; i <= maxDaysOfMonth; i++) {
            selectedDate.set(Calendar.DAY_OF_MONTH, i);
            boolean isFriday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
            boolean isSaturday = selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            if (isFriday || isSaturday) {
                response.getWriter().print("        <th></th>\n");
                if (isFriday) // Skip Saturday if Friday is printed as blank <td>
                    i++;

                continue;
            }
            response.getWriter().print("        <th><span>" + dayNameFormatter.format(selectedDate.getTime()) + "</span></th>\n");
        }
        writer.print("  </tr>\n" +
                "</thead>" +
                "<tbody>");

        StudentDao studentDao = new StudentDatabaseAccess();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Date semesterStarting = dateFormatter.parse(currentSemester.getStartingDate());
        Date semesterEnding = dateFormatter.parse(currentSemester.getEndingDate());

        // Check if the days of selected month in the current semester. Cache it so no need to calculate it for each student.
        boolean[] inCurrentSemester = new boolean[maxDaysOfMonth];
        for (int i = 1; i <= maxDaysOfMonth; i++) {
            selectedDate.set(Calendar.DAY_OF_MONTH, i);
            Date date = selectedDate.getTime();
            inCurrentSemester[i - 1] = date.after(semesterStarting) && date.before(semesterEnding);
        }

        for (Student s : studentDao.byHalaqa(halaqa.getname())) {
            System.out.println("WE START");
            writer.print("<tr><td>" + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</td>");

            List<Attendance> attendanceList = attendanceDao.monthlyAttendance(s.getNationalID(), year, month);
            int attendanceIndex = 0;

            Date registrationDate = dateFormatter.parse(s.getRegistrationDate());

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
                        if (inCurrentSemester[i - 1]) {
                            writer.print("<td><a  class='fa fa-1x fa-" + (attendance.isPresent() ? "check green-color" : "times red-color") + "' href='/teacher-panel/attendance?action=edit&sid=" +
                                    s.getNationalID() + "&date=" + attendance.getDate() + "'></a></td>");
                        } else {
                            writer.print("<td><span  class='fa fa-1x fa-" + (attendance.isPresent() ? "check" : "times") + "'></span></td>");
                        }
                        attendanceIndex++;
                        continue;
                    }
                }

                // Allow the teacher to add new attendance data only to the current semester with previous days..
                if (inCurrentSemester[i - 1] && !selectedDate.getTime().after(today.getTime()) && !selectedDate.getTime().before(registrationDate)) {
                    writer.print("<td><a class='not-print blue-color fa fa-1x fa-plus' href='/teacher-panel/attendance?action=create&sid=" +
                            s.getNationalID() + "&date=" + dateFormatter.format(selectedDate.getTime()) + "'>"
                            + "</a></td>");
                } else {
                    writer.print("<td></td>");
                }

            }

            writer.print("</tr>");
        }
        response.getWriter().print("</tbody></table></div>");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ParseException, IOException {
        PrintWriter writer = response.getWriter();
        String nationalId = request.getParameter("sid");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();

        Student student = studentDao.get(nationalId);
        if (student == null) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير مسجل!"));
            return;
        }
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        if (!halaqa.getname().equals(student.getHalaqaName())) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك صلاحية التعديل على الطالب"));
        }


        String date = request.getParameter("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            simpleDateFormat.parse(date);
        } catch (ParseException e) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return;
        }


        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        Attendance attendance = attendanceDao.get(student.getNationalID(), date);
        if (attendance == null) {
            writer.print(ViewUtils.formatErrorMessage("لا يوجد سجل للطالب في التاريخ المحدد!"));
            return;
        }
        String presentParam = request.getParameter("present");
        boolean present = presentParam != null && presentParam.equals("on");
        attendance.setPresent(present);
        attendanceDao.update(attendance);
        writer.print(ViewUtils.formatSuccessMessage("تمت العملية بنجاح"));
        writer.print("<meta http-equiv='Refresh' content='2;url=/teacher-panel/attendance'>");
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, ParseException, IOException {
        PrintWriter writer = response.getWriter();

        SemesterDao semesterDao = new SemesterDatabaseAccess();
        Semester currentSemester = semesterDao.getLast();
        if (currentSemester == null) {
            writer.print(ViewUtils.formatErrorMessage("يرجى إضافة فصل دراسي!"));
            return;
        }

        String nationalId = request.getParameter("sid");
        if (nationalId == null || nationalId.length() != 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();

        Student student = studentDao.get(nationalId);
        if (student == null) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير مسجل!"));
            return;
        }
        Halaqa halaqa = halaqaDao.byTeacher((String) request.getSession().getAttribute(AppConstants.NATIONAL_ID_KEY));
        if (!halaqa.getname().equals(student.getHalaqaName())) {
            writer.print(ViewUtils.formatErrorMessage("لا تملك صلاحية التعديل على الطالب"));
        }


        String date = request.getParameter("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date attendanceDate;
        try {
            attendanceDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            writer.print(ViewUtils.formatErrorMessage("تاريخ غير صالح!"));
            return;
        }

        Date today = new Date();
        Date registrationDate = simpleDateFormat.parse(student.getRegistrationDate());
        if (attendanceDate.before(registrationDate)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن تحضير طالب قبل تاريخ تسجيله!"));
            return;
        }
        if (attendanceDate.after(today)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن تحضير طالب في المستقبل!"));
            return;
        }

        Date startingDate = simpleDateFormat.parse(currentSemester.getStartingDate());
        if (attendanceDate.before(startingDate)) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن التعديل على سجلات الفصول الدراسية المنتهية!"));
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(attendanceDate);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            writer.print(ViewUtils.formatErrorMessage("لا يمكن تحضير طالب في إجازة نهاية الأسبوع!"));
            return;
        }

        AttendanceDao attendanceDao = new AttendanceDatabaseAccess();
        if (attendanceDao.get(student.getNationalID(), date) != null) {
            writer.print(ViewUtils.formatErrorMessage("يوجد سجل سابق للطالب في التاريخ المحدد!"));
            return;
        }
        String presentParam = request.getParameter("present");
        boolean present = presentParam != null && presentParam.equals("on");

        attendanceDao.insert(new Attendance(student.getNationalID(), date, present));
        writer.print(ViewUtils.formatSuccessMessage("تمت العملية بنجاح"));
        writer.print("<meta http-equiv='Refresh' content='2;url=/teacher-panel/attendance'>");


    }

}
