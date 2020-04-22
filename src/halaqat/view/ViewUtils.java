package halaqat.view;

import halaqat.data.dao.abs.RecitationDao;
import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.dao.imp.RecitationDatabaseAccess;
import halaqat.data.dao.imp.SemesterDatabaseAccess;
import halaqat.data.dao.imp.StudentDatabaseAccess;
import halaqat.data.pojos.Recitation;
import halaqat.data.pojos.Semester;
import halaqat.data.pojos.Student;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ViewUtils {

    public static String formatErrorMessage(String message) {
        return "<center><span class='error-message'>" + message + "</span></center>";
    }

    public static String formatSuccessMessage(String message) {
        return "<center><span class='success-message'>" + message + "</span></center>";
    }

    public static String formatInfoMessage(String message) {
        return "<center><span class='info-message'>" + message + "</span></center>";
    }


    public static void printStudentRecitationsTable(PrintWriter writer, String nationalId, int month, int year, boolean allowEditing) throws IOException, ParseException, SQLException, ClassNotFoundException {

        Calendar selectedDate = new GregorianCalendar(year, month - 1, 1); // Convert the month to 0-11 range
        StudentDao studentDao = new StudentDatabaseAccess();
        RecitationDao recitationDao = new RecitationDatabaseAccess();
        SemesterDao semesterDao = new SemesterDatabaseAccess();
        Semester currentSemester = semesterDao.getLast();

        List<Recitation> newRecitations = recitationDao.studentRecitations(nationalId, Recitation.TYPE_NEW, year, month);
        List<Recitation> revisions = recitationDao.studentRecitations(nationalId, Recitation.TYPE_REVISION, year, month);

        if (!allowEditing && newRecitations.size() == 0 && revisions.size() == 0) { // If editing is allowed, print the table even if it is empty.
            // To allow creating recitation
            writer.print(ViewUtils.formatInfoMessage("لا توجد بيانات!"));
            return;
        }
        Student student = studentDao.get(nationalId);

        writer.print("<button class='button-primary fa fa-print print-button' onclick='window.print()'>طباعة</button>");
        writer.print("<br><br><br>");
        writer.print("  <div id='section-to-print'>");
        writer.print("        <table id='section-to-print student-recitations-table'>\n" +
                "            <thead>\n" +
                "            <tr>\n" +
                "\n" +
                "                <th rowspan='2'> اليوم</th>\n" +
                "                <th rowspan='2'> النوع</th>\n" +
                "                <th colspan='2'> من</th>\n" +
                "                <th colspan='2'> إلى</th>\n" +
                "                <th rowspan='2'> التقدير</th>\n" +
                (allowEditing ? "<th class='not-print' rowspan='2'>خيارات</th>" : "") +
                "\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "                <th>االسورة</th>\n" +
                "                <th>الآية</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "\n" +
                "            <tbody>");


        int maxDaysOfMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dayNameFormatter = new SimpleDateFormat("EEEE'<br>'dd/MM", new Locale("ar", "sa"));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Date semesterStarting = dateFormatter.parse(currentSemester.getStartingDate());
        Date registrationDate = dateFormatter.parse(student.getRegistrationDate());
        Date today = new Date();
        for (int i = 1; i < maxDaysOfMonth; i++) {
            selectedDate.set(Calendar.DAY_OF_MONTH, i);

            int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                writer.print("<tr class='not-print'>" +
                        "<td class='vacation-day'>" + dayNameFormatter.format(selectedDate.getTime()) + "</td>" +
                        "<td class='vacation-day' colspan='" + (allowEditing ? 7 : 6) + "'></td>" +
                        "</tr>");
                continue;
            }


            String date = dateFormatter.format(selectedDate.getTime());
            Recitation newRecitation = recitationOfDay(newRecitations, date);

            if (newRecitation != null) {
                writer.print("<tr style=''>\n" +
                        "                <td rowspan='2'>" + dayNameFormatter.format(selectedDate.getTime()) + "</td>\n" +
                        "                <td>حفظ</td>\n" +
                        "                <td>" + newRecitation.getstartSorah() + "</td>\n" +
                        "                <td>" + (newRecitation.getstartAyah() == -1 ? "هـ" : newRecitation.getstartAyah()) + "</td>\n" +
                        "                <td>" + newRecitation.getendSorah() + "</td>\n" +
                        "                <td>" + (newRecitation.getendAyah() == -1 ? "هـ" : newRecitation.getendAyah()) + "</td>\n" +

                        "                <td>" + newRecitation.gradeText() + "</td>\n");

            } else {
                writer.print("<tr>\n" +
                        "                <td rowspan='2'>" + dayNameFormatter.format(selectedDate.getTime()) + "</td>\n" +
                        "                <td  class='no-recitation'>حفظ</td>\n"
                        + "               <td  class='no-recitation' colspan='5'></td>");

            }

            if (allowEditing) {
                Date iterDate = selectedDate.getTime();
                if (!iterDate.before(semesterStarting) && !iterDate.after(today)  && !iterDate.before(registrationDate)) {
                    writer.print("<td class='not-print'>");
                    writer.print("<a href='/teacher-panel/index?action=edit&national-id=" + nationalId + "&date=" + dateFormatter.format(iterDate) + "&type=" + Recitation.TYPE_NEW + "' class='fa fa-pencil edit-icon'></a>\n");
                    if (newRecitation != null)
                        writer.print("<a href='/teacher-panel/index?action=delete&national-id=" + nationalId + "&date=" + dateFormatter.format(iterDate) + "&type=" + Recitation.TYPE_NEW + "' class='fa fa-times delete-icon'></a>\n");
                    writer.print("</td>");
                } else {
                    writer.print("<td class='not-print disabled-cell'></td>");
                }
            }
            writer.print("            </tr>");

            // End of printing new recitation table row

            Recitation revisionRecitation = recitationOfDay(revisions, date);

            if (revisionRecitation != null) {
                writer.print("<tr>\n" +
                        "                <td>مراجعة</td>\n" +
                        "                <td>" + revisionRecitation.getstartSorah() + "</td>\n" +
                        "                <td>" + (revisionRecitation.getstartAyah() == -1 ? "هـ" : revisionRecitation.getstartAyah()) + "</td>\n" +
                        "                <td>" + revisionRecitation.getendSorah() + "</td>\n" +
                        "                <td>" + (revisionRecitation.getendAyah() == -1 ? "هـ" : revisionRecitation.getendAyah()) + "</td>\n" +

                        "                <td>" + revisionRecitation.gradeText() + "</td>\n");

            } else {
                writer.print("<tr>\n" +
                        "                <td  class='no-recitation'>مراجعة</td>\n"
                        + "               <td  class='no-recitation' colspan='5'></td>");

            }
            if (allowEditing) {
                Date iterDate = selectedDate.getTime();
                if (!iterDate.before(semesterStarting) && !iterDate.after(today)  && !iterDate.before(registrationDate)) {
                    writer.print("<td class='not-print'>");
                    writer.print("<a href='/teacher-panel/index?action=edit&national-id=" + nationalId + "&date=" + dateFormatter.format(iterDate) + "&type=" + Recitation.TYPE_REVISION + "' class='fa fa-pencil edit-icon'></a>\n");
                    if (revisionRecitation != null)
                        writer.print("<a href='/teacher-panel/index?action=delete&national-id=" + nationalId + "&date=" + dateFormatter.format(iterDate) + "&type=" + Recitation.TYPE_REVISION + "' class='fa fa-times delete-icon'></a>\n");
                    writer.print("</td>");
                } else {
                    writer.print("<td class='not-print disabled-cell'></td>");
                }
            }
            writer.print("            </tr>");

        }

        writer.print("            </tbody>\n" +
                "        </table>\n");

        writer.print("  </div>");
    }


    private static Recitation recitationOfDay(List<Recitation> newRecitaions, String date) throws ParseException {
        for (Recitation r : newRecitaions) {
            if (r.getDate().equals(date))
                return r;
        }

        return null;
    }

}
