package my.groupId;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static java.lang.StringTemplate.RAW;
import static java.util.FormatProcessor.FMT;

@Path("/hello")
public class GreetingResource {

    @Inject
    DataSource dataSource;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        var user = new User("fiorenzo", "pizza");
        var world = "flower";
        var age = 48;
        return STR. """
            Hello \{ world }! an older \{ age } man!
            \{ user.name() }  \{ user.surname() }
        """ ;
    }

    @Path("/sql")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sql() {
        var i = 0;
        String s = FMT. "The answer is %5d\{ i }" ;
        System.out.println(s);
        StringBuffer stringBuffer = new StringBuffer();
        try (var conn = dataSource.getConnection()) {
            var name = "TABLE_CONSTRAINTS";
            var SQL = new QueryBuilder(conn);
            var ps = SQL. "SELECT * FROM INFORMATION_SCHEMA.TABLES where TABLE_NAME = \{ name }" ;
            var rs = ps.executeQuery();
            var rsMetaData = rs.getMetaData();
            while (rs.next()) {
                stringBuffer.append(rsMetaData.getColumnName(1) + ": " + rs.getString(1) + "<br/>");
                stringBuffer.append(rsMetaData.getColumnName(2) + ": " + rs.getString(2) + "<br/>");
                stringBuffer.append(rsMetaData.getColumnName(3) + ": " + rs.getString(3) + "<br/>");
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stringBuffer.toString();
    }
}
