const params = new URLSearchParams(window.location.search);

const username = params.get("username");
const userId = params.get("id");

document.getElementById("username").textContent = "@" + username;
document.getElementById("user-id").textContent = "ID: " + userId;

function goBack() {
    window.history.back();
}

/* ACTIONS */
function blockUser() {
    alert("User blocked");
    // TODO: API
}

function addContact() {
    alert("Added to contacts");
    // TODO: API
}

function archiveUser() {
    alert("User archived");
    // TODO: API
}