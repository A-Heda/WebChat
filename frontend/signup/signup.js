async function signup() {

    const id = document.getElementById("id").value;

    const username = document.getElementById("username").value;

    const password = document.getElementById("password").value;

    const confirmPassword = document.getElementById("confirmPassword").value;

    if (id === "") {
        alert("User ID is required.");
        return;
    }

    if (username === "") {
        alert("Username is required.");
        return;
    }

    if (password === "") {
        alert("Password is required.");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match.");
        return;
    }

    try {

        const response = await fetch(
            "http://localhost:8080/users/register",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    id: id,
                    username: username,
                    password: password,
                    repeatPassword: confirmPassword
                })
            }
        );

        const result = await response.json();

        if (response.ok) {

            alert("Account created successfully!");

            window.location.href = "../login/login.html";

        } else {

            alert(result);
        }

    } catch (error) {

        alert("Cannot connect to server.");
        console.error(error);
    }
}
