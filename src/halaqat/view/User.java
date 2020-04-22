package halaqat.view;

import halaqat.AppConstants;
import halaqat.data.dao.abs.EmployeeDao;
import halaqat.data.dao.abs.ParentDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.EmployeeDatabaseAccess;
import halaqat.data.dao.imp.ParentDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Employee;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;
import halaqat.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class User extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("/");
            return;
        }
        try {
            switch (action) {
                case "login":
                    login(request, response);
                    break;
                case "change-password":
                    changePassword(request, response);
                    break;
                default:
                    response.sendRedirect("/");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().print(ViewUtils.formatErrorMessage("حدث خطأ غير متوقع"));
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession(false) == null) {
            response.sendRedirect("/");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("/");
            return;
        }

        switch (action) {
            case "logout":
                request.getSession(false).invalidate();
                response.sendRedirect("/");
                break;
            case "change-password":
                printChangePasswordForm(request, response);
                break;
            default:
                response.sendRedirect("/");
        }


    }

    private void printChangePasswordForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        printHeader(request, response);
        PrintWriter writer = response.getWriter();
        String message = request.getParameter("message");
        if (message != null) {
            switch (message) {
                case "changed":
                    writer.print("<br><br>");
                    writer.print(ViewUtils.formatSuccessMessage("تم تغيير كلمة المرور بنجاح!"));
                    writer.print("<br><br>");
                    break;
                case "short-pass":
                    writer.print("<br><br>");
                    response.getWriter().print(ViewUtils.formatErrorMessage("كلمة المرور قصيرة جداً"));
                    writer.print("<br><br>");
                    break;
                case "old-pass-incorrect":
                    writer.print("<br><br>");
                    response.getWriter().print(ViewUtils.formatErrorMessage("كلمة المرور القديمة غير صحيحة!"));
                    writer.print("<br><br>");
                    break;
                default:
                    response.sendRedirect("/");
                    return;
            }
        } else {

            writer.print("<center>" +
                    "      <form action='/user' method='post'>\n" +
                    "        <div class='input-item'>\n" +
                    "          <label for='old'>كلمة المرور القديمة:</label>\n" +
                    "          <input type='password' id='old' name='old' class='text-input'/>\n" +
                    "        </div>\n" +
                    "        <div class='input-item'>\n" +
                    "          <label for='new'>كلمة المرور الجديدة:</label>\n" +
                    "          <input type='password' id='new' name='new' class='text-input'/>\n" +
                    "        </div>\n" +
                    "        <input type='hidden' name='action' value='change-password' />\n" +
                    "        <br><br>\n" +
                    "        <input type='submit' value='تغيير'  class='button-primary' />\n" +
                    "        <br><br>\n" +
                    "      </form>\n" +
                    "    </center>\n");


        }


        printFooter(request, response);

    }

    private void printHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        String name = request.getSession().getAttribute(AppConstants.FNAME_KEY) + " " + request.getSession().getAttribute(AppConstants.LNAME_KEY);
        response.getWriter().print("<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>تغيير كلمة المرور</title>\n" +
                "    <link rel='stylesheet' type='text/css' href='css/admin-panel.css'/>\n" +
                "    <link rel='stylesheet' type='text/css' href='css/style.css'/>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id='header-container'>\n" +
                "    <h1>نظام رتِّل لإدارة الحلقات</h1>\n" +
                "\n" +
                "  <div id='user'>\n" +
                "      <div class='fa fa-user' id='user-name'><span>" + name + "</span></div>\n" +
                "      <div id='user-options'>\n" +
                "        <a href='/user?action=change-password'>تغيير كلمة المرور</a>\n" +
                "        <a href='/user?action=logout'>تسجيل الخروج</a>\n" +
                "      </div>\n" +
                "  </div>" +
                "</div>\n" +
                "\n" +
                "<div id='change-password-container'>\n" +
                "    <h3>تغيير كلمة المرور</h3>\n");
    }


    private void printFooter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().print("</div>\n" +
                "</body>\n" +
                "</html>\n");
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        HttpSession session = request.getSession(false);
        if (session != null) { // User already logged in..
            // Forward the user to the index
            response.sendRedirect("/index");
            return;
        }

        String nationalId = request.getParameter("national-id");
        String password = request.getParameter("password");

        if (password == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال كلمة المرور!"));
            return;
        }
        String hashedPassword = Utils.hashPassword(password);

        StudentDao studentDao = new StudentDatabaseAccess();
        Student student = studentDao.login(nationalId, hashedPassword);

        if (student != null) {
            HttpSession newSession = request.getSession();
            newSession.setAttribute(AppConstants.NATIONAL_ID_KEY, nationalId);
            newSession.setAttribute(AppConstants.USER_TYPE_KEY, AppConstants.STUDENT_USER);
            newSession.setAttribute(AppConstants.FNAME_KEY, student.getFirstName());
            newSession.setAttribute(AppConstants.LNAME_KEY, student.getLastName());
            response.sendRedirect("/student");
            return;
        }

        EmployeeDao employeeDao = new EmployeeDatabaseAccess();

        Employee employee = employeeDao.login(nationalId, hashedPassword);
        if (employee != null) {
            HttpSession newSession = request.getSession();
            newSession.setAttribute(AppConstants.NATIONAL_ID_KEY, nationalId);
            int type = employee.getJobTitle() == Employee.TYPE_ADMINISTRATOR ? AppConstants.ADMIN_USER : AppConstants.TEACHER_USER;
            newSession.setAttribute(AppConstants.USER_TYPE_KEY, type);
            newSession.setAttribute(AppConstants.FNAME_KEY, employee.getFirstName());
            newSession.setAttribute(AppConstants.LNAME_KEY, employee.getLastName());
            response.sendRedirect(type == AppConstants.ADMIN_USER ? "/admin-panel/index" : "/teacher-panel/index");
            return;
        }


        ParentDao parentDao = new ParentDatabaseAccess();

        Parent parent = parentDao.login(nationalId, hashedPassword);
        if (parent != null) {
            HttpSession newSession = request.getSession();
            newSession.setAttribute(AppConstants.NATIONAL_ID_KEY, nationalId);
            newSession.setAttribute(AppConstants.USER_TYPE_KEY, AppConstants.PARENT_USER);
            newSession.setAttribute(AppConstants.FNAME_KEY, parent.getFirstName());
            newSession.setAttribute(AppConstants.LNAME_KEY, parent.getLastName());
            response.sendRedirect("/parent-panel/index");
            return;
        }


        response.sendRedirect("/index?login-failed=true");


    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/");
            return;
        }

        String oldPassword = request.getParameter("old");
        if (oldPassword == null) {
            response.getWriter().print(ViewUtils.formatErrorMessage("يرجى إدخال كلمة المرور القديمة"));
            return;
        }
        String hashedOldPassword = Utils.hashPassword(oldPassword);
        String newPassword = request.getParameter("new");

        if (newPassword == null || newPassword.length() < 6) {
            response.sendRedirect("/user?action=change-password&message=short-pass");
            return;
        }
        String hashedNewPassword = Utils.hashPassword(newPassword);

        String nationalId = (String) session.getAttribute(AppConstants.NATIONAL_ID_KEY);
        int userType = (int) session.getAttribute(AppConstants.USER_TYPE_KEY);
        switch (userType) {
            case AppConstants.ADMIN_USER:
            case AppConstants.TEACHER_USER:
                EmployeeDao employeeDao = new EmployeeDatabaseAccess();

                if (employeeDao.login(nationalId, hashedOldPassword) == null) {
                    response.sendRedirect("/user?action=change-password&message=old-pass-incorrect");
                    return;
                }

                employeeDao.changePassword(nationalId, hashedNewPassword);
                break;

            case AppConstants.STUDENT_USER:
                StudentDao studentDao = new StudentDatabaseAccess();
                if (studentDao.login(nationalId, hashedOldPassword) == null) {
                    response.sendRedirect("/user?action=change-password&message=old-pass-incorrect");
                    return;
                }
                studentDao.changePassword(nationalId, hashedNewPassword);
                break;
            case AppConstants.PARENT_USER:
                ParentDao parentDao = new ParentDatabaseAccess();
                if (parentDao.login(nationalId, hashedOldPassword) == null) {
                    response.sendRedirect("/user?action=change-password&message=old-pass-incorrect");
                    return;
                }
                parentDao.changePassword(nationalId, hashedNewPassword);
                break;
        }

        response.sendRedirect("/user?action=change-password&message=changed");
    }


}
