package com.punitpv48.automation.tests.api;

import com.punitpv48.automation.api.ApiClient;
import com.punitpv48.automation.base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"regression", "api"})
public class ApiRegressionTests extends BaseTest {
    @Test
    public void userEndpointReturnsSuccessAndExpectedFields() {
        Response response = ApiClient.get("/users/1");

        Assert.assertEquals(response.statusCode(), 200, "User endpoint should return HTTP 200.");
        Assert.assertEquals(response.jsonPath().getInt("id"), 1);
        Assert.assertEquals(response.jsonPath().getString("name"), "Leanne Graham");
        Assert.assertTrue(response.jsonPath().getString("email").contains("@"));
    }

    @Test
    public void missingPostReturnsNotFound() {
        Response response = ApiClient.get("/posts/999999");
        Assert.assertEquals(response.statusCode(), 404, "A missing resource should return HTTP 404.");
    }
}
