package com.collegebot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The "brain" of the chatbot.
 *
 * - Loads all FAQ intents from SQL into memory once (acts like a small knowledge base).
 * - For every user message, scores each intent by keyword overlap and returns the
 *   best-matching grounded answer (this is the RAG-style "retrieve, then answer" idea,
 *   done with simple keyword matching instead of embeddings).
 * - If the message looks like a roll-number / status query, it instead does a direct
 *   structured SQL lookup on the students table (this plays the role of an MCP tool call:
 *   the bot reaches into structured data instead of just returning free text).
 * - Every exchange is logged along with a rough word-count "token estimate", as a nod
 *   to token-usage tracking.
 */
public class ChatEngine {

    private final List<Intent> intents = new ArrayList<>();

    public ChatEngine() {
        loadIntents();
    }

    private void loadIntents() {
        String sql = "SELECT keywords, answer FROM intents";
        try (Connection con = DatabaseManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                List<String> keywords = Arrays.asList(rs.getString("keywords").toLowerCase().split(","));
                intents.add(new Intent(keywords, rs.getString("answer")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String processMessage(String rawMessage) {
        String message = rawMessage.toLowerCase().trim();
        if (message.isEmpty()) {
            return "Kuch to poochiye! 🙂";
        }

        // --- Structured "tool call" path: roll-number based status lookup ---
        if (message.contains("roll") || message.contains("status")) {
            String rollNo = extractRollNumber(message);
            if (rollNo != null) {
                String info = lookupStudent(rollNo);
                logInteraction(rawMessage, info);
                return info;
            }
        }

        // --- Retrieval path: keyword-overlap match against grounded FAQ answers ---
        String[] words = message.split("\\W+");
        Set<String> userWords = new HashSet<>(Arrays.asList(words));

        Intent best = null;
        int bestScore = 0;
        for (Intent intent : intents) {
            int score = 0;
            for (String kw : intent.keywords) {
                if (userWords.contains(kw.trim())) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = intent;
            }
        }

        String response = (best != null && bestScore > 0)
                ? best.answer
                : "Maaf kijiye, mujhe iska exact jawab nahi pata. Aap college office se contact kar sakte hain, "
                  + "ya apna sawaal thoda alag tarike se poochiye (jaise: admission, fees, hostel, placement, library).";

        logInteraction(rawMessage, response);
        return response;
    }

    /** Picks out anything that looks like a roll number (letters + digits, 4-15 chars). */
    private String extractRollNumber(String message) {
        for (String token : message.split("\\W+")) {
            if (token.matches("[a-z0-9]{4,15}") && token.matches(".*\\d.*")) {
                return token.toUpperCase();
            }
        }
        return null;
    }

    private String lookupStudent(String rollNo) {
        String sql = "SELECT name, course, year, fee_status FROM students WHERE roll_no = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                            "Roll No %s: %s (%s, Year %d) — Fee Status: %s",
                            rollNo, rs.getString("name"), rs.getString("course"),
                            rs.getInt("year"), rs.getString("fee_status")
                    );
                } else {
                    return "Roll number " + rollNo + " ke koi records nahi mile. Please check karke dobara try kijiye.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: student record fetch nahi ho paaya.";
        }
    }

    private void logInteraction(String userMsg, String response) {
        String sql = "INSERT INTO chat_logs(user_message, bot_response, token_estimate) VALUES (?, ?, ?)";
        int tokenEstimate = userMsg.split("\\s+").length + response.split("\\s+").length;
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userMsg);
            ps.setString(2, response);
            ps.setInt(3, tokenEstimate);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class Intent {
        final List<String> keywords;
        final String answer;

        Intent(List<String> keywords, String answer) {
            this.keywords = keywords;
            this.answer = answer;
        }
    }
}
