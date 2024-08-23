package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.steps.UserSteps;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserCreateTest {
    private UserSteps userSteps = new UserSteps();
    private User user;
    private String accessToken;

    @Before
    public void setUp(){
        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setName(RandomStringUtils.randomAlphabetic(8));
    }

    @After
    public void tearDown(){
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Проверка создания нового пользователя")
    public void userCreateTest() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        response.statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован")
    public void userDoubleCreateTest(){
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        userSteps.createUser(user).statusCode(403).body("success", Matchers.is(false)).body("message", Matchers.is("User already exists"));
    }
    @Test
    @DisplayName("Проверка создания пользователя, без заполнения пароля")
    public void userCreateWithoutPasswordTest(){
        user.setPassword(null);
        ValidatableResponse response = userSteps.createUser(user);
        response.statusCode(403).body("success", Matchers.is(false)).body("message", Matchers.is("Email, password and name are required fields"));
    }
}
