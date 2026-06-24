const API = "http://localhost:8080";

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

    loadChats();
};

/*Get all chats of current user*/
async function loadChats() {

    const userId =
        localStorage.getItem("userId");

    if (!userId) {

        alert("Please login first.");

        window.location.href =
            "../login/login.html";

        return;
    }

    try {

        const response =
            await fetch(
                API +
                "/chats/user?userId=" +
                userId
            );

        const chats =
            await response.json();

        renderChats(chats);

    } catch (error) {

        console.error(error);

        alert("Cannot connect to server.");
    }
}

/*Render chats in sidebar*/
function renderChats(chats) {

    chatList.innerHTML = "";

    chats.forEach(chat => {

        const div =
            document.createElement("div");

        div.className =
            "chat-item";

        div.innerHTML =
            `
            <div>
                ${chat.otherUsername}
            </div>
            `;

        div.onclick =
            function () {

                openChat(chat.chatId);
            };

        chatList.appendChild(div);
    });
}

/*Open selected chat*/
function openChat(chatId) {

    window.location.href =
        "../chat/chat.html?id=" +
        chatId;
}

/*Create private chat*/
async function createPrivateChat() {

    const otherUserId =
        contactInput.value.trim();

    if (otherUserId === "") {

        alert("Enter user id.");

        return;
    }

    try {

        const response =
            await fetch( API + "/chats/create-private",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        user1Id: localStorage.getItem("userId"),

                        user2Id: otherUserId
                    })
                }
            );

        const result = await response.json();

        if (response.ok) {

            contactInput.value = "";

            loadChats();

        } else {

            alert(result);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot connect to server.");
    }
}

/*Logout*/
function logout() {

    localStorage.clear();

    window.location.href = "../login/login.html";
}