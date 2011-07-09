package controllers.admin;

import java.text.ParseException;

import controllers.Dbtit;
import controllers.Secure;

import play.libs.Codec;

import models.User;

public class Security extends controllers.Secure.Security {

    /**
     * @Override Secure.Security.authentify()
     */
    public static boolean authenticate(String username, String password) {
        return Codec.hexMD5(username).equals("21232f297a57a5a743894a0e4a801fc3")
                && Codec.hexMD5(password).equals("0046718057498ad1f7363454ccae1925");
    }

    public static void onDisconnected() {
        Dbtit.index();
    }
}
