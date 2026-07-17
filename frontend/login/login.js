async function login() {

    const username =
        document.getElementById("username").value.trim();

    const password =
        document.getElementById("password").value;

    if (username === "") {
        alert("Username is required.");
        return;
    }

    if (password === "") {
        alert("Password is required.");
        return;
    }

    try {

        const response = await fetch(
            "http://localhost:8080/users/login",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    username: username,
                    password: password
                })
            }
        );

        const data = await response.json();

        if (response.ok) {

            alert("Login successful!");

            localStorage.setItem(
                "userId",
                data.id
            );

            localStorage.setItem(
                "username",
                data.username
            );

            window.location.href = "../home/home.html";

        } else {

            alert(data);

        }

    } catch (error) {

        alert("Cannot connect to server.");

        console.error(error);
    }
}
