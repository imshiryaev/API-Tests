package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class GetOrderTest {
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
    @DisplayName("Проверка получения списка заказов пользователя с авторизацией")
    public void getUserOrdersWithAuth(){
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        orderSteps.createOrder(order, accessToken);
        orderSteps.getOrdersWithAuth(accessToken);
    }

    @Test
    @DisplayName("Проверка получения списка заказов пользователя с авторизацией")
    public void getUserOrdersWithoutAuth(){
        ValidatableResponse response = userSteps.createUser(user);
        accessToken = response.extract().path("accessToken");
        orderSteps.createOrder(order, accessToken);
        orderSteps.getOrdersWithAuth("").statusCode(401).body("success", Matchers.is(false)).body("message", Matchers.is("You should be authorised"));;
    }
}
