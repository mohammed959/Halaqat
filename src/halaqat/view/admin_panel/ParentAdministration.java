
package halaqat.view.admin_panel;

import halaqat.data.dao.abs.ParentDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.ParentDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;
import halaqat.utils.Utils;
import halaqat.utils.Validator;
import halaqat.view.ViewUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParentAdministration extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/admin-panel/index");
            return;
        }
        AdminViewUtils.printHeaderWithMenu(request, response);

        try {
            switch (action) {
                case "search":
                    printsearchparent(request, response);
                    break;
                case "create":
                    printCreateParent(request, response);
                    break;
                case "edit":
                    printeditingparent(request, response);
                    break;
                case "delete":
                    printDeleteConfirmation(request, response);
                    break;
                case "view":
                    showParent(request, response);
                    break;
                default:
                    response.sendRedirect("/admin-panel/index");
                    break;


            }
        } catch (SQLException | ClassNotFoundException ex) {
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        AdminViewUtils.printFooter(response.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AdminViewUtils.printHeaderWithMenu(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("/admin-panel/index");
            return;
        }

        try {
            switch (action) {
                case "create":
                    createParent(request, response);
                    break;
                case "edit":
                    editParent(request, response);
                    break;
                case "delete":
                    deleteParent(request, response);
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }
        AdminViewUtils.printFooter(response.getWriter());
    }

    private void showParent(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();
        String nationalId = request.getParameter("national-id");

        if (nationalId == null || nationalId.length() != 10) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح"));
            return;
        }

        ParentDao parentDao = new ParentDatabaseAccess();

        Parent parent = parentDao.get(nationalId);
        if (parent == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("ولي الأمر برقم الهوية " + nationalId + " غير موجود!"));
            return;
        }

        StudentDao studentDao = new StudentDatabaseAccess();

        List<Student> students = studentDao.byParent(nationalId);

        writer.print("    <h3 id='section-title'>بيانات ولي أمر</h3>\n" +
                "\n" +
                "    <div class='data-item'>\n" +
                "        <span>رقم الهوية:</span>\n" +
                "        <span>" + parent.getNationalID() + "</span>\n" +
                "    </div>\n" +
                "    <div class='data-item'>\n" +
                "        <span>الاسم:</span>\n" +
                "        <span>" + parent.getFirstName() + " " + parent.getMiddleName() + " " + parent.getLastName() + "</span>\n" +
                "    </div>\n" +
                "    <div class='data-item'>\n" +
                "        <span>الجنسية:</span>\n" +
                "        <span>" + parent.getNationality() + "</span>\n" +
                "    </div>\n" +
                "    <div class='data-item'>\n" +
                "        <span>رقم الجوال:</span>\n" +
                "        <span>" + parent.getMobileN() + "</span>\n" +
                "    </div>\n" +
                "    <div class='data-item'>\n" +
                "        <span>العنوان:</span>\n" +
                "        <span>" + parent.getAddress() + "</span>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class='data-item'>\n" +
                "        <span>الطلاب التابعين:</span>\n" +
                "        <ul id='children-list'>\n");

        if (students.size() > 0) {
            for (Student s :
                    students) {
                writer.print("            <li>" + s.getFirstName() + "  -  " + "<a class='blue-color' " +
                        "href='/admin-panel/students?action=by-halaqa&halaqa-name=" + s.getHalaqaName()
                        + "'>" + s.getHalaqaName() + "</a></li>\n");
            }
        } else {
            writer.print("<li>لا يوجد!</li>");
        }

        writer.print("        </ul>\n" +
                "    </div>\n" +
                "\n" +
                "    <div id='parent-options'>\n" +
                "        <a href='#' class='fa fa-pencil button-green'>&nbsp;&nbsp;تعديل</a>\n" +
                "        <a href='#' class='fa fa-times button-red'>&nbsp;&nbsp;حذف</a>\n" +
                "    </div>\n" +
                "\n");


    }

    private void printDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter printWriter = response.getWriter();
        String nationalId = request.getParameter("national-id");
        if (nationalId == null || nationalId.length() != 10) {
            printWriter.print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
            return;
        }

        ParentDao parentDao = new ParentDatabaseAccess();
        Parent parent = parentDao.get(nationalId);
        if (parent == null) {
            printWriter.print(ViewUtils.formatErrorMessage("ولي الأمر برقم الهوية " + nationalId + " غير موجود!"));
            return;
        }

        StudentDao studentDao = new StudentDatabaseAccess();

        int numberOfDependents = studentDao.byParent(nationalId).size();

        if (numberOfDependents > 0) {
            printWriter.print("<center class='red-color'>يوجد " + numberOfDependents + "طلاب تابعين لولي الأمر!</center>");
        }

        printWriter.print("<center><br><br><h5>هل أنت متأكد أنك تريد حذف ولي الأمر برقم الهوية " + nationalId + "("
                + parent.getFirstName() + " " + parent.getMiddleName() + " " + parent.getLastName() + ") </h5>\n" +
                "<form action='/admin-panel/parents' method='post'>\n" +
                "\n" +
                "<input type='hidden' name='national-id' value='" + nationalId + "' />\n" +
                "<input type='hidden' name='action' value='delete' />\n" +
                "\n<br><br>" +
                "<input type='submit' class='button-red' value='نعم' />\n" +
                "<button type='button' class='button-green' onclick='window.history.back()'>لا</button>\n" +
                "\n" +
                "</form></center>");
    }


    private void deleteParent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {

        String nationalId = request.getParameter("national-id");
        ParentDao parentDao = new ParentDatabaseAccess();
        if (parentDao.get(nationalId) == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير موجود!"));
            return;
        }
        StudentDao studentDao = new StudentDatabaseAccess();
        if(studentDao.numberOfDependents(nationalId)>0){
            response.getWriter().print(ViewUtils.formatErrorMessage("لا يمكن حذف ولي أمر يتبع له طلاب!"));
            return;
        }
        parentDao.delete(nationalId);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم حذف ولي الأمر."));


    }

    private void printsearchparent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {

        PrintWriter printWriter = response.getWriter();
        String searchMethod = request.getParameter("search-method"); // Get search method if submitted
        String nationalId = request.getParameter("national-id"); // Get nationalId if submitted
        String name = request.getParameter("name"); // Get name submitted


        printWriter.print("    <h3 id='section-title'>بحث عن ولي أمر</h3>\n" +
                "    <form action='/admin-panel/parents' method='get'>\n" +
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
                "    </form><br><br>\n");

        if (searchMethod == null)
            return;

        ParentDao parentDao = new ParentDatabaseAccess();
        List<Parent> result = null;

        if (searchMethod.equals("national-id")) {
            if (nationalId != null && nationalId.length() == 10) {
                Parent parent = parentDao.get(nationalId);
                if (parent != null) {
                    result = new LinkedList<>();
                    result.add(parent);
                }
            } else {
                printWriter.print(ViewUtils.formatErrorMessage("يرجى إدخال رقم هوية صالح."));
            }

        } else {
            if (name != null && name.length() > 0) {
                result = parentDao.searchByName(name);
            }

            if (result != null && result.size() > 0)
                printParentTable(result, response.getWriter());
            else
                printWriter.print(ViewUtils.formatInfoMessage("لم يتم العثور على أي نتائج!"));
        }
    }

    private void printParentTable(List<Parent> parent, PrintWriter writer) throws SQLException, ClassNotFoundException, IOException {

        writer.print("<table id='parent-search-results'>\n" +
                "        <thead>\n" +
                "        <tr>\n" +
                "            <th>الاسم</th>\n" +
                "            <th>الهوية/الإقامة</th>\n" +
                "            <th>الجنسية</th>\n" +
                "            <th>رقم الجوال</th>\n" +
                "            <th>العنوان</th>\n" +
                "            <th>خيارات</th>\n" +
                "        </tr>\n" +
                "        </thead>\n" +
                "\n" +
                "        <tbody>");

        for (Parent parents : parent) {
            writer.print("<tr>\n" +
                    "            <td><a href='" + "/admin-panel/parents?action=view&national-id=" +
                    parents.getNationalID() + "'>" + parents.getFirstName() + " " + parents.getMiddleName() +
                    " " + parents.getLastName() + "</a></td>\n" +

                    "            <td>" + parents.getNationalID() + "</td>\n" +
                    "            <td>" + parents.getNationality() + "</td>\n" +
                    "             <td>" + parents.getMobileN() + "</td>\n" +
                    "             <td>" + parents.getAddress() + "</td>\n" +
                    "             <td>" +
                    "                <a href='/admin-panel/parents?action=edit&national-id=" + parents.getNationalID() + "' class='fa fa-pencil edit-icon'></a>\n" +
                    "                <a href='/admin-panel/parents?action=delete&national-id=" + parents.getNationalID() + "' class='fa fa-times delete-icon'></a>\n" +
                    "            </td>\n" +
                    "        </tr>");
        }

        writer.print("</tbody></table>");


    }


    private void printCreateParent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {


        response.getWriter().print("<h3 id='section-title'> إضافة ولي أمر</h3>");
        printParentForm(response, false, null);

    }

    private void printeditingparent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        response.getWriter().print("<h3 id='section-title'> تعديل ولي أمر</h3>");
        ParentDao parentdao = new ParentDatabaseAccess();
        String nationalId = request.getParameter("national-id");
        Parent parent = parentdao.get(nationalId);
        if (parent == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("ولي الأمر غير موجود!"));
            return;
        }
        printParentForm(response, true, parent);


    }

    private void printParentForm(HttpServletResponse response, boolean editing, Parent parentditing) throws SQLException, ClassNotFoundException, IOException {

        PrintWriter writer = response.getWriter();
        writer.print("<form action='/admin-panel/parents' method='post' id='create-edit-parent-form'>"
                + "       <label> رقم الهــوية  :</label>\n"
                + "        <input name='national-id' class=' text-input ' " + (editing ? " value='" + parentditing.getNationalID() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label>  الإســم الأول  :</label>\n"
                + "        <input name='first-name' class=' text-input ' " + (editing ? " value='" + parentditing.getFirstName() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label> الإسم الأوسط :</label>\n"
                + "        <input name='middle-name' class=' text-input ' " + (editing ? " value='" + parentditing.getMiddleName() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label> العائلــة   :</label>\n"
                + "        <input name='last-name' class=' text-input ' " + (editing ? " value='" + parentditing.getLastName() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label> الجنسيــة   :</label>\n"
                + "        <input name='nationality' class=' text-input ' " + (editing ? "value='" + parentditing.getNationality() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label> رقم الجــوال :</label>\n"
                + "        <input name='mobileNumber' class=' text-input ' " + (editing ? " value='" + parentditing.getMobileN() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n"
                + "       <label> العنــوان :</label>\n"
                + "        <input name='address' class=' text-input ' " + (editing ? " value='" + parentditing.getAddress() + "'" : "") + " type='text' >\n"
                + "        <br>\n"
                + "        <br>\n");
        if (!editing)
            writer.print("       <label> كلمة المــرور :</label>\n"
                    + "        <input name='password' class=' text-input' type='password' >\n"
                    + "        <br>\n"
                    + "        <br>\n");
        writer.print("         <input type='hidden' name='action' value='" + (editing ? "edit" : "create") + "' />"
                + "         <input type='submit' value='حفظ' class='button-primary' />"
        );

        writer.print("</form>");


    }

    private void createParent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {
        ParentDao parentdao = new ParentDatabaseAccess();

        String nationalId = request.getParameter("national-id");
        if (!Validator.nationalId(nationalId)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الهوية غير صالح!"));
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


        String nationality = request.getParameter("nationality");
        if (!Validator.words(nationality)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الجنسية غير صالحة!"));
            return;
        }

        String mobileNo = request.getParameter("mobileNumber");
        if (!Validator.mobileNo(mobileNo)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("فضلا ادخل رقم الجوال !"));
            return;
        }

        String address = request.getParameter("address");

        String password = request.getParameter("password");
        if (password == null || password.length() < 6) {
            response.getWriter().print(ViewUtils.formatErrorMessage("كلمة المرور قصيرة !"));
            return;
        }

        Parent p = new Parent(nationalId, firstName, middleName, lastName, mobileNo, nationality, address);
        p.setPassword(Utils.hashPassword(password));
        parentdao.insert(p);


        response.getWriter().print(ViewUtils.formatSuccessMessage("تمت اضافة المعلومات الخاصة بولي الأمر بنجاح "));

    }

    private void editParent(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, IOException {

        ParentDao parentdao = new ParentDatabaseAccess();

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


        String nationality = request.getParameter("nationality");
        if (!Validator.words(nationality)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الجنسية غير صالحة!"));
            return;

        }

        String mobileNumber = request.getParameter("mobileNumber");
        if (!Validator.mobileNo(mobileNumber)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الجوال غير صالح!"));
            return;
        }

        String address = request.getParameter("address");

        Parent p = new Parent(nationalId, firstName, middleName, lastName, mobileNumber, nationality, address);
        parentdao.update(p);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تم تعديل بيانات ولي الأمر بنجاح"));

    }


}