package halaqat.view.admin_panel;

import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.dao.imp.SemesterDatabaseAccess;
import halaqat.data.pojos.Recitation;
import halaqat.data.pojos.Semester;
import halaqat.view.ViewUtils;
import sun.rmi.runtime.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Settings extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminViewUtils.printHeaderWithMenu(request, response);
        String action = request.getParameter("action");
        try {
            if (action == null) {
                printSettingsPage(request, response);
            } else {
                switch (action) {
                    case "previous-semesters":
                        printPreviousSemesters(request, response);
                        break;
                    case "create-semester":
                        printCreateSemesterPage(request, response);
                        break;
                    case "edit-semester":
                        printEditSemesterPage(request, response);
                        break;
                    case "delete-semester":
                        printSemesterDeleteConfirmation(request, response);
                        break;
                    default:
                        printSettingsPage(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        AdminViewUtils.printFooter(response.getWriter());
    }

    private void printSemesterDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        String idParam = request.getParameter("id");
        int id = 0;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الفصل غير صالح"));
        }


        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester semester = semesterDao.get(id);

        if (semester == null) {
            writer.print(ViewUtils.formatErrorMessage("الفصل الدراسي غير موجود"));
            return;
        }


        writer.print("<center>" +
                ViewUtils.formatErrorMessage("تحذير: حذف الفصل الدراسي يؤدي إلى حذف جميع درجات الطلاب المرتبطة به!")
                + "<br><br><h5>هل أنت متأكد أنك تريد حذف الفصل الدراسي " + semester.getName() + "</h5>" +
                "<form action='/admin-panel/settings' method='post'>\n" +
                "\n" +
                "<input type='hidden' name='id' value='" + semester.getId() + "' />\n" +
                "<input type='hidden' name='action' value='delete-semester' />\n" +
                "\n<br><br>" +
                "<input type='submit' class='button-red' value='نعم' />\n" +
                "<button type='button' class='button-green' onclick='window.history.back()'>لا</button>\n" +
                "\n" +
                "</form></center>");

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
                case "create-semester":
                    createSemester(request, response);
                    break;
                case "edit-semester":
                    editSemester(request, response);
                    break;
                case "delete-semester":
                    deleteSemester(request, response);
                    break;
                default:
                    response.sendRedirect("/admin-panel/index");
                    return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع!"));
        }

        AdminViewUtils.printFooter(response.getWriter());
    }

    private void deleteSemester(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        String idParam = request.getParameter("id");
        int id = 0;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الفصل غير صالح"));
        }


        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester semester = semesterDao.get(id);

        if (semester == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الفصل الدراسي غير موجود"));
            return;
        }

        semesterDao.delete(id);
        response.getWriter().print(ViewUtils.formatSuccessMessage("تم حذف الفصل الدراسي والدرجات المرتبطة به"));
        response.getWriter().print("<meta http-equiv='Refresh' content='2;url=/admin-panel/settings?action=previous'>");

    }

    private void printEditSemesterPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter printWriter = response.getWriter();
        String idParam = request.getParameter("id");
        int id = 0;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            printWriter.print(ViewUtils.formatErrorMessage("رقم الفصل غير صالح"));
        }

        printSemesterForm(printWriter, true, id);

    }

    private void printPreviousSemesters(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        SemesterDao semesterDao = new SemesterDatabaseAccess();

        List<Semester> semesters = semesterDao.getAll();

        if (semesters.size() == 0) {
            writer.print(ViewUtils.formatInfoMessage("لا توجد فصول دراسية!"));
            return;
        }

        writer.print("<h3 id='section-title'>الفصول الدراسية السابقة</h3>\n" +
                "\n" +
                "    <center><br><br>\n" +
                "\n" +
                "      <table>\n" +
                "        <thead>\n" +
                "          <tr>\n" +
                "            <th>#</th>\n" +
                "            <th>الفصل الدراسي</th>\n" +
                "            <th>تاريخ البداية</th>\n" +
                "            <th>تاريخ النهاية</th>\n" +
                "            <th>خيارات</th>\n" +
                "          </tr>\n" +
                "        </thead>\n" +
                "        <tbody>\n");


        for (Semester semester :
                semesters) {
            writer.print("          <tr>\n" +
                    "            <td>" + semester.getId() + "</td>\n" +
                    "            <td>" + semester.getName() + "</td>\n" +
                    "            <td>" + semester.getStartingDate().replace('-', '/') + "</td>\n" +
                    "            <td>" + semester.getEndingDate().replace('-', '/') + "</td>\n" +
                    "            <td>" +
                    "               <a class='fa fa-pencil edit-icon' href='/admin-panel/settings?action=edit-semester&id=" + semester.getId() + "'></a>" +
                    "               <a class='fa fa-times delete-icon' href='/admin-panel/settings?action=delete-semester&id=" + semester.getId() + "'></a>" +
                    "</td>\n" +
                    "          </tr>\n");

        }

        writer.print("        </tbody>\n" +
                "\n" +
                "      </table>\n" +
                "\n" +
                "    </center>");
    }


    private void printCreateSemesterPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        response.getWriter().print("    <h3 id='section-title'>فصل دراسي جديد</h3>\n");
        printSemesterForm(response.getWriter(), false, 0);
    }

    private void printSemesterForm(PrintWriter writer, boolean edit, int editingSemesterId) throws SQLException, ClassNotFoundException {

        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester editingSemester = null;
        if (edit) {
            editingSemester = semesterDao.get(editingSemesterId);

            if (editingSemester == null) {
                writer.print(ViewUtils.formatErrorMessage("الفصل الدراسي غير موجود"));
                return;
            }
        }
        int year = new Date().getYear() + 1900;

        writer.print("    <form method='post' action='/admin-panel/settings'>\n" +
                "      <div class='input-item'>\n" +
                "        <label for='semester-year'>الفصل الدارسي :</label>\n" +
                "        <input name='name' type='text' placeholder='الفصل الدراسي الأول " + year
                + "/" + (year + 1) + "' id='semester-year' value='" + (edit ? editingSemester.getName() : "")
                + "' class='text-input' />" +
                "      </div>\n" +
                "      <div class='input-item'>\n" +
                "        <label for='starting-date'>تاريخ البداية : </label>\n" +
                "        <input name='starting-date' value='" + (edit ? editingSemester.getStartingDate() : "") + "' type='date' id='starting-date' />\n" +
                "      </div>\n" +
                "\n" +
                "      <div class='input-item'>\n" +
                "        <label for='ending-date'>تاريخ النهاية : </label>\n" +
                "        <input name='ending-date' value='" + (edit ? editingSemester.getEndingDate() : "") + "' type='date' id='starting-date' />\n" +
                "      </div>\n" +
                "\n" +
                "      <br><br>\n" +
                "\n" +
                "      <p class='red-color'>* إنشاء فصل دراسي جديد يمنع المدرسين من تعديل أي بيانات (تحضير، تسميع، درجات) خاصة بالفصول القديمة.</p><br>\n");

        if (edit) {
            writer.print("      <input type='hidden' name='id' value='" + editingSemester.getId() + "' />");
            writer.print("      <input type='hidden' name='action' value='edit-semester' />");
        } else {
            writer.print("      <input type='hidden' name='action' value='create-semester' />");
        }

        writer.print("      <center><input type='submit' value='حفظ' class='button-primary save' /></center>\n" +
                "    </form>");
    }

    private void printSettingsPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        PrintWriter writer = response.getWriter();

        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester current = semesterDao.getLast();

        writer.print("<h3 id='section-title'>إعدادات النظام</h3>\n" +
                "\n" +
                "      <h4 class='settings-item blue-color'>الفصل الدراسي الحالي: </h4>\n" +
                "      <div class='settings-item-container'>\n" +
                "        <span>" + (current != null ? current.getName() : "لا يوجد") + "</span>\n" +
                "        <div id='semester-options'>\n" +
                "          <a class='button-green' href='/admin-panel/settings?action=previous-semesters'>عرض الفصول الدراسية السابقة</a>\n" +
                "          <a class='button-primary' href='/admin-panel/settings?action=create-semester'>إنشاء فصل دراسي جديد</a>\n" +
                "        </div>\n" +
                "      </div>");


    }


    private void editSemester(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {

        String idParam = request.getParameter("id");
        int id = 0;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("رقم الفصل غير صالح"));
        }

        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester semester = semesterDao.get(id);

        if (semester == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("الفصل الدراسي غير موجود"));
            return;
        }

        String name = request.getParameter("name");
        if (name == null || name.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الفصل الدراسي"));
            return;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String startingDateParam = request.getParameter("starting-date");
        if (startingDateParam == null || startingDateParam.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال تاريخ بداية الفصل الدراسي"));
            return;
        }

        Date startingDate = null;
        try {
            startingDate = dateFormatter.parse(startingDateParam);
        } catch (ParseException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ بداية الفصل غير صالح!"));
            return;
        }

        String endingDateParam = request.getParameter("ending-date");
        if (endingDateParam == null || endingDateParam.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال تاريخ بداية الفصل الدراسي"));
            return;
        }

        Date endingDate = null;
        try {
            endingDate = dateFormatter.parse(endingDateParam);
        } catch (ParseException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ نهاية الفصل غير صالح!"));
            return;
        }

        if (endingDate.before(startingDate)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ نهاية الفصل لا يمكن أن يكون قبل تاريخ بدايته"));
            return;
        }


        Semester editedSemester = new Semester(id, name, startingDateParam, endingDateParam);
        semesterDao.update(editedSemester);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تم تعديل الفصل الدراسي"));
        response.getWriter().print("<meta http-equiv='Refresh' content='2;url=/admin-panel/settings?action=previous-semesters'>");
    }

    private void createSemester(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        String name = request.getParameter("name");
        if (name == null || name.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال اسم الفصل الدراسي"));
            return;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String startingDateParam = request.getParameter("starting-date");
        if (startingDateParam == null || startingDateParam.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال تاريخ بداية الفصل الدراسي"));
            return;
        }

        Date startingDate = null;
        try {
            startingDate = dateFormatter.parse(startingDateParam);
        } catch (ParseException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ بداية الفصل غير صالح!"));
            return;
        }

        String endingDateParam = request.getParameter("ending-date");
        if (endingDateParam == null || endingDateParam.length() == 0) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال تاريخ بداية الفصل الدراسي"));
            return;
        }

        Date endingDate = null;
        try {
            endingDate = dateFormatter.parse(endingDateParam);
        } catch (ParseException e) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ نهاية الفصل غير صالح!"));
            return;
        }

        if (endingDate.before(startingDate)) {
            response.getWriter().print(ViewUtils.formatErrorMessage("تاريخ نهاية الفصل لا يمكن أن يكون قبل تاريخ بدايته"));
            return;
        }

        SemesterDao semesterDao = new SemesterDatabaseAccess();

        Semester semester = new Semester(name, startingDateParam, endingDateParam);
        semesterDao.insert(semester);

        response.getWriter().print(ViewUtils.formatSuccessMessage("تم إنشاء فصل دراسي جديد"));
        response.getWriter().print("<meta http-equiv='Refresh' content='2;url=/admin-panel/settings'>");

    }

}
