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

function isNumberKey(txt, evt) {
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode == 46) {
        //Check if the text already contains the . character
        if (txt.value.indexOf('.') === -1) {
          return true;
        } else {
		  alert("Please only enter in numbers for the trade value!");
		  //document.getElementById("qty-input").value = 0;
          return false;
        }
      } else {
        if (charCode > 31 &&
          (charCode < 48 || charCode > 57)) {
			  alert("Please only enter in numbers for the trade value!");
			  //document.getElementById("qty-input").value = 0;
	          return false;
			}
      }

      return true;
    }

function deleteCookie() {
		let user = getCookie();
        document.cookie = "user=" + user + ";expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/";
    }

function handleBuy() {
	
	let currentTime = new Date();
	if(currentTime.getDay() != 0 && currentTime.getDay != 6) {
		if(currentTime.getHours() > 5 || (currentTime.getHours() == 5 && currentTime.getMinutes() > 29)) {
			if(currentTime.getHours() < 12) {
				document.getElementById("open-or-closed").innerHTML = "Market is Open";
			} else {
				alert("Market is closed for the day! No trades can be completed. ");
				return;
			}
		} else {
			alert("Market isn't Open Yet! No trades can be completed until market is open.");
			return;
		}
	} else {
		alert("Market is Closed on Weekends");
		return;
	}
	
	let user = getCookie();
	
	let tick = document.getElementById("lg-ticker").innerHTML;
	let quantity = document.getElementById("qty-input").value;
	let price = document.getElementById("last").innerHTML;
	var today = new Date().getTime();
	console.log(today);
	
	fetch('TradeServlet', {
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify({
			userID: user,
			numStock: quantity,
			stockPrice: price,
			dateInt: today,
			ticker: tick,
			type: "buy"
		})
		}).then(res => res.json())
			.then((data) => {
				alert("Bought " + quantity + " shares of " + tick + " for $" + (quantity*price).toFixed(2));
			})
}

function toggleStar() {
	let tick = document.getElementById("lg-ticker").innerHTML;
	
	let user = getCookie();
	if(document.getElementById("fav").classList.contains("fa-star")) {
		document.getElementById("fav").classList.remove("fa-star");
		document.getElementById("fav").classList.add("fa-star-o");
		
		fetch("FavoritesServlet", {
			method: 'POST',
			header: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				userID: user,
				ticker: tick,
				type: "delete"
			})
		}).then(res => res.json())
			.then((data) => {
				alert("Removed " + tick + " from favorites")
			})
		
	} else {
		document.getElementById("fav").classList.remove("fa-star-o");
		document.getElementById("fav").classList.add("fa-star");
		
		fetch("FavoritesServlet", {
			method: 'POST',
			header: {'Content-Type': 'application/json'},
			body: JSON.stringify({
				userID: user,
				ticker: tick,
				type: "add"
			})
		}).then(res => res.json())
			.then((data) => {
				alert("Added " + tick + " to favorites")
			})
		}
	
}

function handleLogout() {
	deleteCookie();
	window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
}
			
