package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.ParentDao;
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


public class ParentDatabaseAccess implements ParentDao {

    @Override
    public boolean insert(Parent parent) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `Parent` VALUES('" + parent.getNationalID()
                + "' , '" + parent.getFirstName()
                + "' ,'" + parent.getMiddleName()
                + "', '" + parent.getLastName()
                + "', '" + parent.getNationality()
                + "', '" + parent.getMobileN()
                + "', '" + parent.getAddress()
                + "', '" + parent.getPassword() + "');";

        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;


    }

    @Override
    public boolean update(Parent parent) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Parent` SET FirstName ='" + parent.getFirstName()
                + "', MiddleName='" + parent.getMiddleName()
                + "', LastName='" + parent.getLastName()
                + "', Nationality='" + parent.getNationality()
                + "', MobileNo='" + parent.getMobileN()
                + "', Address='" + parent.getAddress()
                + "' WHERE NationalId= '" + parent.getNationalID() + "'";


        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;

    }

    @Override
    public boolean changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Parent` SET `Password` ='" + password
                + "' WHERE `NationalId`= '" + nationalId + "'";
        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;

    }

    @Override
    public boolean delete(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "DELETE FROM `Parent` WHERE `NationalId`= '" + nationalId + "'";
        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);
        connection.close();
        return b;

    }

    @Override
    public Parent get(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Parent` WHERE `Parent`.NationalId = '" + nationalId + "'";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Parent parent = parentFromResult(resultSet);
            resultSet.close();
            connection.close();
            return parent;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public Parent login(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT * FROM `Parent` WHERE `Parent`.NationalId = '" + nationalId + "' AND `Parent`.`Password`='" + password + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Parent parent = parentFromResult(resultSet);
            connection.close();
            return parent;
        }

        return null;
    }

    @Override
    public List<Parent> searchByName(String name) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Parent` WHERE CONCAT(`Parent`.`FirstName`,' ',`Parent`.`MiddleName`,' ', `Parent`.`LastName`) Like '%" + name + "%'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Parent> parents = parentsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return parents;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Parent>();
        }

    }

    private Parent parentFromResult(ResultSet resultSet) throws SQLException {

        String nationalId = resultSet.getString(1);
        String firstName = resultSet.getString(2);
        String middleName = resultSet.getString(3);
        String lastName = resultSet.getString(4);
        String nationality = resultSet.getString(5);
        String mobileNo = resultSet.getString(6);
        String address = resultSet.getString(7);
        Parent parent = new Parent(nationalId, firstName, middleName, lastName, mobileNo, nationality, address);

        return parent;
    }

    public List<Parent> parentsFromResult(ResultSet resultSet) throws SQLException {
        List<Parent> parents = new LinkedList<Parent>();
        do {
            parents.add(parentFromResult(resultSet));
        } while (resultSet.next());
        return parents;
    }
}
