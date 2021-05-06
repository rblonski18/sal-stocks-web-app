function getCookie() {
	  var name = "user=";
	  var decodedCookie = decodeURIComponent(document.cookie);
	  var ca = decodedCookie.split(';');
	  for(var i = 0; i <ca.length; i++) {
	    var c = ca[i];
	    while (c.charAt(0) == ' ') {
	      c = c.substring(1);
	    }
	    if (c.indexOf(name) == 0) {
	      return c.substring(name.length, c.length);
	    }
	  }
	  return "";
	}
	
var user = getCookie();
$(document).ready(function() {
	  if (user.length > 0) {
	      $("#nav-login").remove();
	  } else {
		  $("#nav-favorites").remove();
		  $("#nav-portfolio").remove();
		  $("#nav-logout").remove();
	  }
	});
	
function setCookie(user) {
    const d = new Date();
    d.setTime(d.getTime() + (24*60*60*1000));
    const expires = "expires="+ d.toUTCString();
    document.cookie = "user=" + user + ";" + expires + ";path=/";
}
	
function handleLogin() {
	var user = document.getElementById("lg-username").value;
	var pass = document.getElementById("lg-password").value;
	console.log("HERE");
	console.log(user);
	console.log(pass);
	
	fetch('LoginServlet', {
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify({
			username: user,
			password: pass,
			type: "normal"
		})
	}).then(res => res.json())
		.then((data) => {
			
			if(data == "Username or password incorrect") { // user validated
				alert("Username or Password incorrect. ");
				document.getElementById("lg-username").value = "";
				document.getElementById("lg-password").value = "";
			} else if(data == "Username or password missing") {
				alert("Username or Password missing.");
			} else {
				
				//window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
				setCookie(data);
				window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
			}
		})
}

function onSignIn(googleUser) {
	
  	var profile = googleUser.getBasicProfile();

	fetch('LoginServlet', {
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify({
			username: profile.getName(),
			password: "Google",
			type: "Google"
		})
	}).then(res => res.json())
		.then((data) => {
			console.log(data);
			if(data == "Username or password incorrect") { // user validated
				alert("Username or Password incorrect. ");
			} else if(data == "Username or password missing") {
				alert("Username or Password incorrect.");
			} else {
				
				//window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
				setCookie(data);
				window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
			}
		})
	
		setCookie(data);
		window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
}

function handleRegister() {
	
	var email = document.getElementById("rg-email").value;
	var user = document.getElementById("rg-username").value;
	var pass1 = document.getElementById("rg-password1").value;
	var pass2 = document.getElementById("rg-password2").value;
	
	if(pass1 != pass2) {
		alert("Passwords must match!");
		document.getElementById("rg-email").value = "";
		document.getElementById("rg-username").value = "";
		document.getElementById("rg-password1").value = "";
		document.getElementById("rg-password2").value = "";
		return;
	}
	
	fetch('RegisterServlet', {
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify({
			username: user,
			password: pass1,
			email: email,
			balance: 50000
		})
	}).then(res => res.json())
		.then((data) => {
			if(data == "Username is taken") { // user validated
				alert("Username is already taken");
				document.getElementById("rg-email").value = "";
				document.getElementById("rg-username").value = "";
				document.getElementById("rg-password1").value = "";
				document.getElementById("rg-password2").value = "";
			} else if(data == "Email is already registered"){
				alert("Email is already taken");
				document.getElementById("rg-email").value = "";
				document.getElementById("rg-username").value = "";
				document.getElementById("rg-password1").value = "";
				document.getElementById("rg-password2").value = "";
			} else {
				//window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
				setCookie(data);
				window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
			}
		})
}