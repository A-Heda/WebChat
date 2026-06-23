const API = "http://localhost:8080";

const params = new URLSearchParams(window.location.search);
const userId = params.get("id");

/* LOAD USER */
async function loadUser() {

    const res = await fetch(`${API}/users/id?id=${userId}`);
    const user = await res.json();

    document.getElementById("username").textContent = "@" + user.username;
    document.getElementById("user-id").textContent = "ID: " + user.id;

    document.getElementById("avatar").src =
        user.profileImagePath || "../assets/default.png";
}

loadUser();

/* MENU */
document.getElementById("menu-btn").onclick = () => {
    document.getElementById("menu").classList.toggle("hidden");
};

/* BACK */
function goBack() {
    window.history.back();
}

/* ACTIONS */
function blockUser() {
    alert("Block user (API later)");
}

function addContact() {
    alert("Add to contacts");
}

function archive() {
    alert("Archive");
}