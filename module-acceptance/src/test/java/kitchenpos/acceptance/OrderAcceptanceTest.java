package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuAcceptanceTest.응답코드_확인;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.dto.OrderLineItemRequest;
import kitchenpos.order.dto.OrderRequest;
import kitchenpos.order.dto.OrderResponse;
import kitchenpos.order.dto.OrderStatusRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class OrderAcceptanceTest extends BaseAcceptanceTest {

    @DisplayName("주문을 관리한다")
    @Test
    public void manageOrder() {
        //메뉴 생성
        //given
        OrderRequest order = OrderRequest.of(1l, Arrays.asList(OrderLineItemRequest.of(1l, 1)));

        //when
        ExtractableResponse<Response> 주문_생성_요청 = 주문_생성_요청(order);
        //then
        응답코드_확인(주문_생성_요청, HttpStatus.CREATED);

        //메뉴 그룹 조회
        //when
        ExtractableResponse<Response> 주문_목록_조회_요청 = 주문_목록_조회_요청();
        //then
        응답코드_확인(주문_목록_조회_요청, HttpStatus.OK);
        주문_조회됨(주문_목록_조회_요청, 주문_생성_요청.as(OrderResponse.class).getId());

        //메뉴 상태 변경
        //given
        OrderStatusRequest 변경주문상태 = OrderStatusRequest.from(OrderStatus.MEAL);
        //when
        ExtractableResponse<Response> 주문_상태_변경_요청 = 주문_상태_변경_요청(주문_생성_요청.as(OrderResponse.class).getId(), 변경주문상태);
        //then
        응답코드_확인(주문_상태_변경_요청, HttpStatus.OK);
        상태_변경됨(주문_상태_변경_요청, OrderStatus.MEAL);

    }

    public static ExtractableResponse<Response> 주문_생성_요청(OrderRequest order) {

        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(order)
            .when().post("/api/orders")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 주문_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/api/orders")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 주문_상태_변경_요청(Long orderId, OrderStatusRequest orderStatusRequest) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(orderStatusRequest)
            .when().put("/api/orders/{orderId}/order-status", orderId)
            .then().log().all()
            .extract();
    }

    public static void 주문_조회됨(final ExtractableResponse<Response> response, Long id) {
        assertThat(response.jsonPath().getList(".", OrderResponse.class).stream().anyMatch(order -> order.getId().equals(id))).isTrue();
    }

    public static void 상태_변경됨(final ExtractableResponse<Response> response, OrderStatus orderStats) {
        assertThat(response.as(OrderResponse.class).getOrderStatus()).isEqualTo(orderStats);
    }


}
