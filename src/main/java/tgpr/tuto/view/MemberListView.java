package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import tgpr.framework.mvc.ViewManager;
import tgpr.framework.ui.Ui;
import tgpr.framework.ui.component.ColumnSpec;
import tgpr.framework.ui.component.ObjectTable;
import tgpr.framework.ui.layout.VBox;
import tgpr.tuto.controller.MemberListController;
import tgpr.tuto.model.Member;

import java.util.List;

import static tgpr.framework.util.Tools.asString;
import static tgpr.framework.util.Tools.ifNull;

public class MemberListView extends BasicWindow {

    private final MemberListController controller;
    private final ObjectTable<Member> table;

    public MemberListView(MemberListController controller) {
        this.controller = controller;

        setTitle("Welcome to MSN");
        setHints(List.of(Hint.EXPANDED));

        var root = VBox.create();
        setComponent(root);

        table = new ObjectTable<>(
                new ColumnSpec<>("Pseudo", Member::getPseudo),
                new ColumnSpec<>("Profile", m -> ifNull(m.getProfile(), "")),
                new ColumnSpec<>("Birth Date", m -> asString(m.getBirthdate())),
                new ColumnSpec<>("Role", m -> m.isAdmin() ? "Admin" : "Member")
        );
        Ui.sizeTo(table, ViewManager.getTerminalColumns(), 15);

        table.setSelectAction(() -> {
            var member = table.getSelected();
            controller.editMember(member);
            reloadData();
            table.setSelected(member);
        });

        root.add(new EmptySpace(), table);

        root.add(new EmptySpace());

        var btnAddMember = new Button("Add Member", () -> {
            Member m = controller.addMember();
            if (m != null)
                reloadData();
        });
        root.add(btnAddMember);

        reloadData();
    }

    public void reloadData() {
        table.clear();
        var members = controller.getMembers();
        table.add(members);
    }
}
