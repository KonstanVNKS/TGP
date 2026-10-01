package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.Message;
import tgpr.tuto.model.Security;
import tgpr.tuto.view.DisplayMemberView;
import java.util.List;

public class DisplayMemberController extends Controller<DisplayMemberView> {
    private final DisplayMemberView view;
    private final Member member;

    public DisplayMemberController(Member member) {
        this.member = member;
        view = new DisplayMemberView(this, member);
    }

    public void delete() {
        if (Security.isLoggedUser(member))
            showError("You may not delete yourself!");
        else if (askConfirmation("You are about to delete this member. Please confirm.", "Delete member")) {
            member.delete();
            view.close();
        }
    }

    public Member update() {
        var controller = new EditMemberController(member);
        navigateTo(controller);
        return controller.getMember();
    }

    public List<Message> getMessages() {
        return member.getVisibleMessagesReceived(Security.getLoggedUser());
    }

    public boolean deleteMessage(Message message) {
        if (message == null) return false;
        if (!message.canDelete(Security.getLoggedUser()))
            showError("You're not allowed to delete this message.");
        else if (askConfirmation("Are you sure you want to delete this message?", "Delete Message")) {
            message.delete();
            return true;
        }
        return false;
    }

    public void postMessage() {
        navigateTo(new AddMessageController(member));
    }

    public Message displayMessage(Message message) {
        var controller = new DisplayMessageController(message);
        navigateTo(controller);
        return controller.getMessage();
    }

    @Override
    public DisplayMemberView getView() {
        return view;
    }
    public void toggleFollow() {
        Security.getLoggedUser().toggleFollowUnfollow(member);
    }
}
