package tgpr.tuto.view;

import com.googlecode.lanterna.TerminalSize;
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

public class EditMemberView extends DialogWindow {

    private final EditMemberController controller;

    private final TextBox txtPseudo;
    private final TextBox txtProfile;
    private final TextBox txtPassword;
    private final TextBox txtPasswordConfirm;
    private final TextBox txtBirthDate;
    private final ComboBox<String> cboRole;
    private final Button btnAddUpdate;
    private FormGrid fieldsGrid;

    private final Member member;

    public EditMemberView(EditMemberController controller, Member member) {
        super((member == null ? "Add " : "Update ") + "Member");

        this.member = member;
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 15));

        fieldsGrid = FormGrid.create();

        txtPseudo = Ui.sizeTo(new TextBox(), 11);
        txtPseudo.setValidationPattern(Pattern.compile("[a-z][a-zA-Z0-9]{0,7}"))
                .setTextChangeListener((txt, byUser) -> validate())
                .setReadOnly(member != null);
        fieldsGrid.row("Pseudo:", txtPseudo, Member.Fields.Pseudo);

        txtProfile = Ui.sizeTo(new TextBox(), 21);
        txtProfile.setTextChangeListener((txt, byUser) -> validate());
        fieldsGrid.row("Profile:", txtProfile, Member.Fields.Profile);

        txtPassword = Ui.sizeTo(new TextBox(), 11);
        txtPassword.setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        fieldsGrid.row("Password:", txtPassword, Member.Fields.Password);

        txtPasswordConfirm = Ui.sizeTo(new TextBox(), 11);
        txtPasswordConfirm.setMask('*')
                .setTextChangeListener((txt, byUser) -> validate());
        fieldsGrid.row("Confirm Password:", txtPasswordConfirm, EditMemberController.Fields.PasswordConfirm);

        txtBirthDate = Ui.sizeTo(new TextBox(), 11);
        txtBirthDate.setValidationPattern(Pattern.compile("[/\\d]{0,10}"))
                .setTextChangeListener((txt, byUser) -> validate());
        fieldsGrid.row("Birth Date:", txtBirthDate, Member.Fields.BirthDate);

        cboRole = Ui.sizeTo(new ComboBox<>("Admin", "Member"), 11);
        cboRole.setSelectedItem("Member");
        cboRole.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
        fieldsGrid.row("Role:", cboRole, Member.Fields.Admin);

        btnAddUpdate = new Button(member == null ? "Add" : "Update", this::add).setEnabled(false);
        var buttons = HBox.create().add(btnAddUpdate, new Button("Cancel", this::close));

        setComponent(VBox.create().spacing(1).padding(Insets.of(1, 1, 0, 0)).add(fieldsGrid, buttons));

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

        fieldsGrid.applyErrors(errors);

        btnAddUpdate.setEnabled(errors.isEmpty());
    }
}
