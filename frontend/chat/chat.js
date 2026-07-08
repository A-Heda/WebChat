const API = "http://localhost:8080";

let lastMessageCount = 0;

const params =
    new URLSearchParams(window.location.search);

const chatId =
    params.get("id");

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

    await loadChatInfo();

    await loadMessages();

    setInterval(loadMessages, 3000);
};

/* Load Chat Header */

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

                    <div class="message-text">

                        ${message.text}

                        ${message.edited ? "<span class='edited'>(edited)</span>" : ""}

                    </div>

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

function toggleMenu(event,messageId,isMine){

    event.stopPropagation();

    document
    .querySelectorAll(".message-menu")
    .forEach(menu=>{

        menu.classList.add("hidden");

    });

    const menu =
    document.getElementById(
        "menu-"+messageId
    );

    if(isMine){

        menu.innerHTML =

        `
        <div onclick="editMessage('${messageId}')">
            ✏️ Edit
        </div>

        <div onclick="deleteMessage('${messageId}')">
            🗑 Delete
        </div>

        <div onclick="reportMessage('${messageId}')">
            🚩 Report
        </div>
        `;

    }else{

        menu.innerHTML=

        `
        <div onclick="reportMessage('${messageId}')">
            🚩 Report
        </div>
        `;
    }

    menu.classList.toggle("hidden");
}

window.onclick=function(){

    document
    .querySelectorAll(".message-menu")
    .forEach(menu=>{

        menu.classList.add("hidden");

    });

}

async function editMessage(messageId){

    const newText=
    prompt("Edit message");

    if(newText==null)
        return;

    const response =
    await fetch(API+"/chats/edit",{

        method:"PUT",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            chatId:chatId,

            messageId:messageId,

            editorId:currentUserId,

            newText:newText
        })

    });

    if(response.ok){

        loadMessages();

    }else{

        alert(await response.json());

    }

}

async function deleteMessage(messageId){

    if(!confirm("Delete message?"))
        return;

    const response =
    await fetch(API+"/chats/delete",{

        method:"DELETE",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            chatId:chatId,

            messageId:messageId,

            requesterId:currentUserId

        })

    });

    if(response.ok){

        loadMessages();

    }else{

        alert(await response.json());

    }

}

async function reportMessage(messageId){

    const response=
    await fetch(API+"/chats/report",{

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            chatId:chatId,

            messageId:messageId,

            reporterId:currentUserId

        })

    });

    if(response.ok){

        alert("Reported.");

    }else{

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

    if (!otherUserId)
        return;

    window.location.href =
        "../user-info/user-info.html?id=" +
        otherUserId;
};