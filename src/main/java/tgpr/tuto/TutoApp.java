package tgpr.tuto;

import tgpr.framework.mvc.Controller;
import tgpr.framework.mvc.Model;
import tgpr.tuto.controller.LoginController;

public class TutoApp {
    public final static String DATABASE_SCRIPT_FILE = "/database/tgpr-msn.sql";

    public static void main(String[] args) {
        if (!Model.checkDb(DATABASE_SCRIPT_FILE))
            Controller.abort("Database is not available!");
        else
            Controller.navigateTo(new LoginController());
    }
}