function handleSearch(event) {
	var value = document.getElementById("searchbar").value;
	var today = new Date();
	var dd = String(today.getDate()).padStart(2, '0');
	var mm = String(today.getMonth() + 1).padStart(2, '0');
	var yyyy = today.getFullYear();
	let user = getCookie();

	today = yyyy + "-" + mm + '-21';
	fetch(`TiingoProxyServlet?ticker=${value}&type=info&dateBought=${today}`)
		.then(res => res.json())
		.then((data) => {
			data = JSON.parse(data);
			if(user.length == 0) {
				document.getElementById("search").style.display = "none";
				document.getElementById("company-info").style.display = "block";
				document.getElementById("ticker").innerHTML = data.ticker;
				document.getElementById("company-name").innerHTML = data.name;
				document.getElementById("exchange").innerHTML = data.exchangeCode;
				document.getElementById("description").innerHTML = data.description;
				document.getElementById("start-date").innerHTML = "Start Date: " + data.startDate;
			} else {
				document.getElementById("company-info-logged-in").style.display = "block";
				document.getElementById("search").style.display = "none";
				document.getElementById("lg-ticker").innerHTML = data.ticker;
				document.getElementById("lg-company-name").innerHTML = data.name;
				document.getElementById("lg-exchange").innerHTML = data.exchangeCode;
				document.getElementById("lg-description").innerHTML = data.description;
				document.getElementById("lg-start-date").innerHTML = "Start Date: " + data.startDate;
				document.getElementById("lg-user-input").style.display = "block";
				document.getElementById("lg-fav-btn").removeAttribute("hidden");
				
				let currentTime = new Date();
				if(currentTime.getDay() != 0 && currentTime.getDay != 6) {
					if(currentTime.getHours() > 5 || (currentTime.getHours() == 5 && currentTime.getMinutes() > 29)) {
						if(currentTime.getHours() < 12) {
							document.getElementById("open-or-closed").innerHTML = "Market is Open";
						} else {
							document.getElementById("open-or-closed").innerHTML = "Market is Closed for the Day.";
						}
					} else {
						document.getElementById("open-or-closed").innerHTML = "Market isn't Open Yet";
					}
				} else {
					document.getElementById("open-or-closed").innerHTML = "Market is Closed on Weekends";
				}
			}
		})
		
	fetch(`TiingoProxyServlet?ticker=${value}&type=prices&dateBought=${today}`)
		.then(res => res.json())
		.then((data) => {
			data = JSON.parse(data);
			if(user.length == 0) {
				document.getElementById("high").innerHTML = "High Price:  " + data[0].high;
				document.getElementById("low").innerHTML = "Low Price:  " + data[0].low;
				document.getElementById("open").innerHTML = "Open Price:  " + data[0].open;
				document.getElementById("close").innerHTML = "Close Price:  " + data[0].close;
				document.getElementById("volume").innerHTML = "Volume:  " + data[0].volume;
			} else {
				document.getElementById("lg-high").innerHTML = "High Price:  " + data[0].high;
				document.getElementById("lg-low").innerHTML = "Low Price:  " + data[0].low;
				document.getElementById("lg-open").innerHTML = "Open Price:  " + data[0].open;
				document.getElementById("lg-close").innerHTML = "Close Price:  " + data[0].close;
				document.getElementById("lg-volume").innerHTML = "Volume:  " + data[0].volume;
			}
		});
		
	fetch(`TiingoProxyServlet?ticker=${value}&type=change&dateBought=${today}`)
		.then(res => res.json())
		.then((data) => {
			data = JSON.parse(data);
			if(user.length > 0) {
				document.getElementById("last").innerHTML = data[0].last;
				let change = data[0].last - data[0].prevClose;
				document.getElementById("change").innerHTML = change.toFixed(2);
				let perc = (change * 100) / data[0].prevClose;
				document.getElementById("percent").innerHTML = "  (" + perc.toFixed(2) + "%)";
				var today = new Date();
				document.getElementById("change-date").innerHTML = today.toString();
				
				document.getElementById("lg-mid").innerHTML = "Mid Price:  " + data[0].mid;
				document.getElementById("lg-ap").innerHTML = "Ask Price:  " + data[0].askPrice;
				document.getElementById("lg-as").innerHTML = "Ask Size:  " + data[0].askSize;
				document.getElementById("lg-bp").innerHTML = "Bid Price:  " + data[0].bidPrice;
				document.getElementById("lg-bs").innerHTML = "Bid Size:  " + data[0].bidSize;
				
				if(change < 0) {
					if(document.getElementById("caret").classList.contains("fa-caret-up")) {
						document.getElementById("caret").classList.remove("fa-caret-up")
					}
					document.getElementById("caret").classList.add("fa-caret-down");
					document.getElementById("top-right").style.color = "red";
				} else {
					if(document.getElementById("caret").classList.contains("fa-caret-down")) {
						document.getElementById("caret").classList.remove("fa-caret-down")
					}
					document.getElementById("caret").classList.add("fa-caret-up");
					document.getElementById("top-right").style.color = "green";
				}
			}
		})
}
			

$(document).ready(function() {
	  var user1 = getCookie();
	  if (user1.length > 0) {
	      $("#nav-login").remove();
	  } else {
		  $("#nav-favorites").remove();
		  $("#nav-portfolio").remove();
		  $("#nav-logout").remove();
	  }
	});