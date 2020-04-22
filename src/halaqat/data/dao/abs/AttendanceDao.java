package halaqat.data.dao.abs;

import halaqat.data.pojos.Attendance;

import java.sql.SQLException;
import java.util.List;

public interface AttendanceDao {

    void insert(Attendance attendance) throws SQLException, ClassNotFoundException;

    void update(Attendance attendance) throws SQLException, ClassNotFoundException;

    Attendance get(String nationalId, String date) throws SQLException, ClassNotFoundException;

    List<Attendance> monthlyAttendance(String nationalId, int year, int month) throws SQLException, ClassNotFoundException;

    List<Integer> distinctYearsForHalaqa(String halaqaName) throws SQLException, ClassNotFoundException;

    List<Integer> distinctYearsForParent(String parentId) throws SQLException, ClassNotFoundException;
}
