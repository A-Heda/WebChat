function signup() {

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    if (username === "") {
        alert("Username is required");
        return;
    }

    if (password === "") {
        alert("Password is required");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
    }

    alert("Account created successfully!");

    window.location.href = "login.html";
}