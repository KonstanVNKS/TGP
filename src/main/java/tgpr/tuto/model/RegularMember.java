package tgpr.tuto.model;

import tgpr.framework.mvc.Params;

import java.time.LocalDate;
import java.util.List;

public class Administrator extends Member {
    public Administrator() {
        super();
    }

    public Administrator(String pseudo, String password) {
        super(pseudo, password);
    }

    public Administrator(String pseudo, String password, String profile, LocalDate birthdate) {
        super(pseudo, password, profile, birthdate);
    }

    public static List<Administrator> getAllAdministrators() {
        return queryList(Administrator.class, "select * from members where admin=1 order by pseudo");
    }

    public static Administrator getAdministratorByPseudo(String pseudo) {
        return queryOne(Administrator.class, "select * from members where pseudo=:pseudo and admin=1",
                new Params("pseudo", pseudo));
    }
}
