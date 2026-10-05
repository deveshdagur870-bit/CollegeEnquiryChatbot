package com.collegebot;

/**
 * This object is injected into the HTML page's JavaScript context as
 * "window.javaBridge". The chat.js file calls javaBridge.getResponse(message)
 * directly, which runs synchronously and returns the bot's reply as a String.
 */
public class JavaBridge {

    private final ChatEngine chatEngine;

    public JavaBridge(ChatEngine chatEngine) {
        this.chatEngine = chatEngine;
    }

    // Called from JavaScript: window.javaBridge.getResponse("some question")
    public String getResponse(String message) {
        try {
            return chatEngine.processMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, kuch technical issue aa gaya. Please dobara try karein.";
        }
    }
}
