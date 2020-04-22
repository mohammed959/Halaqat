package halaqat.view;

import halaqat.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class Index extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) { // The user is logged in
            int userType = (int) session.getAttribute(AppConstants.USER_TYPE_KEY);
            switch (userType) {
                case AppConstants.STUDENT_USER:
                    response.sendRedirect("/student");
                    break;
                case AppConstants.PARENT_USER:
                    response.sendRedirect("/parent-panel/index");
                    break;
                case AppConstants.TEACHER_USER:
                    response.sendRedirect("/teacher-panel/index");
                    break;
                case AppConstants.ADMIN_USER:
                    response.sendRedirect("/admin-panel/index");
                    break;
            }
        } else { // The user is not logged in
            displayLogin(request, response);
        }
    }

    private void displayLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        writer.println("<!DOCTYPE html>\n" +
                "<html lang='ar'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>نظام رتَِل لإدارة الحلقات</title>\n" +
                "    <link rel='stylesheet' type='text/css' href='/css/style.css'/>\n" +
                "       <link href='https://fonts.googleapis.com/css?family=Cairo:600' rel='stylesheet'>\n" +
                "</head>\n" +
                "<body id='index-body'>\n" +
                "<div class='overlay'>" +
                "<div id='header-container'>\n" +
                "    <h1>نظام رتِّل لإدارة الحلقات</h1>\n" +
                "</div>\n" +
                "\n" +
                "<div id='login-container'>\n" +
                "    <h3>تسجيل الدخول</h3>\n" +
                "    <form action='/user' method='post' id='login-halaqat.data-container'>\n" +
                "        <input required type='text' maxlength='10' placeholder='رقم الهوية/الإقامة' name='national-id' id='national-id'\n" +
                "               class='text-input'/>\n" +
                "        <input required type='password' name='password' placeholder='كلمة المرور' id='password' class='text-input'/>\n" +
                "\n");
        if (request.getParameterMap().containsKey("login-failed"))
            writer.println("<div class='error-message'>خطأ في رقم الهوية/الإقامة أو كلمة المرور</div>");


        writer.println(" <input type='hidden' name='action' value='login' />" +
                "       <input type='submit' value='تسجيل الدخول' name='login' class='button-primary' id='login-btn'/>\n" +
                "   </form>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}
