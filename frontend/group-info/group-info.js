const params = new URLSearchParams(window.location.search);

const groupId = params.get("groupId");

document.getElementById("group-id").textContent = "Group ID: " + groupId;

/* dummy data */
document.getElementById("group-name").textContent = "OOP Project";
document.getElementById("members").textContent = "8 members";

function goBack() {
    window.history.back();
}

/* ACTIONS */
function leaveGroup() {
    alert("Left group");
}

function addMember() {
    alert("Member added");
}

function editGroup() {
    alert("Edit group info");
}

function openArchive() {
    alert("Archived");
}

function openHistory() {
    window.location.href = "group-history.html?groupId=" + groupId;
}