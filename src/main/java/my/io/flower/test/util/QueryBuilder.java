package my.io.flower.test.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryBuilder implements StringTemplate.Processor<PreparedStatement, SQLException> {

    Connection conn;

    public QueryBuilder(Connection conn) {
        this.conn = conn;
    }

    public PreparedStatement process(StringTemplate st) throws SQLException {
        // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
        String query = String.join("?", st.fragments());

        // 2. Create the PreparedStatement on the connection
        PreparedStatement ps = conn.prepareStatement(query);

        // 3. Set parameters of the PreparedStatement
        int index = 1;
        for (Object value : st.values()) {
            switch (value) {
                case Integer i -> ps.setInt(index++, i);
                case Float f -> ps.setFloat(index++, f);
                case Double d -> ps.setDouble(index++, d);
                case Boolean b -> ps.setBoolean(index++, b);
                default -> ps.setString(index++, String.valueOf(value));
            }
        }

        return ps;
    }
}
