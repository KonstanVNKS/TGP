package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.tuto.model.Member;
import tgpr.tuto.view.MemberListView;

import java.util.List;

public class MemberListController extends Controller<MemberListView> {

    @Override
    public MemberListView getView() {
        return new MemberListView(this);
    }

    public List<Member> getMembers() {
        return Member.getAll();
    }
}
