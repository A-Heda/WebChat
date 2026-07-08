const API = "http://localhost:8080";

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

    loadUserInfo();

    loadChats();
};

function loadUserInfo() {

    const username =
        localStorage.getItem("username");

    const label =
        document.getElementById("current-username");

    if (label) {
        label.textContent = username;
    }
}


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

    if(chats.length === 0) {

        chatList.innerHTML =
            "<p class='empty-msg'>No chats yet.</p>";

        return;
    }

    chats.forEach(chat => {

        const div =
            document.createElement("div");

        div.className =
            "chat-item";

        div.innerHTML = `
    <img class="chat-avatar"
         src="${chat.imagePath || "../assets/default-avatar.png"}">

    <div class="chat-info">
        <div class="chat-name">
            ${chat.otherUsername}
        </div>

        <div class="chat-type">
            ${chat.type}
        </div>
    </div>
`;

        div.onclick =
            function () {

                openChat(chat);
            };

        chatList.appendChild(div);
    });
}

/*Open selected chat*/
function openChat(chat){

    window.location.href =
        "../chat/chat.html?id=" +
        chat.chatId +
        "&type=" +
        chat.type;
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

            closeModal("add-contact-modal");

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

function openSavedMessages() {

    alert("Coming Soon");
}

function openSettings() {

    openModal("settings-modal");
}

function closeModal(id) {

    const modal =
        document.getElementById(id);

    if(modal)
        modal.style.display = "none";
}

function openModal(id) {

    const modal =
        document.getElementById(id);

    if(modal)
        modal.style.display = "flex";
}

function openArchive() {

    alert("Coming Soon");
}

function openNewChat() {

    openModal("add-contact-modal");
}
