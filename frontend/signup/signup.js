function signup() {

    const id =
        document.getElementById("id").value;

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

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

    alert("Account created successfully!");

    window.location.href =
        "../login/login.html";
}