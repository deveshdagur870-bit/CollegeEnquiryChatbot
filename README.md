# AI-Based College Enquiry Chatbot (Java + HTML/CSS/JS + SQL)

A desktop application built with **JavaFX** (Java) that embeds an **HTML/CSS/JS**
chat interface inside a native window, backed by a **SQLite (SQL)** database.

## Tech stack
- **Java (JavaFX)** – desktop shell + WebView + JS↔Java bridge
- **HTML/CSS/JS** – the actual chat UI, rendered inside the JavaFX WebView
- **SQL (SQLite)** – stores FAQ "knowledge base", student records, and chat logs

## How it maps to the resume bullets
| Resume term | What it actually is in this project |
|---|---|
| **LLM** | `ChatEngine.java` – keyword-based intent matching engine (a lightweight stand-in for an LLM) |
| **RAG** | The bot never invents answers — it retrieves the best-matching answer from the `intents` table in SQL, then returns it ("retrieve, then respond", grounded in your own data) |
| **MCP** | When a user mentions a roll number / status, the bot makes a **structured SQL lookup** on the `students` table instead of free text — like a tool call to a data source |
| **Infra** | Lightweight, single-file SQLite DB + a Maven project — deploys as one runnable app, no server setup |
| **Token** | Every exchange is logged in `chat_logs` with a rough word-count `token_estimate`, as a simple analogue of token usage tracking |

## Project structure
```
CollegeEnquiryChatbot/
├── pom.xml
├── src/main/java/com/collegebot/
│   ├── Main.java            (JavaFX window + WebView + JS bridge setup)
│   ├── JavaBridge.java       (object exposed to JavaScript)
│   ├── ChatEngine.java       (matching logic + SQL lookups)
│   └── DatabaseManager.java  (SQLite connection + schema setup)
└── src/main/resources/
    ├── web/
    │   ├── chat.html
    │   ├── style.css
    │   └── script.js
    └── db/
        └── schema.sql        (tables + sample FAQ & student data)
```

## Prerequisites
- **JDK 17** or later
- **Maven** (or import as a Maven project in IntelliJ IDEA / Eclipse / NetBeans)
- Internet connection the *first* time you build (Maven downloads JavaFX + SQLite JDBC)

## How to run

### Option A: Command line (Maven)
```bash
cd CollegeEnquiryChatbot
mvn clean javafx:run
```

### Option B: IntelliJ IDEA
1. Open the folder as a Maven project (`File > Open`, select the folder with `pom.xml`).
2. Let Maven download dependencies.
3. Right-click `Main.java` → **Run**.
   (If JavaFX complains about missing runtime, run via `mvn javafx:run` in the built-in terminal instead — the Maven plugin handles the JavaFX module path for you.)

### Option C: NetBeans
1. `File > Open Project`, select the folder.
2. Right-click the project → **Run** (it will use the javafx-maven-plugin automatically).

The first run creates `college_chatbot.db` in the project folder and seeds it
with sample FAQs (admission, fees, courses, hostel, placements, library, exams,
contact, scholarships) and 4 sample students (`CS101`, `CS102`, `ME201`, `MBA301`).

## Try it out
- "How to apply for admission?"
- "What is the fee structure?"
- "Is hostel available?"
- "Tell me my status for CS101"

## Extending it
- **Add more FAQs**: insert new rows into the `intents` table in `schema.sql`
  (or directly into `college_chatbot.db` with any SQLite browser) — `keywords`
  is a comma-separated list of trigger words.
- **Add more students**: insert rows into the `students` table the same way.
- **Improve the "LLM" part later**: `ChatEngine.processMessage()` is the single
  place to swap in a real API call (e.g. to an LLM provider) if you ever want
  to move from offline keyword matching to true generative answers — the rest
  of the app (UI, bridge, database) won't need to change.
- **Package as a standalone .exe/.jar**: once it runs correctly, `jpackage`
  (bundled with JDK 17+) can turn this into a native installer if your
  submission needs a double-clickable app.
