package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
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

    private final MemberListController controller;
    private final ObjectTable<Member> table;

    public MemberListView(MemberListController controller) {
        this.controller = controller;

        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));

        MenuBar menuBar = new MenuBar();
        var menuFile = new Menu("File");
        menuBar.add(menuFile);
        menuFile.add(new MenuItem("Logout", controller::logout));
        menuFile.add(new MenuItem("Exit", controller::exit));

        var body = VBox.create()
                .padding(Insets.getDefault())
                .spacing(1);

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

        body.add(table);

        if (Security.isAdmin()) {
            var btnAddMember = new Button("Add Member", () -> {
                Member m = controller.addMember();
                if (m != null)
                    reloadData();
            });
            body.add(btnAddMember);
        }
        setComponent(BorderPane.create()
                .spacing(Spacing.of(0, 1))
                .centerAlign(Pos.TOP_LEFT)
                .top(menuBar)
                .center(body));

        reloadData();
    }

    public void reloadData() {
        table.clear();
        var members = controller.getMembers();
        table.add(members);
    }

    private String getTitleWithUser() {
        return "Welcome to MSN (" + Security.getLoggedUser().getPseudo() + " - " + (Security.isAdmin() ? "Admin" : "Member") + ")";
    }
}
