package com.nogiax.security.oauth2openid;

import com.nogiax.security.oauth2openid.permissions.Scope;
import com.nogiax.security.oauth2openid.server.SupportedClaims;
import com.nogiax.security.oauth2openid.server.SupportedScopes;
import com.nogiax.security.oauth2openid.token.CombinedTokenManager;

/**
 * Created by Xorpherion on 29.01.2017.
 */
public class ServerServices {
    ProvidedServices providedServices;
    CombinedTokenManager tokenManager;
    SupportedScopes supportedScopes;
    SupportedClaims supportedClaims;

    public ServerServices(ProvidedServices providedServices) {
        this.providedServices = providedServices;
        this.tokenManager = new CombinedTokenManager();
        this.supportedScopes = new SupportedScopes(defaultScopes());
        this.supportedClaims = new SupportedClaims(supportedClaims());

    }

    private Scope[] defaultScopes() {
        return new Scope[]{
                new Scope(Constants.SCOPE_OPENID),
                new Scope(Constants.SCOPE_PROFILE, Constants.CLAIM_NAME, Constants.CLAIM_FAMILY_NAME, Constants.CLAIM_GIVEN_NAME,
                        Constants.CLAIM_MIDDLE_NAME, Constants.CLAIM_NICKNAME, Constants.CLAIM_PREFERRED_USERNAME,
                        Constants.CLAIM_PROFILE, Constants.CLAIM_PICTURE, Constants.CLAIM_WEBSITE, Constants.CLAIM_GENDER,
                        Constants.CLAIM_BIRTHDATE, Constants.CLAIM_ZONEINFO, Constants.CLAIM_LOCALE, Constants.CLAIM_UPDATED_AT),
                new Scope(Constants.SCOPE_EMAIL, Constants.CLAIM_EMAIL, Constants.CLAIM_EMAIL_VERIFIED),
                new Scope(Constants.SCOPE_ADDRESS, Constants.CLAIM_ADDRESS),
                new Scope(Constants.SCOPE_PHONE, Constants.CLAIM_PHONE_NUMBER, Constants.CLAIM_PHONE_NUMBER_VERIFIED)
        };
    }

    private String[] supportedClaims() {
        return new String[]{Constants.CLAIM_SUB, Constants.CLAIM_NAME, Constants.CLAIM_GIVEN_NAME, Constants.CLAIM_FAMILY_NAME, Constants.CLAIM_MIDDLE_NAME, Constants.CLAIM_NICKNAME, Constants.CLAIM_PREFERRED_USERNAME,
                Constants.CLAIM_PROFILE, Constants.CLAIM_PICTURE, Constants.CLAIM_WEBSITE, Constants.CLAIM_EMAIL, Constants.CLAIM_EMAIL_VERIFIED, Constants.CLAIM_GENDER, Constants.CLAIM_BIRTHDATE, Constants.CLAIM_ZONEINFO, Constants.CLAIM_LOCALE,
                Constants.CLAIM_PHONE_NUMBER, Constants.CLAIM_PHONE_NUMBER_VERIFIED, Constants.CLAIM_ADDRESS, Constants.CLAIM_UPDATED_AT};
    }

    public ProvidedServices getProvidedServices() {
        return providedServices;
    }

    public CombinedTokenManager getTokenManager() {
        return tokenManager;
    }

    public SupportedScopes getSupportedScopes() {
        return supportedScopes;
    }

    public SupportedClaims getSupportedClaims() {
        return supportedClaims;
    }
}
