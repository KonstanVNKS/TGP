package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tuto.controller.AddMessageController;
import tgpr.tuto.model.Message;
import tgpr.framework.ui.Ui;
import tgpr.framework.ui.layout.FormGrid;
import tgpr.framework.ui.layout.HBox;
import tgpr.framework.ui.layout.VBox;

import java.util.List;

public class AddMessageView extends DialogWindow {

    private final AddMessageController controller;

    private final TextBox txtBody = new TextBox("", TextBox.Style.MULTI_LINE);
    private final CheckBox chkPrivate = new CheckBox();
    private final Button btnPost = new Button("Post");
    private FormGrid fieldsGrid;

    public AddMessageView(AddMessageController controller) {
        super("Post a New Message");

        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        setCloseWindowWithEscape(true);

        txtBody.setTextChangeListener((txt, byUser) -> validate());

        setComponent(VBox.createCentered().add(
                createFields(),
                createButtons()
        ));

        txtBody.takeFocus();
        validate();
    }

    private Panel createFields() {
        fieldsGrid = FormGrid.createPadded()
                .row("Body:", Ui.sizeTo(txtBody, 30, 5), Message.Fields.Body)
                .row("Is Private:", chkPrivate);
        return fieldsGrid;
    }

    private HBox createButtons() {
        btnPost.setEnabled(false).addListener(button -> post());

        return HBox.createButtonRow(btnPost, new Button("Cancel", this::close));
    }

    private void validate() {
        var errors = controller.validate(txtBody.getText());
        fieldsGrid.applyErrors(errors);
        btnPost.setEnabled(errors.isEmpty());
    }

    private void post() {
        controller.post(txtBody.getText(), chkPrivate.isChecked());
        close();
    }
}
