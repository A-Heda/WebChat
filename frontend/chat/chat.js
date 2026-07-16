const API = "http://localhost:8080";

//let lastChatMessageCount = 0;          we will compare the value not the count to render again if there was new msgs
let lastMessagesSnapshot = "";
//let lastSavedMessageCount = 0;               هم چنین
let lastSavedSnapshot = "";

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

const mediaInput = document.getElementById("media-input");

const mediaButton = document.getElementById("media-btn");

mediaButton.onclick = function () {

    mediaInput.click();

};


document.getElementById("media-input").addEventListener("change", async function () {

    const file = this.files[0];

    if (!file)
        return;

    const reader = new FileReader();

    reader.onload = async function () {

        const base64 =
            reader.result.split(",")[1];

        await uploadMedia(
            file.name,
            file.type,
            base64
        );

    };

    reader.readAsDataURL(file);

});

async function uploadMedia(fileName, type, data){

    const response =
        await fetch(API + "/media/upload",{

            method:"POST",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify({

                fileName:fileName,

                contentType:type,

                data:data

            })

        });

    const result =
        await response.json();

    if(response.ok){

        mediaInput.value = "";

        await sendMedia(result.url);

    }

    else{

        alert(result);

    }

}

async function sendMedia(mediaUrl){

    let endpoint;

    let body;

    if(chatType === "SAVED"){

        endpoint = API + "/chats/saved/send";

        body = {
            userId: currentUserId,
            text: "",
            mediaUrl: mediaUrl
        };

    }else{

        endpoint = API + "/chats/media";

        body = {
            senderId: currentUserId,
            chatId: chatId,
            mediaUrl: mediaUrl
        };

    }

    const response = await fetch(endpoint,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify(body)
    });

    if(response.ok){

        if(chatType==="SAVED")
            loadSavedMessages();
        else
            loadMessages();

    }else{

        alert(await response.json());

    }
}


/* Page Load */

window.onload = async function () {

    if (chatType === "SAVED") {

        chatName.textContent = "Saved Messages";

        chatSubtitle.textContent =
            "Only you can see these messages";

        const avatar = document.getElementById("chat-avatar");

        avatar.outerHTML = `
        <div id="chat-avatar" class="saved-avatar">
            <i class="fa-solid fa-bookmark"></i>
        </div>
    `;

        await loadSavedMessages();

        setInterval(loadSavedMessages, 3000);

        return;
    }

    if (!chatId) {

        alert("Chat not found");

        window.location.href =
            "../home/home.html";

        return;
    }

    if (chatType === "GROUP") {

        await loadGroupInfo();

    } else {

        await loadChatInfo();
    }

    await loadMessages();

    await fetch(API + "/chats/mark-read", {

        method: "PUT",

        headers: {
            "Content-Type":"application/json"
        },

        body: JSON.stringify({

            userId: currentUserId,

            chatId: chatId

        })

    });

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
            otherUserId = chat.otherUserId;
            otherUsername = chat.otherUsername;

            chatName.textContent = chat.otherUsername;
            chatSubtitle.textContent = "User ID: " + chat.otherUserId;

            document.getElementById("chat-avatar").src =
                chat.imagePath && chat.imagePath.trim() !== ""
                ? chat.imagePath
                : "../assets/default-avatar.png";

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
                "/groups/id?groupId=" +
                groupId
            );

        const group =
            await response.json();

        if (response.ok) {

            // برای صفحه Group Info
            otherUserId = group.id;

            chatName.textContent =
                group.name;

            chatSubtitle.textContent =
                "Group ID: " + group.id;

            document
                .getElementById("chat-avatar")
                .src =
                group.groupImagePath &&
                    group.groupImagePath.trim() !== ""
                    ? group.groupImagePath
                    : "../assets/group.png";

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

            const snapshot = JSON.stringify(messages);

            if (snapshot !== lastMessagesSnapshot) {

                lastMessagesSnapshot = snapshot;

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

async function loadSavedMessages() {

    try {

        const response =
            await fetch(
                API +
                "/chats/saved?userId=" +
                currentUserId
            );

        const messages =
            await response.json();

        if (response.ok) {

            const snapshot = JSON.stringify(messages);

            if (snapshot !== lastSavedSnapshot) {

                lastSavedSnapshot = snapshot;
                renderMessages(messages);

            }

        } else {

            alert(messages);

        }

    } catch (error) {

        console.error(error);

    }

}

/* Render Messages */

function renderMessages(messages) {

    messagesContainer.innerHTML = "";

    messages.forEach(message => {

        const div = document.createElement("div");

        if (message.senderId === currentUserId) {

            div.className = "message sent";

        } else {

            div.className = "message received";
        }

        let html = "";

        if (message.deleted) {

            html = `
                <div class="deleted-message">
                    This message was deleted.
                </div>
            `;

        } else {

            html = `
                <div class="message-body">

                    ${(chatType === "GROUP" || chatType === "SAVED") ? `
            <div class="sender-header">
                <img class="sender-avatar"
                    src="${message.senderImagePath || "../assets/default-avatar.png"}">
                    <span class="sender-name">
                        ${message.senderUsername || "Unknown"}
                    </span>
            </div>
        ` : ""
                }

            <div class="message-text">

                ${message.text || ""}

                ${message.edited ? "<span class='edited'>(edited)</span>" : ""}

            </div>

            ${renderMedia(message)}

                    <button
                        class="menu-btn"
                        onclick="toggleMenu(event,'${message.id}', ${message.senderId === currentUserId})">
                        ⋮
                    </button>

                    <div
                        class="message-menu hidden"
                        id="menu-${message.id}">
                    </div>

                </div>
            `;
        }

        div.innerHTML = html;

        messagesContainer.appendChild(div);

    });

    messagesContainer.scrollTop =
        messagesContainer.scrollHeight;
}


function renderMedia(message) {

    if (!message.mediaUrl)
        return "";

    const url = API + message.mediaUrl;

    if (message.mediaUrl.match(/\.(png|jpg|jpeg|gif|webp)$/i)) {

        return `
            <img
                class="chat-image"
                src="${url}">
        `;
    }

    if (message.mediaUrl.match(/\.(mp4|webm|mov)$/i)) {

        return `
            <video
                class="chat-video"
                controls>

                <source src="${url}">

            </video>
        `;
    }

    if (message.mediaUrl.match(/\.(mp3|wav|ogg)$/i)) {

        return `
            <audio controls>

                <source src="${url}">

            </audio>
        `;
    }

    return `
        <a href="${url}" target="_blank">

            📄 Download File

        </a>
    `;
}

/* Send Message */

async function sendMessage() {

    if (chatType === "SAVED") {

        return sendSavedMessage();

    }

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

async function sendSavedMessage() {

    const text =
        messageInput.value.trim();

    if (!text)
        return;

    const response =
        await fetch(API + "/chats/saved/send", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                userId: currentUserId,

                text: text

            })

        });

    const result =
        await response.json();

    if (response.ok) {

        messageInput.value = "";

        await loadSavedMessages();

    } else {

        alert(result);

    }

}

