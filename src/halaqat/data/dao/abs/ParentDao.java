package halaqat.data.dao.abs;

import halaqat.data.pojos.Halaqa;
import halaqat.data.pojos.Parent;

import java.sql.SQLException;
import java.util.List;

public interface ParentDao {
    boolean insert(Parent parent) throws SQLException, ClassNotFoundException;

    boolean update(Parent parent) throws SQLException, ClassNotFoundException;

    boolean changePassword(String nationalId, String password) throws SQLException, ClassNotFoundException;

    boolean delete(String nationalId) throws SQLException, ClassNotFoundException;

    Parent get(String nationalId) throws SQLException, ClassNotFoundException;

    Parent login(String nationalId, String password) throws SQLException, ClassNotFoundException;

    List<Parent> searchByName(String name) throws SQLException, ClassNotFoundException;
}
