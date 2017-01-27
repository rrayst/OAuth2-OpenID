package com.nogiax.security.oauth2openid.server.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nogiax.http.Exchange;
import com.nogiax.http.Response;
import com.nogiax.http.ResponseBuilder;
import com.nogiax.http.util.UriUtil;
import com.nogiax.security.oauth2openid.Constants;
import com.nogiax.security.oauth2openid.ServerProvider;
import com.nogiax.security.oauth2openid.Session;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xorpherion on 25.01.2017.
 */
public abstract class Endpoint {


    Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ServerProvider serverProvider;
    String[] paths;

    public Endpoint(ServerProvider serverProvider, String... paths){
        this.serverProvider = serverProvider;
        this.paths = paths;
    }

    public Exchange useIfResponsible(Exchange exc) throws Exception {
        if(isResponsible(exc))
            return invokeOn(exc);
        return exc;
    }

    public boolean isResponsible(Exchange exc) {
        for(String path : paths)
            if(exc.getRequest().getUri().getPath().endsWith(path))
                return true;
        return false;
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

    protected Response redirectToLogin(Map<String,String> params) throws UnsupportedEncodingException, JsonProcessingException {
        return new ResponseBuilder().redirectTemp(Constants.ENDPOINT_LOGIN +"#params=" + prepareJSParams(params)).build();
    }

    protected String prepareJSParams(Map<String,String> params) throws JsonProcessingException, UnsupportedEncodingException {
        String json = new ObjectMapper().writeValueAsString(params);
        return UriUtil.encode(Base64.encode(json.getBytes()));
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

    protected Response redirectToConsent(Map<String,String> params) throws UnsupportedEncodingException, JsonProcessingException {
        return new ResponseBuilder().redirectTemp(Constants.ENDPOINT_CONSENT +"#params=" + prepareJSParams(params)).build();
    }
}
