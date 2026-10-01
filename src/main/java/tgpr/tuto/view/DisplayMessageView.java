package tgpr.tuto.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tuto.controller.DisplayMessageController;
import tgpr.tuto.model.Message;
import tgpr.tuto.model.Security;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.VBox;

import java.util.List;

import static tgpr.framework.util.Tools.asString;

public class DisplayMessageView extends DialogWindow {

    private final DisplayMessageController controller;
    private final Message message;

    private final Label lblPostId = new Label("").addStyle(SGR.BOLD);
    private final Label lblAuthor = new Label("").addStyle(SGR.BOLD);
    private final Label lblRecipient = new Label("").addStyle(SGR.BOLD);
    private final Label lblBody = new Label("").addStyle(SGR.BOLD).setLabelWidth(30);
    private final Label lblDateTime = new Label("").addStyle(SGR.BOLD);
    private final Label lblIsPrivate = new Label("").addStyle(SGR.BOLD);

    public DisplayMessageView(DisplayMessageController controller, Message message) {
        super("View Message");

        this.controller = controller;
        this.message = message;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        setComponent(VBox.createCentered().add(
                createFieldsPanel(),
                createButtonsPanel()
        ));

        refresh();
    }

    private Panel createFieldsPanel() {
        return FormGrid.createPadded()
                .row("Post Id:", lblPostId)
                .row("Author:", lblAuthor)
                .row("Recipient:", lblRecipient)
                .row("Body:", lblBody)
                .row("Is Private:", lblIsPrivate)
                .row("Sent On:", lblDateTime);
    }

    private HBox createButtonsPanel() {
        return HBox.createButtonRow(
                new Button("Delete", this::delete)
                        .setVisible(message.canDelete(Security.getLoggedUser())),
                new Button("Close", this::close)
        );
    }

    private void refresh() {
        if (message != null) {
            lblPostId.setText(String.valueOf(message.getPostId()));
            lblAuthor.setText(message.getAuthorPseudo());
            lblRecipient.setText(message.getRecipientPseudo());
            lblBody.setText(message.getBody());
            lblDateTime.setText(asString(message.getDateTime()));
            lblIsPrivate.setText(message.getPrivate() ? "Yes" : "No");
        }
    }

    private void delete() {
        controller.delete();
    }
}
