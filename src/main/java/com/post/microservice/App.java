package com.post.microservice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.microservice.requests.CreateCommentRequestDTO;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class App {
	private static final String EXCHANGE_NAME = "comment_worker";
	private static final String[] topics = { "post.comment.create" };
	private static final List<String> BINDING_KEYS = new ArrayList<>(Arrays.asList(topics));
	private static final String CREATE_COMMENT_URL = "http://topic-backend:4000/comments";

	public static void main(String[] args) throws Exception {
		try {
			var factory = new ConnectionFactory();
			factory.setUri("amqp://guest:guest@rabbitMQ:5672/%2F");
			var connection = factory.newConnection();
			var channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			var queueName = channel.queueDeclare().getQueue();

			for (String key : BINDING_KEYS) {
				channel.queueBind(queueName, EXCHANGE_NAME, key);
			}

			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {

				try {
					var body = delivery.getBody();
					var mapper = new ObjectMapper();
					var message = mapper.readValue(body, new TypeReference<Map<String, String>>() {
					});
					System.out.println(" [x] Success Received Message =" + message + "'");

					var dto = new CreateCommentRequestDTO();
					dto.topicId = message.get("topicId");
					dto.description = message.get("description");
					dto.authorId = message.get("authorId");
					createComment(dto);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean createComment(CreateCommentRequestDTO dto) throws Exception {
		var body = new HashMap<String, String>() {
			private static final long serialVersionUID = -3474031491790826608L;
			{
				put("topicId", dto.topicId);
				put("description", dto.description);
				put("authorId", dto.authorId);
			}
		};

		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper
				.writeValueAsString(body);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(CREATE_COMMENT_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		System.out.println(response.body());
		return true;
	}
}
