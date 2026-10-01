package tgpr.tuto.model;

import org.springframework.util.Assert;
import tgpr.framework.mvc.Model;
import tgpr.framework.mvc.Params;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static tgpr.framework.util.Tools.hash;


public abstract class Member extends Model{
    public enum Fields {
        Pseudo, Password, Profile, Admin, BirthDate
    }

    public enum RelationshipType {
        Unrelated("This member is not related to you."),
        Followee("You are following this member."),
        Follower("This member is following you."),
        Mutual("This member and you are mutual friends."),
        Yourself("This is you!");

        private final String text;

        RelationshipType(final String text) {
            this.text = text;
        }

        public String toText() {
            return text;
        }

        // retourne les valeurs de l'enum triées par ordre alphabétique
        public static List<String> getSortedStrings() {
            return Arrays.stream(Member.RelationshipType.values())
                    .sorted(Comparator.comparing(Enum::toString))
                    .map(Enum::toString)
                    .collect(Collectors.toList());
        }
    }

    private String pseudo;
    private String password;
    private String profile;
    private LocalDate birthdate;
    private boolean admin;

    public Member(){
    }

    public Member(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
    }

    public Member(String pseudo, String password, String profile, LocalDate birthdate) {
        this.pseudo = pseudo;
        this.password = password;
        this.profile = profile;
        this.birthdate = birthdate;
    }

    public static Member createMember(String pseudo, String password, String profile, LocalDate birthdate, boolean isAdmin) {
        if (isAdmin)
            return new Administrator(pseudo, password, profile, birthdate);
        else
            return new RegularMember(pseudo, password, profile, birthdate);
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
        return this instanceof Administrator;
    }

    @Override
    public String toString() {
        return "tgpr.tuto.model.Member{" +
                "pseudo='" + pseudo + '\'' +
                ", profile='" + profile + '\'' +
                ", birthdate=" + birthdate +
                ", admin=" + isAdmin() +
                '}';
    }

    protected void mapper(ResultSet resultSet) throws SQLException {
        pseudo = resultSet.getString("pseudo");
        password = resultSet.getString("password");
        profile = resultSet.getString("profile");
        birthdate = resultSet.getObject("birthdate", LocalDate.class);
    }
    @Override
    public void reload() {
        reload("select * from members where pseudo=:pseudo", new Params("pseudo", pseudo));
    }

    protected static Member newInstance(ResultSet rs) {
        try {
            return rs.getInt("admin") == 0 ? new RegularMember() : new Administrator();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Member> getAll() {
        return queryList(Member::newInstance, "select * from members order by pseudo");
    }


    public static Member getByPseudo(String pseudo){
        return queryOne(Member::newInstance, "select * from members where pseudo=:pseudo",
                new Params("pseudo", pseudo));
    }

    public Member save() {
        int c;
        Member m = getByPseudo(pseudo);
        String sql;
        if (m == null) {
            Assert.isTrue(Security.isAdmin(), "Only admin may create members");
            sql = "insert into members (pseudo, password, profile, admin, birthdate) " +
                    "values (:pseudo,:password,:profile,:admin,:birthdate)";
        }
        else {
            Assert.isTrue(Security.isAdmin() || Security.isLoggedUser(this), "Update only allowed if admin or own data");
            Assert.isTrue(!Security.isLoggedUser(this) || m.isAdmin() == isAdmin(), "Connected user may not change his own role");
            if (password == null || password.isBlank())
                sql = "update members set password=:password, profile=:profile, admin=:admin, " +
                        "birthdate=:birthdate where pseudo=:pseudo";
            else
                sql = "update members set password=:password, profile=:profile, admin=:admin, " +
                        "birthdate=:birthdate where pseudo=:pseudo";
        }
        c = execute(sql, new Params()
                .add("pseudo", pseudo)
                .add("password", password)
                .add("profile", profile)
                .add("admin", isAdmin() ? 1 : 0)
                .add("birthdate", birthdate));
        Assert.isTrue(c == 1, "Something went wrong");
        return this;
    }

    public void delete() {
        Assert.isTrue(Security.isAdmin(), "Only admin may delete members");
        execute("delete from follows where follower=:pseudo or followee=:pseudo", new Params("pseudo", pseudo));
        execute("delete from messages where author=:pseudo or recipient=:pseudo", new Params("pseudo", pseudo));
        int c = execute("delete from members where pseudo=:pseudo", new Params("pseudo", pseudo));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    @Override
    public boolean equals(Object o) {
        // s'il s'agit du même objet en mémoire, retourne vrai
        if (this == o) return true;
        // avec l'héritage, on compare via instanceof : un RegularMember et un Administrator
        // qui partagent le même pseudo représentent le même membre logique
        if (!(o instanceof Member member))
            return false;
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

    public List<Member> getFollowers() {
        return queryList(Member::newInstance,
                "select m.* from follows join members m on follows.follower = m.pseudo where followee=:pseudo",
                new Params("pseudo", pseudo));
    }

    public List<Member> getFollowees() {
        return queryList(Member::newInstance,
                "select m.* from follows join members m on follows.followee = m.pseudo where follower=:pseudo",
                new Params("pseudo", pseudo));
    }

    public void follow(Member other) {
        if (other.equals(this))
            return;
        if (!getFollowees().contains(other)) {
            int c = execute("insert into follows values (:follower,:followee)",
                    new Params("follower", pseudo)
                            .add("followee", other.pseudo));
            Assert.isTrue(c == 1, "Something went wrong");
        }
    }

    public void unfollow(Member other) {
        if (other.equals(this))
            return;
        int c = execute("delete from follows where follower=:follower and followee=:followee",
                new Params("follower", pseudo)
                        .add("followee", other.pseudo));
        Assert.isTrue(c == 1, "Something went wrong");
    }

    public RelationshipType getRelationshipType(Member otherMember) {
        if (otherMember == null)
            return RelationshipType.Unrelated;
        else if (otherMember.equals(this))
            return RelationshipType.Yourself;
        var followee = getFollowees().contains(otherMember);
        var follower = getFollowers().contains(otherMember);
        if (followee && follower)
            return RelationshipType.Mutual;
        else if (followee)
            return RelationshipType.Followee;
        else if (follower)
            return RelationshipType.Follower;
        else
            return RelationshipType.Unrelated;
    }

    public void toggleFollowUnfollow(Member otherMember) {
        if (otherMember == null) return;
        switch (getRelationshipType(otherMember)) {
            case Mutual, Followee -> unfollow(otherMember);
            default -> follow(otherMember);
        }
    }

    public List<Message> getMessagesSent() {
        return queryList(Message.class,
                "select * from messages where author=:pseudo order by date_time desc",
                new Params("pseudo", pseudo));
    }

    public List<Message> getMessagesReceived() {
        return queryList(Message.class,
                "select * from messages where recipient=:pseudo order by date_time desc",
                new Params("pseudo", pseudo));
    }

    public List<Message> getVisibleMessagesReceived(Member current) {
        boolean showPrivate = current.isAdmin() || current.equals(this);
        return queryList(Message.class,
                "select * from messages where recipient=:recipient and (not private or :showPrivate or author=:author) order by date_time desc",
                new Params("author", current.pseudo)
                        .add("showPrivate", showPrivate)
                        .add("recipient", pseudo));
    }

    public Message send(Member to, String body, boolean isPrivate) {
        return new Message(-1, pseudo, to.pseudo, body, isPrivate, LocalDateTime.now()).save();
    }


}
