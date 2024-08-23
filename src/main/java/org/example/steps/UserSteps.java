package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.config.BaseHttpClient;
import org.example.model.User;


import static io.restassured.RestAssured.given;

public class UserSteps {
    private static final String CREATE_USER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";
    private static final String USER_ENDPOINT = "/api/auth/user";

    @Step("Создание нового пользователя")
    public ValidatableResponse createUser(User user) {
            return given(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .post(CREATE_USER).then();
    }

    @Step("Изменения данных пользователя")
    public ValidatableResponse changeUserData(User user, String accessToken) {
        return given(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER_ENDPOINT)
                .then();
    }

    @Step("Изменения данных пользователя без авторизации")
    public ValidatableResponse changeUserDataWithoutAuth(User user) {
        return given(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .patch(USER_ENDPOINT)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken){
        given(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_ENDPOINT)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse authUser(User user){
        return given(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .post(LOGIN)
                .then();
    }
}
