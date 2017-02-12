package com.nogiax.security.oauth2openid.tokenanswers;

import com.nogiax.http.Exchange;
import com.nogiax.security.oauth2openid.Constants;
import com.nogiax.security.oauth2openid.ServerServices;
import com.nogiax.security.oauth2openid.Session;
import com.nogiax.security.oauth2openid.Util;
import com.nogiax.security.oauth2openid.permissions.ClaimsParameter;
import com.nogiax.security.oauth2openid.token.Token;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Xorpherion on 28.01.2017.
 */
public class TokenResponseGenerator extends ResponseGenerator {
    public TokenResponseGenerator(ServerServices serverServices, Exchange exc) {
        super(serverServices, exc,Constants.TOKEN_TYPE_TOKEN, Constants.TOKEN_TYPE_ID_TOKEN);
    }

    @Override
    public Map<String, String> invokeResponse() throws Exception {
        String username = getSession().getValue(Constants.LOGIN_USERNAME);
        String clientId = getSession().getValue(Constants.PARAMETER_CLIENT_ID);
        String scope = getSession().getValue(Constants.PARAMETER_SCOPE);
        String claims = getSession().getValue(Constants.PARAMETER_CLAIMS);
        String code = getSession().getValue(Constants.SESSION_AUTHORIZATION_CODE);
        String grantType = getSession().getValue(Constants.PARAMETER_GRANT_TYPE);
        String refreshTokenValue = getSession().getValue(Constants.PARAMETER_REFRESH_TOKEN);
        String state = getSession().getValue(Constants.PARAMETER_STATE);
        Set<String> responseTypes = new HashSet<String>(Arrays.asList(getSession().getValue(Constants.PARAMETER_RESPONSE_TYPE).split(Pattern.quote(" "))));

        Token parentToken = null;
        if (refreshTokenValue != null) {
            parentToken = getTokenManager().getRefreshTokens().getToken(refreshTokenValue);
            getSession().removeValue(Constants.PARAMETER_REFRESH_TOKEN);
        } else if (getSession().getValue(Constants.SESSION_ENDPOINT).equals(Constants.ENDPOINT_AUTHORIZATION) || code == null) {
            Token fakeAuthToken = getTokenManager().createBearerTokenWithDefaultDuration(username, clientId, scope, claims);
            getTokenManager().getAuthorizationCodes().addToken(fakeAuthToken);
            parentToken = getTokenManager().getAuthorizationCodes().getToken(fakeAuthToken.getValue());
        } else {
            parentToken = getTokenManager().getAuthorizationCodes().getToken(code);
            getSession().removeValue(Constants.SESSION_AUTHORIZATION_CODE);
        }

        Map<String, String> result = new HashMap<>();
        result.put(Constants.PARAMETER_STATE,state);
        String accessTokenValue = null;
        if(responseTypes.contains(Constants.PARAMETER_VALUE_TOKEN)) {
            Token accessToken = getTokenManager().addTokenToManager(getTokenManager().getAccessTokens(), getTokenManager().createChildBearerTokenWithDefaultDuration(parentToken));
            Token refreshToken = getTokenManager().addTokenToManager(getTokenManager().getRefreshTokens(), getTokenManager().createChildBearerToken(Token.getDefaultValidForLong(), parentToken));

            result.put(Constants.PARAMETER_ACCESS_TOKEN, accessToken.getValue());
            result.put(Constants.PARAMETER_TOKEN_TYPE, Constants.PARAMETER_VALUE_BEARER);
            result.put(Constants.PARAMETER_EXPIRES_IN, String.valueOf(accessToken.getValidFor().getSeconds()));
            if (grantType != null && !(grantType.equals(Constants.PARAMETER_VALUE_TOKEN) || grantType.equals(Constants.PARAMETER_VALUE_CLIENT_CREDENTIALS)))
                result.put(Constants.PARAMETER_REFRESH_TOKEN, refreshToken.getValue());

            accessTokenValue = accessToken.getValue();
        }

        if (responseTypes.contains(Constants.PARAMETER_VALUE_ID_TOKEN) && isOpenIdScope()) {
            String authTime = getSession().getValue(Constants.PARAMETER_AUTH_TIME);
            String nonce = getSession().getValue(Constants.PARAMETER_NONCE);
            Set<String> idTokenClaimNames = new ClaimsParameter(claims).getAllIdTokenClaimNames();
            idTokenClaimNames.addAll(getServerServices().getSupportedScopes().getClaimsForScope(scope));
            idTokenClaimNames = getServerServices().getSupportedClaims().getValidClaims(idTokenClaimNames);
            Map<String, String> idTokenClaims = getServerServices().getProvidedServices().getUserDataProvider().getClaims(username, idTokenClaimNames);

            idTokenClaims.put(Constants.CLAIM_AT_HASH, Util.atHashFromValue(Constants.ALG_SHA_256, accessTokenValue));
            idTokenClaims.put(Constants.CLAIM_C_HASH,Util.atHashFromValue(Constants.ALG_SHA_256,code));
            idTokenClaims.put(Constants.PARAMETER_NONCE,nonce);
            idTokenClaims.put(Constants.PARAMETER_AUTH_TIME,authTime);

            Token idToken = getServerServices().getTokenManager().createChildIdToken(getIssuer(), getSubClaim(username), clientId, Token.getDefaultValidFor(), authTime, nonce, idTokenClaims, parentToken);

            result.put(Constants.PARAMETER_ID_TOKEN, idToken.getValue());
        }

        parentToken.incrementUsage();

        return result;
    }

    private String getSubClaim(String username) {
        return getServerServices().getProvidedServices().getUserDataProvider().getSubClaim(username);
    }

    private String getIssuer() {
        return getServerServices().getProvidedServices().getIssuer();
    }

    private boolean isOpenIdScope() throws Exception {
        Session session = getSession();
        String scope = session.getValue(Constants.PARAMETER_SCOPE);
        if (scope != null && scope.contains(Constants.SCOPE_OPENID))
            return true;
        return false;
    }

}
