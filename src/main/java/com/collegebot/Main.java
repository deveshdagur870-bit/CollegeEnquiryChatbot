package com.collegebot;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * Entry point of the desktop application.
 * Opens a JavaFX window containing a WebView that loads our HTML/CSS/JS chat UI.
 * A JavaBridge object is exposed to JavaScript so the front-end (HTML/JS) can
 * call back into Java (which talks to the SQL database) — this is the "MCP-style"
 * connection between the chat UI and structured data.
 */
public class Main extends Application {

    private ChatEngine chatEngine;

    @Override
    public void start(Stage stage) {
        DatabaseManager.initialize();
        chatEngine = new ChatEngine();

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Once the HTML page has finished loading, inject the Java bridge into
        // the page's JavaScript "window" object.
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaBridge", new JavaBridge(chatEngine));
            }
        });

        webEngine.load(getClass().getResource("/web/chat.html").toExternalForm());

        Scene scene = new Scene(webView, 430, 650);
        stage.setTitle("AI-Based College Enquiry Chatbot");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
