package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.config.BaseHttpClient;
import org.example.model.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String ORDERS_ENDPOINT = "/api/orders";
    private static final String GET_INGREDIENTS = "/api/ingredients";

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Получение всех ингредиентов")
    public ValidatableResponse getIngredients() {
        return given(BaseHttpClient.baseRequestSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse getOrdersWithAuth(String accessToken) {
        return given(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_ENDPOINT)
                .then();
    }
}
