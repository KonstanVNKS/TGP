import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class Member {

    protected static Connection db;

    static {
        try {
            db = DriverManager.getConnection("jdbc:mariadb://localhost:3306/tgpr-msn?user=root&password=root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String pseudo;

    public Member(){

    }

    public Member(String psuedo){
        this.pseudo = psuedo;
    }

    public String getPsuedo() {
        return pseudo;
    }

    public void setPsuedo(String psuedo) {
        this.pseudo = psuedo;
    }

    @Override
    public String toString(){
        return "Member{" +
                "pseudo='" + this.pseudo + '\'' +
                '}';
    }

    public static void mapper(ResultSet rs, Member member) throws SQLException {
        member.pseudo = rs.getString("pseudo");
    }

    public static List<Member> getAll() {
        var list = new ArrayList<Member>();
        try {
            var stmt = db.prepareStatement("select * from members order by pseudo");
            var rs = stmt.executeQuery();
            while (rs.next()) {
                var member = new Member();
                mapper(rs, member);
                list.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
