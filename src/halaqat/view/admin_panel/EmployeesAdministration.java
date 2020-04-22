package halaqat.view.admin_panel;

import halaqat.data.dao.abs.EmployeeDao;
import halaqat.data.dao.imp.EmployeeDatabaseAccess;
import halaqat.data.pojos.Employee;
import halaqat.utils.Utils;
import halaqat.utils.Validator;
import halaqat.view.ViewUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
import java.util.List;

public class EmployeesAdministration extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/admin-panel/index");
            return;
        }
        AdminViewUtils.printHeaderWithMenu(request, response);

        try {
            switch (action) {
                case "all":
                    printAllPage(request, response);
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
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().print(e.getMessage());
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
                    createEmployee(request, response);
                    break;
                case "edit":
                    editEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
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

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        String nationalId = request.getParameter("national-id");
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();
        if (employeeDao.get(nationalId) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير موجود!"));
            return;
        }
        employeeDao.delete(nationalId);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم حذف الموظف."));
    }


    private void printDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() != 10) {
            printWriter.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
        } else {
            printWriter.print("<center><br><br><h5>هل أنت متأكد أنك تريد حذف الموظف برقم الهوية " + nationalId + ": </h5>\n" +
                    "<form action='/admin-panel/employees' method='post'>\n" +
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

    private void printEditPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() < 10) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();

        Employee employee = employeeDao.get(nationalId);

        if (employee == null) {
            writer.print(ViewUtils.formatErrorMessage("رقم الهوية غير مسجل!"));
            return;
        }
        printEmployeeForm(writer, true, employee);
    }

    private void printCreatePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        printEmployeeForm(response.getWriter(), false, null);
    }

    private void printEmployeeForm(PrintWriter writer, boolean editMode, Employee editingEmployee) {
        writer.print("    <h3 id='section-title'>" + (editMode ? "تعديل بيانات موظف" : "إضافة موظف") + "</h3>\n" +
                "    <form method='post' action='/admin-panel/employees' id='employee-info-form'>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='national-id'>رقم الهوية/الإقامة</label>\n" +
                "            <input type='text' name='national-id' class='text-input' " + (editMode ? "readonly='readonly' value='" + editingEmployee.getNationalID() + "'" : "") + " id='national-id'/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='first-name'>الاسم الأول</label>\n" +
                "            <input type='text' class='text-input' name='first-name' " + (editMode ? "value='" + editingEmployee.getFirstName() + "'" : "") + " id='first-name'/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "\n" +
                "            <label for='middle-name'>الاسم الأوسط</label>\n" +
                "            <input type='text' class='text-input' name='middle-name'" + (editMode ? " value='" + editingEmployee.getMiddleName() + "'" : "") + " id='middle-name'/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='last-name'>اسم العائلة</label>\n" +
                "            <input type='text' class='text-input' name='last-name'" + (editMode ? " value='" + editingEmployee.getLastName() + "'" : "") + " id='last-name'/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='nationality'>الجنسية</label>\n" +
                "            <input type='text' class='text-input' name='nationality' " + (editMode ? " value='" + editingEmployee.getNationality() + "'" : "") + " id='nationality'/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='address'>العنوان</label>\n" +
                "            <input type='text' class='text-input' name='address' id='address' " + (editMode ? " value='" + editingEmployee.getAddress() + "'" : "") + "/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='qualification'>المؤهل العلمي</label>\n" +
                "            <input type='text' class='text-input' name='qualification' id='qualification' " + (editMode ? " value='" + editingEmployee.getQualification() + "'" : "") + "/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='mobile-number'>رقم الجوال</label>\n" +
                "            <input type='text' maxlength='10' class='text-input' name='mobile-number' id='mobile-number' " + (editMode ? " value='" + editingEmployee.getMobileNo() + "'" : "") + "/>\n" +
                "        </div>\n" +
                "        <div class='input-item'>\n" +
                "            <label for='job-title'>العمل</label>\n" +
                "            <select id='job-title' name='job-title' class='select-input'>\n" +
                "                <option value=" + Employee.TYPE_ADMINISTRATOR + (editMode && editingEmployee.getJobTitle() == Employee.TYPE_ADMINISTRATOR ? " selected" : "") + ">إداري</option>\n" +
                "                <option value=" + Employee.TYPE_TEACHER + (editMode && editingEmployee.getJobTitle() == Employee.TYPE_TEACHER ? " selected" : "") + ">مدرس</option>\n" +
                "            </select>\n" +
                "        </div>\n");

        if (editMode) { // Don't display employee state on creation, it is ON_WORK by default..
            writer.print("<div class='input-item'>\n" +
                    "            <label for='employee-state'>حالة الموظف</label>\n" +
                    "            <select id='employee-state' name='employee-state' class='select-input'>\n" +
                    "                <option value=" + Employee.STATE_ON_WORK + (editingEmployee.getEmployeeState() == Employee.STATE_ON_WORK ? " selected" : "") + ">على رأس العمل</option>\n" +
                    "                <option value=" + Employee.STATE_IN_VACATION + (editingEmployee.getEmployeeState() == Employee.STATE_IN_VACATION ? " selected" : "") + ">في إجازة</option>\n" +
                    "                <option value=" + Employee.STATE_RETIRED + (editingEmployee.getEmployeeState() == Employee.STATE_RETIRED ? " selected" : "") + ">متقاعد</option>\n" +
                    "                <option value=" + Employee.STATE_FIRED + (editingEmployee.getEmployeeState() == Employee.STATE_FIRED ? " selected" : "") + ">طي قيد</option>" +
                    "            </select>\n" +
                    "        </div>\n");
        }

        writer.print(
                "        <div class='input-item'>\n" +
                        "            <label for='birth-date'>تاريخ الميلاد</label>\n" +
                        "            <input type='date' id='birth-date' name='birth-date' class='text-input'" + (editMode ? " value='" + editingEmployee.getBirthDay() + "'" : "") + "/>\n" +
                        "        </div>\n");
        if (editMode) {
            writer.print("<input type='hidden' name='action' value='edit' />");
        } else {
            writer.print("        <div class='input-item'>\n" +
                    "            <label for='password'>كلمة المرور:</label>\n" +
                    "            <input type='password' id='password' name='password' class='text-input'/>\n" +
                    "        </div>\n" +
                    "        <input type='hidden' name='action' value='create' />"
            );
        }

        writer.print("        <input type='submit' value='حفظ' class='button-primary' id='save'/>\n" +
                "    </form>");
    }

    private void printAllPage(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        PrintWriter writer = response.getWriter();
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();
        List<Employee> employees = employeeDao.getAll();

        writer.print("<table id='employees-table'>\n" +
                "        <thead>\n" +
                "        <tr>\n" +
                "            <th>\n" +
                "                اسم الموظف\n" +
                "            </th>\n" +
                "            <th>الهوية/الإقامة</th>\n" +
                "            <th>العمل</th>\n" +
                "            <th>رقم الجوال</th>\n" +
                "\n" +
                "            <th>الجنسية</th>\n" +
                "            <th>المؤهل العلمي</th>\n" +
                "            <th>العنوان</th>\n" +
                "            <th>تاريخ الميلاد</th>\n" +
                "            <th>تاريخ الإنضمام</th>\n" +
                "            <th>خيارات</th>\n" +
                "        </tr>\n" +
                "        </thead>\n" +
                "        <tbody>\n");

        for (Employee employee : employees) {
            writer.print(
                    "            <tr>\n" +
                            "                <td>" + employee.getFirstName() + " " + employee.getMiddleName() + " " + employee.getLastName() + "</td>\n" +
                            "                <td>" + employee.getNationalID() + "</td>\n" +
                            "                <td>" + (employee.getJobTitle() == Employee.TYPE_ADMINISTRATOR ? "إداري" : "أستاذ") + "</td>\n" +
                            "                <td>" + employee.getMobileNo() + "</td>\n" +
                            "                <td>" + employee.getNationality() + "</td>\n" +
                            "                <td>" + employee.getQualification() + "</td>\n" +
                            "                <td>" + employee.getAddress() + "</td>\n" +
                            "                <td>" + employee.getBirthDay().replace('-', '/') + "</td>\n" +
                            "                <td>" + employee.getRegistrationDate().replace('-', '/') + "</td>\n" +
                            "                <td>\n" +
                            "                    <a href='/admin-panel/employees?action=edit&national-id=" + employee.getNationalID() + "' class='fa fa-pencil edit-icon'></a>\n" +
                            "                    <a href='/admin-panel/employees?action=delete&national-id=" + employee.getNationalID() + "' class='fa fa-times delete-icon'></a>\n" +
                            "                </td>\n" +
                            "            </tr>");
        }

    }


    private void createEmployee(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {

        String nationalId = request.getParameter("national-id");

        if (!Validator.nationalId(nationalId)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }

        EmployeeDao employeeDao = new EmployeeDatabaseAccess();
        if (employeeDao.get(nationalId) != null) {
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


        int jobTitle;
        try {
            jobTitle = Integer.parseInt(request.getParameter("job-title"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("نوع العمل غير صالح!"));
            return;
        }
        if (jobTitle != Employee.TYPE_ADMINISTRATOR && jobTitle != Employee.TYPE_TEACHER) {
            response.getWriter().print(ViewUtils.formatErrorMessage("نوع العمل غير صالح!"));
            return;
        }


        String nationality = request.getParameter("nationality");
        if (!Validator.words(nationality)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الجنسية غير صالحة!"));
            return;
        }

        String address = request.getParameter("address");

        String qualification = request.getParameter("qualification");

        String mobileNo = request.getParameter("mobile-number");
        if (!Validator.mobileNo(mobileNo)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الجوال غير صالح!"));
            return;
        }

        String birthDateParam = request.getParameter("birth-date");
        System.out.println(birthDateParam);
        Calendar today = Calendar.getInstance();
        // Date constructor takes the year minus 1900, minimum age for employees is 18: subtract (1900 + 18) from current year
        // To get the maximum birth date.
        Date maximum = new Date(today.get(Calendar.YEAR) - 1918, today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        // The same thing here, maximum age for employees is 100: subtract (1900 + 100) from current year
        // To get the minimum birth date.
        Date minimum = new Date(today.get(Calendar.YEAR) - 2000, today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));

        if (!Validator.date(birthDateParam, minimum, maximum)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ الميلاد غير صالح!"));
            return;
        }

        String password = request.getParameter("password");
        if (password == null || password.length() < 6) {
            response.getWriter().print(ViewUtils.formatErrorMessage("كلمة المرور قصيرة!"));
            return;
        }


        Employee employee = new Employee(nationalId, firstName, middleName, lastName, jobTitle, address, qualification, Employee.STATE_ON_WORK, nationality, mobileNo, null, birthDateParam); // When inserting new employees, it is on work by default. Registration date will be inserted by MySQL
        employee.setPassword(Utils.hashPassword(password));
        employeeDao.insert(employee);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تمت إضافة الموظف بنجاح!"));
    }


    private void editEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        String nationalId = request.getParameter("national-id");
        if (!Validator.nationalId(nationalId)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }

        EmployeeDao employeeDao = new EmployeeDatabaseAccess();
        if (employeeDao.get(nationalId) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير موجود!"));
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


        int jobTitle;
        try {
            jobTitle = Integer.parseInt(request.getParameter("job-title"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("نوع العمل غير صالح!"));
            return;
        }
        if (jobTitle != Employee.TYPE_ADMINISTRATOR && jobTitle != Employee.TYPE_TEACHER) {
            response.getWriter().print(ViewUtils.formatErrorMessage("نوع العمل غير صالح!"));
            return;
        }

        int state;
        try {
            state = Integer.parseInt(request.getParameter("employee-state"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حالة الموظف غير صالحة!"));
            return;
        }
        if (!(state == Employee.STATE_FIRED || state == Employee.STATE_ON_WORK ||
                state == Employee.STATE_IN_VACATION || state == Employee.STATE_RETIRED)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حالة الموظف غير صالحة!"));
            return;
        }

        String nationality = request.getParameter("nationality");
        if (!Validator.words(nationality)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الجنسية غير صالحة!"));
            return;
        }

        String address = request.getParameter("address");

        String qualification = request.getParameter("qualification");

        String mobileNo = request.getParameter("mobile-number");
        if (!Validator.mobileNo(mobileNo)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الجوال غير صالح!"));
            return;
        }

        String birthDateParam = request.getParameter("birth-date");

        Calendar today = Calendar.getInstance();
        // Date constructor takes the year minus 1900, minimum age for employees is 18: subtract (1900 + 18) from current year
        // To get the maximum birth date.
        Date maximum = new Date(today.get(Calendar.YEAR) - 1918, today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        // The same thing here, maximum age for employees is 100: subtract (1900 + 100) from current year
        // To get the minimum birth date.
        Date minimum = new Date(today.get(Calendar.YEAR) - 2000, today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        if (!Validator.date(birthDateParam, minimum, maximum)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ الميلاد غير صالح!"));
            return;
        }


        Employee employee = new Employee(nationalId, firstName, middleName, lastName, jobTitle, address, qualification, Employee.STATE_ON_WORK, nationality, mobileNo, null, birthDateParam); // When inserting new employees, it is on work by default. Registration date will be inserted by MySQL

        employeeDao.update(employee);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تم تعديل الموظف بنجاح!"));
    }


}
