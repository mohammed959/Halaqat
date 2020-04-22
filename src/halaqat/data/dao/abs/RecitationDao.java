package halaqat.data.dao.abs;

import halaqat.data.pojos.Recitation;

import java.sql.SQLException;
import java.util.List;

public interface RecitationDao {
    void insert(Recitation recitation) throws SQLException, ClassNotFoundException;

    void update(Recitation recitation) throws SQLException, ClassNotFoundException;

    void delete(String nationalId, String date, int type) throws SQLException, ClassNotFoundException;

    List<Recitation> studentRecitations(String nationalId, int recitationsType, int year, int month) throws SQLException, ClassNotFoundException;

    Recitation studentRecitation(String nationalId, int recitationsType, String date) throws SQLException, ClassNotFoundException;

    List<Integer> distinctYears(String nationalId) throws SQLException, ClassNotFoundException;

}
