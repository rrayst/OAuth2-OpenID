package com.bornium.security.oauth2openid.responsegenerators;

import com.bornium.http.Exchange;
import com.bornium.security.oauth2openid.server.ServerServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xorpherion on 28.01.2017.
 */
public class CombinedResponseGenerator {

    protected final Exchange exc;
    protected final ServerServices serverServices;

    protected ArrayList<ResponseGenerator> responseGenerators;

    public CombinedResponseGenerator(ServerServices serverServices, Exchange exc) {
        this.serverServices = serverServices;
        this.exc = exc;

        this.responseGenerators = new ArrayList<>();
        responseGenerators.add(new CodeResponseGenerator(serverServices, exc));
        responseGenerators.add(new TokenResponseGenerator(serverServices, exc));
    }

    public Map<String, String> invokeResponse(String responseType) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        for (ResponseGenerator responseGenerator : responseGenerators)
            if (responseGenerator.isMyResponseType(responseType))
                result.putAll(responseGenerator.invokeResponse());
        return result;
    }
}
