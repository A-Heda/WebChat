const API = "http://localhost:8080";

let lastMessageCount = 0;

const params =
    new URLSearchParams(window.location.search);

const chatId =
    params.get("id");

const chatType =
    params.get("type");

const currentUserId =
    localStorage.getItem("userId");

/* Elements */
const openInfo = document.getElementById("open-info");

let otherUserId = null;
let otherUsername = null;

const chatName = document.getElementById("chat-name");

const chatSubtitle = document.getElementById("chat-subtitle");

const messagesContainer = document.getElementById("messages-container");

const messageInput = document.getElementById("message-input");

const sendButton = document.getElementById("send-button");

const backButton = document.getElementById("back-btn");

const homeButton = document.getElementById("home-btn");

/* Page Load */

window.onload = async function () {

    if (!chatId) {

        alert("Chat not found");

        window.location.href =
            "../home/home.html";

        return;
    }

    if(chatType === "GROUP"){

    await loadGroupInfo();

    }else{

    await loadChatInfo();
    }

    await loadMessages();

    setInterval(loadMessages, 3000);
};

/* Load PrivateChat Header */

async function loadChatInfo() {

    try {

        const response =
            await fetch(
                API +
                "/chats/info?chatId=" +
                chatId +
                "&userId=" +
                currentUserId
            );

        const chat =
            await response.json();

        if (response.ok) {
            otherUserId =
                chat.otherUserId;

            otherUsername =
                chat.otherUsername;

            chatName.textContent =
                chat.otherUsername;

            chatSubtitle.textContent =
                "User ID: " + chat.otherUserId;
        } else {

            alert(chat);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot load chat info.");
    }
}

/* Load GroupChat Header */
async function loadGroupInfo() {

    try {

        const groupId =
            chatId.replace("group_", "");

        const response =
            await fetch(
                API +
                "/groups/info?groupId=" +
                groupId
            );

        const group =
            await response.json();

        if (response.ok) {

            otherUserId =
                group.otherUserId;

            otherUsername =
                group.otherUsername;

            chatName.textContent =
                group.otherUsername;

            chatSubtitle.textContent =
                "Group ID: " +
                group.otherUserId;

            document
                .getElementById("chat-avatar")
                .src =
                group.imagePath ||
                "../assets/group.png";

        } else {

            alert(group);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot load group info.");
    }
}

/* Load Messages */

async function loadMessages() {

    try {

        const response =
            await fetch(
                API +
                "/chats/messages?chatId=" +
                chatId
            );

        const messages =
            await response.json();

        if (response.ok) {

            if (messages.length !== lastMessageCount) {

            lastMessageCount = messages.length;
        
            renderMessages(messages);
            }

        } else {

            alert(messages);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot load messages.");
    }
}

/* Render Messages */

function renderMessages(messages) {

    messagesContainer.innerHTML = "";

    messages.forEach(message => {

        const div =
            document.createElement("div");

        if (
            message.senderId ===
            currentUserId
        ) {

            div.className =
                "message sent";

        } else {

            div.className =
                "message received";
        }

        div.innerHTML =
            `
            <div>
                ${message.text}
            </div>
            `;

        messagesContainer
            .appendChild(div);
    });

    messagesContainer.scrollTop =
        messagesContainer.scrollHeight;
}

/* Send Message */

async function sendMessage() {

    const text =
        messageInput.value.trim();

    if (!text)
        return;

    try {

        const response =
            await fetch(
                API + "/chats/send",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        senderId:
                            currentUserId,

                        chatId:
                            chatId,

                        text:
                            text
                    })
                }
            );

        const result =
            await response.json();

        if (response.ok) {

            messageInput.value = "";

            await loadMessages();

        } else {

            alert(result);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot send message.");
    }
}

/* Buttons */

sendButton.onclick =
    sendMessage;

messageInput.addEventListener(
    "keypress",
    function (event) {

        if (event.key === "Enter") {

            sendMessage();
        }
    }
);

backButton.onclick =
    function () {

        window.location.href =
            "../home/home.html";
    };

homeButton.onclick =
    function () {

        window.location.href =
            "../home/home.html";
    };

openInfo.onclick = function () {

    if (!otherUserId)
        return;

    if(chatType==="PRIVATE"){

    window.location.href =
    "../user-info/user-info.html?id="+
    otherUserId;

}else{

    window.location.href =
    "../group-info/group-info.html?id="+
    otherUserId;
}
};