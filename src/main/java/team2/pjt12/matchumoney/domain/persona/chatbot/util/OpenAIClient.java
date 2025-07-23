package team2.pjt12.matchumoney.domain.persona.chatbot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@PropertySource("classpath:/openAI.properties")
public class OpenAIClient {

    @Value("${openai.api.key}") // 🔥 application.properties에서 불러옴
    private String apiKey;

    public String callChatGPT(String userMessage) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode requestJson = mapper.createObjectNode();
            requestJson.put("model", "gpt-3.5-turbo");

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            requestJson.set("messages", messages);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("🔎 OpenAI raw response: " + responseBody);

            JsonNode json = mapper.readTree(responseBody);
            JsonNode choices = json.get("choices");

            if (choices != null && choices.isArray() && choices.size() > 0) {
                return choices.get(0).get("message").get("content").asText();
            } else {
                System.err.println("❌ Unexpected response structure: " + responseBody);
                return "GPT 응답 오류: " + responseBody;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "GPT 호출 중 오류: " + e.getMessage();
        }
    }
}
