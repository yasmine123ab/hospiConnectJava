package org.hospiconnect.service.laboratoire;
import io.github.cdimascio.dotenv.Dotenv;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenAiService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");

    private static final OpenAiService instance = new OpenAiService();

    private final OpenAIClient client;

    private OpenAiService() {
        client = OpenAIOkHttpClient.builder()
                .fromEnv()
                .apiKey(OPENAI_API_KEY)
                .build();
    }

    public static OpenAiService getInstance() {
        return instance;
    }

    public String askOpenAi(String question) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(question)
                .model(ChatModel.O3_MINI)
                .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);
        return chatCompletion.choices()
                .getFirst()
                .message()
                .content()
                .orElseThrow();
    }

    public static void main(String[] args) {
        System.out.println(instance.askOpenAi("Hello how are you?"));
    }

}
