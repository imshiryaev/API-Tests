package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OrderTest {
    Order order;
    OrderSteps orderSteps = new OrderSteps();
    String accessToken;
    User user;
    UserSteps userSteps = new UserSteps();
    @Before
    public void setUp(){
        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@mail.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(6));
        user.setName(RandomStringUtils.randomAlphabetic(8));

        List<String> response = orderSteps.getIngredients().extract().path("data._id");
        Collections.shuffle(response);
        order = new Order(response);
    }
    @After
    public void tearDown(){
        if (accessToken != null) userSteps.deleteUser(accessToken);
    }
    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    public void orderTestWithAuth() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        orderSteps.createOrder(order, accessToken).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    public void orderTestWithoutAuth() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        orderSteps.createOrder(order, "").statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка создания заказа с игредиентами")
    public void orderTestWithIngredients() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        orderSteps.createOrder(order, accessToken).statusCode(200).body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    public void orderTestWithoutIngredients() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");

        ArrayList<String> emptyArrayList = new ArrayList<String>();

        orderSteps.createOrder(new Order(emptyArrayList), accessToken).statusCode(400).body("success", Matchers.is(false)).body("message", Matchers.is("Ingredient ids must be provided"));
    }
    @Test
    @DisplayName("Проверка создания заказа с неверным хешем ингредиентов")
    public void orderTestWithoutBrokenIngredient() {
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");

        ArrayList<String> brokenHash = new ArrayList<String>();
        brokenHash.add("45ghtrehrteh");

        orderSteps.createOrder(new Order(brokenHash), accessToken).statusCode(500).body("success", Matchers.is(false));
    }
}
