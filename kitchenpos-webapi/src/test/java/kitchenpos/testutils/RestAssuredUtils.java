package kitchenpos.testutils;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RestAssuredUtils {

	public static ExtractableResponse<Response> get(String path) {
		return RestAssured.given().log().all()
			.when().get(path)
			.then()
			.log().all().extract();
	}

	public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
		return RestAssured.given().log().all()
			.when()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(requestBody)
			.post(path)
			.then().log().all().extract();
	}

	public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
		return RestAssured.given().log().all()
			.when()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(requestBody)
			.put(path)
			.then().log().all().extract();
	}

	public static ExtractableResponse<Response> delete(String path) {
		return RestAssured.given().log().all()
			.when()
			.delete(path)
			.then().log().all().extract();
	}
}