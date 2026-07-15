const API = "http://localhost:8080";

const params = new URLSearchParams(location.search);

let selectedImage = "";

const groupId = params.get("groupId");

window.onload = loadGroup;

async function loadGroup() {

    const response =
        await fetch(
            API + "/groups/id?groupId=" + groupId
        );

    const group = await response.json();

    document.getElementById("group-name").value =
        group.groupName;

    document.getElementById("group-id").value =
        group.id;

    if (group.groupImagePath &&
        group.groupImagePath.trim() !== "") {

        document.getElementById("avatar").src =
            group.groupImagePath;

        selectedImage =
            group.groupImagePath;
    }

}

async function saveChanges() {

    const response =
        await fetch(API + "/groups/edit", {

            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({

                groupId: groupId,

                newGroupName:
                    document.getElementById("group-name").value,

                imagePath:
                    selectedImage

            })

        });

    const result = await response.json();

    if (response.ok) {

        alert("Group updated successfully");

        history.back();

    } else {

        alert(result);

    }

}

document
    .getElementById("imageInput")
    .addEventListener("change", function () {

        const file = this.files[0];

        if (!file)
            return;

        const reader = new FileReader();

        reader.onload = function (e) {

            selectedImage = e.target.result;

            document.getElementById("avatar").src =
                selectedImage;

        };

        reader.readAsDataURL(file);

    });

function goBack() {

    history.back();

}