package com.nogiax.security.oauth2openid.providers;

import com.nogiax.http.Exchange;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public interface SessionProvider {

    Session getSession(Exchange exc);
}
