package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.EmployeeDao;
import halaqat.data.pojos.Employee;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.sql.Statement;

public class EmployeeDatabaseAccess implements EmployeeDao {

    @Override
    public boolean insert(Employee employee) throws SQLException, ClassNotFoundException {

        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `Employee` VALUES('" + employee.getNationalID()
                + "' , '" + employee.getFirstName()
                + "' ,'" + employee.getMiddleName()
                + "', '" + employee.getLastName()
                + "', '" + employee.getNationality()
                + "', '" + employee.getAddress()
                + "', '" + employee.getMobileNo()
                + "', " + employee.getJobTitle()
                + " , '" + employee.getQualification()
                + "' , '" + employee.getBirthDay()
                + "', " + employee.getEmployeeState()
                + " , CURDATE() , '" + employee.getPassword() + "');";


        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;
    }

    @Override
    public boolean update(Employee employee) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Employee` SET FirstName ='" + employee.getFirstName()
                + "', MiddleName='" + employee.getMiddleName()
                + "', LastName='" + employee.getLastName()
                + "', Nationality='" + employee.getNationality()
                + "', Address='" + employee.getAddress()
                + "', MobileNo='" + employee.getMobileNo()
                + "', JobTitle=" + employee.getJobTitle()
                + ", Qualification='" + employee.getQualification()
                + "', BirthDate='" + employee.getBirthDay()
                + "', EmployeeState=" + employee.getEmployeeState()
                + " WHERE NationalId= '" + employee.getNationalID() + "'";


        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;
    }

    @Override
    public boolean changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Employee` SET `Password` ='" + password
                + "' WHERE `NationalId`= '" + nationalId + "'";
        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);

        connection.close();
        return b;
    }

    @Override
    public boolean delete(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "DELETE FROM `Employee` WHERE `NationalId`= '" + nationalId + "'";
        Statement statement = connection.createStatement();
        boolean b = statement.execute(sql);
        connection.close();
        return b;
    }

    @Override
    public Employee get(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Employee` WHERE `Employee`.NationalId = " + nationalId + "";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Employee employee = employeeFromResult(resultSet);
            resultSet.close();
            connection.close();
            return employee;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public Employee login(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT * FROM `Employee` WHERE `Employee`.NationalId = '" + nationalId + "' AND `Employee`.`Password`='" + password + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Employee employee = employeeFromResult(resultSet);
            connection.close();
            return employee;
        }

        return null;
    }

    @Override
    public List<Employee> getAll() throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql;
        sql = "SELECT * FROM `Employee`";


        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        List<Employee> employees = new LinkedList<>();
        if (resultSet.first()) {
            do {
                Employee emp = employeeFromResult(resultSet);
                employees.add(emp);
            } while (resultSet.next());
        }
        return employees;
    }

    @Override
    public HashMap<Integer, Integer> numberOfEmployees() throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT JobTitle, COUNT(*) FROM `Employee` GROUP BY JobTitle";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        HashMap<Integer, Integer> employeesCount = new HashMap<>();

        if (resultSet.first()) {
            do {
                employeesCount.put(resultSet.getInt(1), resultSet.getInt(2));
            } while (resultSet.next());
        }
        return employeesCount;
    }


    private Employee employeeFromResult(ResultSet resultSet) throws SQLException {

        String nationalId = resultSet.getString(1);
        String firstName = resultSet.getString(2);
        String middleName = resultSet.getString(3);
        String lastName = resultSet.getString(4);
        String nationality = resultSet.getString(5);
        String address = resultSet.getString(6);
        String mobileNo = resultSet.getString(7);
        int jobTitle = resultSet.getInt(8);
        String qualification = resultSet.getString(9);
        String birthDay = resultSet.getString(10);
        int employeeState = resultSet.getInt(11);
        String registrationDate = resultSet.getString(12);

        Employee emp = new Employee(nationalId, firstName, middleName, lastName, jobTitle, address, qualification, employeeState, nationality, mobileNo, registrationDate, birthDay);

        return emp;
    }
}
