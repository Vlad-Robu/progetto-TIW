import { submitFormWithAjax } from './js/utils.js';

const submitLogin = (event) => {
	event.preventDefault();
	const formElement = event.target;
	const setUserAndRedirect = (user) => {
		sessionStorage.setItem("user", JSON.stringify(user));
		const baseUrl = window.location.href
		const path = user.role === 'docente' ? 'docente.html' : 'studente.html'
		window.location.href = baseUrl + path
	}

	try {
		submitFormWithAjax(formElement, "CheckLogin", setUserAndRedirect)
	} catch (error) {
		console.error("An error occurred:", error);
	}
}

document.getElementById("loginForm").addEventListener("submit", submitLogin);
