import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Login;
import models.User;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import methods.UserRequests;

import static constants.ApiConstants.BURGERS_URL;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class LoginTest {
    private final String email;
    private final String password;
    private final int statusCode;
    private final boolean success;
    private User user;
    private UserRequests userRequests;
    private Login login;
    private String accessToken;
    private final int createStatusCode;
    private final String message;


    public LoginTest(String email, String password, int statusCode, boolean success, int createStatusCode, String message) {
        this.email = email;
        this.password = password;
        this.statusCode = statusCode;
        this.success = success;
        this.createStatusCode = createStatusCode;
        this.message = message;
    }


    @Parameterized.Parameters
    public static Object[][] getData() {
        Faker faker = new Faker();
        return new Object[][]{
                {"iigorek@yandex.ru", "!smolensk256", 200, true, 200, null},
                {"iigorek@yandex.ru", faker.internet().password(), 401, false, 200, "email or password are incorrect"},
                {null, "!smolensk256", 401, false, 200, "email or password are incorrect"},
                {"iigorek@yandex.ru", null, 401, false, 200, "email or password are incorrect"},
                {faker.internet().emailAddress(), "987283467", 401, false, 200, "email or password are incorrect"}
        };
    }

    @Before
    public void setUp()  {
        RestAssured.baseURI = BURGERS_URL;
        user = new User("iigorek@yandex.ru", "!smolensk256", "Igor");
        userRequests = new UserRequests();
        login = new Login(email, password);
    }


    @Test
    @DisplayName("Проверка логина пользователя")
    public void loginUser(){
        Response responseCreate = userRequests.createUser(user);
        accessToken = responseCreate.then().log().all().statusCode(createStatusCode).extract().path("accessToken");
        Response responseLogin = userRequests.loginUser(login);
        responseLogin.then().log().all().statusCode(statusCode).body("success", equalTo(success)).body("message", equalTo(message));
    }

    @After
    public void deleteUser() {
        if (createStatusCode == 200) {
            Response responseDelete = userRequests.deleteUser(accessToken);
            responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
        }
    }
}
