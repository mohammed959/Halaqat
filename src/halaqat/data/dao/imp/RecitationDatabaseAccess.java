package halaqat.data.dao.imp;

import halaqat.data.DatabaseConnectionManager;
import halaqat.data.dao.abs.RecitationDao;
import halaqat.data.pojos.Parent;
import halaqat.data.pojos.Recitation;
import halaqat.data.pojos.Student;
import halaqat.utils.ResultUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class RecitationDatabaseAccess implements RecitationDao {

    @Override
    public void insert(Recitation recitation) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "INSERT INTO `Recitation` VALUES('" + recitation.getDate()
                + "', " + recitation.gettype()
                + " ,'" + recitation.getStudentId()
                + "', '" + recitation.getstartSorah()
                + "', " + recitation.getstartAyah()
                + ", '" + recitation.getendSorah()
                + "', " + recitation.getendAyah()
                + ", " + recitation.getgrade() + ");";

        Statement statement = connection.createStatement();
        statement.execute(sql);
        connection.close();
    }

    @Override
    public void update(Recitation recitation) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "UPDATE `Recitation` SET StartSorah='" + recitation.getstartSorah()
                + "', StartAyah=" + recitation.getstartAyah()
                + ", EndSorah='" + recitation.getendSorah()
                + "', EndAyah=" + recitation.getendAyah()
                + ", Grade=" + recitation.getgrade()
                + " WHERE SID= '" + recitation.getStudentId() + "' AND RDate ='" + recitation.getDate() + "' AND `Type`=" + recitation.gettype();

        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public void delete(String nationalId, String date, int type) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "DELETE FROM `Recitation`  WHERE SID= '" + nationalId + "' AND RDate ='" + date + "' AND `Type`=" + type;
        Statement statement = connection.createStatement();
        statement.execute(sql);

        connection.close();
    }

    @Override
    public List<Recitation> studentRecitations(String nationalId, int recitationsType, int year, int month) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT * FROM Recitation WHERE SID = '" + nationalId + "' AND `Type` = " + recitationsType + " AND RDate LIKE '" + year + "-" + (month < 10 ? "0" + month : month) + "-__'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Recitation> recitations = recitationsFromResult(resultSet);
            resultSet.close();
            connection.close();
            return recitations;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Recitation>();
        }
    }

    @Override
    public Recitation studentRecitation(String nationalId, int recitationsType, String date) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();
        String sql = "SELECT * FROM Recitation WHERE SID = '" + nationalId + "' AND `Type` = " + recitationsType + " AND RDATE = '" + date + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.first()) {
            Recitation recitation = recitationFromResult(resultSet);
            resultSet.close();
            connection.close();
            return recitation;
        } else {
            resultSet.close();
            connection.close();
            return null;
        }
    }

    @Override
    public List<Integer> distinctYears(String nationalId) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseConnectionManager.getConnection();

        String sql = "SELECT DISTINCT YEAR(RDate) FROM Recitation WHERE SID = '" + nationalId + "'";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            List<Integer> recitations = ResultUtils.integerListFromResult(resultSet);
            resultSet.close();
            connection.close();
            return recitations;
        } else {
            resultSet.close();
            connection.close();
            return new LinkedList<Integer>();
        }
    }

    private Recitation recitationFromResult(ResultSet resultSet) throws SQLException {

        String date = resultSet.getString(1);
        int type = resultSet.getInt(2);
        String studentId = resultSet.getString(3);
        String startSorah = resultSet.getString(4);
        int startAyah = resultSet.getInt(5);
        String endSorah = resultSet.getString(6);
        int endAyah = resultSet.getInt(7);
        int grade = resultSet.getInt(8);
        Recitation recitation = new Recitation(grade, date, type, startAyah, endAyah, startSorah, endSorah, studentId);

        return recitation;
    }

    private List<Recitation> recitationsFromResult(ResultSet resultSet) throws SQLException {
        List<Recitation> recitations = new LinkedList<Recitation>();
        do {
            recitations.add(recitationFromResult(resultSet));
        } while (resultSet.next());
        return recitations;
    }

}
