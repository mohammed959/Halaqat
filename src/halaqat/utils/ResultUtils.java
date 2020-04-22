package halaqat.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ResultUtils {

    public static List<Integer> integerListFromResult(ResultSet resultSet) throws SQLException {
        List<Integer> list = new LinkedList<>();
        do {
            list.add(resultSet.getInt(1));
        } while (resultSet.next());
        return list;
    }

}
