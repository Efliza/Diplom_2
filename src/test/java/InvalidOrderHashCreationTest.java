import methods.RequestSpec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;

import org.junit.runners.Parameterized;
import methods.OrderRequests;

import java.util.List;

@RunWith(Parameterized.class)
public class InvalidOrderHashCreationTest {
    private final List<String> ingredients;
    private OrderRequests orderRequests;
    private Order order;


    public InvalidOrderHashCreationTest(List<String> ingredients) {
        this.ingredients = ingredients;
    }


    @Parameterized.Parameters
    public static Object[][] getData(){
        Faker faker = new Faker();
        return new Object[][]{
                {List.of(faker.number().digits(7))},
                {List.of(faker.number().digits(23))},
                {List.of(faker.number().digits(25))}
        };
    }

    @Before
    public void setUp()  {
        RestAssured.requestSpecification = RequestSpec.requestSpecification();
        orderRequests = new OrderRequests();
        order = new Order(ingredients);
    }

    @Test
    @DisplayName("Проверка отправки невалидного хэша при создании заказа")
    public void createOrderInvalidHash(){
        Response responseCreate = orderRequests.createOrder(order);
        responseCreate.then().log().all().statusCode(500);
    }
}
