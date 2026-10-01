package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.framework.validation.ErrorList;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.MemberValidator;
import tgpr.tuto.view.EditMemberView;
import tgpr.tuto.model.Security;


import static tgpr.framework.util.Tools.*;


public class EditMemberController extends Controller<EditMemberView> {
    public enum Fields {
        PasswordConfirm
    }

    private final EditMemberView view;
    private Member member;
    private final boolean isNew;

    public EditMemberController() {
        this(null);
    }

    public EditMemberController(Member member) {
        this.member = member;
        isNew = member == null;
        view = new EditMemberView(this, member);
    }

    @Override
    public EditMemberView getView() {
        return view;
    }

    public Member getMember() {
        return member;
    }

    public void save(String pseudo, String profile, String password, String confirmPassword, String birthDate, String role) {
        var errors = validate(pseudo, profile, password, confirmPassword, birthDate, role);
        if (errors.isEmpty()) {
            var hashedPassword = password.isBlank() ? password : hash(password);
            member = Member.createMember(pseudo, hashedPassword, profile, toDate(birthDate), role.contentEquals("Admin"));
            member.save();
            view.close();
        } else
            showErrors(errors);
    }

    public ErrorList validate(String pseudo, String profile, String password, String confirmPassword, String birthDate, String role) {
        var errors = new ErrorList();

        if (isNew) {
            errors.add(MemberValidator.isValidAvailablePseudo(pseudo));
            errors.add(MemberValidator.isValidPassword(password));
        }
        // Seul un admin peut changer le rôle d'un membre, à l'exception de son propre rôle
        var isAdmin = role.contentEquals("Admin");
        if (pseudo.equals(Security.getLoggedUser().getPseudo()) && Security.isAdmin() != isAdmin)
            errors.add("you may not change your role", Member.Fields.Admin);

        if (!birthDate.isBlank() && !isValidDate(birthDate))
            errors.add("invalid birth date", Member.Fields.BirthDate);
        if (!password.equals(confirmPassword))
            errors.add("must match password", Fields.PasswordConfirm);

        var hashedPassword = password.isBlank() ? password : hash(password);
        var member = Member.createMember(pseudo, hashedPassword, profile, toDate(birthDate), role.contentEquals("Admin"));
        errors.addAll(MemberValidator.validate(member));

        return errors;
    }


}
