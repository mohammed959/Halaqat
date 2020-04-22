package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.ParentDao;
import halaqat.data.dao.abs.StudentDao;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StudentDatabaseAccess implements StudentDao {
    @Override
    public void insert(Student student) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "INSERT INTO `Student` VALUES('" + student.getNationalID()
                + "' , '" + student.getFirstName()
                + "' , '" + student.getMiddleName()
                + "' , '" + student.getLastName()
                + "' , " + student.getSchoollvl()
                + " , '" + student.getBirthDate()
                + "' , CURDATE() , '" + student.getHalaqaName()
                + "', '" + student.getParentId()
                + "', " + Student.STATE_ENROLLED // New Students is obviously enrolled
                + ", '" + student.getPassword() + "');";


        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void update(Student student) throws SQLException, ClassNotFoundException {

        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Student` SET FirstName ='" + student.getFirstName()
                + "' , MiddleName = '" + student.getMiddleName()
                + "' , LastName = '" + student.getLastName()
                + "' , SchoolLevel= " + student.getSchoollvl()
                + " , BirthDate= '" + student.getBirthDate()
                + "', HalaqaName= '" + student.getHalaqaName()
                + "', State= " + student.getState()
                + " WHERE NationalId = '" + student.getNationalID() + "'";


        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();

    }

    @Override
    public void changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "UPDATE `Student` SET `Password` ='" + password
                + "' WHERE `NationalId`= '" + nationalId + "'";
        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void delete(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String parentId = get(nationalId).getParentId();

        String sql = "DELETE FROM `Student` WHERE `NationalId`= '" + nationalId + "'";

        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();

        // If this is the parent does not have any other students, delete him.
        if (numberOfDependents(parentId) == 0) {
            ParentDao parentDao = new ParentDatabaseAccess();
            parentDao.delete(parentId);
        }


    }

    @Override
    public Student get(String nationalId) throws SQLException, ClassNotFoundException {

        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student` WHERE `Student`.NationalId = '" + nationalId + "'";


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Student student = studentFromResult(resultSet);
            resultSet.close();
            connection.close();
            return student;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public Student login(String nationalId, String password) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student` WHERE `Student`.NationalId = '" + nationalId +
                "' AND `Student`.`Password`='" + password + "'";


        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Student student = studentFromResult(resultSet);
            connection.close();
            return student;
        }

        return null;
    }

    @Override
    public List<Student> byHalaqa(String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student` WHERE `Student`.`HalaqaName` = '" + halaqaName +
                "' AND `Student`.State = " + Student.STATE_ENROLLED;

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Student> students = studentsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return students;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Student>();
        }
    }

    @Override
    public List<Student> byTeacher(String teacherId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student`,`Halaqa` WHERE `Student`.`State`=" + Student.STATE_ENROLLED + " AND `Student`.`HalaqaName` = `Halaqa`.`Name` " +
                "AND `Halaqa`.`TeacherId` = '" + teacherId + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Student> students = studentsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return students;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Student>();
        }
    }

    @Override
    public List<Student> byParent(String parentNationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student` WHERE `Student`.`ParentId` = '" + parentNationalId + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Student> students = studentsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return students;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Student>();
        }
    }

    @Override
    public List<Student> searchByName(String name) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM `Student` WHERE CONCAT(`FirstName`,' ',`MiddleName`, ' ', " +
                "`LastName`) Like '%" + name + "%'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Student> students = studentsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return students;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<>();
        }

    }

    @Override
    public int numberOfStudents(String halaqaName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT COUNT(*) FROM `Student` WHERE `Student`.`HalaqaName` = '" + halaqaName + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.first();
        return resultSet.getInt(1);
    }

    @Override
    public int numberOfDependents(String parentId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT COUNT(*) FROM `Student` WHERE `Student`.`ParentId` = '" + parentId + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.first();
        return resultSet.getInt(1);
    }

    @Override
    public HashMap<String, Integer> numberOfStudentsByHalaqat() throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT HalaqaName, COUNT(*) FROM `Student` GROUP BY HalaqaName";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        HashMap<String, Integer> employeesCount = new HashMap<>();

        if (resultSet.first()) {
            do {
                employeesCount.put(resultSet.getString(1), resultSet.getInt(2));
            } while (resultSet.next());
        }
        return employeesCount;
    }

    public List<Student> studentsFromResult(ResultSet resultSet) throws SQLException {
        List<Student> students = new LinkedList<Student>();
        do {
            students.add(studentFromResult(resultSet));
        } while (resultSet.next());
        return students;
    }


    public Student studentFromResult(ResultSet resultSet) throws SQLException {
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

        Student student = new Student(studentId, firstName, middleName, lastName, birthDate, registrationDate, state, schoolLevel, halaqaName, parentId);
        return student;
    }
}
