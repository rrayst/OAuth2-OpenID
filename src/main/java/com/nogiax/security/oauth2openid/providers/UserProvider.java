package com.nogiax.security.oauth2openid.providers;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public interface UserProvider {
    boolean verifyUser(String username, String secret);
}
