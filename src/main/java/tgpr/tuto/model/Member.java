package tgpr.tuto.model;

import tgpr.framework.mvc.Model;
import tgpr.framework.mvc.Params;


import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;
import java.time.LocalDate;


public class Member extends Model{
    private String pseudo;
    private String password;
    private String profile;
    private LocalDate birthdate;
    private boolean admin;

    public Member(){
    }

    public Member(String pseudo, String password, boolean admin) {
        this.pseudo = pseudo;
        this.password = password;
        this.admin = admin;
    }

    public Member(String pseudo, String password, String profile, LocalDate birthdate, boolean admin) {
        this.pseudo = pseudo;
        this.password = password;
        this.profile = profile;
        this.birthdate = birthdate;
        this.admin = admin;
    }

    public String getPsuedo() {
        return pseudo;
    }

    public void setPsuedo(String psuedo) {
        this.pseudo = psuedo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "tgpr.tuto.model.Member{" +
                "pseudo='" + pseudo + '\'' +
                ", profile='" + profile + '\'' +
                ", birthdate=" + birthdate +
                ", admin=" + admin +
                '}';
    }

    protected void mapper(ResultSet resultSet) throws SQLException {
        pseudo = resultSet.getString("pseudo");
        password = resultSet.getString("password");
        profile = resultSet.getString("profile");
        admin = resultSet.getBoolean("admin");
        birthdate = resultSet.getObject("birthdate", LocalDate.class);
    }
    @Override
    public void reload() {
        reload("select * from members where pseudo=:pseudo", new Params("pseudo", pseudo));
    }

    public static List<Member> getAll() {
        return queryList(Member.class, "select * from members order by pseudo");
    }


    public static Member getByPseudo(String pseudo){
        return queryOne(Member.class, "select * from members where pseudo=:pseudo", new Params("pseudo", pseudo));
    }

    public boolean save() {
        int c;
        Member m = getByPseudo(pseudo);
        String sql;
        if (m == null)
            sql = "insert into members (pseudo, password, profile, admin, birthdate) " +
                    "values (:pseudo,:password,:profile,:admin,:birthdate)";
        else
            sql = "update members set password=:password, profile=:profile, admin=:admin, " +
                    "birthdate=:birthdate where pseudo=:pseudo";
        c = execute(sql, new Params()
                .add("pseudo", pseudo)
                .add("password", password)
                .add("profile", profile)
                .add("admin", admin)
                .add("birthdate", birthdate));
        return c == 1;
    }

    public boolean delete() {
        int c = execute("delete from members where pseudo=:pseudo", new Params("pseudo", pseudo));
        return c == 1;
    }




}
