package tgpr.tuto.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.Insets;
import tgpr.framework.ui.layout.VBox;
import tgpr.tuto.controller.DisplayMemberController;
import tgpr.tuto.model.Member;
import tgpr.framework.ui.layout.Pos;
import tgpr.tuto.model.Security;

import java.util.List;

import static tgpr.framework.util.Tools.asString;
import static tgpr.framework.util.Tools.ifNull;

public class DisplayMemberView extends DialogWindow {

    private final DisplayMemberController controller;
    private Member member;

    private final Label lblPseudo = new Label("").addStyle(SGR.BOLD);
    private final Label lblProfile = new Label("").addStyle(SGR.BOLD);
    private final Label lblBirthDate = new Label("").addStyle(SGR.BOLD);
    private final Label lblRole = new Label("").addStyle(SGR.BOLD);

    public DisplayMemberView(DisplayMemberController controller, Member member) {
        super("View Member");

        this.controller = controller;
        this.member = member;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        var fields = FormGrid.create()
                .padding(Insets.of(1, 1, 1, 0))
                .row("Pseudo:", lblPseudo)
                .row("Profile:", lblProfile)
                .row("Birth Date:", lblBirthDate)
                .row("Role:", lblRole);

        var buttons = HBox.create();
        // Le membre connecté ne peut modifier que ses données ou celles des autres s'il est admin
        if (Security.isAdmin() || Security.isLoggedUser(member))
            buttons.add(new Button("Update", this::update));
        // Seul un admin peut supprimer un membre
        if (Security.isAdmin())
            buttons.add(new Button("Delete", this::delete));
        buttons.add(new Button("Close", this::close));

        setComponent(VBox.create().spacing(1)
                .add(fields)
                .add(buttons.alignInParent(Pos.CENTER)));

        refresh();
    }

    private void refresh() {
        if (member != null) {
            lblPseudo.setText(member.getPseudo());
            lblProfile.setText(ifNull(member.getProfile(), ""));
            lblBirthDate.setText(asString(member.getBirthdate()));
            lblRole.setText(member.isAdmin() ? "Admin" : "Member");
        }
    }

    private void update() {
        member = controller.update();
        refresh();
    }

    private void delete() {
        controller.delete();
    }
}
