package halaqat.data.dao.abs;

import halaqat.data.pojos.Student;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface StudentDao {
    void insert(Student student) throws SQLException, ClassNotFoundException;

    void update(Student student) throws SQLException, ClassNotFoundException;

    void changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException;

    void delete(String nationalId) throws SQLException, ClassNotFoundException;

    Student get(String nationalId) throws SQLException, ClassNotFoundException;

    Student login(String nationalId, String password) throws SQLException, ClassNotFoundException;

    List<Student> byHalaqa(String halaqaName) throws SQLException, ClassNotFoundException;

    List<Student> byTeacher(String teacherId) throws SQLException, ClassNotFoundException;

    List<Student> byParent(String parentNationalId) throws SQLException, ClassNotFoundException;

    List<Student> searchByName(String name) throws SQLException, ClassNotFoundException;

    int numberOfStudents(String halaqaName) throws SQLException, ClassNotFoundException;

    int numberOfDependents(String parentId) throws SQLException, ClassNotFoundException;

    HashMap<String, Integer> numberOfStudentsByHalaqat() throws SQLException, ClassNotFoundException;
}
