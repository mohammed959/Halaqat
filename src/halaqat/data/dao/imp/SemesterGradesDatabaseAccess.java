package halaqat.data.dao.imp;

import halaqat.data.dao.abs.SemesterGradesDao;
import halaqat.data.data_structures.Pair;
import halaqat.data.pojos.MonthlyExam;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.SemesterGrades;
import halaqat.data.DatabaseConnectionManager;
import halaqat.data.pojos.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class SemesterGradesDatabaseAccess implements SemesterGradesDao {

    @Override
    public void insert(SemesterGrades semesterGrades) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `SemesterGrades` VALUES(" + semesterGrades.getSemesterId()
                + " ,'" + semesterGrades.getStudentId()
                + "' , " + semesterGrades.getFinalG()
                + " , " + semesterGrades.getAttendance()
                + " , " + semesterGrades.getBehavior() + ");";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(SemesterGrades semesterGrades) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `SemesterGrades` SET Final= " + semesterGrades.getFinalG()
                + ", Attendance= " + semesterGrades.getAttendance()
                + ", Behavior= " + semesterGrades.getBehavior()
                + " WHERE SemesterId = " + semesterGrades.getSemesterId() + " AND SID = '" + semesterGrades.getStudentId() + "';";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void delete(int semesterId, String studentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "DELETE FROM `SemesterGrades` WHERE `SemesterId`= " + semesterId + " AND `SID`= '" + studentId + "'";
        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();
    }

    @Override
    public SemesterGrades get(int semesterId, String studentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `SemesterGrades` WHERE `SemesterId`= " + semesterId + " AND `SID`= '" + studentId + "'";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            SemesterGrades semesterGrades = semesterGradesFromResult(resultSet);
            resultSet.close();
            connection.close();
            return semesterGrades;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<Pair<Student, SemesterGrades>> halaqaGrades(int semesterId, String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        sql = "SELECT * FROM Student LEFT JOIN SemesterGrades ON (Student.NationalId = SemesterGrades.SID AND SemesterGrades.SemesterId = " + semesterId + ") WHERE Student.HalaqaName= '" + halaqaName + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<Pair<Student, SemesterGrades>> halaqaGrades = new LinkedList<>();
        if (resultSet.first()) {
            do {
                halaqaGrades.add(studentGradesFromResult(resultSet));
            } while (resultSet.next());
        }
        return halaqaGrades;
    }

    @Override
    public List<Pair<Student, SemesterGrades>> dependentsGrades(int semesterId, String parentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        sql = "SELECT * FROM Student LEFT JOIN SemesterGrades ON (Student.NationalId = SemesterGrades.SID AND SemesterGrades.SemesterId = " + semesterId + ") WHERE Student.ParentId = '" + parentId + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<Pair<Student, SemesterGrades>> dependentsGrades = new LinkedList<>();
        if (resultSet.first()) {
            do {
                dependentsGrades.add(studentGradesFromResult(resultSet));
            } while (resultSet.next());
        }
        return dependentsGrades;
    }

    private SemesterGrades semesterGradesFromResult(ResultSet resultSet) throws SQLException {
        int semesterId = resultSet.getInt(1);
        String sid = resultSet.getString(2);
        int finalG = resultSet.getInt(3);
        int attendance = resultSet.getInt(4);
        int behavior = resultSet.getInt(5);

        return new SemesterGrades(semesterId, finalG, attendance, behavior, sid);
    }

    private Pair<Student, SemesterGrades> studentGradesFromResult(ResultSet resultSet) throws SQLException, ClassNotFoundException {
        String studentId = resultSet.getString(1);
        String firstName = resultSet.getString(2);
        String middleName = resultSet.getString(3);
        String lastName = resultSet.getString(4);
        int schoolLevel = resultSet.getInt(5);
        String birthDate = resultSet.getString(6);
        String registrationDate = resultSet.getString(7);
        String halaqaName = resultSet.getString(8);
        String parentId = resultSet.getString(9);
        int state = resultSet.getInt(10);
        // Column 11 for student password


        int semesterId = resultSet.getInt(12);
        String SID = resultSet.getString(13);
        SemesterGrades semesterGrades = null;
        if (SID != null) {
            int FinalG = resultSet.getInt(14);
            int Attendance = resultSet.getInt(15);
            int Behaviors = resultSet.getInt(16);
            semesterGrades = new SemesterGrades(semesterId, FinalG, Attendance, Behaviors, SID);
        }
        Student student = new Student(studentId, firstName, middleName, lastName, birthDate, registrationDate, state, schoolLevel, halaqaName, parentId);

        return new Pair<>(student, semesterGrades);
    }


}
