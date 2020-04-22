package halaqat.data.dao.abs;

import halaqat.data.pojos.Halaqa;

import java.sql.SQLException;
import java.util.List;

public interface HalaqaDao {
    void insert(Halaqa halaqa) throws SQLException, ClassNotFoundException;

    void update(Halaqa halaqa, String halaqaName) throws SQLException, ClassNotFoundException;

    void delete(String halaqaName) throws SQLException, ClassNotFoundException;

    Halaqa get(String name, boolean withTeacher) throws SQLException, ClassNotFoundException;

    Halaqa byTeacher(String teacherId) throws SQLException, ClassNotFoundException;

    List<Halaqa> getAll(boolean withTeachers) throws SQLException, ClassNotFoundException;
}
