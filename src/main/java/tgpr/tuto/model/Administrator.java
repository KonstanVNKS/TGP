package tgpr.tuto.model;

import tgpr.framework.mvc.Params;

import java.time.LocalDate;
import java.util.List;

public class RegularMember extends Member {
    public RegularMember() {
        super();
    }

    public RegularMember(String pseudo, String password) {
        super(pseudo, password);
    }

    public RegularMember(String pseudo, String password, String profile, LocalDate birthdate) {
        super(pseudo, password, profile, birthdate);
    }

    public static List<RegularMember> getAllRegularMembers() {
        return queryList(RegularMember.class, "select * from members where admin=0 order by pseudo");
    }

    public static RegularMember getRegularMemberByPseudo(String pseudo) {
        return queryOne(RegularMember.class, "select * from members where pseudo=:pseudo and admin=0",
                new Params("pseudo", pseudo));
    }
}
