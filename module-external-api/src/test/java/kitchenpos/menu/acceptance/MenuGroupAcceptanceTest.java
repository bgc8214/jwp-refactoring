package kitchenpos.menu.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.AcceptanceTest;
import kitchenpos.menu.dto.MenuGroupRequest;
import kitchenpos.menu.dto.MenuGroupResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 그룹 인수 테스트")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void createProduct() {
        MenuGroupRequest 튀김종류 = new MenuGroupRequest("튀김종류");
        // when
        ExtractableResponse<Response> response = 메뉴_그룹_생성_요청(튀김종류);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("메뉴 그룹 목록을 조회한다.")
    @Test
    void findAllProduct() {
        // when
        ExtractableResponse<Response> response = 메뉴_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 메뉴_그룹_생성_요청(MenuGroupRequest menuGroupRequest) {
        return ofRequest(Method.POST, "/api/menu-groups", menuGroupRequest);
    }

    private ExtractableResponse<Response> 메뉴_목록_조회_요청() {
        return ofRequest(Method.GET, "/api/menu-groups");
    }

    public static MenuGroupResponse 메뉴_그룹_생성됨(MenuGroupRequest menuGroupRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(menuGroupRequest)
                .when()
                .post("/api/menu-groups")
                .then().log().all()
                .extract()
                .as(MenuGroupResponse.class);
    }
}