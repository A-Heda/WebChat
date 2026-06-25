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

window.onload = async function () {

    if (!userId) {

        alert("User not found");

        window.history.back();

        return;
    }

    await loadUserInfo();
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

function goBack() {

    window.history.back();
}

function blockUser() {

    alert("User blocked");
}

function addContact() {

    alert("Added to contacts");
}

function archiveUser() {

    alert("User archived");
}