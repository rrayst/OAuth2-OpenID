package com.bornium.security.oauth2openid.unit.tokenEndpoint;

import com.bornium.http.Exchange;
import com.bornium.http.util.UriUtil;
import com.bornium.security.oauth2openid.Constants;
import com.bornium.security.oauth2openid.ConstantsTest;
import com.bornium.security.oauth2openid.unit.Common;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Xorpherion on 06.02.2017.
 */
@DisplayName("TokenEndpoint.Password")
public class Password extends BaseTokenEndpointTests {
    @Override
    public String getGrantType() {
        return Constants.PARAMETER_VALUE_PASSWORD;
    }

    @Override
    public String getRedirectUri() {
        return null;
    }

    @Override
    public String getScope() {
        return ConstantsTest.CLIENT_DEFAULT_SCOPE;
    }

    @Override
    public String getClientId() {
        return ConstantsTest.CLIENT_DEFAULT_ID;
    }

    @Override
    public String getClientSecret() {
        return ConstantsTest.CLIENT_DEFAULT_SECRET;
    }

    @Override
    public String getUsername() {
        return ConstantsTest.USER_DEFAULT_NAME;
    }

    @Override
    public String getPassword() {
        return ConstantsTest.USER_DEFAULT_PASSWORD;
    }

    @Override
    public Supplier<Exchange> getPreStep() {
        return null;
    }

    @Override
    public String getDeviceCode() {
        return null;
    }

    @Test
    public void goodRequest() throws Exception {
        Common.testExchangeOn(server,
                () -> {
                    try {
                        return Common.preStepAndTokenRequest(getPreStep(), getGrantType(), getRedirectUri(), getScope(), getClientId(), getClientSecret(), getUsername(), getPassword(), getDeviceCode());
                    } catch (Exception e) {
                        return Common.defaultExceptionHandling(e);
                    }
                },
                (exc) -> {
                    assertAll(
                            Common.getMethodName(),
                            () -> assertEquals(200, exc.getResponse().getStatuscode()),
                            () -> assertNotNull(Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_ACCESS_TOKEN)),
                            () -> assertNotNull(Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_EXPIRES_IN)),
                            () -> assertNull(Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_CODE)),
                            () -> assertNotNull(Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_REFRESH_TOKEN))
                    );
                });
    }

    @Test
    public void missingUsername() throws Exception {
        Common.testExchangeOn(server,
                () -> {
                    try {
                        Exchange exc = Common.preStepAndTokenRequest(getPreStep(), getGrantType(), getRedirectUri(), getScope(), getClientId(), getClientSecret(), getUsername(), getPassword(), getDeviceCode());
                        Map<String, String> params = UriUtil.queryToParameters(exc.getRequest().getBody());
                        params.remove(Constants.PARAMETER_USERNAME);
                        exc.getRequest().setBody(UriUtil.parametersToQuery(params));
                        return exc;
                    } catch (Exception e) {
                        return Common.defaultExceptionHandling(e);
                    }
                },
                (exc) -> {
                    assertAll(
                            Common.getMethodName(),
                            () -> assertEquals(400, exc.getResponse().getStatuscode()),
                            () -> assertEquals(Constants.ERROR_INVALID_REQUEST, Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_ERROR))
                    );
                });
    }

    @Test
    public void badUsername() throws Exception {
        Common.testExchangeOn(server,
                () -> {
                    try {
                        Exchange exc = Common.preStepAndTokenRequest(getPreStep(), getGrantType(), getRedirectUri(), getScope(), getClientId(), getClientSecret(), getUsername(), getPassword(), getDeviceCode());
                        Map<String, String> params = UriUtil.queryToParameters(exc.getRequest().getBody());
                        params.remove(Constants.PARAMETER_USERNAME);
                        params.put(Constants.PARAMETER_USERNAME, "43097438904723843280492304238904789407230");
                        exc.getRequest().setBody(UriUtil.parametersToQuery(params));
                        return exc;
                    } catch (Exception e) {
                        return Common.defaultExceptionHandling(e);
                    }
                },
                (exc) -> {
                    assertAll(
                            Common.getMethodName(),
                            () -> assertEquals(401, exc.getResponse().getStatuscode()),
                            () -> assertEquals(Constants.ERROR_ACCESS_DENIED, Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_ERROR))
                    );
                });
    }

    @Test
    public void missingPassword() throws Exception {
        Common.testExchangeOn(server,
                () -> {
                    try {
                        Exchange exc = Common.preStepAndTokenRequest(getPreStep(), getGrantType(), getRedirectUri(), getScope(), getClientId(), getClientSecret(), getUsername(), getPassword(), getDeviceCode());
                        Map<String, String> params = UriUtil.queryToParameters(exc.getRequest().getBody());
                        params.remove(Constants.PARAMETER_PASSWORD);
                        exc.getRequest().setBody(UriUtil.parametersToQuery(params));
                        return exc;
                    } catch (Exception e) {
                        return Common.defaultExceptionHandling(e);
                    }
                },
                (exc) -> {
                    assertAll(
                            Common.getMethodName(),
                            () -> assertEquals(400, exc.getResponse().getStatuscode()),
                            () -> assertEquals(Constants.ERROR_INVALID_REQUEST, Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_ERROR))
                    );
                });
    }

    @Test
    public void badPassword() throws Exception {
        Common.testExchangeOn(server,
                () -> {
                    try {
                        Exchange exc = Common.preStepAndTokenRequest(getPreStep(), getGrantType(), getRedirectUri(), getScope(), getClientId(), getClientSecret(), getUsername(), getPassword(), getDeviceCode());
                        Map<String, String> params = UriUtil.queryToParameters(exc.getRequest().getBody());
                        params.remove(Constants.PARAMETER_PASSWORD);
                        params.put(Constants.PARAMETER_PASSWORD, "43097438904723843280492304238904789407230");
                        exc.getRequest().setBody(UriUtil.parametersToQuery(params));
                        return exc;
                    } catch (Exception e) {
                        return Common.defaultExceptionHandling(e);
                    }
                },
                (exc) -> {
                    assertAll(
                            Common.getMethodName(),
                            () -> assertEquals(401, exc.getResponse().getStatuscode()),
                            () -> assertEquals(Constants.ERROR_ACCESS_DENIED, Common.getBodyParamsFromResponse(exc).get(Constants.PARAMETER_ERROR))
                    );
                });
    }
}
