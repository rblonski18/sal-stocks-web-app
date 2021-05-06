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
	

	
function deleteCookie() {
	let user = getCookie();
    document.cookie = "user=" + user + ";expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/";
}
	
function handleLogout() {
	deleteCookie();
	window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
}

$(document).ready(function() {
	var user = getCookie();
	if (user.length > 0) {
		$("#nav-login").remove();
		$("#nav-favorites").remove();
	} else {
		window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
	}
	
	fetch(`FavoritesServlet?userID=${user}`)
		.then(res => res.json())
		.then((data) => {
			if(data.length == 0) alert("You have no favorites!");
			// now we have the ticker. 
			data.forEach((favorite) => {
				let ticker = favorite.ticker;
				
				let el = document.createElement("div");
				el.classList.add("card");
				el.classList.add("fav-card");
				let left = document.createElement("div");
				left.classList.add("fav-left");
				let right = document.createElement("div");
				right.classList.add("fav-right");
				let leftTick = document.createElement("h3");
				let leftComp = document.createElement("h5");
				
				left.appendChild(leftTick);
				left.appendChild(leftComp);
				
				let lastTag = document.createElement("h2");
				let caret = document.createElement("h3");
				let caretFA = document.createElement("i");
				caret.appendChild(caretFA);
				let changeTag = document.createElement("h3");
				let percentTag = document.createElement("h3");
				let iRB = document.createElement("div");
				let deleteFavButton = document.createElement("button");
				deleteFavButton.innerHTML = " x ";
				deleteFavButton.classList.add("delete-fav");
				deleteFavButton.onclick = function() {
					fetch("FavoritesServlet", {
						method: 'POST',
						header: {'Content-Type': 'application/json'},
						body: JSON.stringify({
							userID: user,
							ticker: ticker,
							type: "delete"
						})
					}).then(res => res.json())
						.then((data) => {
							alert("Removed " + ticker + " from favorites");
							window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/favorites.html");
						})
				}
				
				iRB.appendChild(caret);
				iRB.appendChild(changeTag);
				iRB.appendChild(percentTag);
				right.appendChild(deleteFavButton)
				right.classList.add("top-right");
				iRB.classList.add("info-right-below");
				
				right.appendChild(lastTag);
				right.appendChild(iRB);
				
				el.appendChild(left);
				el.appendChild(right);
				
				var today = new Date();
				var dd = String(today.getDate()).padStart(2, '0');
				var mm = String(today.getMonth() + 1).padStart(2, '0');
				var yyyy = today.getFullYear();
				let user = getCookie();
				
				today = yyyy + "-" + mm + '-21';
				fetch(`TiingoProxyServlet?ticker=${ticker}&type=info&dateBought=${today}`)
					.then(res => res.json())
					.then((data) => {
						data = JSON.parse(data);
						leftTick.innerHTML = data.ticker;
						leftComp.innerHTML = data.name;
					})
				
				fetch(`TiingoProxyServlet?ticker=${ticker}&type=change&dateBought=${today}`)
					.then(res => res.json())
					.then((data) => {
						data = JSON.parse(data);
						if(user.length > 0) {
							lastTag.innerHTML = data[0].last;
							let change = data[0].last - data[0].prevClose;
							changeTag.innerHTML = change.toFixed(2);
							let perc = (change * 100) / data[0].prevClose;
							percentTag.innerHTML = "  (" + perc.toFixed(2) + "%)";
							if(change < 0) {
								caretFA.classList.add("fa");
								caretFA.classList.add("fa-caret-down");
								right.style.color = "red";
							} else {
								caretFA.classList.add("fa");
								caretFA.classList.add("fa-caret-up");
								right.style.color = "green";
							}
						}
					})
						
				
				document.getElementById("card-array").appendChild(el);
			})
		})
	
	
});

