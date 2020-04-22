package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.HalaqaDao;
import halaqat.data.pojos.Employee;
import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes.Name;


public class HalaqaDatabaseAccess implements HalaqaDao {

    @Override
    public void insert(Halaqa halaqa) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "INSERT INTO `Halaqa` VALUES('" + halaqa.getname()
                + "' , " + halaqa.getnumberOfNewLine()
                + " , " + halaqa.getnumberOfReviewLine()
                + " , '" + halaqa.getTeacherId() + "')";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(Halaqa halaqa, String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Halaqa` SET Name ='" + halaqa.getname()
                + "' , NumberOfNewLine=" + halaqa.getnumberOfNewLine()
                + " , NumberOfReviewLine=" + halaqa.getnumberOfReviewLine()
                + " ,TeacherId= '" + halaqa.getTeacherId()
                + "' WHERE Name= '" + halaqaName + "'";

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();

    }

    @Override
    public void delete(String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "DELETE FROM `Halaqa` WHERE `Name`= '" + halaqaName + "'";

        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();
    }

    @Override
    public Halaqa get(String name, boolean withTeacher) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        if (withTeacher) {
            sql = "SELECT * FROM `Halaqa`, `Employee` WHERE `Halaqa`.Name = '" + name + "' AND `Halaqa`.TeacherId = `Employee`.NationalId";
        } else {
            sql = "SELECT * FROM `Halaqa` WHERE `Name`= '" + name + "'";
        }

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Halaqa halaqa = halaqaFromResult(resultSet, withTeacher);
            resultSet.close();
            connection.close();
            return halaqa;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }

    }


    @Override
    public Halaqa byTeacher(String teacherId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT * FROM `Halaqa` WHERE `TeacherId`= '" + teacherId + "'";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Halaqa halaqa = halaqaFromResult(resultSet, false);
            resultSet.close();
            connection.close();
            return halaqa;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<Halaqa> getAll(boolean withTeachers) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        if (withTeachers) {
            sql = "SELECT * FROM `Halaqa`, `Employee` WHERE `Halaqa`.TeacherId = `Employee`.NationalId";
        } else {
            sql = "SELECT * FROM `Halaqa`";
        }

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<Halaqa> halaqat = new LinkedList<>();
        if (resultSet.first()) {
            do {
                Halaqa h = halaqaFromResult(resultSet, withTeachers);
                halaqat.add(h);
            } while (resultSet.next());
        }
        return halaqat;
    }


    private Halaqa halaqaFromResult(ResultSet resultSet, boolean withTeacher) throws SQLException {
        String name = resultSet.getString(1);
        int numberOfNewLine = resultSet.getInt(2);
        int numberOfReviewLine = resultSet.getInt(3);
        String teacherId = resultSet.getString(4);

        Halaqa halaqa = new Halaqa(numberOfNewLine, numberOfReviewLine, name, teacherId);
        if (withTeacher) {
            String nationalId = resultSet.getString(5);
            String firstName = resultSet.getString(6);
            String middleName = resultSet.getString(7);
            String lastName = resultSet.getString(8);
            String nationality = resultSet.getString(9);
            String address = resultSet.getString(10);
            String mobileNo = resultSet.getString(11);
            int jobTitle = resultSet.getInt(12);
            String qualification = resultSet.getString(13);
            String birthDay = resultSet.getString(14);
            int employeeState = resultSet.getInt(15);
            String registrationDate = resultSet.getString(16);
            Employee emp = new Employee(nationalId, firstName, middleName, lastName, jobTitle, address, qualification, employeeState, nationality, mobileNo, registrationDate, birthDay);
            halaqa.setTeacher(emp);
        }
        return halaqa;
    }
}
