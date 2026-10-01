package tgpr.tuto.controller;

import tgpr.framework.mvc.Controller;
import tgpr.framework.validation.ErrorList;
import tgpr.tuto.model.Member;
import tgpr.tuto.model.MessageValidator;
import tgpr.tuto.model.Security;
import tgpr.tuto.view.AddMessageView;

public class AddMessageController extends Controller<AddMessageView> {

    private final Member member;

    public AddMessageController(Member member) {
        super();
        this.member = member;
    }

    public void post(String body, boolean isPrivate) {
        var errors = validate(body);
        if (errors.isEmpty())
            Security.getLoggedUser().send(member, body, isPrivate);
        else
            showErrors(errors);
    }

    public ErrorList validate(String body) {
        var errors = new ErrorList();
        errors.add(MessageValidator.isValidBody(body));
        return errors;
    }

    @Override
    public AddMessageView getView() {
        return new AddMessageView(this);
    }
}
