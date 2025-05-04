package org.hospiconnect.service;
import io.github.cdimascio.dotenv.Dotenv;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class OpenAiService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String OPENAI_API_KEY = dotenv.get("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");

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
        try {
            // Ajout d'un contexte pour des réponses plus pertinentes
            String systemMessage = "Vous êtes HospiChat, un assistant médical intelligent. " +
                    "Répondez de manière claire, concise et professionnelle. " +
                    "Pour les questions médicales, demandez toujours des précisions supplémentaires. " +
                    "En cas d'urgence médicale, recommandez immédiatement de contacter les services d'urgence.";

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addSystemMessage(systemMessage)
                    .addUserMessage(question)
                    .model(ChatModel.GPT_3_5_TURBO) // Utilisez un modèle plus puissant
                    .temperature(0.7) // Contrôle de la créativité
                    .maxTokens(500) // Limite de réponse
                    .build();

            ChatCompletion chatCompletion = client.chat().completions().create(params);

            return chatCompletion.choices()
                    .getFirst()
                    .message()
                    .content()
                    .orElse("Désolé, je n'ai pas pu générer de réponse. Veuillez reformuler votre question.");

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec le service OpenAI: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        // Test avec une question médicale typique
        String response = instance.askOpenAi("Que faire en cas de forte fièvre chez un adulte ?");
        System.out.println("Réponse du chatbot: " + response);
    }
}
