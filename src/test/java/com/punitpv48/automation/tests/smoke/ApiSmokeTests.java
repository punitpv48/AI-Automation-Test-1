package com.punitpv48.automation.tests.smoke;

import com.punitpv48.automation.api.ApiClient;
import com.punitpv48.automation.base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"smoke", "api"})
public class ApiSmokeTests extends BaseTest {
    public void apiIsRespondingWithoutServerErrors() {
        Response response = ApiClient.get("/posts/1");
        Assert.assertTrue(response.statusCode() < 500,
                "API returned a server error: HTTP " + response.statusCode());
        Assert.assertFalse(response.asString().isBlank(), "API returned an empty response body.");
    }
}
