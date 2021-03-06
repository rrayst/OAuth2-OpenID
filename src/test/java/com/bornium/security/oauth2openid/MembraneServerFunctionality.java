package com.bornium.security.oauth2openid;

import com.bornium.security.oauth2openid.provider.MembraneClientDataProvider;
import com.bornium.security.oauth2openid.provider.MembraneSessionProvider;
import com.bornium.security.oauth2openid.provider.MembraneTokenPersistenceProvider;
import com.bornium.security.oauth2openid.provider.MembraneUserDataProvider;
import com.bornium.security.oauth2openid.providers.*;
import com.bornium.security.oauth2openid.server.ProvidedServices;
import com.bornium.security.oauth2openid.token.BearerTokenProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public class MembraneServerFunctionality implements ProvidedServices {

    private final String issuer;
    MembraneSessionProvider sessionProvider;
    MembraneClientDataProvider clientDataProvider;
    MembraneUserDataProvider userDataProvider;
    MembraneTokenPersistenceProvider tokenPersistenceProvider;
    TimingProvider timingProvider;
    TokenProvider tokenProvider;

    public MembraneServerFunctionality(String issuer) {
        sessionProvider = new MembraneSessionProvider("SC_ID");
        clientDataProvider = new MembraneClientDataProvider();
        userDataProvider = new MembraneUserDataProvider();
        tokenPersistenceProvider = new MembraneTokenPersistenceProvider();
        timingProvider = new DefaultTimingProvider();
        tokenProvider = new BearerTokenProvider();
        this.issuer = issuer;
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

    @Override
    public TokenPersistenceProvider getTokenPersistenceProvider() {
        return tokenPersistenceProvider;
    }

    @Override
    public TimingProvider getTimingProvider() {
        return timingProvider;
    }

    @Override
    public TokenProvider getTokenProvider() {
        return tokenProvider;
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return null;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public Set<String> getSupportedClaims() {
        HashSet<String> result = new HashSet<>();
        result.add(ConstantsTest.CUSTOM_CLAIM_NAME);
        return result;
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getSubClaimName() {
        return "username";
    }
}
