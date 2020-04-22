package halaqat.data.dao.abs;

import halaqat.data.pojos.Semester;

import java.sql.SQLException;
import java.util.List;

public interface SemesterDao {

    void insert(Semester semester) throws SQLException, ClassNotFoundException;

    void update(Semester semester) throws SQLException, ClassNotFoundException;

    void delete(int id) throws SQLException, ClassNotFoundException;

    Semester get(int id) throws SQLException, ClassNotFoundException;

    Semester get(String name) throws SQLException, ClassNotFoundException;

    List<Semester> getAll() throws SQLException, ClassNotFoundException;

    Semester getLast() throws SQLException, ClassNotFoundException;

}
