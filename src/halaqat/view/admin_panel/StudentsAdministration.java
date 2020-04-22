package halaqat.view.admin_panel;

import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.ParentDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.HalaqaDatabaseAccess;
import halaqat.data.dao.imp.ParentDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;
import halaqat.utils.Utils;
import halaqat.utils.Validator;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class StudentsAdministration extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/admin-panel/index");
            return;
        }
        AdminViewUtils.printHeaderWithMenu(request, response);

        try {
            switch (action) {
                case "search":
                    printSearchPage(request, response);
                    break;
                case "by-halaqa":
                    printByHalaqa(request, response);
                    break;
                case "create":
                    printCreatePage(request, response);
                    break;
                case "edit":
                    printEditPage(request, response);
                    break;
                case "delete":
                    printDeleteConfirmation(request, response);
                    break;
                default:
                    response.sendRedirect("/admin-panel/index");
                    return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        AdminViewUtils.printFooter(response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        AdminViewUtils.printHeaderWithMenu(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/admin-panel/index");
            return;
        }

        try {
            switch (action) {
                case "create":
                    createStudent(request, response);
                    break;
                case "edit":
                    editStudent(request, response);
                    break;
                case "delete":
                    deleteStudent(request, response);
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }
        AdminViewUtils.printFooter(response.getWriter());
    }


    private void printSearchPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter printWriter = response.getWriter();

        String searchMethod = request.getParameter("search-method"); // Get search method if submitted
        String nationalId = request.getParameter("national-id"); // Get nationalId if submitted
        String name = request.getParameter("name"); // Get name submitted


        printWriter.print("    <h3 id='section-title'>بحث عن طالب</h3>\n" +
                "    <form action='/admin-panel/students?search=true&' method='get'>\n" +
                "        <div class='search-choice'>\n" +
                "            <span>\n" +
                "            <input type='radio' " + (searchMethod == null || searchMethod.equals("national-id") ? "checked='checked'" : "") + " name='search-method' value='national-id'/> برقم الهوية\n" +
                "            </span>\n" +
                "            <input type='text' name='national-id' value='" + (nationalId != null ? nationalId : "") + "' class='text-input'/>\n" +
                "        </div>\n" +
                "        <div class='search-choice'>\n" +
                "            <span>\n" +
                "            <input type='radio' name='search-method' " + (searchMethod != null && searchMethod.equals("name") ? "checked='checked" : "") + "value='name'/> بالاسم\n" +
                "            </span>\n" +
                "            <input type='text' name='name' value='" + (name != null ? name : "") + "' class='text-input'/>\n" +
                "        </div>\n" +
                "        <input type='submit' class='button-primary' id='search-button' value='بحث'/>\n" +
                "        <input type='hidden' name='action' value='search' />" +
                "    </form>\n");


        if (searchMethod == null)
            return;

        StudentDao studentDao = new StudentDatabaseAccess();
        List<Student> result = null;

        if (searchMethod.equals("national-id")) {
            if (Validator.nationalId(nationalId)) {
                Student student = studentDao.get(nationalId);
                if (student != null) {
                    result = new LinkedList<>();
                    result.add(student);
                }
            } else {
                printWriter.print(ViewUtils.formatErrorMessage("يرجى إدخال رقم هوية صالح."));
                return;
            }
        } else { // Search by name otherwise..
            if (Validator.words(name)) {
                result = studentDao.searchByName(name);
            }
        }
        if (result != null && result.size() > 0)
            printStudentTable(result, response.getWriter());
        else {
            printWriter.print(ViewUtils.formatInfoMessage("لم يتم العثور على أي نتائج!"));
        }
    }

    private void printStudentTable(List<Student> students, PrintWriter printWriter) {
        printWriter.print("<table id='student-search-results'>\n" +
                "        <thead>\n" +
                "        <tr>\n" +
                "            <th>اسم الطالب</th>\n" +
                "            <th>الهوية/الإقامة</th>\n" +
                "            <th>المستوى الدراسي</th>\n" +
                "            <th>تاريخ الميلاد</th>\n" +
                "            <th>تاريخ التسجيل</th>\n" +
                "            <th>حالة الطالب</th>\n" +
                "            <th>خيارات</th>\n" +
                "        </tr>\n" +
                "        </thead>\n" +
                "\n" +
                "        <tbody>");

        for (Student s : students) {
            printWriter.print("<tr>\n" +
                    "            <td>" + s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName() + "</td>\n" +
                    "            <td>" + s.getNationalID() + "</td>\n" +
                    "            <td>" + s.getSchoolLevelName() + "</td>\n" +
                    "            <td>" + s.getBirthDate().replace("-", "/") + "</td>\n" +
                    "            <td>" + s.getRegistrationDate().replace("-", "/") + "</td>\n" +
                    "            <td>" + (s.getState() == Student.STATE_ENROLLED ? "ملتزم" : "منقطع") + "</td>\n" +
                    "            <td>\n" +
                    "                <a href='/admin-panel/parents?action=view&national-id=" + s.getParentId() + "' class='fa fa-user parent-icon'></a>\n" +
                    "                <a href='/admin-panel/students?action=edit&national-id=" + s.getNationalID() + "' class='fa fa-pencil edit-icon'></a>\n" +
                    "                <a href='/admin-panel/students?action=delete&national-id=" + s.getNationalID() + "' class='fa fa-times delete-icon'></a>\n" +
                    "            </td>\n" +
                    "        </tr>");
        }

        printWriter.print("</tbody></table>");
    }

    private void printByHalaqa(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        List<Halaqa> halaqat = halaqaDao.getAll(false);

        String halaqaName = request.getParameter("halaqa-name");
        writer.print("    <h3 id='section-title'>عرض الطلاب حسب الحلقة</h3>\n" +
                "    <form action='/admin-panel/students' id='halaqa-selection'>\n" +
                " <input type='hidden' name='action' value='by-halaqa' />" +
                "        <label for='halaqa-input'>الحلقة</label>\n" +
                "        <select name='halaqa-name' id='halaqa-input' class='select-input'>\n");

        for (Halaqa halaqa : halaqat) {
            writer.print("<option " + (halaqaName != null && halaqa.getname().equals(halaqaName) ? "selected>" : ">") + halaqa.getname() + "</option>");
        }
        writer.print("        </select>\n" +
                "        <input type='submit' class='button-primary' value='عرض'/>\n" +
                "    </form>\n");

        if (halaqaName != null && halaqaName.length() > 0) {
            StudentDao studentDao = new StudentDatabaseAccess();
            List<Student> students = studentDao.byHalaqa(halaqaName);

            if (students.size() == 0) {
                writer.print(ViewUtils.formatInfoMessage("الحلقة المختارة لا يوجد بها طلاب!"));
                return;
            }

            printStudentTable(students, writer);
        }
    }


    private void printCreatePage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        response.getWriter().print("<h3 id='section-title'> إنشاء طالب</h3>");
        printStudentForm(response, false, null);
    }

    private void printEditPage(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        String nationalId = request.getParameter("national-id");
        StudentDao studentDao = new StudentDatabaseAccess();
        Student student = studentDao.get(nationalId);
        if (student != null)
            printStudentForm(response, true, student);
        else
            response.getWriter().print(ViewUtils.formatErrorMessage("لم يتم العثور على الطالب المحدد!"));
    }


    private void printStudentForm(HttpServletResponse response, boolean isEditMode, Student editingStudent) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        List<Halaqa> halaqat = halaqaDao.getAll(false);


        writer.print("<form action='/admin-panel/students' method='post' id='create-edit-student-form'>" +
                "       <label> رقم الهوية :</label>\n" +
                "        <input name='national-id' class='text-input ' " + (isEditMode ? "readonly='readonly' value='" + editingStudent.getNationalID() + "'" : "") + " type='text' >\n" +
                "        <br>\n" +
                "        <br>\n" +
                "       <label>الاسم الأول :</label>\n" +
                "        <input name='first-name' class='text-input' " + (isEditMode ? "value='" + editingStudent.getFirstName() + "'" : "") + " type='text' >\n" +
                "        <br>\n" +
                "        <br>\n" + "       <label>اسم الأب :</label>\n" +
                "        <input name='middle-name' class='text-input' " + (isEditMode ? "value='" + editingStudent.getMiddleName() + "'" : "") + " type='text' >\n" +
                "        <br>\n" +
                "        <br>\n" + "       <label>اسم العائلة :</label>\n" +
                "        <input name='last-name' class='text-input' " + (isEditMode ? "value='" + editingStudent.getLastName() + "'" : "") + " type='text' >\n" +
                "        <br>\n" +
                "        <br>\n");

        if (!isEditMode)
            writer.print("       <label>رقم هوية الأب :</label>\n" +
                    "        <input name='parent-national-id' class='text-input ' type='text' >\n" +
                    "        <br>\n" +
                    "        <br>\n"
            );

        writer.print("        <label> المستوى الدراسي :</label>\n" +
                "        <select name='school-level' class='select-input'>\n");

        for (int i = 0; i < Student.SCHOOL_LEVELS.length; i++)
            writer.print("<option value='" + (i + 1) + "' " +
                    (isEditMode && editingStudent.getSchoollvl() == (i + 1) ? "selected>" : ">")
                    + Student.SCHOOL_LEVELS[i] + "</option>");
        writer.print("          </select><br>\n" +
                "        <br>\n" +
                "        <label> تاريخ الميــلاد :</label>\n" +
                "        <input name='birth-date' class=' text-input' type='date' " + (isEditMode ? "value='" + editingStudent.getBirthDate() + "'>" : ">") +
                "        <br>\n" +
                "        <br>\n");

        if (isEditMode) {
            writer.print(
                    "        <label> حالة الطـــالب :</label>\n" +
                            "        <select name='state' class='select-input'>\n" +
                            "            <option value=" + Student.STATE_ENROLLED + (editingStudent.getState() == Student.STATE_ENROLLED ? " selected" : "") + "> ملتزم</option>\n" +
                            "            <option value=" + Student.STATE_NOT_ENROLLED + (editingStudent.getState() == Student.STATE_NOT_ENROLLED ? " selected" : "") + "> منقطع</option>\n" +
                            "        </select>\n" +
                            "\n" +
                            "        <br>\n" +
                            "        <br>\n");
        }

        writer.print("        <label> الحــــلـــقـــــة :</label>\n" +
                "        <select name='halaqa' class='select-input'>\n");

        for (Halaqa halaqa : halaqat) {
            writer.print("<option value='" + halaqa.getname() + "'" + (isEditMode && editingStudent.getHalaqaName().equals(halaqa.getname()) ? " selected >" : " >") + halaqa.getname() + "</option>");
        }
        writer.print("        </select>\n" +
                "        <br><br>\n");
        if (isEditMode) {
            writer.print("<input type='hidden' name='action' value='edit' />");
        } else {
            writer.print("      <label> كلمة المرور :</label>\n" +
                    "        <input name='password' class=' text-input' type='password' />\n" +
                    "        <br>\n" +
                    "        <br>\n" +
                    "<input type='hidden' name='action' value='create' />");
        }
        writer.print("        <input class='save button-primary' type='submit' value='حفظ'>\n" +
                "    </form>\n");
    }

    private void printDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() != 10) {
            printWriter.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
        } else {
            printWriter.print("<center><br><br><h5>هل أنت متأكد أنك تريد حذف الطالب برقم الهوية " + nationalId + ": </h5>\n" +
                    "<form action='/admin-panel/students' method='post'>\n" +
                    "\n" +
                    "<input type='hidden' name='national-id' value='" + nationalId + "' />\n" +
                    "<input type='hidden' name='action' value='delete' />\n" +
                    "\n<br><br>" +
                    "<input type='submit' class='button-red' value='نعم' />\n" +
                    "<button type='button' class='button-green' onclick='window.history.back()'>لا</button>\n" +
                    "\n" +
                    "</form></center>");
        }
    }


    private void createStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        String nationalId = request.getParameter("national-id");
        if (!Validator.nationalId(nationalId)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }

        StudentDao studentDao = new StudentDatabaseAccess();
        if (studentDao.get(nationalId) != null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية موجود مسبقاً!"));
            return;
        }

        String firstName = request.getParameter("first-name");
        if (!Validator.words(firstName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الاسم الأول غير صالح!"));
            return;
        }

        String middleName = request.getParameter("middle-name");
        if (!Validator.words(middleName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الاسم الأوسط غير صالح!"));
            return;
        }

        String lastName = request.getParameter("last-name");
        if (!Validator.words(lastName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم العائلة غير صالح!"));
            return;
        }


        String parentNationalId = request.getParameter("parent-national-id");
        if (!Validator.nationalId(parentNationalId)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم هوية ولي الأمر غير صالح!"));
            return;
        }
        ParentDao parentDao = new ParentDatabaseAccess();
        Parent parent = parentDao.get(parentNationalId);
        if (parent == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("ولي الأمر غير موجود في النظام، يرجى تسجيله لتمكينه من متابعة ابنه."));
            return;
        }


        int schoolLevel;
        try {
            schoolLevel = Integer.parseInt(request.getParameter("school-level"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المستوى الدراسي غير صالح!"));
            return;
        }
        if (schoolLevel > 12 || schoolLevel < 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المستوى الدراسي غير صالح!"));
            return;
        }

        String birthDate = request.getParameter("birth-date");
        Calendar calendar = Calendar.getInstance();
        //  Date constructor takes the year minus 1900, maximum age for student is 22: subtract (1900 + 22) from current year
        // To get the minimum birth date.
        Date min = new Date(calendar.get(Calendar.YEAR) - 1922, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        // The same thing here, minimum age for student is 4: subtract (1900 + 4) from current year
        // To get the maximum birth date.
        Date max = new Date(calendar.get(Calendar.YEAR) - 1904, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (!Validator.date(birthDate, min, max)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ الميلاد غير صالح!"));
            return;
        }


        String halaqaName = request.getParameter("halaqa");
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        Halaqa halaqa = halaqaDao.get(halaqaName, false);
        if (halaqa == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم حلقة غير صالح!"));
            return;
        }

        String password = request.getParameter("password");
        if (password == null || password.length() < 6) {
            response.getWriter().print(ViewUtils.formatErrorMessage("كلمة المرور قصيرة!"));
            return;
        }

        Student student = new Student(nationalId, firstName, middleName, lastName, birthDate, schoolLevel, halaqaName, parentNationalId);
        student.setPassword(Utils.hashPassword(password));

        studentDao.insert(student);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تمت إضافة الطالب بنجاح!"));
    }


    private void editStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        String nationalId = request.getParameter("national-id");

        String firstName = request.getParameter("first-name");
        if (!Validator.words(firstName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الاسم الأول غير صالح!"));
            return;
        }

        String middleName = request.getParameter("middle-name");
        if (!Validator.words(middleName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الاسم الأوسط غير صالح!"));
            return;
        }

        String lastName = request.getParameter("last-name");
        if (!Validator.words(lastName)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم العائلة غير صالح!"));
            return;
        }


        int schoolLevel;
        try {
            schoolLevel = Integer.parseInt(request.getParameter("school-level"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المستوى الدراسي غير صالح!"));
            return;
        }
        if (schoolLevel > 12 || schoolLevel < 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المستوى الدراسي غير صالح!"));
            return;
        }

        String birthDate = request.getParameter("birth-date");
        Calendar calendar = Calendar.getInstance();
        Date min = new Date(calendar.get(Calendar.YEAR) - 22, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        Date max = new Date(calendar.get(Calendar.YEAR) - 4, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (!Validator.date(birthDate, min, max)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ الميلاد غير صالح!"));
            return;
        }

        int state;
        try {
            state = Integer.parseInt(request.getParameter("state"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حالة الطالب غير صالحة!"));
            return;
        }
        if (!(state == Student.STATE_ENROLLED || state == Student.STATE_NOT_ENROLLED)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حالة الطالب غير صالحة!"));
            return;
        }

        String halaqaName = request.getParameter("halaqa");
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        Halaqa halaqa = halaqaDao.get(halaqaName, false);
        if (halaqa == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم حلقة غير صالح!"));
            return;
        }

        StudentDao studentDao = new StudentDatabaseAccess();
        Student student = new Student(nationalId, firstName, middleName, lastName, birthDate, schoolLevel, halaqaName, null);
        student.setState(state);

        studentDao.update(student);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم تعديل الطالب بنجاح"));

    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        String nationalId = request.getParameter("national-id");
        StudentDao studentDao = new StudentDatabaseAccess();
        if (studentDao.get(nationalId) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير موجود!"));
            return;
        }
        studentDao.delete(nationalId);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم حذف الطالب."));
    }

}
