import { Studente } from './studente.js'
import { sendGetRequest, submitFormWithAjax } from './utils.js';
const studente = new Studente();
const corsoSelect = document.getElementById("corso");
const appelloSelect = document.getElementById("appelloSelect");
const selectedAppelloSection = document.getElementById("selectedAppelloSection");
const errorDiv = document.getElementById('id_alert');


studente.load((listaCorsi) => {
	listaCorsi.forEach(corso => {
		const option = document.createElement("option");
		option.value = corso.id; 
		option.text = corso.nome; 
		corsoSelect.appendChild(option);
	});

	fetchAndSetAppelli(studente.listaCorsi[0].id)
})


corsoSelect.addEventListener('change', (e) => {
	const target = e.target
	fetchAndSetAppelli(target.value)
})

appelloSelect.addEventListener('change', () => {
	setSelectedAppelloSection()
})

function fetchAndSetAppelli(id) {
	studente.getAppelliByCorso(id, (listaAppelli) => {
		resetElement(appelloSelect)
		const placeholderOption = document.createElement("option");
		placeholderOption.value = "";
		placeholderOption.text = "Please select one";
		placeholderOption.disabled = true;
		placeholderOption.selected = true;
		appelloSelect.appendChild(placeholderOption);

		listaAppelli.forEach(appello => {
			errorDiv.style.display = 'none';

			const option = document.createElement("option");
			option.value = appello.id;
			option.text = appello.date
			appelloSelect.appendChild(option)
		})
		errorDiv.style.display = 'none';

		setSelectedAppelloSection()
	})

}

function resetElement(element) {
	element.innerHTML = ""
}

function setSelectedAppelloSection() {
	const selectedOption = appelloSelect.options[appelloSelect.selectedIndex];
	if (selectedOption.value === "") {
		selectedAppelloSection.textContent = "Seleziona un corso e un appello";
		errorDiv.style.display = 'none';

	} else {
		errorDiv.style.display = 'none';
		selectedAppelloSection.textContent = ``;
		const endpointUrl = `DettaglioAppello?selectedAppelloId=${selectedOption.value}`;
		sendGetRequest(endpointUrl, function(response) {
			const defineds = ['pubblicato', 'verbalizzato', 'rifiutato']
			const isVotoDefined = defineds.includes(response.statoValutazione)

			if (isVotoDefined) {

				import('./components/SingleStudentTable.js')
					.then(module => {
						const studentTable = module.SingleStudentTable(response);
						const existingTable = selectedAppelloSection.querySelector('#studentTable');
						if (existingTable) {
							selectedAppelloSection.replaceChild(studentTable, existingTable);
						} else {
							selectedAppelloSection.appendChild(studentTable);
						}

						const shouldShowRifiutatoMessage = response.statoValutazione === 'rifiutato';

						if (shouldShowRifiutatoMessage) {
							const rifiutatoMessage = document.createElement('p');
							rifiutatoMessage.textContent = "Il voto e' stato rifiutato.";
							selectedAppelloSection.appendChild(rifiutatoMessage);
						} else {
							const shouldShowRifiutaButton =
								response.statoValutazione === 'pubblicato' &&
								response.voto !== null &&
								!', ,assente,rimandato,riprovato,rifiutato'.includes(response.voto);

							if (shouldShowRifiutaButton) {
								const button = document.createElement("button");
								button.innerText = "Rifiuta";
								button.addEventListener('click', () => {
									const form = document.createElement("form")

									const appelloIdInput = document.createElement("input");
									appelloIdInput.setAttribute("type", "hidden");
									appelloIdInput.setAttribute("name", "selectedAppelloId");
									appelloIdInput.setAttribute("value", selectedOption.value);
									form.appendChild(appelloIdInput);

									const idstudentInput = document.createElement("input");
									idstudentInput.setAttribute("type", "hidden");
									idstudentInput.setAttribute("name", "idstudente");
									idstudentInput.setAttribute("value", response.idstudente);
									form.appendChild(idstudentInput);
									resetElement(selectedAppelloSection);

									submitFormWithAjax(form, "RifiutaVoto", (response) => {
										errorDiv.style.display = 'none';

										const singleTable = module.SingleStudentTable(response);
										selectedAppelloSection.appendChild(singleTable);

										const rifiutatoMessage = document.createElement('p');
										rifiutatoMessage.textContent = "Il voto e' stato rifiutato.";
										selectedAppelloSection.appendChild(rifiutatoMessage);
									})
								});
								selectedAppelloSection.appendChild(button);
							}
						}
					})

					.catch(error => {
						console.error('Error importing table module:', error);
					});



			} else {
				const messageElement = document.createElement('p');
				messageElement.textContent = 'Voto non ancora definito.';
				selectedAppelloSection.appendChild(messageElement);
			}
		});
	}


}
