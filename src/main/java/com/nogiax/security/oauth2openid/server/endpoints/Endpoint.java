package com.nogiax.security.oauth2openid.server.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nogiax.http.Exchange;
import com.nogiax.http.Response;
import com.nogiax.http.ResponseBuilder;
import com.nogiax.security.oauth2openid.Constants;
import com.nogiax.security.oauth2openid.ServerProvider;
import com.nogiax.security.oauth2openid.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public abstract class Endpoint {


    Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ServerProvider serverProvider;
    String path;

    public Endpoint(ServerProvider serverProvider, String path){
        this.serverProvider = serverProvider;
        this.path = path;
    }

    public Exchange useIfResponsible(Exchange exc) throws Exception {
        if(isResponsible(exc))
            return invokeOn(exc);
        return exc;
    }

    public boolean isResponsible(Exchange exc){
        return exc.getRequest().getUri().getPath().endsWith(path);
    }

    public Exchange invokeOn(Exchange exc) throws Exception {
        if(checkParametersOAuth2(exc))
            if(invokeOnOAuth2(exc))
                if(hasOpenIdScope(getScope(exc)))
                    if(checkParametersOpenID(exc))
                        invokeOnOpenId(exc);
        return exc;
    }

    public abstract boolean checkParametersOAuth2(Exchange exc) throws Exception;
    public abstract boolean invokeOnOAuth2(Exchange exc) throws Exception;
    public abstract boolean checkParametersOpenID(Exchange exc) throws Exception;
    public abstract boolean invokeOnOpenId(Exchange exc) throws Exception;
    public abstract String getScope(Exchange exc) throws Exception;

    private boolean hasOpenIdScope(String scope){
        return scope != null && scope.contains(Constants.SCOPE_OPENID);
    }

    protected Response informResourceOwnerError(String error) throws JsonProcessingException {
        return new ResponseBuilder().statuscode(400).body(getErrorBody(error)).build();
    }

    private String getErrorBody(String error) throws JsonProcessingException {
        HashMap<String,String> result = new HashMap<>();
        result.put(Constants.PARAMETER_ERROR,error);
        return new ObjectMapper().writeValueAsString(result);
    }

    protected boolean clientExists(String clientId) {
        return serverProvider.getClientDataProvider().clientExists(clientId);
    }

    protected Response redirectToCallbackWithError(String callbackUrl, String error) {
        String newCallbackUrl = callbackUrl + "?" + Constants.PARAMETER_ERROR +"="+error;
        return new ResponseBuilder().redirectTemp(newCallbackUrl).build();
    }

    protected Response redirectToLogin() {
        return new ResponseBuilder().redirectTemp(Constants.ENDPOINT_LOGIN).build();
    }

    protected boolean isLoggedIn(Exchange exc) throws Exception {
        Session session = serverProvider.getSessionProvider().getSession(exc);
        String loggedIn = session.getValue(Constants.SESSION_LOGGED_IN);
        return Constants.VALUE_YES.equals(loggedIn);
    }

    protected boolean hasGivenConsent(Exchange exc) throws Exception {
        Session session = serverProvider.getSessionProvider().getSession(exc);
        String consentGiven = session.getValue(Constants.SESSION_CONSENT_GIVEN);
        return Constants.VALUE_YES.equals(consentGiven);
    }

    protected boolean isLoggedInAndHasGivenConsent(Exchange exc) throws Exception {
        return isLoggedIn(exc) && hasGivenConsent(exc);
    }
}
