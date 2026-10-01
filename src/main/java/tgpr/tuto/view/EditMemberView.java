package tgpr.tuto.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.ui.Ui;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.Insets;
import tgpr.framework.ui.layout.VBox;
import tgpr.tuto.controller.EditMemberController;
import tgpr.tuto.model.Member;

import java.util.List;
import java.util.regex.Pattern;

import static tgpr.framework.util.Tools.asString;
import static tgpr.framework.util.Tools.ifNull;

import java.util.List;

public class EditMemberView extends DialogWindow {

    private final EditMemberController controller;

    private final TextBox txtPseudo;
    private final TextBox txtProfile;
    private final TextBox txtPassword;
    private final TextBox txtPasswordConfirm;
    private final TextBox txtBirthDate;
    private final ComboBox<String> cboRole;
    private final Label errPseudo;
    private final Label errProfile;
    private final Label errPassword;
    private final Label errPasswordConfirm;
    private final Label errBirthDate;
    private final Label errRole;
    private final Button btnAddUpdate;

    private final Member member;

    public EditMemberView(EditMemberController controller, Member member) {
        // définit le titre de la fenêtre
        super((member == null ? "Add " : "Update ") + "Member");

        this.member = member;
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 15 lignes et 70 colonnes
        setFixedSize(new TerminalSize(70, 15));
        var form = FormGrid.create();

        txtPseudo = Ui.sizeTo(new TextBox(), 11);
        txtPseudo.setValidationPattern(Pattern.compile("[a-z][a-zA-Z0-9]{0,7}"))
                .setTextChangeListener((txt, byUser) -> validate())
                .setReadOnly(member != null);
        errPseudo = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Pseudo:", txtPseudo, errPseudo);

        txtProfile = Ui.sizeTo(new TextBox(), 21);
        txtProfile.setTextChangeListener((txt, byUser) -> validate());
        errProfile = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Profile:", txtProfile, errProfile);

        txtPassword = Ui.sizeTo(new TextBox(), 11);
        txtPassword.setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        errPassword = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Password:", txtPassword, errPassword);

        txtPasswordConfirm = Ui.sizeTo(new TextBox(), 11);
        txtPasswordConfirm.setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        errPasswordConfirm = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Confirm Password:", txtPasswordConfirm, errPasswordConfirm);

        txtBirthDate = Ui.sizeTo(new TextBox(), 11);
        txtBirthDate.setValidationPattern(Pattern.compile("[/\\d]{0,10}"))
                .setTextChangeListener((txt, byUser) -> validate());
        errBirthDate = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Birth Date:", txtBirthDate, errBirthDate);

        cboRole = Ui.sizeTo(new ComboBox<>("Admin", "Member"), 11);
        cboRole.setSelectedItem("Member");
        cboRole.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
        errRole = new Label("").setForegroundColor(TextColor.ANSI.RED);
        form.row("Role:", cboRole, errRole);

        btnAddUpdate = new Button(member == null ? "Add" : "Update", this::add).setEnabled(false);
        var buttons = HBox.create().add(btnAddUpdate, new Button("Cancel", this::close));

        setComponent(VBox.create().spacing(1).padding(Insets.of(1, 1, 0, 0)).add(form, buttons));

        if (member != null) {
            txtPseudo.setText(member.getPseudo());
            txtProfile.setText(ifNull(member.getProfile(), ""));
            txtBirthDate.setText(asString(member.getBirthdate()));
            cboRole.setSelectedItem(member.isAdmin() ? "Admin" : "Member");
        }
    }

    private void add() {
        controller.save(
                txtPseudo.getText(),
                txtProfile.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText(),
                txtBirthDate.getText(),
                cboRole.getText()
        );
    }

    private void validate() {
        var errors = controller.validate(
                txtPseudo.getText(),
                txtProfile.getText(),
                txtPassword.getText(),
                txtPasswordConfirm.getText(),
                txtBirthDate.getText(),
                cboRole.getText()
        );

        errPseudo.setText(errors.getFirstErrorMessage(Member.Fields.Pseudo));
        errProfile.setText(errors.getFirstErrorMessage(Member.Fields.Profile));
        errPassword.setText(errors.getFirstErrorMessage(Member.Fields.Password));
        errPasswordConfirm.setText(errors.getFirstErrorMessage(EditMemberController.Fields.PasswordConfirm));
        errBirthDate.setText(errors.getFirstErrorMessage(Member.Fields.BirthDate));
        errRole.setText(errors.getFirstErrorMessage(Member.Fields.Admin));

        btnAddUpdate.setEnabled(errors.isEmpty());
    }

}
