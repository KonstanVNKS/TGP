package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.framework.util.Configuration;
import tgpr.tuto.controller.LoginController;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.Insets;
import tgpr.framework.ui.layout.Layout;
import tgpr.framework.ui.layout.Pos;
import tgpr.framework.ui.layout.Spacing;
import tgpr.framework.ui.layout.VBox;

import java.util.List;

public class LoginView extends BasicWindow {

    private final LoginController controller;

    private final TextBox txtPseudo = new TextBox();
    private final TextBox txtPassword = new TextBox();
    private Button btnLogin;

    public LoginView(LoginController controller) {
        this.controller = controller;

        setTitle("Login");
        setHints(List.of(Hint.CENTERED));

        setComponent(VBox.createCentered().spacing(1).add(
                createFieldsPanel(),
                createButtonsPanel(),
                createDebugPanel()
        ));

        btnLogin.takeFocus();
    }

    private Panel createFieldsPanel() {
        txtPseudo.takeFocus();

        return FormGrid.create()
                .padding(Insets.of(1, 1, 1, 0))
                .spacing(Spacing.of(1))
                .row("User:", txtPseudo)
                .row("Password:", txtPassword.setMask('*'))
                .alignInParent(Pos.CENTER);
    }

    private Panel createButtonsPanel() {
        btnLogin = new Button("Login", this::login);
        var btnExit = new Button("Exit", this::exit);
        return HBox.createButtonRow(btnLogin, btnExit);
    }

    private Border createDebugPanel() {
        var panel = VBox.create()
                .childrenAlign(Pos.CENTER)
                .add(
                        new Button("Login as default admin", this::logAsDefaultAdmin),
                        new Button("Login as default member", this::logAsDefaultMember),
                        new Button("Reset Database", this::seedData)
                );
        return Layout.bordered(" For debug purpose ", panel);
    }

    private void seedData() {
        controller.seedData();
    }

    private void exit() {
        controller.exit();
    }

    private void login() {
        var errors = controller.login(txtPseudo.getText(), txtPassword.getText());
        if (!errors.isEmpty()) {
            txtPseudo.takeFocus();
        }
    }

    private void logAsDefaultAdmin() {
        controller.login(Configuration.get("default.admin.pseudo"), Configuration.get("default.admin.password"));
    }

    private void logAsDefaultMember() {
        controller.login(Configuration.get("default.member.pseudo"), Configuration.get("default.member.password"));
    }
}
