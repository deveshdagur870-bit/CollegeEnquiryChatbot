function sendMessage() {
    const input = document.getElementById('userInput');
    const text = input.value.trim();
    if (!text) return;

    appendMessage(text, 'user-message');
    input.value = '';

    let response;
    try {
        // javaBridge is injected by Main.java after the page loads.
        response = window.javaBridge.getResponse(text);
    } catch (e) {
        response = "Bridge se connect nahi ho paaya. Ye page sirf Java desktop app ke andar se hi kaam karega.";
    }

    appendMessage(response, 'bot-message');
}

function appendMessage(text, className) {
    const messages = document.getElementById('messages');
    const div = document.createElement('div');
    div.className = className;
    div.textContent = text;
    messages.appendChild(div);
    messages.scrollTop = messages.scrollHeight;
}
