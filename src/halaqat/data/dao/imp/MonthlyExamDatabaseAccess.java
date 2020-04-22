package halaqat.data.dao.imp;

import halaqat.data.dao.abs.MonthlyExamDao;
import halaqat.data.pojos.Employee;
import halaqat.data.DatabaseConnectionManager;
import halaqat.data.pojos.MonthlyExam;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MonthlyExamDatabaseAccess implements MonthlyExamDao {
    @Override
    public void insert(MonthlyExam monthlyExam) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `MonthlyExam` VALUES(" + monthlyExam.getSemesterId()
                + " , '" + monthlyExam.getNationalId()
                + "' , " + monthlyExam.getExamNumber()
                + " , " + monthlyExam.getGrade() + ");";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(MonthlyExam monthlyExam) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `MonthlyExam` SET Grade =" + monthlyExam.getGrade()
                + " WHERE SemesterId = " + monthlyExam.getSemesterId() + " AND SID = '" + monthlyExam.getNationalId() + "' AND Number = " + monthlyExam.getExamNumber() + ";";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void delete(int semesterId, String studentId, int number) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "DELETE FROM `MonthlyExam` WHERE `SemesterId`= " + semesterId + " AND `SID`= '" + studentId + "' AND `Number`= " + number;
        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();
    }

    @Override
    public MonthlyExam get(int semesterId, String studentId, int number) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `MonthlyExam` WHERE `SemesterId`= " + semesterId + " AND `SID`= '" + studentId + "' AND `Number`= " + number;

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            MonthlyExam monthlyExam = monthlyExamFromResult(resultSet);
            resultSet.close();
            connection.close();
            return monthlyExam;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<MonthlyExam> getAll(int semesterId, String studentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        sql = "SELECT * FROM `MonthlyExam` WHERE `SemesterId`=" + semesterId + " AND `SID`='" + studentId + "' ";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<MonthlyExam> monthlyExams = new LinkedList<>();
        if (resultSet.first()) {
            do {
                MonthlyExam ME = monthlyExamFromResult(resultSet);
                monthlyExams.add(ME);
            } while (resultSet.next());
        }
        return monthlyExams;
    }

    private MonthlyExam monthlyExamFromResult(ResultSet resultSet) throws SQLException {
        int semesterId = resultSet.getInt(1);
        String SID = resultSet.getString(2);
        int examNumber = resultSet.getInt(3);
        int grade = resultSet.getInt(4);

        return new MonthlyExam(semesterId, SID, examNumber, grade);
    }
}
