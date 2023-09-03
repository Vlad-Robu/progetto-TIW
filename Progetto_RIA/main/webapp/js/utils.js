/**
 * Send an Ajax request using XMLHttpRequest.
 * @param {string} method - The HTTP method ('GET' or 'POST').
 * @param {string} url - The URL to send the request to.
 * @param {FormData|object|null} data - Data to send with the request.
 * @param {function} callback - Callback function to handle the response.
 */
function sendAjaxRequest(method, url, data, callback) {
	const xhr = new XMLHttpRequest();

	if (method === "GET" && data) {
		const urlParams = new URLSearchParams(data);
		url = `${url}?${urlParams.toString()}`;
	}

	xhr.open(method, url, true);

	xhr.onreadystatechange = function() {
		if (xhr.readyState === XMLHttpRequest.DONE) {
			if (xhr.status === 200) {
				if (xhr.getResponseHeader("Content-Type").indexOf("application/json") !== -1) {
					const response = JSON.parse(xhr.responseText);
					callback(response);
				}
			} else if (xhr.status == 403) {
				window.location.href = req.getResponseHeader("Location");
				window.sessionStorage.removeItem('user');
			} else {
				const errorDiv = document.getElementById('id_alert'); 
				const errorMessage = parseErrorMessageFromResponse(xhr.responseText);
				errorDiv.textContent = `Error ${xhr.status}: ${errorMessage}`; 
				errorDiv.style.display = 'block';
			}
		}
	};

	if (method === "POST" && data) {
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		const urlEncodedDataPairs = [];

		data.forEach((value, key) => {
			urlEncodedDataPairs.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
		});

		const urlEncodedData = urlEncodedDataPairs.join("&");
		xhr.send(urlEncodedData);
	} else {
		xhr.send();
	}
}


function parseErrorMessageFromResponse(responseText) {
	const match = responseText.match(/<b>Message<\/b>\s*(.*?)<\/p>/);
	if (match && match[1]) {
		return match[1];
	}
	return 'An error occurred. Please try again later.';
}

/**
 * Submit a form using Ajax POST request.
 * @param {HTMLFormElement} formElement - The form element to submit.
 * @param {string} url - The URL to send the request to.
 * @param {function} callback - Callback function to handle the response.
 */
export function submitFormWithAjax(formElement, url, callback) {
	const formData = new FormData(formElement);
	sendAjaxRequest("POST", url, formData, callback);
}

/**
 * Send a GET request with optional parameters.
 * @param {string} url - The URL to send the request to.
 * @param {function} callback - Callback function to handle the response.
 * @param {object|null} params - Query parameters for the GET request.
 */
export function sendGetRequest(url, callback, params = null) {
	sendAjaxRequest("GET", url, params, callback);
}

export function getSession() {
	const user = sessionStorage.getItem("user");
	return JSON.parse(user)
}

