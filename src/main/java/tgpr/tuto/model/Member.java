package tgpr.tuto.model;

import tgpr.framework.mvc.Model;
import tgpr.framework.mvc.Params;


import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Objects;

import static tgpr.framework.util.Tools.hash;


public class Member extends Model{
    public enum Fields {
        Pseudo, Password, Profile, Admin, BirthDate
    }

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
        else if (password == null || password.isBlank())
            sql = "update members set profile=:profile, admin=:admin, " +
                    "birthdate=:birthdate where pseudo=:pseudo";
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

    @Override
    public boolean equals(Object o) {
        // s'il s'agit du même objet en mémoire, retourne vrai
        if (this == o) return true;
        // si l'objet à comparer est null ou n'est pas issu de la même classe que l'objet courant, retourne faux
        if (o == null || getClass() != o.getClass()) return false;
        // transtype l'objet reçu en Member
        Member member = (Member) o;
        // retourne vrai si les deux objets ont le même pseudo
        // remarque : cela veut dire que les deux objets sont considérés comme identiques s'ils ont le même pseudo
        //            ce qui a du sens car c'est la clé primaire de la table. Attention cependant car cela signifie
        //            que si d'autres attributs sont différents, les objets seront malgré tout considérés égaux.
        return pseudo.equals(member.pseudo);
    }

    @Override
    public int hashCode() {
        // on retourne le hash code du pseudo qui est "unique" puisqu'il correspond à la clé primaire
        return Objects.hash(pseudo);
    }

    public static Member checkCredentials(String pseudo, String password) {
        var member = Member.getByPseudo(pseudo);
        if (member != null && member.password.equals(hash(password)))
            return member;
        return null;
    }

}
