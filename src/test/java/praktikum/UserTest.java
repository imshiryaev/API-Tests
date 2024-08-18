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

public class UserTest {

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

    @Test
    @DisplayName("Проверка создания нового пользователя")
    public void userCreateTest() {
        userSteps.createUser(user).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован")
    public void userDoubleCreateTest(){
        userSteps.createUser(user);
        userSteps.createUser(user).statusCode(403).body("success", Matchers.is(false)).body("message", Matchers.is("User already exists"));
    }
    @Test
    @DisplayName("Проверка создания пользователя, без заполнения пароля")
    public void userCreateWithoutPasswordTest(){
        user.setPassword(null);
        userSteps.createUser(user).statusCode(403).body("success", Matchers.is(false)).body("message", Matchers.is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя с существующим логином и паролем")
    public void userAuthTest(){
        userSteps.createUser(user);
        userSteps.authUser(user).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя c неверным логином и паролем")
    public void userAuthWithoutPasswordTest(){
        userSteps.createUser(user);
        user.setPassword("etjbmpobeiomptrbromip2345");
        userSteps.authUser(user).statusCode(401).body("success", Matchers.is(false)).body("message", Matchers.is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка изменения email пользователя с авторизацией")
    public void userUpdateEmailWithAuth() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(RandomStringUtils.randomAlphabetic(7) + "@mail.ru");
        user.setPassword(null);
        user.setName(null);
        userSteps.changeUserData(user, accessToken).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка изменения password пользователя с авторизацией")
    public void userUpdatePasswordWithAuth() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        user.setPassword(RandomStringUtils.randomAlphabetic(7));
        user.setName(null);
        userSteps.changeUserData(user, accessToken).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка изменения name пользователя с авторизацией")
    public void userUpdateNameWithAuth() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        user.setPassword(null);
        user.setName(RandomStringUtils.randomAlphabetic(7));
        userSteps.changeUserData(user, accessToken).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка изменения данных пользователя без авторизации")
    public void userChangeDataWithoutAuth() {
        userSteps.createUser(user);
        user.setEmail(null);
        user.setPassword(RandomStringUtils.randomAlphabetic(7));
        user.setName(null);
        accessToken = "";
        userSteps.changeUserData(user, accessToken).statusCode(401).body("success", Matchers.is(false)).body("message",Matchers.is("You should be authorised"));
    }

    @After
    public void tearDown(){
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }
}
