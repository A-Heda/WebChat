const params = new URLSearchParams(window.location.search);

const groupId = params.get("groupId");

document.getElementById("group-id").textContent = "Group ID: " + groupId;

const API = "http://localhost:8080";

window.onload = function () {

    loadGroup();
    checkStatus();
    loadArchiveStatus();
}

async function loadGroup() {

    const response = await fetch(
        API + "/groups/id?groupId=" + groupId
    );

    const group = await response.json();

    if (!response.ok) {
        alert(group);
        return;
    }

    document.getElementById("group-name").textContent =
        group.name;

    document.getElementById("group-id").textContent =
        "Group ID: " + group.id;

    document.getElementById("members").textContent =
        group.memberIds.length + " members";

    if (group.groupImagePath &&
        group.groupImagePath.trim() !== "") {

        document.getElementById("avatar").src =
            group.groupImagePath;
    }
}

async function loadArchiveStatus() {

    const userId = localStorage.getItem("userId");

    const response =
        await fetch(
            API +
            "/users/status?ownerId=" +
            userId +
            "&contactId=&chatId=group_" +
            groupId
        );

    const status =
        await response.json();

    archived = status.archived;

    document.querySelector(
        'button[onclick="openArchive()"]'
    ).textContent =
        archived
            ? "Remove from Archive"
            : "Add to Archive";
}

function goBack() {
    window.history.back();
}

/* ACTIONS */
async function leaveGroup() {

    const userId =
        localStorage.getItem("userId");

    const response = await fetch(
        API + "/groups/leave",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                groupId,
                userId
            })
        }
    );

    const result = await response.json();

    if (response.ok) {

        alert("Left group");

        location.href = "../home/home.html";

    } else {

        alert(result);
    }
}

function addMember(){

    document
        .getElementById("add-member-modal")
        .classList.remove("hidden");

}

function editGroup() {

    location.href =
        "../edit-group/edit-group.html?groupId=" +
        groupId;
}

let archived = false;

async function openArchive() {

    const userId =
        localStorage.getItem("userId");

    const response =
        await fetch(
            API + "/users/archive",
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: userId,

                    chatId: "group_" + groupId,

                    archived: !archived
                })
            }
        );

    if (response.ok) {

        archived = !archived;
        await loadArchiveStatus();
        alert(
            archived ?
                "Added to archive" :
                "Removed from archive"
        );

    } else {

        alert("Error");
    }
}

async function checkStatus() {

    const userId =
        localStorage.getItem("userId");

    const response =
        await fetch(
            API +
            "/groups/status?userId=" +
            userId +
            "&groupId=" +
            groupId
        );

    const status =
        await response.json();


}

function openHistory() {
    window.location.href =
        "../group-history/group-history.html?groupId=" + groupId;
}

function closeAddMember(){

    document
        .getElementById("add-member-modal")
        .classList.add("hidden");

}

async function submitAddMember(){

    const userId =
        document
        .getElementById("member-id")
        .value
        .trim();

    if(userId===""){

        alert("Enter user ID");

        return;
    }

    const response =
        await fetch(
            API + "/groups/add-member",
            {

                method:"POST",

                headers:{
                    "Content-Type":"application/json"
                },

                body:JSON.stringify({

                    groupId:groupId,

                    userId:userId

                })

            });

    const result =
        await response.json();

    if(response.ok){

        alert("Member added.");

        closeAddMember();

        document
            .getElementById("member-id")
            .value="";

        loadGroup();

    }else{

        alert(result);

    }

}