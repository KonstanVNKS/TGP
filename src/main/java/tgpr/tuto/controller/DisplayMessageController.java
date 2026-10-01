package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.tuto.model.Message;
import tgpr.tuto.view.DisplayMessageView;

public class DisplayMessageController extends Controller<DisplayMessageView> {

    private final DisplayMessageView view;
    private Message message;

    public DisplayMessageController(Message message) {
        this.message = message;
        view = new DisplayMessageView(this, message);
    }

    public void delete() {
        if (askConfirmation("You are about to delete this message. Please confirm.", "Delete Message")) {
            message.delete();
            view.close();
            message = null;
        }
    }

    @Override
    public DisplayMessageView getView() {
        return view;
    }

    public Message getMessage() {
        return message;
    }
}
