package tgpr.tuto.view;

import com.googlecode.lanterna.gui2.BasicWindow;
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

    public MemberListView(MemberListController controller) {
        this.controller = controller;

        setTitle("Welcome to MSN");
        setHints(List.of(Hint.EXPANDED));

        var root = VBox.create();
        setComponent(root);
    }
}
