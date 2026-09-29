package com.punitpv48.automation.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.punitpv48.automation.api.ApiClient;
import com.punitpv48.automation.base.BaseWebTest;
import com.punitpv48.automation.driver.DriverFactory;
import com.punitpv48.automation.pages.JsonResponsePage;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"regression", "api", "web"})
public class ApiUiComparisonTests extends BaseWebTest {
    public void apiUserFieldsMatchTheBrowserRenderedRecord() throws Exception {
        Response apiResponse = ApiClient.get("/users/1");
        Assert.assertEquals(apiResponse.statusCode(), 200, "API request must succeed before comparing data.");

        String browserJson = new JsonResponsePage(DriverFactory.getDriver()).openAndReadJson();
        JsonNode uiRecord = new ObjectMapper().readTree(browserJson);

        Assert.assertEquals(uiRecord.path("id").asInt(), apiResponse.jsonPath().getInt("id"));
        Assert.assertEquals(uiRecord.path("name").asText(), apiResponse.jsonPath().getString("name"));
        Assert.assertEquals(uiRecord.path("email").asText(), apiResponse.jsonPath().getString("email"));
    }
}
