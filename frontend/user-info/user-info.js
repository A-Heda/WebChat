const API = "http://localhost:8080";

const params =
    new URLSearchParams(window.location.search);

const userId =
    params.get("id");

const avatar =
    document.getElementById("avatar");

const nameElement =
    document.getElementById("name");

const usernameElement =
    document.getElementById("username");

const userIdElement =
    document.getElementById("user-id");

const currentUserId = localStorage.getItem("userId");

const chatId =
    "private_" +
    [currentUserId, userId].sort().join("_");

const blockButton =
    document.querySelector(".actions button:nth-child(1)");

const contactButton =
    document.querySelector(".actions button:nth-child(2)");

const archiveButton =
    document.querySelector(".actions button:nth-child(3)");

let blocked = false;
let isContact = false;
let archived = false;

window.onload = async function () {

    if (!userId) {

        alert("User not found");

        window.history.back();

        return;
    }

    await loadUserInfo();
    await loadUserStatus();
    await loadMutualGroups();
};

async function loadUserInfo() {

    try {

        const response =
            await fetch(
                API +
                "/users/id?id=" +
                userId
            );

        const user =
            await response.json();

        if (response.ok) {

            nameElement.textContent =
                user.username;

            usernameElement.textContent =
                "@" + user.username;

            userIdElement.textContent =
                "ID: " + user.id;

            if (
                user.profileImagePath &&
                user.profileImagePath.trim() !== ""
            ) {

                avatar.src =
                    user.profileImagePath;
            }

        } else {

            alert(user);
        }

    } catch (error) {

        console.error(error);

        alert("Cannot load user info.");
    }
}

async function loadUserStatus() {

    const response =
        await fetch(
            API +
            "/users/status?ownerId=" +
            currentUserId +
            "&contactId=" +
            userId +
            "&chatId=" +
            chatId
        );

    const status =
        await response.json();

    blocked =
        status.blocked;

    isContact =
        status.contact;

    archived =
        status.archived;

    blockButton.textContent =
        blocked ?
            "Unblock User" :
            "Block User";

    contactButton.textContent =
        isContact ?
            "Remove Contact" :
            "Add to Contacts";

    archiveButton.textContent =
        archived ?
            "Remove from Archive" :
            "Add to Archive";
}

async function loadMutualGroups() {

    const response =
        await fetch(
            API +
            "/users/mutual-groups?user1=" +
            currentUserId +
            "&user2=" +
            userId
        );

    const groups =
        await response.json();

    const container =
        document.getElementById("groups");

    container.innerHTML = "";

    if (groups.length === 0) {

        container.innerHTML =
            "<p>No mutual groups</p>";

        return;
    }

    groups.forEach(group => {

        const div =
            document.createElement("div");

        div.className = "group-item";

        div.textContent =
            group.name +
            " (" +
            group.id +
            ")";

        container.appendChild(div);

    });

}

async function blockUser() {

    const url =
        blocked ?
            "/users/unblock" :
            "/users/block";

    const response =
        await fetch(API + url, {

            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                ownerId: currentUserId,

                contactId: userId

            })

        });

    if (response.ok) {

        await loadUserStatus();

    } else {

        alert(await response.json());

    }
}

async function addContact() {

    const url =
        isContact
            ? "/users/remove-contact"
            : "/users/contact";

    const method =
        isContact
            ? "DELETE"
            : "POST";

    const response =
        await fetch(API + url, {

            method: method,

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                ownerId: currentUserId,

                contactId: userId

            })

        });

    if (response.ok) {

        loadUserStatus();

    } else {

        alert(await response.json());

    }

}

async function archiveUser() {

    const response =
        await fetch(API + "/users/archive", {

            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                userId: currentUserId,

                chatId: chatId,

                archived: !archived

            })

        });

    if (response.ok) {

        await loadUserStatus();

    } else {

        alert(await response.json());

    }
}

function goBack() {

    window.history.back();
}

