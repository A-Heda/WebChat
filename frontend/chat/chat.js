const API = "http://localhost:8080";

/* CHAT STATE */
let currentChat = {
    type: "private",
    chatId: "1",

    username: "ali123",
    fullName: "Ali",

    groupName: "OOP Project",
    membersCount: 8
};

/* ELEMENTS */
const chatName = document.getElementById("chat-name");
const chatSubtitle = document.getElementById("chat-subtitle");
const menu = document.getElementById("chat-menu");
const messagesContainer = document.getElementById("messages-container");
const input = document.getElementById("message-input");

/* HEADER SETUP */
if (currentChat.type === "private") {

    chatName.textContent = currentChat.fullName;
    chatSubtitle.textContent = "@" + currentChat.username;

    menu.innerHTML = `
        <button>Block User</button>
        <button>Add To Contacts</button>
        <button>Archive</button>
    `;

} else {

    chatName.textContent = currentChat.groupName;
    chatSubtitle.textContent = currentChat.membersCount + " members";

    menu.innerHTML = `
        <button>Leave Group</button>
        <button>Add Member</button>
        <button>Edit Group</button>
        <button>History</button>
        <button>Archive</button>
    `;
}

/* MENU TOGGLE */
document.getElementById("menu-button").onclick = () => {
    menu.classList.toggle("hidden");
};

/* BACK */
document.getElementById("back-btn").onclick = () => {
    window.history.back();
};

/* HOME */
document.getElementById("home-btn").onclick = () => {
    window.location.href = "home.html";
};

/* INFO PAGE */
document.getElementById("open-info").onclick = () => {

    if (currentChat.type === "private") {
        window.location.href = `user-info.html?username=${currentChat.username}&id=${currentChat.chatId}`;
    } else {
        window.location.href = `group-info.html?groupId=${currentChat.chatId}`;
    }
};

/* LOAD MESSAGES */
async function loadMessages() {

    const res = await fetch(`${API}/chats/messages?chatId=${currentChat.chatId}`);
    const data = await res.json();

    messagesContainer.innerHTML = "";

    data.forEach(m => renderMessage(m));
}

/* RENDER */
function renderMessage(msg) {

    const div = document.createElement("div");

    div.classList.add("message");
    div.classList.add(msg.senderId === "me" ? "sent" : "received");

    div.textContent = msg.text || "";

    messagesContainer.appendChild(div);
}

/* SPAM CONTROL */
let msgTimes = [];

function canSend() {
    const now = Date.now();
    msgTimes = msgTimes.filter(t => now - t < 5000);
    return msgTimes.length < 5;
}

/* SEND */
async function sendMessage() {

    const text = input.value.trim();
    if (!text) return;

    if (!canSend()) {
        alert("Too many messages");
        return;
    }

    msgTimes.push(Date.now());

    await fetch(`${API}/chats/send`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            chatId: currentChat.chatId,
            senderId: "me",
            text
        })
    });

    input.value = "";
    loadMessages();
}

/* EVENTS */
document.getElementById("send-button").onclick = sendMessage;

input.addEventListener("keypress", e => {
    if (e.key === "Enter") sendMessage();
});

/* SEARCH */
document.getElementById("search-box").addEventListener("input", e => {

    const value = e.target.value.toLowerCase();

    document.querySelectorAll(".message").forEach(m => {
        m.style.display =
            m.textContent.toLowerCase().includes(value)
                ? "block"
                : "none";
    });
});

/* INIT */
loadMessages();