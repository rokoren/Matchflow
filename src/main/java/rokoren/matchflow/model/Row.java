/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package rokoren.matchflow.model;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author Rok Koren
 */
public record Row(
    String matchId,
    int marketId,
    String outcomeId,
    String specifiers
) 
{
    public static Row fromLine(String line) {
        try {
            String[] parts = line.split("\\|", -1); // -1 ohrani prazne zadnje stolpce

            if (parts.length < 3) {
                System.err.println("⚠️ Premalo stolpcev: " + line);
                return null;
            }

            String matchId = unquote(parts[0].trim());         // 'sr:match:13762991'
            int marketId = Integer.parseInt(parts[1].trim());   // 60
            String outcomeId = unquote(parts[2].trim());        // '2'
            String specifiers = parts.length > 3 ? unquote(parts[3].trim()) : ""; // prazno ali string

            return new Row(matchId, marketId, outcomeId, specifiers);

        } catch (Exception e) {
            System.err.println("⚠️ Napaka pri parsiranju: " + line + " => " + e.getMessage());
            return null;
        }
    }

    private static String unquote(String input) {
        if (input.startsWith("'") && input.endsWith("'")) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("matchId", matchId)
                .put("marketId", marketId)
                .put("outcomeId", outcomeId)
                .put("specifiers", specifiers);
    }

    public static Row fromJson(JsonObject json) {
        return new Row(
            json.getString("matchId"),
            json.getInteger("marketId"),
            json.getString("outcomeId"),
            json.getString("specifiers")
        );
    }
}
