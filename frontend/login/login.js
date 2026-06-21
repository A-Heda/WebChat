function login(){

    const id =
        document.getElementById("id").value;

    const password =
        document.getElementById("password").value;

    if(id === ""){
        alert("User ID is required.");
        return;
    }

    if(password === ""){
        alert("Password is required.");
        return;
    }

    alert("Welcome to Blink!");
}