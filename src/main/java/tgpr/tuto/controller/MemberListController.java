package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.Security;
import tgpr.tuto.view.MemberListView;
import tgpr.framework.util.SortOrder;

import java.util.List;

public class MemberListController extends Controller<MemberListView> {


    @Override
    public MemberListView getView() {
        return new MemberListView(this);
    }

    public List<Member> getMembers(String filter, boolean filterOnAdmin, boolean filterOnMember,
                                   Member.Fields sortField, SortOrder sortOrder, Member.RelationshipType relationshipType) {
        return Member.getFiltered(filter, filterOnAdmin && filterOnMember ? null : filterOnAdmin, sortField, sortOrder, relationshipType);
    }

    public Member addMember() {
        var controller = new EditMemberController();
        navigateTo(controller);
        return controller.getMember();
    }
    public void editMember(Member member) {
        navigateTo(new DisplayMemberController(member));
    }

    public void logout() {
        Security.logout();
        navigateTo(new LoginController());
    }

    public void exit() {
        System.exit(0);
    }
}
