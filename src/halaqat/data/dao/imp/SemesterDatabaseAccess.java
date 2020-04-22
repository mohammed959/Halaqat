package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.SemesterDao;
import halaqat.data.pojos.Semester;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class SemesterDatabaseAccess implements SemesterDao {

    @Override
    public void insert(Semester semester) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `Semester` (`Name`, `StartingDate`, `EndingDate`) VALUES('" + semester.getName()
                + "' ,'" + semester.getStartingDate()
                + "' ,'" + semester.getEndingDate() + "');";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(Semester semester) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Semester` SET `Name`='" + semester.getName()
                + "', StartingDate='" + semester.getStartingDate()
                + "', EndingDate='" + semester.getEndingDate()
                + "' WHERE Id = " + semester.getId() + " ;";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void delete(int id) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "DELETE FROM `Semester` WHERE `Id`= " + id;
        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();
    }

    @Override
    public Semester get(int id) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Semester` WHERE `Id`= " + id;

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Semester semester = semesterFromResult(resultSet);
            resultSet.close();
            connection.close();
            return semester;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public Semester get(String name) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Semester` WHERE `Name`= '" + name + "'";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Semester semester = semesterFromResult(resultSet);
            resultSet.close();
            connection.close();
            return semester;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<Semester> getAll() throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        sql = "SELECT * FROM `Semester` ORDER BY Id DESC";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<Semester> semesters = new LinkedList<>();
        if (resultSet.first()) {
            do {
                Semester semester = semesterFromResult(resultSet);
                semesters.add(semester);
            } while (resultSet.next());
        }
        return semesters;
    }

    @Override
    public Semester getLast() throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Semester` ORDER BY `Id` DESC LIMIT 1";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Semester semester = semesterFromResult(resultSet);
            resultSet.close();
            connection.close();
            return semester;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    private Semester semesterFromResult(ResultSet resultSet) throws SQLException {
        int Id = resultSet.getInt(1);
        String Name = resultSet.getString(2);
        String StartDate = resultSet.getString(3);
        String EndDate = resultSet.getString(4);

        Semester semester = new Semester(Id, Name, StartDate, EndDate);
        return semester;
    }


}
