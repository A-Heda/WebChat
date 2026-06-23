const API = "http://localhost:8080";

const params = new URLSearchParams(window.location.search);
const chatId = params.get("id");

/* STATE */
let currentChat = null;
let msgTimes = [];

/* ELEMENTS */
const chatName = document.getElementById("chat-name");
const chatSubtitle = document.getElementById("chat-subtitle");
const menu = document.getElementById("chat-menu");
const messagesContainer = document.getElementById("messages-container");
const input = document.getElementById("message-input");
const avatar = document.getElementById("chat-avatar");

/* INIT */
async function initChat() {
    if (!chatId) return;

    const res = await fetch(`${API}/chats/${chatId}`);
    currentChat = await res.json();

    renderHeader();
    loadMessages();
}

function renderHeader() {

    if (currentChat.type === "private") {

        chatName.textContent = currentChat.fullName;
        chatSubtitle.textContent = `@${currentChat.username} • last seen recently`;
        avatar.src = currentChat.avatar || "../assets/default.png";

        menu.innerHTML = `
            <button>Block User</button>
            <button>Add To Contacts</button>
            <button>Archive</button>
        `;

    } else {

        chatName.textContent = currentChat.groupName;
        chatSubtitle.textContent = `${currentChat.membersCount} members`;
        avatar.src = currentChat.groupAvatar || "../assets/default.png";

        menu.innerHTML = `
            <button>Leave Group</button>
            <button>Add Member</button>
            <button>Edit Group</button>
            <button>History</button>
            <button>Archive</button>
        `;
    }
}

/* MENU */
document.getElementById("menu-button").onclick = () => {
    menu.classList.toggle("hidden");
};

/* BACK */
document.getElementById("back-btn").onclick = () => {
    window.location.href = "../home/home.html";
};

/* HOME */
document.getElementById("home-btn").onclick = () => {
    window.location.href = "../home/home.html";
};

/* INFO PAGE */
document.getElementById("open-info").onclick = () => {

    if (!currentChat) return;

    if (currentChat.type === "private") {
        window.location.href = `user-info.html?username=${currentChat.username}&id=${chatId}`;
    } else {
        window.location.href = `group-info.html?groupId=${chatId}`;
    }
};

/* LOAD MESSAGES */
async function loadMessages() {

    const res = await fetch(`${API}/chats/messages?chatId=${chatId}`);
    const data = await res.json();

    messagesContainer.innerHTML = "";
    data.forEach(renderMessage);
}

/* RENDER MESSAGE */
function renderMessage(msg) {

    const div = document.createElement("div");

    div.classList.add("message");
    div.classList.add(msg.senderId === "me" ? "sent" : "received");

    div.textContent = msg.text || "";

    messagesContainer.appendChild(div);
}

/* SPAM CONTROL (5 msg / 1 sec) */
function canSend() {
    const now = Date.now();

    msgTimes = msgTimes.filter(t => now - t < 1000);

    return msgTimes.length < 5;
}

/* SEND MESSAGE */
async function sendMessage() {

    const text = input.value.trim();
    if (!text) return;

    if (!canSend()) {
        alert("Too many messages (spam detected)");
        return;
    }

    msgTimes.push(Date.now());

    await fetch(`${API}/chats/send`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            chatId,
            text
        })
    });

    input.value = "";
    loadMessages();
}

/* SEARCH MESSAGES */
document.getElementById("search-box").addEventListener("input", e => {

    const value = e.target.value.toLowerCase();

    document.querySelectorAll(".message").forEach(m => {
        m.style.display =
            m.textContent.toLowerCase().includes(value)
                ? "block"
                : "none";
    });
});

/* EVENTS */
document.getElementById("send-button").onclick = sendMessage;

input.addEventListener("keypress", e => {
    if (e.key === "Enter") sendMessage();
});

/* START */
initChat();