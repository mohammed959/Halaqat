package halaqat.view.admin_panel;

import halaqat.data.dao.abs.EmployeeDao;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.EmployeeDatabaseAccess;
import halaqat.data.dao.imp.HalaqaDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Employee;
import halaqat.data.pojos.Halaqa;
import halaqat.view.ViewUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class HalaqatAdministration extends HttpServlet {

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
                    printAllHalaqat(request, response);
                    break;
                case "create":
                    printAddhalaqa(request, response);
                    break;
                case "edit":
                    printeditinghalaqa(request, response);
                    break;
                case "delete":
                    printdeletehalaqa(request, response);
                    break;
                default:
                    response.sendRedirect("/admin-panel/index");
                    break;
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
            e.printStackTrace();
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
                    createHalaqa(request, response);
                    break;
                case "edit":
                    editHalaqa(request, response);
                    break;
                case "delete":
                    deleteHalaqa(request, response);
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

    private void printAllHalaqat(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        {
            PrintWriter writer = response.getWriter();
            StudentDao studentDao = new StudentDatabaseAccess();
            HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
            List<Halaqa> halaqat = halaqaDao.getAll(true);

            HashMap<String, Integer> halaqatStudents = studentDao.numberOfStudentsByHalaqat();

            writer.print("<center><h3 id='section-title'>كل الحلقات</h3></center>");
            writer.print("      <table id='halaqat-table'>"
                    + "        <thead>\n"
                    + "        <tr>\n"
                    + "            <th>\n"
                    + "                اسم الحلقة\n"
                    + "            </th>\n"
                    + "            <th>المدرس</th>\n"
                    + "            <th>عدد الطلاب	</th>\n"
                    + "            <th>عدد أسطر الحفظ	</th>\n"
                    + "\n"
                    + "            <th>عدد أسطر المراجعة	</th>\n"
                    + "            <th>خيارات</th>\n"
                    + "        </tr>\n"
                    + "        </thead>\n"
                    + "        <tbody>\n");

            for (Halaqa halaqa : halaqat) {

                Integer numberOfStudents = halaqatStudents.get(halaqa.getname());
                writer.print("            <tr>\n"
                        + "                <td>" + halaqa.getname() + "</td>\n"
                        + "                <td>" + halaqa.getTeacher().getFirstName() + " " + halaqa.getTeacher().getLastName() + "</td>\n"
                        + "                <td>" + (numberOfStudents == null ? 0 : numberOfStudents) + "</td>\n"
                        + "                <td>" + halaqa.getnumberOfNewLine() + "</td>\n"
                        + "                <td>" + halaqa.getnumberOfReviewLine() + "</td>\n"
                        + "                <td>\n"
                        + "                    <a href='/admin-panel/halaqat?action=edit&name=" + halaqa.getname() + "' class='fa fa-pencil edit-icon'></a>\n"
                        + "                    <a href='/admin-panel/halaqat?action=delete&name=" + halaqa.getname() + "' class='fa fa-times delete-icon'></a>\n"
                        + "                </td>\n"
                        + "            </tr>");

            }
            writer.print("</tbody></table>");
        }

    }

    private void printeditinghalaqa(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        response.getWriter().print("<h3 id='section-title'> تعديل حلقة</h3>");
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        String halaqaName = request.getParameter("name");
        Halaqa halaqa = halaqaDao.get(halaqaName, false);
        if (halaqa == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الحلقة غير موجودة!"));
        }
        printHalaqaForm(response, true, halaqa);

    }

    private void printAddhalaqa(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {

        response.getWriter().print("<center><h3 id='section-title'> إنشاء حلقة</h3></center>");
        printHalaqaForm(response, false, null);
    }

    private void printHalaqaForm(HttpServletResponse response, boolean editing, Halaqa halaqaediting) throws IOException, SQLException, ClassNotFoundException {

        PrintWriter writer = response.getWriter();
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();

        List<Employee> employees = employeeDao.getAll();

        writer.print("<form action='/admin-panel/halaqat' method='post' id='create-edit-halaqat-form'>"
                + "       <label> اسم الحلقة :</label>\n"
                + "        <input name='nameOfHalaqa' class=' text-input ' " + (editing ? " value='" + halaqaediting.getname() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n");
        writer.print(" <label>المدرس:</label>\n"
                + "        <select name='teacher-id' class='select-input '>\n");

        for (Employee employee : employees) {
            if (employee.getJobTitle() == Employee.TYPE_TEACHER) {
                writer.print("<option value='" + employee.getNationalID() + "'" + (editing && halaqaediting.getTeacherId().equals(employee.getNationalID()) ? " selected >" : " >") + employee.getFirstName() + " " + employee.getLastName() + "</option>");
            }

        }

        writer.print("</select><br><br> <label>عدد أسط الحفظ :</label>\n"
                + "<input name = 'numberOfNewLine' class=' text-input '" + (editing ? "value='" + halaqaediting.getnumberOfNewLine() + "'" : "") + " type='text' />\n"
                + "<br>\n"
                + "<br>\n"
                + " <label>عدد أسط المراجعة :</label>\n"
                + "<input name = 'numberOfReviewLine' class=' text-input '" + (editing ? "value='" + halaqaediting.getnumberOfReviewLine() + "'" : "") + " type ='text' />\n"
                + "<br>\n"
                + "<br>\n"
                + "<input type='hidden' name='action' value='" + (editing ? "edit" : "create") + "' />"
                + ""
                + "<input type='submit' value='حفظ' class='button-primary' />"
        );
        if (editing) {
            writer.print("<input type='hidden' name='old_name' value='" + halaqaediting.getname() + "' />");
        }
        writer.print("</form>");

    }

    private void createHalaqa(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        HalaqaDao halaqado = new HalaqaDatabaseAccess();
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();


        String nameOfHalaqa = request.getParameter("nameOfHalaqa");
        if (nameOfHalaqa == null || nameOfHalaqa.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الحلقة!"));
            return;
        }
        if (halaqado.get(nameOfHalaqa, false) != null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم الحلقة موجود مسبقاً!"));
            return;
        }

        if (nameOfHalaqa.length() < 5) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الحلقة بعدد أحرف يتجاوز الخمسة!"));
            return;
        }

        String teacherId = request.getParameter("teacher-id");

        if (teacherId == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى اختيار اسم المدرس!"));
            return;
        }

        Employee employee = employeeDao.get(teacherId);

        if (employee == null || employee.getJobTitle() != Employee.TYPE_TEACHER) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اختيار المدرس غير صحيح!"));
            return;
        }
        if (halaqado.byTeacher(teacherId) != null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المدرس مرتبط بحلقة أخرى !"));
            return;
        }

        String newLines = request.getParameter("numberOfNewLine");
        if (newLines == null || newLines.isEmpty()) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال عدد أسطر الحفظ!"));
            return;
        }

        int numberOfNewLine;
        try {
            numberOfNewLine = Integer.parseInt(request.getParameter("numberOfNewLine"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر الحفظ غير صالح!"));
            return;
        }
        if (numberOfNewLine <= 0 || numberOfNewLine > 100) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر الحفظ غير صالح!"));
            return;
        }

        String numberOfReviewLinesParam = request.getParameter("numberOfReviewLine");
        if (numberOfReviewLinesParam == null || numberOfReviewLinesParam.isEmpty()) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة غير صالح!"));
            return;
        }


        int numberOfReviewLine;
        try {
            numberOfReviewLine = Integer.parseInt(request.getParameter("numberOfReviewLine"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة صالح!"));
            return;
        }

        if (numberOfReviewLine <= 0 || numberOfReviewLine > 100) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة صالح!"));
            return;
        }

        Halaqa h = new Halaqa(numberOfNewLine, numberOfReviewLine, nameOfHalaqa, teacherId);
        halaqado.insert(h);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تمت إضافة الحلقة بنجاح!"));
    }

    private void editHalaqa(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        HalaqaDao halaqado = new HalaqaDatabaseAccess();
        EmployeeDao employeeDao = new EmployeeDatabaseAccess();

        String oldName = request.getParameter("old_name");

        String nameOfHalaqa = request.getParameter("nameOfHalaqa");
        if (nameOfHalaqa == null || nameOfHalaqa.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الحلقة!"));
            return;
        }
        if (halaqado.get(nameOfHalaqa, false) != null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اسم الحلقة موجود مسبقاً!"));
            return;
        }

        if (nameOfHalaqa.length() < 5) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الحلقة بعدد أحرف يتجاوز الخمسة!"));
            return;
        }

        String teacherId = request.getParameter("teacher-id");

        if (teacherId == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى اختيار اسم المدرس!"));
            return;
        }

        Employee employee = employeeDao.get(teacherId);

        if (employee == null || employee.getJobTitle() != Employee.TYPE_TEACHER) {
            response.getWriter().print(ViewUtils.formatErrorMessage("اختيار المدرس غير صحيح!"));
            return;
        }
        if (halaqado.byTeacher(teacherId) != null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("المدرس مرتبط بحلقة أخرى !"));
            return;
        }

        String newLines = request.getParameter("numberOfNewLine");
        if (newLines == null || newLines.isEmpty()) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال عدد أسطر الحفظ!"));
            return;
        }

        int numberOfNewLine;
        try {
            numberOfNewLine = Integer.parseInt(request.getParameter("numberOfNewLine"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر الحفظ غير صالح!"));
            return;
        }
        if (numberOfNewLine <= 0 || numberOfNewLine > 100) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر الحفظ غير صالح!"));
            return;
        }

        String numberOfReviewLinesParam = request.getParameter("numberOfReviewLine");
        if (numberOfReviewLinesParam == null || numberOfReviewLinesParam.isEmpty()) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة غير صالح!"));
            return;
        }


        int numberOfReviewLine;
        try {
            numberOfReviewLine = Integer.parseInt(request.getParameter("numberOfReviewLine"));
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة صالح!"));
            return;
        }

        if (numberOfReviewLine <= 0 || numberOfReviewLine > 100) {
            response.getWriter().print(ViewUtils.formatErrorMessage("عدد أسطر المراجعة صالح!"));
            return;
        }

        Halaqa halaqa = new Halaqa(numberOfReviewLine, numberOfNewLine, nameOfHalaqa, teacherId);

        halaqado.update(halaqa, oldName);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم تعديل الحلقة بنجاح!"));
    }

    private void printdeletehalaqa(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter printWriter = response.getWriter();
        String halaqaName = request.getParameter("name");
        if (halaqaName == null) {
            printWriter.print(ViewUtils.formatErrorMessage("اسم الحلقة غير صالح!"));
            return;
        }
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        if (halaqaDao.get(halaqaName, false) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الحلقة غير موجودة!"));
            return;
        }

        printWriter.print("<center><br><br><h5>هل أنت متأكد أنك تريد حذف الحلقة " + halaqaName + ": </h5>\n"
                + "<form action='/admin-panel/halaqat' method='post'>\n"
                + "\n"
                + "<input type='hidden' name='name' value='" + halaqaName + "' />\n"
                + "<input type='hidden' name='action' value='delete' />\n"
                + "\n<br><br>"
                + "<input type='submit' class='button-red' value='نعم' />\n"
                + "<button type='button' class='button-green' onclick='window.history.back()'>لا</button>\n"
                + "\n"
                + "</form></center>");

    }

    private void deleteHalaqa(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        String halaqaName = request.getParameter("name");
        HalaqaDao halaqaDao = new HalaqaDatabaseAccess();
        StudentDao studentDao = new StudentDatabaseAccess();

        if (halaqaDao.get(halaqaName, false) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الحلقة غير موجودة!"));
            return;
        }

        if (studentDao.numberOfStudents(halaqaName) > 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("لا يمكن حذف حلقة بها طلاب!"));
            return;
        }

        halaqaDao.delete(halaqaName);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم حذف الحلقة " + halaqaName + " بنجاح!"));

    }
}
