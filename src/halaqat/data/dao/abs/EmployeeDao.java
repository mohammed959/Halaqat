package halaqat.data.dao.abs;

import halaqat.data.pojos.Employee;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface EmployeeDao {
    boolean insert(Employee employee) throws SQLException, ClassNotFoundException;

    boolean update(Employee employee) throws SQLException, ClassNotFoundException;

    boolean changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException;

    boolean delete(String nationalId) throws SQLException, ClassNotFoundException;

    Employee get(String nationalId) throws SQLException, ClassNotFoundException;

    Employee login(String nationalId, String password) throws SQLException, ClassNotFoundException;

    List<Employee> getAll() throws SQLException, ClassNotFoundException;

    HashMap<Integer, Integer> numberOfEmployees() throws SQLException, ClassNotFoundException;
}
