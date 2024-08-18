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

public class UserAuthTest {
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
    @DisplayName("Проверка авторизации пользователя с существующим логином и паролем")
    public void userAuthTest(){
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        userSteps.authUser(user).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя c неверным логином и паролем")
    public void userAuthWithoutPasswordTest(){
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword("etjbmpobeiomptrbromip2345");
        userSteps.authUser(user).statusCode(401).body("success", Matchers.is(false)).body("message", Matchers.is("email or password are incorrect"));
    }
}
