const API = "http://localhost:8080";

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

    loadTheme();

    loadUserInfo();

    loadChats();

};

async function loadUserInfo(){

    const userId =
        localStorage.getItem("userId");

    const response =
        await fetch(API+"/users/id?id="+userId);

    if(!response.ok)
        return;

    const user =
        await response.json();

    document.getElementById("current-username").textContent =
        user.username;

    document.getElementById("profile-pic").src =
        user.profileImagePath ||
        "../assets/default-avatar.png";

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

    if (chats.length === 0) {

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
function openChat(chat) {

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
            await fetch(API + "/chats/create-private",
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

    if (modal)
        modal.style.display = "none";
}

function openModal(id) {

    const modal =
        document.getElementById(id);

    if (modal)
        modal.style.display = "flex";
}

function openArchive() {

    alert("Coming Soon");
}

function openNewChat() {

    openModal("add-contact-modal");
}

function openChangePhoto() {

    document.getElementById("photo-upload").click();

}

function openChangeName() {

    openModal("change-name-modal");

}

function openChangeId() {

    openModal("change-id-modal");

}

function openDeleteAccount() {

    openModal("delete-account-modal");

}

function loadTheme() {

    const darkMode =
        localStorage.getItem("darkMode");

    if (darkMode === "true") {

        document.body.classList.add("dark");

        document
            .getElementById("dark-mode-toggle")
            .classList.add("active");

    }

}

function toggleDarkMode() {

    document.body.classList.toggle("dark");

    document
        .getElementById("dark-mode-toggle")
        .classList.toggle("active");

    localStorage.setItem(

        "darkMode",

        document.body.classList.contains("dark")

    );

    async function saveName() {

        const newName =
            document.getElementById("new-name-input").value.trim();

        if (newName === "") {
            alert("Enter a username.");
            return;
        }

        const response =
            await fetch(API + "/users/username", {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: localStorage.getItem("userId"),

                    username: newName

                })

            });

        const result = await response.json();

        if (response.ok) {

            localStorage.setItem("username", newName);

            document.getElementById("current-username").textContent =
                newName;

            closeModal("change-name-modal");

            alert("Username updated.");

        } else {

            alert(result);

        }

    }

    async function saveUserId() {

        const newId =
            document.getElementById("new-id-input").value.trim();

        if (newId === "") {
            alert("Enter new ID.");
            return;
        }

        const response =
            await fetch(API + "/users/id", {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    oldId: localStorage.getItem("userId"),

                    newId: newId

                })

            });

        const result = await response.json();

        if (response.ok) {

            localStorage.setItem("userId", newId);

            closeModal("change-id-modal");

            alert("User ID updated.");

        } else {

            alert(result);

        }

    }

    async function handlePhotoUpload(event) {

        const file =
            event.target.files[0];

        if (!file)
            return;

        const imagePath =
            "../assets/" + file.name;

        const response =
            await fetch(API + "/users/profile-image", {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: localStorage.getItem("userId"),

                    imagePath: imagePath

                })

            });

        const result =
            await response.json();

        if (response.ok) {

            document
                .getElementById("profile-pic")
                .src = imagePath;

            alert("Profile updated.");

        } else {

            alert(result);

        }

    }

    async function deleteAccount() {

        const password =
            document.getElementById(
                "delete-confirm-password"
            ).value;

        const response =
            await fetch(API + "/users/delete", {

                method: "DELETE",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: localStorage.getItem("userId"),

                    password: password

                })

            });

        const result =
            await response.json();

        if (response.ok) {

            alert("Account deleted.");

            localStorage.clear();

            window.location.href =
                "../login/login.html";

        } else {

            alert(result);

        }

    }

}
