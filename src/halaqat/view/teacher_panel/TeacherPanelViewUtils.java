package halaqat.view.teacher_panel;

import halaqat.AppConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TeacherPanelViewUtils {

    public static void printHeaderWithMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        String teacherName = request.getSession().getAttribute(AppConstants.FNAME_KEY) + " "
                + request.getSession().getAttribute(AppConstants.LNAME_KEY);

        response.getWriter().print("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\n" +
                "    <meta charset='utf-8'>\n" +
                "    <title> لوحة تحكم الأستاذ </title>\n" +
                "    <link rel='stylesheet' href='/css/teacher-panel.css'>\n" +
                "    <link rel='stylesheet' href='/css/font-awesome.min.css'>\n" +
                "    <link rel='stylesheet' href='/css/style.css'>\n" +
                "    <link href='https://fonts.googleapis.com/css?family=Cairo:600' rel='stylesheet'>\n" +
                "    <script src='/js/script.js'></script>"+
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id='header-container'>\n" +
                "<div id='header-content'>" +
                "    <h1>نظام رتِّل لإدارة الحلقات</h1>\n" +
                "\n" +
                "  <div id='user'>\n" +
                "      <div class='fa fa-user' id='user-name'><span>" + teacherName + "</span></div>\n" +
                "      <div id='user-options'>\n" +
                "        <a href='/user?action=change-password'>تغيير كلمة المرور</a>\n" +
                "        <a href='/user?action=logout'>تسجيل الخروج</a>\n" +
                "      </div>\n" +
                "  </div>" +
                "</div>\n" +
                "<div class='navbar-container'>\n" +
                "    <div class='navbar'>\n" +
                "\n" +
                "        <ul>\n" +
                "            <li>\n" +
                "\n" +
                "                <a href='/teacher-panel/index'>تسميع اليوم</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "\n" +
                "                <a href='/teacher-panel/grades?action=edit'>درجات الطلاب</a>\n" +
                "\n" +
                "            </li>\n" +
                "            <li>\n" +
                "\n" +
                "                <a href='/teacher-panel/attendance'>سجل الحضور</a>\n" +
                "\n" +
                "            </li>\n" +
                "\n" +
                "        </ul>\n" +
                "\n" +
                "    </div>\n" +
                "    <br>\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "<div id='content-container'>");
    }


    public static void printFooter(PrintWriter printWriter) {
        printWriter.write("</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }

}
