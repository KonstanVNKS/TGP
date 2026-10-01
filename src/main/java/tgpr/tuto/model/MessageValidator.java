package tgpr.tuto.model;

import tgpr.framework.validation.Error;
import tgpr.framework.validation.ErrorList;

import java.util.List;

public abstract class MessageValidator {

    public static Error isValidBody(String s) {
        if (s != null && !s.trim().isEmpty())
            return null;
        return new Error("message body is required", Message.Fields.Body);
    }

    public static List<Error> validate(Message message) {
        var errors = new ErrorList();
        errors.add(isValidBody(message.getBody()));
        return errors;
    }

}
