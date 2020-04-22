package halaqat.data.dao.abs;

import halaqat.data.data_structures.Pair;
import halaqat.data.pojos.Semester;
import halaqat.data.pojos.SemesterGrades;
import halaqat.data.pojos.Student;

import java.sql.SQLException;
import java.util.List;

public interface SemesterGradesDao {

    void insert(SemesterGrades semesterGrades) throws SQLException, ClassNotFoundException;

    void update(SemesterGrades semesterGrades) throws SQLException, ClassNotFoundException;

    void delete(int semesterId, String studentId) throws SQLException, ClassNotFoundException;

    SemesterGrades get(int semesterId, String studentId) throws SQLException, ClassNotFoundException;

    List<Pair<Student, SemesterGrades>> halaqaGrades(int semesterId, String halaqaName) throws SQLException, ClassNotFoundException;

    List<Pair<Student, SemesterGrades>> dependentsGrades(int semesterId, String parentId) throws SQLException, ClassNotFoundException;
}
