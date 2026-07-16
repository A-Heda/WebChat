const API = "http://localhost:8080";

const isArchivePage =
    window.location.pathname.includes("archive");

let contacts = [];
let selectedMembers = [];
let chatMap = {};
let allChats = [];

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

    loadTheme();

    loadUserInfo();

    loadChats();

    loadContacts();

    setInterval(loadChats, 4000);
    setInterval(loadContacts, 8000);

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

        allChats = chats;
        searchChats();

    } catch (error) {

        console.error(error);

        alert("Cannot connect to server.");
    }
}

/*Render chats in sidebar*/
function renderChats(chats) {

    chatMap = {};

    chatList.innerHTML = "";

    if (isArchivePage) {

        chats = chats.filter(chat => chat.archived);

    } else {

        chats = chats.filter(chat => !chat.archived);

    }

    chats.sort((a, b) => {
        if (a.pinned !== b.pinned)
            return b.pinned - a.pinned;

        return (b.lastMessageTime || 0) - (a.lastMessageTime || 0);
    });

    if (chats.length === 0) {

        chatList.innerHTML =
            "<p class='empty-msg'>No chats yet.</p>";

        return;
    }

    chats.forEach(chat => {

        chatMap[chat.chatId] = chat;

        const div = document.createElement("div");

        div.className = "chat-item";

        const image =
            chat.imagePath &&
            chat.imagePath.trim() !== "" &&
            chat.imagePath !== "null"
                ? chat.imagePath
                : "../assets/default-avatar.png";

        div.innerHTML = `
    <img class="chat-avatar" src="${image}">

    <div class="chat-info">
        <div class="chat-name">
            ${chat.otherUsername}
            ${chat.pinned ? "📌" : ""}
            ${
                chat.unreadCount > 0
                    ? `<span class="unread-badge">${chat.unreadCount}</span>`
                    : ""
            }
        </div>

        <div class="chat-type">
            ${chat.type}
        </div>
    </div>

    <div class="chat-menu">
        <button
            class="menu-btn"
            onclick="event.stopPropagation();toggleMenu(this)"
        >
            <i class="fa-solid fa-ellipsis-vertical"></i>
        </button>

        <div class="menu-dropdown">
            <div onclick="event.stopPropagation();togglePin(chatMap['${chat.chatId}'])">
                ${chat.pinned ? "📌 Unpin" : "📌 Pin"}
            </div>

            <div onclick="event.stopPropagation();toggleArchive(chatMap['${chat.chatId}'])">
                ${chat.archived ? "📦 Unarchive" : "📦 Archive"}
            </div>
        </div>
    </div>
`;


        div.onclick = function () {

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

/*Search chats in sidebar*/
function searchChats() {

    const keyword = document
        .getElementById("search-input")
        .value
        .toLowerCase()
        .trim();

    if (keyword === "") {

        renderChats(allChats);
        return;
    }

    const filtered = allChats.filter(chat => {

        const name =
            (chat.otherUsername || "")
                .toLowerCase();

        const id =
            (chat.chatId || "")
                .toLowerCase();

        return (
            name.includes(keyword) ||
            id.includes(keyword)
        );

    });

    renderChats(filtered);
}

/*Logout*/
function logout() {

    localStorage.clear();

    window.location.href = "../login/login.html";
}

function openSavedMessages() {

    window.location.href =
        "../chat/chat.html?id=saved&type=SAVED";
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

    window.location.href =
        "../archive/archive.html";

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

    closeModal("settings-modal");

    openModal("change-photo-modal");

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

    async function saveProfileImage() {

    const imagePath =
        document.getElementById("profile-image-input").value.trim();

    if (imagePath === "") {

        alert("Image path cannot be empty.");

        return;
    }

    try {

        const response =
            await fetch(
                API + "/users/profile-image",
                {

                    method: "PUT",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({

                        userId: currentUserId,

                        imagePath: imagePath

                    })

                });

        const result =
            await response.json();

        if (response.ok) {

            closeModal("change-photo-modal");

            loadChats();

            alert("Profile photo updated.");

        } else {

            alert(result);

        }

    } catch (error) {

        console.error(error);

        alert("Cannot update profile photo.");

    }

}

    async function handlePhotoUpload(event){


    const file =
        event.target.files[0];


    if(!file)
        return;



    const reader =
        new FileReader();



    reader.onload = async function(){


        const base64 =
            reader.result;



        const response =
            await fetch(
                API + "/users/profile-image",
                {

                method:"PUT",

                headers:{
                    "Content-Type":"application/json"
                },


                body:JSON.stringify({

                    userId:
                    localStorage.getItem("userId"),


                    image:
                    base64

                })

            });



        const result =
            await response.json();



        if(response.ok){


            document
            .getElementById("profile-pic")
            .src = base64;



            closeModal(
                "change-photo-modal"
            );


            loadChats();


            alert(
                "Profile updated"
            );


        }else{

            alert(result);

        }


    };


    reader.readAsDataURL(file);

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

        async function togglePin(chat) {

    const response = await fetch(API + "/chats/pin", {

        method: "PUT",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({

            userId: localStorage.getItem("userId"),

            chatId: chat.chatId,

            pinned: !chat.pinned

        })

    });

    if(response.ok){

        chat.pinned = !chat.pinned;

        loadChats();

    }else{

        alert(await response.json());

    }
}

async function toggleArchive(chat){

    const response = await fetch(API + "/chats/archive",{

        method:"PUT",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            userId:localStorage.getItem("userId"),

            chatId:chat.chatId,

            archived:!chat.archived

        })

    });

    if(response.ok){

        chat.archived = !chat.archived;

        loadChats();

    }else{

        alert(await response.json());

    }

}

function toggleMenu(button) {

    const menu = button.nextElementSibling;

    document.querySelectorAll(".menu-dropdown").forEach(m => {

        if (m !== menu)
            m.classList.remove("show");

    });

    menu.classList.toggle("show");
}

    
