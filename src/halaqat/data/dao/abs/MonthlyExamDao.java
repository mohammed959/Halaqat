package halaqat.data.dao.abs;

import halaqat.data.pojos.MonthlyExam;

import java.sql.SQLException;
import java.util.List;

public interface MonthlyExamDao {

    void insert(MonthlyExam monthlyExam) throws SQLException, ClassNotFoundException;

    void update(MonthlyExam monthlyExam) throws SQLException, ClassNotFoundException;

    void delete(int semesterId, String studentId, int number) throws SQLException, ClassNotFoundException;

    MonthlyExam get(int semesterId, String studentId, int number) throws SQLException, ClassNotFoundException;

    List<MonthlyExam> getAll(int semesterId, String studentId) throws SQLException, ClassNotFoundException;

}
