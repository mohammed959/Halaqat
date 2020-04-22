package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.AttendanceDao;
import halaqat.data.pojos.Attendance;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;
import halaqat.utils.ResultUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class AttendanceDatabaseAccess implements AttendanceDao {
    @Override
    public void insert(Attendance attendance) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "INSERT INTO `Attendance` VALUES('" + attendance.getStudentNationalId()
                + "' , '" + attendance.getDate()
                + "' , " + (attendance.isPresent() ? 1 : 0) + ")";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(Attendance attendance) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Attendance` SET Present = " +
                (attendance.isPresent() ? 1 : 0) + " WHERE SID='" + attendance.getStudentNationalId()
                + "' AND ADate='" + attendance.getDate() + "'";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public Attendance get(String nationalId, String date) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Attendance` WHERE `SID` = '" + nationalId +
                "' AND `ADate`='" + date + "'";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Attendance attendance = attendanceFromResult(resultSet);
            resultSet.close();
            connection.close();
            return attendance;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<Attendance> monthlyAttendance(String nationalId, int year, int month) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Attendance` WHERE `SID` = '" + nationalId +
                "' AND `ADate` LIKE '" + year + "-" + (month < 10 ? "0" + month : month) + "-__' ORDER BY `ADate`";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            List<Attendance> attendanceList = attendanceListFromResult(resultSet);
            resultSet.close();
            connection.close();
            return attendanceList;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<>();
        }
    }

    @Override
    public List<Integer> distinctYearsForHalaqa(String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT DISTINCT YEAR(`ADate`) FROM `Attendance` WHERE `SID` IN (SELECT `NationalId` FROM `Student` WHERE `HalaqaName` = '" + halaqaName + "')";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            List<Integer> yearsList = ResultUtils.integerListFromResult(resultSet);
            resultSet.close();
            connection.close();
            return yearsList;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<>();
        }
    }



    @Override
    public List<Integer> distinctYearsForParent(String parentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT DISTINCT YEAR(`ADate`) FROM `Attendance` WHERE `SID` IN (SELECT `NationalId` FROM `Student` WHERE `ParentId` = '" + parentId + "')";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            List<Integer> yearsList = ResultUtils.integerListFromResult(resultSet);
            resultSet.close();
            connection.close();
            return yearsList;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<>();
        }
    }

    private List<Attendance> attendanceListFromResult(ResultSet resultSet) throws SQLException {
        List<Attendance> attendanceList = new LinkedList<>();
        do {
            attendanceList.add(attendanceFromResult(resultSet));
        } while (resultSet.next());
        return attendanceList;
    }


    private Attendance attendanceFromResult(ResultSet resultSet) throws SQLException {
        String studentId = resultSet.getString(1);
        String date = resultSet.getString(2);
        
        boolean present = resultSet.getInt(3) == 1 ? true : false;

        return new Attendance(studentId, date, present);
    }

}
