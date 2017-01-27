package com.nogiax.security.oauth2openid;

import com.nogiax.security.oauth2openid.provider.MembraneClientDataProvider;
import com.nogiax.security.oauth2openid.provider.MembraneSessionProvider;
import com.nogiax.security.oauth2openid.provider.MembraneUserDataProvider;
import com.nogiax.security.oauth2openid.providers.ClientDataProvider;
import com.nogiax.security.oauth2openid.providers.SessionProvider;
import com.nogiax.security.oauth2openid.providers.UserDataProvider;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public class MembraneServerFunctionality implements ServerProvider {

    MembraneSessionProvider sessionProvider;
    MembraneClientDataProvider clientDataProvider;
    MembraneUserDataProvider userDataProvider;

    public MembraneServerFunctionality() {
        sessionProvider = new MembraneSessionProvider("SC_ID");
        clientDataProvider = new MembraneClientDataProvider();
        userDataProvider = new MembraneUserDataProvider();
    }

    @Override
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }

    @Override
    public ClientDataProvider getClientDataProvider() {
        return clientDataProvider;
    }

    @Override
    public UserDataProvider getUserDataProvider() {
        return userDataProvider;
    }
}