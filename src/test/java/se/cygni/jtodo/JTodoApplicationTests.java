package se.cygni.jtodo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import se.cygni.jtodo.domain.TodoEntity;
import se.cygni.jtodo.domain.TodoRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JTodoApplicationTests {

	@LocalServerPort
	private Integer port;

	private HttpClient http;
	private String baseUrl;
	private final JsonParser parser = JsonParserFactory.getJsonParser();

	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

	static {
		postgres.start();
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
		http = HttpClient.newHttpClient();
		baseUrl = "http://localhost:" + port + "/api/todos";
		todoRepository.deleteAll();
	}

	private HttpResponse<String> post(String path, String body) throws Exception {
		return http.send(
				HttpRequest.newBuilder()
						.uri(URI.create(baseUrl + path))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(body))
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> put(String path, String body) throws Exception {
		return http.send(
				HttpRequest.newBuilder()
						.uri(URI.create(baseUrl + path))
						.header("Content-Type", "application/json")
						.PUT(HttpRequest.BodyPublishers.ofString(body))
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> delete(String path) throws Exception {
		return http.send(
				HttpRequest.newBuilder()
						.uri(URI.create(baseUrl + path))
						.DELETE()
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> get() throws Exception {
		return http.send(
				HttpRequest.newBuilder()
						.uri(URI.create(baseUrl))
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	@Test
	void addTodos() throws Exception {
		var response = post("", """
				{"task": "test task", "done": false}""");
		assertThat(response.statusCode()).isEqualTo(200);
		var body = parser.parseMap(response.body());
		assertThat(body.get("task")).isEqualTo("test task");
		assertThat(body.get("done")).isEqualTo(false);
	}

	@Test
	void modifyTodo() throws Exception {
		var addResponse = post("", """
				{"task": "to be changed"}""");
		Integer id = (Integer) parser.parseMap(addResponse.body()).get("id");

		var response = put("/" + id, """
				{"task": "Updated task"}""");
		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(parser.parseMap(response.body()).get("task")).isEqualTo("Updated task");
	}

	@Test
	void modifyUnknownTodo() throws Exception {
		var response = put("/1231232", """
				{"task": "Updated task"}""");
		assertThat(response.statusCode()).isEqualTo(404);
	}

	@Test
	void deleteTodo() throws Exception {
		var addResponse = post("", """
				{"task": "to be changed"}""");
		Integer id = (Integer) parser.parseMap(addResponse.body()).get("id");

		var response = delete("/" + id);
		assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	void deleteUnknownTodo() throws Exception {
		var response = delete("/1231232");
		assertThat(response.statusCode()).isEqualTo(404);
	}

	@Test
	void shouldGetAllTodos() throws Exception {
		todoRepository.saveAll(List.of(
				new TodoEntity(null, "Create a todo", null, false),
				new TodoEntity(null, "Profit", null, true)
		));

		var response = get();
		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(parser.parseList(response.body())).hasSize(2);
	}
}
