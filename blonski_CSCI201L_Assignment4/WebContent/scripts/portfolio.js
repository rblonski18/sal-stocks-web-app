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
			$("#nav-portfolio").remove();
		} else {
			window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/index.html");
		}
		
		let totalAssets = 0;
		
		fetch(`PortfolioServlet?userID=${user}&type=balance`)
			.then(res => res.json())
			.then((data) => {
				document.getElementById("balance").innerHTML = "Cash Balance: $" + data;
				totalAssets += data;
			})
		
		fetch(`PortfolioServlet?userID=${user}&type=portfolio`)
		.then(res => res.json())
		.then((data) => {
			if(data.length == 0) alert("You have nothing in your portfolio!");
			// now we have the ticker. 
			data.forEach((stock) => {
				console.log(stock);
				totalAssets += (stock.numStock * stock.avgCost);
				
				let ticker = stock.ticker;
				
				let el = document.createElement("div");
				el.classList.add("card");
				el.classList.add("card-portfolio");
				let leftTick = document.createElement("h5");
				leftTick.classList.add("card-header");
				
				el.appendChild(leftTick);
				
				
				let tableDiv = document.createElement("div");
				tableDiv.classList.add("table-align");
				let table = document.createElement("table");
				let tableRow1 = document.createElement("tr");
				let tableRow2 = document.createElement("tr");
				let tableRow3 = document.createElement("tr");
				let qLabel = document.createElement("td");
				let avgLabel = document.createElement("td");
				let tcLabel = document.createElement("td");
				let qAmount = document.createElement("td");
				let avgAmount = document.createElement("td");
				let tcAmount = document.createElement("td");
				let chLabel = document.createElement("td");
				let cpLabel = document.createElement("td");
				let mvLabel = document.createElement("td");
				let chAmount = document.createElement("td");
				let cpAmount = document.createElement("td");
				let mvAmount = document.createElement("td");
				
				let chCaret = document.createElement("i");
				
				qLabel.innerHTML = "Quantity: ";
				avgLabel.innerHTML = "Avg. Cost / Share";
				tcLabel.innerHTML = "Total Cost: ";
				qAmount.innerHTML = stock.numStock;
				avgAmount.innerHTML = stock.avgCost;
				tcAmount.innerHTML = stock.totalCost.toFixed(2);
				chLabel.innerHTML = "Change: ";
				cpLabel.innerHTML = "Current Price: ";
				mvLabel.innerHTML = "Market Value: ";
				
				tableRow1.appendChild(qLabel);
				tableRow1.appendChild(qAmount);
				tableRow2.appendChild(avgLabel);
				tableRow2.appendChild(avgAmount);
				tableRow3.appendChild(tcLabel);
				tableRow3.appendChild(tcAmount);
				
				tableRow1.appendChild(chLabel);
				tableRow1.appendChild(chAmount);
				tableRow2.appendChild(cpLabel);
				tableRow2.appendChild(cpAmount);
				tableRow3.appendChild(mvLabel);
				tableRow3.appendChild(mvAmount);
				
				table.appendChild(tableRow1);
				table.appendChild(tableRow2);
				table.appendChild(tableRow3);
				tableDiv.appendChild(table);
				el.appendChild(tableDiv);
				
				let footerDiv = document.createElement("div");
				footerDiv.classList.add("card-footer");
				
				let footer = document.createElement("div");
				footer.classList.add("card-footer-div");
				
				footerDiv.appendChild(footer);
				let q = document.createElement("div");
				let qSpan = document.createElement("span");
				qSpan.innerHTML = "Quantity: ";
				
				
				let qInput = document.createElement("input");
				qInput.setAttribute("type", "text");
				qInput.classList.add("input-width");
				q.appendChild(qSpan);
				q.appendChild(qInput);
				footer.appendChild(q);
				
				let radios = document.createElement("div");
				radios.classList.add("radios");
				let r1 = document.createElement("input");
				let r2 = document.createElement("input");
				r1.setAttribute("type", "radio");
				r2.setAttribute("type", "radio");
				r1.setAttribute("name", "bors");
				r2.setAttribute("name", "bors");
				
				let r1Label = document.createElement("span");
				r1Label.innerHTML = " BUY";
				r1Label.classList.add("padding-right");
				
				let r2Label = document.createElement("span");
				r2Label.innerHTML = " SELL";
				
				let submitButton = document.createElement("button");
				
				radios.appendChild(r1);
				radios.appendChild(r1Label);
				radios.appendChild(r2);
				radios.appendChild(r2Label);
				footer.appendChild(radios);
				
				submitButton.innerHTML = "Submit";
				
				
				footer.appendChild(submitButton)
				
				el.appendChild(footerDiv);
				
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
						leftTick.innerHTML = data.ticker + " - " + data.name;
					})
				
				fetch(`TiingoProxyServlet?ticker=${ticker}&type=change&dateBought=${today}`)
					.then(res => res.json())
					.then((data) => {
						data = JSON.parse(data);
						console.log(data);
						if(user.length > 0) {
							cpAmount.innerHTML = data[0].last;
							let change = stock.avgCost - data[0].last;
							if(change < 0) {
								chCaret.classList.add("fa");
								chCaret.classList.add("fa-caret-down");
								chAmount.style.color = "red";
							} else {
								chCaret.classList.add("fa");
								chCaret.classList.add("fa-caret-up");
								chAmount.style.color = "green";
							}
							chAmount.appendChild(chCaret);
							chAmount.innerHTML += change.toFixed(2);
							let marketValue = (data[0].last * stock.numStock);
							mvAmount.innerHTML = marketValue.toFixed(2);
							
							
						}
					})
					
					submitButton.onclick = function() {
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
						
						var user = getCookie();
						let type = "";
						if(r1.checked) type = "buy";
						else if(r2.checked) type = "sell";
						let qty = qInput.value;
						
						fetch("TradeServlet", {
							method: 'POST',
							header: {'Content-Type': 'application/json'},
							body: JSON.stringify({
								userID: user,
								ticker: ticker,
								numStock: qty,
								stockPrice: cpAmount.innerHTML, 
								dateInt: new Date().getTime(),
								type: type
							})
						}).then(res => {
							window.location.replace("http://localhost:6060/blonski_CSCI201L_Assignment4/portfolio.html");
						}).then((data) => {})
					}
						
				
				document.getElementById("portfolio").appendChild(el);
			})	
			
			document.getElementById("account-value").innerHTML = "Total Account Value: $" + totalAssets.toFixed(2);
		})
	})