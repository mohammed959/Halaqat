package halaqat.view.admin_panel;

import halaqat.AppConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminViewUtils {

    public static void printHeaderWithMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = response.getWriter();
        String employeeName = request.getSession().getAttribute(AppConstants.FNAME_KEY).toString() + " " +
                request.getSession().getAttribute(AppConstants.LNAME_KEY).toString();


        printWriter.print("<!DOCTYPE html>\n" +
                "<html lang='ar'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>لوحة تحكم الإدارة</title>\n" +
                "    <link rel='stylesheet' type='text/css' href='/css/admin-panel.css'/>\n" +
                "    <link rel='stylesheet' type='text/css' href='/css/font-awesome.min.css'/>\n" +
                "    <link rel='stylesheet' type='text/css' href='/css/style.css'/>\n" +
                "    <link href='https://fonts.googleapis.com/css?family=Cairo:600' rel='stylesheet'>\n"+
                "</head>\n" +
                "<body dir='rtl'>\n" +
                "\n" +
                "<div id='header-container'>\n" +
                "    <h1>نظام رتِّل لإدارة الحلقات</h1>\n" +
                "\n" +
                "  <div id='user'>\n" +
                "      <div class='fa fa-user' id='user-name'><span>" + employeeName + "</span></div>\n" +
                "      <div id='user-options'>\n" +
                "        <a href='/user?action=change-password'>تغيير كلمة المرور</a>\n" +
                "        <a href='/user?action=logout'>تسجيل الخروج</a>\n" +
                "      </div>\n" +
                "  </div>" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div id='menu-container'>\n" +
                "\n" +
                "    <ul>\n" +
                "        <li>\n" +
                "            <a class='current-item' href='/admin-panel/index'>الرئيسية</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <ul>\n" +
                "        <span>إدارة الموظفين</span>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/employees?action=all'>كل الموظفين</a>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/employees?action=create'>أضف موظف</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <ul>\n" +
                "        <span>إدارة الحلقات</span>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/halaqat?action=all'>كل الحلقات</a>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/halaqat?action=create'>أضف حلقة</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <ul>\n" +
                "        <span>إدارة الطلاب</span>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/students?action=search'>بحث عن طالب</a>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/students?action=by-halaqa'>عرض حسب الحلقة</a>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/students?action=create'>أضف طالب</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "\n" +
                "\n" +
                "    <ul>\n" +
                "        <span>إدارة أولياء الأمور</span>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/parents?action=search'>بحث عن ولي أمر</a>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/parents?action=create'>أضف ولي أمر</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "    <ul>\n" +
                "        <li>\n" +
                "            <a href='/admin-panel/settings'>الإعدادات</a>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "\n" +
                "<div id='content-container'>");
    }


    public static void printFooter(PrintWriter printWriter) throws IOException {
        printWriter.write("    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }

}