function toggleMenu(event, messageId, isMine) {

    event.stopPropagation();

    document
        .querySelectorAll(".message-menu")
        .forEach(menu => {

            menu.classList.add("hidden");

        });

    const menu =
        document.getElementById(
            "menu-" + messageId
        );


    if (isMine) {

        menu.innerHTML =

            `
        <div onclick="editMessage('${messageId}')">
            ✏️ Edit
        </div>

        <div onclick="deleteMessage('${messageId}')">
            🗑 Delete
        </div>

        <div onclick="saveMessage('${messageId}')">
            🔖 Save
        </div>

        <div onclick="reportMessage('${messageId}')">
            🚩 Report
        </div>
        `;

    } else {

        menu.innerHTML =

            `
        <div onclick="reportMessage('${messageId}')">
            🚩 Report
        </div>
        <div onclick="saveMessage('${messageId}')">
            🔖 Save
        </div>
        `;
    }

    menu.classList.toggle("hidden");
}

window.onclick = function () {

    document
        .querySelectorAll(".message-menu")
        .forEach(menu => {

            menu.classList.add("hidden");

        });

}

async function editMessage(messageId) {

    const newText =
        prompt("Edit message");

    if (newText == null)
        return;

    const response =
        await fetch(API + "/chats/edit", {

            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                chatId: chatId,

                messageId: messageId,

                editorId: currentUserId,

                newText: newText
            })

        });

    if (response.ok) {

        loadMessages();

    } else {

        alert(await response.json());

    }

}

async function saveMessage(messageId) {
    const response = await fetch(API + "/chats/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            userId: currentUserId,
            chatId: chatId,
            messageId: messageId
        })
    });

    const result = await response.json();

    if (response.ok) {
        alert("Saved");
    } else {
        alert(result);
    }
}

async function deleteMessage(messageId) {

    if (!confirm("Delete message?"))
        return;

    const response =
        await fetch(API + "/chats/delete", {

            method: "DELETE",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                chatId: chatId,

                messageId: messageId,

                requesterId: currentUserId

            })

        });

    if (response.ok) {

        loadMessages();

    } else {

        alert(await response.json());

    }

}

async function reportMessage(messageId) {

    const response =
        await fetch(API + "/chats/report", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                chatId: chatId,

                messageId: messageId,

                reporterId: currentUserId

            })

        });

    if (response.ok) {

        alert("Reported.");

    } else {

        alert(await response.json());

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

    if (chatType === "SAVED")
        return;

    if (!otherUserId)
        return;

    if (chatType === "PRIVATE") {

        window.location.href =
            "../user-info/user-info.html?id=" +
            otherUserId;

    } else {

        window.location.href =
            "../group-info/group-info.html?groupId=" +
            otherUserId;
    }
};