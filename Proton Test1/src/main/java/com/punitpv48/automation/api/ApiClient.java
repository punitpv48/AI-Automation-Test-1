package com.punitpv48.automation.api;

import com.punitpv48.automation.config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public final class ApiClient {
    private ApiClient() {
    }

    public static Response get(String path) {
        return RestAssured.given()
                .baseUri(Config.get("apiBaseUrl"))
                .accept(ContentType.JSON)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }
}
