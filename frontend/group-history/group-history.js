const API = "http://localhost:8080";

const params =
    new URLSearchParams(window.location.search);

const groupId =
    params.get("groupId");

window.onload = function () {

    loadHistory();

};

async function loadHistory() {

    const response =
        await fetch(

            API +
            "/groups/history?groupId=" +
            groupId

        );

    const history =
        await response.json();

    const container =
        document.getElementById(
            "history-list"
        );

    container.innerHTML = "";

    if(history.length===0){

        container.innerHTML=
            "<p>No history.</p>";

        return;

    }

    for(const item of history){

        let username =
            item.senderId;

        try{

            const userResponse =
                await fetch(

                    API+
                    "/users/id?id="+
                    item.senderId

                );

            if(userResponse.ok){

                const user =
                    await userResponse.json();

                username =
                    user.username;

            }

        }catch(e){}

        const div =
            document.createElement("div");

        div.className =
            "history-item";

        const date =
            new Date(item.time);

        const action =
            item.action==="EDIT"
            ?
            "<div class='history-type edit'>📝 Edited Message</div>"
            :
            "<div class='history-type delete'>🗑 Deleted Message</div>";

        div.innerHTML =

        `
        ${action}

        <div class="sender">

            ${username}

            <br>

            ID: ${item.senderId}

        </div>

        <div class="old-message">

            ${item.text}

        </div>

        <div class="time">

            ${date.toLocaleString()}

        </div>
        `;

        container.appendChild(div);

    }

}

function goBack(){

    window.history.back();

}
