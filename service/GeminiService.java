package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import util.EnvLoader;
java -cp "lib\mysql-connector-j-26.7.0.jar;out" main.MainApplicationjava -cp "lib\mysql-connector-j-26.7.0.jar;out" main.MainApplication
public class GeminiService {

    private static final String API_KEY =
            EnvLoader.get("ROUTEWISE_GEMINI_API_KEY");

    private static final String MODEL = "gemini-2.5-flash";

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL
                    + ":generateContent?key="
                    + API_KEY;

    private final HttpClient httpClient;

    public GeminiService() {
        httpClient = HttpClient.newHttpClient();
    }

    public String askGemini(String userQuestion) {
        return askGemini(userQuestion, null);
    }

    public String askGemini(
            String userQuestion,
            String routeWiseContext) {

        if (API_KEY == null || API_KEY.isBlank()) {
            return "Gemini API key is not configured.";
        }

        if (userQuestion == null || userQuestion.isBlank()) {
            return "Please enter a question.";
        }

        String prompt =
                "You are RouteWise AI Travel Assistant.\n\n"

                + "Your role is to help users with general travel "
                + "information, destination information, basic trip "
                + "planning, and general bus travel safety advice.\n\n"

                + "You may also receive CURRENT ROUTEWISE DATA "
                + "from the RouteWise application.\n\n"

                + "IMPORTANT RULES:\n"

                + "1. When RouteWise data is provided, treat it as "
                + "the source of truth for RouteWise-specific "
                + "information such as buses, fares, schedules, "
                + "and available seats.\n"

                + "2. Do not invent RouteWise-specific information. "
                + "If the required RouteWise information is not "
                + "provided, clearly say that it is not available "
                + "in the current RouteWise context.\n"

                + "3. You may use general travel knowledge for "
                + "destination information, sightseeing, planning, "
                + "and general safety advice.\n"

                + "4. Clearly distinguish RouteWise-specific "
                + "information from general travel information "
                + "when both are used.\n"

                + "5. Do not perform bookings, cancellations, "
                + "payments, or seat locking through the AI assistant.\n\n"

                + "Keep answers practical, clear, concise, and easy "
                + "to understand.\n\n";

        if (routeWiseContext != null
                && !routeWiseContext.isBlank()) {

            prompt +=
                    "### CURRENT ROUTEWISE DATA ###\n"
                    + routeWiseContext
                    + "\n### END ROUTEWISE DATA ###\n\n";
        }

        prompt +=
                "### USER QUESTION ###\n"
                + userQuestion
                + "\n### END USER QUESTION ###\n\n"

                + "Answer the user's question using the rules above.";

        String jsonBody =
                "{"
                        + "\"contents\":["
                        + "{"
                        + "\"parts\":["
                        + "{"
                        + "\"text\":\""
                        + escapeJson(prompt)
                        + "\""
                        + "}"
                        + "]"
                        + "}"
                        + "]"
                        + "}";

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                return "Gemini request failed. HTTP status: "
                        + response.statusCode();
            }

            return extractText(response.body());

        } catch (IOException e) {

            e.printStackTrace();
            return "Unable to connect to Gemini.";

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            return "Gemini request was interrupted.";
        }
    }

    private String escapeJson(String input) {

        if (input == null) {
            return "";
        }

        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractText(String json) {

        String marker = "\"text\"";

        int textPosition = json.indexOf(marker);

        if (textPosition == -1) {
            return "Gemini returned an unexpected response.";
        }

        int colonPosition = json.indexOf(":", textPosition);

        if (colonPosition == -1) {
            return "Could not read Gemini response.";
        }

        int start = colonPosition + 1;

        while (start < json.length()
                && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()
                || json.charAt(start) != '"') {
            return "Could not read Gemini response.";
        }

        start++;

        StringBuilder result = new StringBuilder();

        boolean escaped = false;

        for (int i = start; i < json.length(); i++) {

            char current = json.charAt(i);

            if (escaped) {

                switch (current) {

                    case 'n':
                        result.append('\n');
                        break;

                    case 'r':
                        result.append('\r');
                        break;

                    case 't':
                        result.append('\t');
                        break;

                    case '"':
                        result.append('"');
                        break;

                    case '\\':
                        result.append('\\');
                        break;

                    default:
                        result.append(current);
                }

                escaped = false;

            } else if (current == '\\') {

                escaped = true;

            } else if (current == '"') {

                break;

            } else {

                result.append(current);
            }
        }

        return result.toString();
    }
}