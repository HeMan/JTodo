package se.cygni.jtodo;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JTodoApplicationTests {

	@LocalServerPort
	private Integer port;

	final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:16-alpine"
	);

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	TodoRepository todoRepository;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + port + "/api/todos";
		todoRepository.deleteAll();
	}

	@Test
	void addTodos() {
		given()
				.contentType(ContentType.JSON)
				.when()
				.body("{\"task\": \"test task\"}")
				.post()
				.then()
				.statusCode(200)
				.body("task", equalTo("test task"))
				.body("id", equalTo(1))
				.body("done", equalTo(false));
	}



	@Test
	void modifyTodo() {
		RequestSpecification addTodo = RestAssured.given();
		addTodo.body("{\"task\": \"to be changed\"}");
		addTodo.contentType(ContentType.JSON);
		Response addResponse = addTodo.post();
		JsonParser springParser = JsonParserFactory.getJsonParser();
		Map<String, Object> json = springParser.parseMap(addResponse.getBody().asString());
		Integer id = (Integer) json.get("id");
		given()
				.contentType(ContentType.JSON)
				.when()
				.body("{\"task\": \"Updated task\"}")
				.put("/" + id)
				.then()
				.statusCode(200)
				.body("task", equalTo("Updated task"));
	}

	@Test
	void modifyUnknownTodo(){
		given()
				.contentType(ContentType.JSON)
				.when()
				.body("{\"task\": \"Updated task\"}")
				.put("/1231232")
				.then()
				.statusCode(404);
	}

	@Test
	void deleteTodo() {
		RequestSpecification addTodo = RestAssured.given();
		addTodo.body("{\"task\": \"to be changed\"}");
		addTodo.contentType(ContentType.JSON);
		Response addResponse = addTodo.post();
		JsonParser springParser = JsonParserFactory.getJsonParser();
		Map<String, Object> json = springParser.parseMap(addResponse.getBody().asString());
		Integer id = (Integer) json.get("id");
		given()
				.contentType(ContentType.JSON)
				.when()
				.delete("/" + id)
				.then()
				.statusCode(204);
	}

	@Test
	void deleteUnknownTodo(){
		given()
				.contentType(ContentType.JSON)
				.when()
				.delete("/1231232")
				.then()
				.statusCode(404);
	}

	@Test
	void shouldGetAllTodos() {
		List<TodoEntity> customers = List.of(
				new TodoEntity(null, "Create a todo", null, false),
				new TodoEntity(null, "Profit", null, true)
		);
		todoRepository.saveAll(customers);

		given()
				.contentType(ContentType.JSON)
				.when()
				.get()
				.then()
				.statusCode(200)
				.body(".", hasSize(2));
	}
}
