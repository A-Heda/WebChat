const API = "http://localhost:8080";

let contacts = [];
let selectedMembers = [];

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

    loadTheme();

    loadUserInfo();

    loadChats();

    loadContacts();

    document.getElementById("contact-search")
        .addEventListener("input", searchContacts);
};

async function loadContacts() {

    const ownerId = localStorage.getItem("userId");

    try {

        const response =
            await fetch(API + "/contacts?ownerId=" + ownerId);

        contacts = await response.json();

    } catch (error) {

        console.error(error);

        alert("Cannot load contacts.");
    }
}

function renderContacts(list) {

    const container =
        document.getElementById("contacts-list");

    container.innerHTML = "";

    if(list.length === 0){

        container.innerHTML =
            "<p class='empty-msg'>No contacts.</p>";

        return;
    }

    list.forEach(contact => {

        const div =
            document.createElement("div");

        div.className =
            "contact-item";

        div.innerHTML = `
<img class="chat-avatar"
src="${contact.imagePath || "../assets/default-avatar.png"}">

<div class="chat-info">

    <div class="chat-name">
        ${contact.username}
    </div>

    <div class="chat-id">
        @${contact.id}
    </div>

</div>
`;

        div.onclick = () => {

            createPrivateChat(contact.id);

        };

        container.appendChild(div);

    });

}

function renderGroupMembers() {
    const list = document.getElementById("group-members-list");

    list.innerHTML = "";
    selectedMembers = [];

    contacts.forEach(c => {
        const item = document.createElement("label");
        item.className = "group-member";

        const image =
        !c.imagePath || c.imagePath === "null"
        ? "../assets/default-avatar.png"
        : c.imagePath;

    item.innerHTML = `
        <input type="checkbox" value="${c.id}">

        <img class="chat-avatar" src="${image}">

        <div class="chat-info">
            <div class="chat-name">${c.username}</div>
            <div class="chat-id">@${c.id}</div>
        </div>
        `;

        item.querySelector("input").addEventListener("change", (e) => {
            selectedMembers = e.target.checked 
                ? [...selectedMembers, c.id] 
                : selectedMembers.filter(id => id !== c.id);

        });

        list.appendChild(item);
    });
}

function searchContacts() {

    const keyword =
        document.getElementById("contact-search")
        .value
        .toLowerCase();

    const filtered =
        contacts.filter(contact =>

            contact.username.toLowerCase().includes(keyword) ||

            contact.id.toLowerCase().includes(keyword)

        );

    renderContacts(filtered);
}


function openAddContact(){

    closeModal("contacts-modal");

    openModal("add-contact-modal");

}

async function addContact(){

    const contactId =
        document.getElementById("contact-id-input")
        .value.trim();

    if(contactId === ""){

        alert("Enter User ID.");

        return;
    }

    try{

        const response =
            await fetch(API + "/contacts/add",{

                method:"POST",

                headers:{
                    "Content-Type":"application/json"
                },

                body:JSON.stringify({

                    ownerId:localStorage.getItem("userId"),

                    contactId:contactId

                })

            });

        const result =
            await response.json();

        if(response.ok){

            document.getElementById("contact-id-input").value="";

            closeModal("add-contact-modal");

            await loadContacts();

            renderContacts(contacts);

            openModal("contacts-modal");
        }

        else{

            alert(result);

        }

    }

    catch(error){

        console.error(error);

    }

}
/*Create private chat*/
async function createPrivateChat(otherUserId) {

    try {

        const response = await fetch(
            API + "/chats/create-private",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    user1Id: localStorage.getItem("userId"),
                    user2Id: otherUserId
                })
            }
        );

        const result = await response.json();

        if (!response.ok) {

            alert(result);

            return;
        }

        closeModal("contacts-modal");

        window.location.href =
            "../chat/chat.html?id=" +
            result +
            "&type=PRIVATE";

    } catch (error) {

        console.error(error);

        alert("Cannot connect to server.");
    }
}


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

async function openNewChat() {
    await loadContacts();
    renderContacts(contacts);
    openModal("contacts-modal");
}


async function openCreateGroup() {

    closeModal("contacts-modal");

    await loadContacts();

    renderGroupMembers();

    openModal("create-group-modal");

}

async function createGroup() {
    const groupId = document.getElementById("group-id-input").value.trim();
    const groupName = document.getElementById("group-name-input").value.trim();
    if (!groupId || !groupName) return alert("Fill all fields.");
    const payload = {
        groupId,
        groupName,
        adminId: localStorage.getItem("userId"),
        memberIds: selectedMembers
    };

    try {
        const res = await fetch(`${API}/groups/create`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data || "Failed to create group");
        closeModal("create-group-modal");

        window.location.href = "../chat/chat.html?id=" + data + "&type=GROUP";
    } catch (err) {
        console.error("Group creation failed:", err);
        alert(err.message);
    }
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
}

    async function saveName() {

        const newName =
            document.getElementById("new-name-input").value.trim();

        if (newName === "") {
            alert("Enter a username.");
            return;
        }

        const response =
            await fetch(API + "/users/change-username", {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: localStorage.getItem("userId"),

                    newUsername: newName

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
            await fetch(API + "/users/change-id", {

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
