package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.framework.util.SortOrder;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.framework.mvc.ViewManager;
import tgpr.framework.ui.Ui;
import tgpr.framework.ui.component.ColumnSpec;
import tgpr.framework.ui.component.ObjectTable;
import tgpr.framework.ui.layout.BorderPane;
import tgpr.framework.ui.layout.Insets;
import tgpr.framework.ui.layout.Pos;
import tgpr.framework.ui.layout.Spacing;
import tgpr.framework.ui.layout.VBox;
import tgpr.tuto.controller.MemberListController;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.Security;

import java.util.List;

import static tgpr.framework.util.Tools.asString;
import static tgpr.framework.util.Tools.ifNull;

public class MemberListView extends BasicWindow {

    static final List<Member.Fields> SortableFields = List.of(Member.Fields.Admin, Member.Fields.Profile, Member.Fields.Pseudo);
    private final MemberListController controller;
    private final TextBox txtFilter = new TextBox();
    private ObjectTable<Member> table;
    private final CheckBoxList<String> cklRole = new CheckBoxList<>();
    private final ComboBox<Member.Fields> cboSortField = new ComboBox<>(MemberListView.SortableFields);
    private final ComboBox<SortOrder> cboSortOrder = new ComboBox<>(SortOrder.values());
    private final Menu menuFile = new Menu("File");
    private ComboBox<String> cboRelationship;

    public MemberListView(MemberListController controller) {
        this.controller = controller;

        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));

        var root = BorderPane.create()
                .spacing(Spacing.of(0, 1))
                .centerAlign(Pos.TOP_LEFT)
                .top(createMenu())
                .center(createBody());
        setComponent(root);

        txtFilter.takeFocus();
    }

    private MenuBar createMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.add(menuFile);
        Ui.addShortcut(this, menuFile, KeyStroke.fromString("<A-f>"));
        menuFile.add(new MenuItem("Logout", controller::logout));
        menuFile.add(new MenuItem("Exit", controller::exit));
        return menuBar;
    }

    private Panel createBody() {
        var body = VBox.create()
                .padding(Insets.getDefault());
        body.add(createContent());
        if (Security.isAdmin()) {
            body.add(createAddButton());
        }
        return body;
    }

    private MenuBar createMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.add(menuFile);
        Ui.addShortcut(this, menuFile, KeyStroke.fromString("<A-f>"));
        menuFile.add(new MenuItem("Logout", controller::logout));
        menuFile.add(new MenuItem("Exit", controller::exit));
        return menuBar;
    }

    private Panel createBody() {
        var body = VBox.create()
                .padding(Insets.getDefault());
        body.add(createContent());
        if (Security.isAdmin()) {
            body.add(createAddButton());
        }
        return body;
    }

    private Panel createContent() {
        return VBox.create().spacing(1).add(createFilterPanel(), createTable());
    }

    private Panel createFilterPanel() {
        for (var s : List.of("Admin", "Member")) {
            cklRole.addItem(s);
            cklRole.setChecked(s, true);
        }
        cklRole.addListener((idx, isChecked) -> {
            // both checkboxes may not be unchecked at the same time
            if (!isChecked && !cklRole.isChecked((idx + 1) % 2))
                cklRole.setChecked(cklRole.getItemAt((idx + 1) % 2), true);
            reloadData();
        });

        var relationshipTypes = Member.RelationshipType.getSortedStrings();
        relationshipTypes.add(0, "All");
        cboRelationship = new ComboBox<>(relationshipTypes)
                .addListener((newIndex, oldIndex, byUser) -> reloadData());

        cboSortField.setSelectedItem(Member.Fields.Pseudo);
        cboSortField.addListener((newIndex, oldIndex, byUser) -> reloadData());

        cboSortOrder.setSelectedItem(SortOrder.Ascending);
        cboSortOrder.addListener((newIndex, oldIndex, byUser) -> reloadData());

        txtFilter.setTextChangeListener((txt, byUser) -> reloadData());

        return GridPane.panel(5, Insets.of(0), Spacing.getDefault(),
                new Label("Filter:"),
                txtFilter,
                cklRole,
                new Label("Relationship:"),
                cboRelationship,
                new Label("Sort:"),
                cboSortField,
                cboSortOrder
        );
    }

    private Component createTable() {
        table = new ObjectTable<>(
                new ColumnSpec<>("Pseudo", Member::getPseudo),
                new ColumnSpec<>("Profile", m -> ifNull(m.getProfile(), "")),
                new ColumnSpec<>("Birth Date", m -> asString(m.getBirthdate())),
                new ColumnSpec<>("Role", m -> m.isAdmin() ? "Admin" : "Member"),
                new ColumnSpec<>("Relationship", m -> Security.getLoggedUser().getRelationshipType(m))
        );
        Ui.sizeTo(table, ViewManager.getTerminalColumns(), 15);
        table.setSelectAction(() -> {
            var member = table.getSelected();
            controller.editMember(member);
            reloadData();
            table.setSelected(member);
        });

        reloadData();

        return table;
    }

    private Component createAddButton() {
        var btnAddMember = new Button("Add Member", () -> {
            Member m = controller.addMember();
            if (m != null)
                reloadData();
        });
        Ui.addShortcut(this, btnAddMember, KeyStroke.fromString("<A-a>"));
        return btnAddMember;
    }

    private Member.RelationshipType getRelationshipFilter() {
        var txt = cboRelationship.getText();
        if (txt.equals("All"))
            return null;
        else
            return Member.RelationshipType.valueOf(txt);
    }

    public void reloadData() {
        boolean filterOnAdmin = cklRole.isChecked("Admin");
        boolean filterOnMember = cklRole.isChecked("Member");
        table.clear();
        var members = controller.getMembers(txtFilter.getText(), filterOnAdmin, filterOnMember,
                cboSortField.getSelectedItem(), cboSortOrder.getSelectedItem(),
                getRelationshipFilter());
        table.add(members);
        table.invalidate();
    }

    private String getTitleWithUser() {
        return "Welcome to MSN (" + Security.getLoggedUser().getPseudo() + " - " + (Security.isAdmin() ? "Admin" : "Member") + ")";
    }
}
