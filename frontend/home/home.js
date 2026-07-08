const API = "http://localhost:8080";

let contacts = [];

/*Elements*/
const chatList = document.getElementById("chat-list");

const contactInput = document.getElementById("contact-id-input");

/*Load chats when page opens*/
window.onload = function () {

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

async function openNewChat() {
    await loadContacts();
    renderContacts(contacts);
    openModal("contacts-modal");
}
