package tgpr.tuto.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.framework.ui.component.ColumnSpec;
import tgpr.framework.ui.component.ObjectTable;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.Insets;
import tgpr.framework.ui.layout.Layout;
import tgpr.framework.ui.layout.Pos;
import tgpr.framework.ui.layout.VBox;
import tgpr.tuto.controller.DisplayMemberController;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.Message;
import tgpr.tuto.model.Security;

import java.util.List;

import static tgpr.framework.util.Tools.asString;
import static tgpr.framework.util.Tools.ifNull;

public class DisplayMemberView extends DialogWindow {

    private final DisplayMemberController controller;
    private Member member;
    private final Label lblRelationship = new Label("");
    private Button btnToggleFollow = null;

    private final Label lblPseudo = new Label("").addStyle(SGR.BOLD);
    private final Label lblProfile = new Label("").addStyle(SGR.BOLD);
    private final Label lblBirthDate = new Label("").addStyle(SGR.BOLD);
    private final Label lblRole = new Label("").addStyle(SGR.BOLD);
    private ObjectTable<Message> messageTable;
    private final Label lblNoMessages = new Label("No messages");
    private VBox pnlMessages;

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
        if (!Security.isLoggedUser(member))
            buttons.add(btnToggleFollow = new Button("", this::toggleFollow));
        // Le membre connecté ne peut modifier que ses données ou celles des autres s'il est admin
        if (Security.isAdmin() || Security.isLoggedUser(member))
            buttons.add(new Button("Update", this::update));
        // Seul un admin peut supprimer un membre
        if (Security.isAdmin())
            buttons.add(new Button("Delete", this::delete));
        buttons.add(new Button("Post Message", this::post));
        buttons.add(new Button("Close", this::close));

        setComponent(VBox.create().spacing(1)
                .add(fields)
                .add(lblRelationship.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT), Pos.CENTER)
                .add(createMessagePanel())
                .add(buttons.alignInParent(Pos.CENTER)));

        refresh();
    }

    private Component createMessagePanel() {
        pnlMessages = VBox.create();
        messageTable = new ObjectTable<>(
                new ColumnSpec<>("Sent", m -> asString(m.getDateTime())),
                new ColumnSpec<>("Author", Message::getAuthorPseudo),
                new ColumnSpec<>("Title", Message::getBody).setWidth(30)
                        .setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Private", m -> m.getPrivate() ? "Yes" : "No")
        );
        messageTable.setSelectAction(this::displayMessage);
        messageTable.setKeyStrokeHandler(ks -> {
            if (isDeleteKey(ks) && controller.deleteMessage(messageTable.getSelected())) {
                refresh();
                messageTable.takeFocus();
                return true;
            }
            return false;
        });
        lblNoMessages.setForegroundColor(TextColor.ANSI.RED);

        return Layout.bordered(" Messages ", pnlMessages);
    }

    private static boolean isDeleteKey(KeyStroke keyStroke) {
        var type = keyStroke.getKeyType();
        return type == KeyType.Delete || type == KeyType.Backspace;
    }


    private void refresh() {
        if (member != null) {
            lblPseudo.setText(member.getPseudo());
            lblProfile.setText(ifNull(member.getProfile(), ""));
            lblBirthDate.setText(asString(member.getBirthdate()));
            lblRole.setText(member.isAdmin() ? "Admin" : "Member");
            var rel = Security.getLoggedUser().getRelationshipType(member);
            lblRelationship.setText(rel.toText());
            if (btnToggleFollow != null)
                btnToggleFollow.setLabel(rel == Member.RelationshipType.Unrelated || rel == Member.RelationshipType.Follower ? "Follow" : "Unfollow");
            var messages = controller.getMessages();
            pnlMessages.clear();
            if (messages.isEmpty()) {
                pnlMessages.add(lblNoMessages);
            } else {
                pnlMessages.add(messageTable);
                messageTable.clear();
                messageTable.add(messages);
            }
            pnlMessages.invalidate();
        }
    }

    private void update() {
        member = controller.update();
        refresh();
    }

    private void delete() {
        controller.delete();
    }

    private void toggleFollow() {
        controller.toggleFollow();
        refresh();
    }

    private void post() {
        controller.postMessage();
        refresh();
    }

    private void displayMessage() {
        var message = messageTable.getSelected();
        if (message == null) return;
        if (controller.displayMessage(message) == null)
            refresh();
    }
}
