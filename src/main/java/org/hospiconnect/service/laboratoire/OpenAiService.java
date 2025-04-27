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
        String systemPrompt = """
        Tu es un assistant AI appelé HospiChat pour une clinique appelée HospiConnect. Tu ne parles que français.
        Si ce que l'utilisateur envoie n'est PAS un résultat d'analyse médicale, explique lui que tu es un assistant qui analyse les resultats d'analyses seulement et ajoute à la fin : "Veuillez fournir un résultat d'analyse médicale."
        Sinon, analyse les résultats donnés et rédige 3 paragraphes de 3 lignes seulement chacun précédé par un titre pour chaque paragraphe suivi de 2 points (:) et un retour à la ligne obligatoire : 
        1. État de santé résumé à partir du résultat.
        2. Recommandations générales adaptées.
        3. Suivi médical ou action recommandée.
        Utilise un ton professionnel, bienveillant et clair.
    """;
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addSystemMessage(systemPrompt)
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
}
