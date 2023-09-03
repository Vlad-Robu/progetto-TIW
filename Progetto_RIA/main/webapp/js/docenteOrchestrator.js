import { Docente } from './docente.js'
import { sendGetRequest, submitFormWithAjax } from './utils.js';
import { StudentsVerbaleTable } from './components/StudentsVerbaleTable.js'
import { StudentsTable } from './components/StudentsTable.js'

const cssLink = document.createElement('link');
cssLink.rel = 'stylesheet';
cssLink.type = 'text/css';
cssLink.href = 'css/mystyle.css';
document.head.appendChild(cssLink);

const docente = new Docente();
const corsoSelect = document.getElementById("corso");
const appelloSelect = document.getElementById("appelloSelect");
const selectedAppelloSection = document.getElementById("selectedAppelloSection");
const buttonsContainer = document.getElementById("buttonsContainer");
const errorDiv = document.getElementById('id_alert');

docente.load((listaCorsi) => {
	listaCorsi.forEach(corso => {
		const option = document.createElement("option");
		option.value = corso.id;
		option.text = corso.nome;
		corsoSelect.appendChild(option);
	});

	fetchAndSetAppelli(docente.listaCorsi[0].id)
})


corsoSelect.addEventListener('change', (e) => {
	const target = e.target
	fetchAndSetAppelli(target.value)
})

appelloSelect.addEventListener('change', () => {
	setSelectedAppelloSection()
})

function fetchAndSetAppelli(id) {
	docente.getAppelliByCorso(id, (listaAppelli) => {
		resetElement(appelloSelect)
		const placeholderOption = document.createElement("option");
		placeholderOption.value = "";
		placeholderOption.text = "Please select one";
		placeholderOption.disabled = true;
		placeholderOption.selected = true;
		appelloSelect.appendChild(placeholderOption);

		listaAppelli.forEach(appello => {
			const option = document.createElement("option");
			option.value = appello.id;
			option.text = appello.date
			appelloSelect.appendChild(option)
		})
		buttonsContainer.style.display = "none";
		errorDiv.style.display = 'none';

		setSelectedAppelloSection()
	})

}

function resetElement(element) {
	element.innerHTML = ""
}

function modificaVoto(studentId) {
	buttonsContainer.style.display = 'none';
	errorDiv.style.display = 'none';

	const studentData = selectedAppelloTableData.find(student => student.idstudente === studentId);

	selectedAppelloSection.innerHTML = '';

	const studentInfoTableElement = document.createElement('table');
	studentInfoTableElement.classList.add('table-container');

	const thead = document.createElement('thead');
	const headerRow = document.createElement('tr');
	const headers = ['Matricola', 'Cognome', 'Nome', 'Email', 'Corso di Laurea', 'Voto', 'Stato di Valutazione'];

	headers.forEach(headerText => {
		const th = document.createElement('th');
		th.textContent = headerText;
		headerRow.appendChild(th);
	});

	thead.appendChild(headerRow);
	studentInfoTableElement.appendChild(thead);

	const tbody = document.createElement('tbody');

	const row = document.createElement('tr');
	row.innerHTML = `
    <td>${studentData.matricola}</td>
    <td>${studentData.cognome}</td>
    <td>${studentData.nome}</td>
    <td>${studentData.email}</td>
    <td>${studentData.corsoDiLaurea}</td>
    <td>${studentData.voto}</td>
    <td>${studentData.statoValutazione}</td>
  `;

	row.setAttribute('data-student-id', studentData.idstudente);

	tbody.appendChild(row);
	studentInfoTableElement.appendChild(tbody);

	selectedAppelloSection.appendChild(studentInfoTableElement);

	const select = document.createElement('select');
	const optionValues = [
		"assente", "rimandato", "riprovato",
		"18", "19", "20", "21", "22", "23", "24",
		"25", "26", "27", "28", "29", "30", "30 e lode"
	];

	const defaultOption = document.createElement('option');
	defaultOption.value = "";
	defaultOption.textContent = "Seleziona un voto";
	defaultOption.disabled = true;
	defaultOption.selected = true;
	select.appendChild(defaultOption);

	optionValues.forEach(optionValue => {
		const option = document.createElement('option');
		option.value = optionValue;
		option.textContent = optionValue;
		select.appendChild(option);
	});


	const saveButton = document.createElement('button');
	saveButton.textContent = 'SALVA';
	saveButton.addEventListener('click', () => {

		if (!select.value) {
			errorDiv.textContent = "Please select a value";
			errorDiv.style.display = 'block';
			return; 
		}

		const selectedOption = appelloSelect.options[appelloSelect.selectedIndex];
		const selectedVoto = select.value;
		const form = document.createElement("form");

		const appelloIdInput = document.createElement("input");
		appelloIdInput.setAttribute("type", "hidden");
		appelloIdInput.setAttribute("name", "selectedAppelloId");
		appelloIdInput.setAttribute("value", selectedOption.value);

		const studentIdInput = document.createElement("input");
		studentIdInput.setAttribute("type", "hidden");
		studentIdInput.setAttribute("name", "idstudente");
		studentIdInput.setAttribute("value", studentId);

		const votoInput = document.createElement("input");
		votoInput.setAttribute("type", "hidden");
		votoInput.setAttribute("name", "voto");
		votoInput.setAttribute("value", selectedVoto);

		form.appendChild(appelloIdInput);
		form.appendChild(studentIdInput);
		form.appendChild(votoInput);

		submitFormWithAjax(form, "ModificaVoto", (response) => {

			errorDiv.style.display = 'none';
			const updatedRow = document.querySelector(`[data-student-id="${studentId}"]`);

			const votoCell = updatedRow.querySelector('td:nth-child(6)'); 
			const statoValutazioneCell = updatedRow.querySelector('td:nth-child(7)'); 

			if (votoCell) {
				votoCell.textContent = selectedVoto;
			}

			if (statoValutazioneCell) {
				statoValutazioneCell.textContent = "inserito";
			}

		});

	});
	selectedAppelloSection.appendChild(select);
	selectedAppelloSection.appendChild(saveButton);
}

window.modificaVoto = modificaVoto;


const button = document.createElement("button");
button.innerText = "Verbalizza";

const pubblicaButton = document.createElement("button");
pubblicaButton.innerText = "Pubblica";

const inserimentoMultiploButton = document.createElement("button");
inserimentoMultiploButton.innerText = "Inserimento multiplo";

buttonsContainer.appendChild(pubblicaButton);
buttonsContainer.appendChild(button);
buttonsContainer.appendChild(inserimentoMultiploButton);

const modal = document.getElementById("myModal");
const inviaButton = document.getElementById("inviaButton");
const closeButton = document.getElementById("closeModalButton");
const modalTable = document.getElementById("modalTable");
let selectedAppelloTableData = [];

inviaButton.addEventListener('click', () => {
	const selectedOption = appelloSelect.options[appelloSelect.selectedIndex];
	const form = document.createElement("form");

	const selectedRows = modalTable.querySelectorAll('tbody tr');
	const studentDataMap = {}; 

	selectedRows.forEach(row => {
		const selectElement = row.querySelector('.voto-select');
		if (selectElement.value !== "") {
			const studentId = row.dataset.studentId; 
			studentDataMap[studentId] = selectElement.value; 
		}
	});

	const studentDataInput = document.createElement("input");
	studentDataInput.setAttribute("type", "hidden");
	studentDataInput.setAttribute("name", "studentDataMap");
	studentDataInput.setAttribute("value", JSON.stringify(studentDataMap));
	form.appendChild(studentDataInput);

	const appelloInput = document.createElement("input");
	appelloInput.setAttribute("type", "hidden");
	appelloInput.setAttribute("name", "selectedAppelloId");
	appelloInput.setAttribute("value", selectedOption.value);
	form.appendChild(appelloInput);

	form.style.display = "none";

	submitFormWithAjax(form, "InserimentoMultiplo", (response) => {
		errorDiv.style.display = 'none';
		modal.style.display = "none";
		setSelectedAppelloSection();
	});
});


inserimentoMultiploButton.addEventListener('click', () => {
	errorDiv.style.display = 'none';

	modal.style.display = "block";
	populateModalTable(selectedAppelloTableData); 
});

function populateModalTable(data) {
	modalTable.innerHTML = "";

	const modalTableElement = document.createElement('table');
	modalTableElement.classList.add('table-container');

	const thead = document.createElement('thead');
	const headerRow = document.createElement('tr');
	const headers = ['Matricola', 'Cognome', 'Nome', 'Email', 'Corso di Laurea', 'Stato di Valutazione', 'Seleziona Voto'];

	headers.forEach(headerText => {
		const th = document.createElement('th');
		th.textContent = headerText;
		headerRow.appendChild(th);
	});

	thead.appendChild(headerRow);
	modalTableElement.appendChild(thead);

	const tbody = document.createElement('tbody');

	const nonInseritoRows = data.filter(student => student.statoValutazione === "non inserito");

	nonInseritoRows.forEach(student => {
		const row = document.createElement('tr');
		row.dataset.studentId = student.idstudente; 
		row.innerHTML = `
      <td>${student.matricola}</td>
      <td>${student.cognome}</td>
      <td>${student.nome}</td>
      <td>${student.email}</td>
      <td>${student.corsoDiLaurea}</td>
      <td>${student.statoValutazione}</td>
      <td>
        <select class="voto-select">
          <option value="" disabled selected hidden>Seleziona</option>
          <option value="assente">assente</option>
          <option value="rimandato">rimandato</option>
          <option value="riprovato">riprovato</option>
          <option value="18">18</option>
          <option value="19">19</option>
          <option value="20">20</option>
          <option value="21">21</option>
          <option value="22">22</option>
          <option value="23">23</option>
          <option value="24">24</option>
          <option value="25">25</option>
          <option value="26">26</option>
          <option value="27">27</option>
          <option value="28">28</option>
          <option value="29">29</option>
          <option value="30">30</option>
          <option value="30 e lode">30 e lode</option>
        </select>
      </td>
    `;
		tbody.appendChild(row);
	});

	modalTableElement.appendChild(tbody);

	modalTable.appendChild(modalTableElement);
}




closeButton.addEventListener('click', () => {
	modal.style.display = "none"; 
});

pubblicaButton.addEventListener('click', () => {
	const selectedOption = appelloSelect.options[appelloSelect.selectedIndex];
	const form = document.createElement("form")
	const input = document.createElement("input")
	input.setAttribute("type", "text")
	input.setAttribute("name", "selectedAppelloId")
	input.setAttribute("value", selectedOption.value)
	form.appendChild(input)
	submitFormWithAjax(form, "PubblicaVoti", (response) => {
		resetElement(selectedAppelloSection)
		errorDiv.style.display = 'none';
		const studentTable = StudentsTable(response);
		updateTable(studentTable);
		addSorting(studentTable)
		setSelectedAppelloSection();


	});
});

button.addEventListener('click', () => {
	const selectedOption = appelloSelect.options[appelloSelect.selectedIndex]; 
	const form = document.createElement("form")
	const input = document.createElement("input")
	input.setAttribute("type", "text")
	input.setAttribute("name", "selectedAppelloId")
	input.setAttribute("value", selectedOption.value)
	form.appendChild(input)
	submitFormWithAjax(form, "VerbalizzaVoti", (response) => {
		resetElement(selectedAppelloSection)
		errorDiv.style.display = 'none';

		buttonsContainer.style.display = "none";

		const dateElement = document.createElement('p');
		dateElement.textContent = `Date: ${response.data_ora}`;

		const codeElement = document.createElement('p');
		codeElement.textContent = `Code: ${response.idverbale}`;

		const codeDateContainer = document.createElement('div');
		codeDateContainer.appendChild(codeElement);
		codeDateContainer.appendChild(dateElement);

		const studentVerbaleTable = StudentsVerbaleTable(response);
		generateTable(studentVerbaleTable, '#studentTable')

		selectedAppelloSection.insertBefore(codeDateContainer, selectedAppelloSection.firstChild);


	});
});



function setSelectedAppelloSection() {
	const selectedOption = appelloSelect.options[appelloSelect.selectedIndex];
	if (selectedOption.value === "") {
		selectedAppelloSection.textContent = "Seleziona un corso ed un appello";
	} else {
		buttonsContainer.style.display = "none";
		selectedAppelloSection.textContent = ``;
		const endpointUrl = `DettaglioAppello?selectedAppelloId=${selectedOption.value}`;
		sendGetRequest(endpointUrl, function(response) {
			import('./components/StudentsTable.js')
				.then(module => {

					const studentTable = module.StudentsTable(response);
					selectedAppelloTableData = response;
					generateTable(studentTable, '#studentTable')
					addSorting(studentTable)
					buttonsContainer.style.display = "block";

				})
				.catch(error => {
					console.error('Error importing table module:', error);
				});
		});
		errorDiv.style.display = 'none';


	}
}

function addSorting(table) {
	const thList = table.querySelectorAll('th');
	const isVotoHeader = (th) => th.innerText.includes("Voto")
	const filteredThList = Array.from(thList).filter(th => !isVotoHeader(th));
	const votoHeader = Array.from(thList).find(isVotoHeader);

	const votoOrder = {
		"": -1,
		"assente": 0,
		"rimandato": 1,
		"riprovato": 2,
		"18": 3,
		"19": 4,
		"20": 5,
		"21": 6,
		"22": 7,
		"23": 8,
		"24": 9,
		"25": 10,
		"26": 11,
		"27": 12,
		"28": 13,
		"29": 14,
		"30": 15,
		"30 e lode": 16
	};
	const rows = Array.from(table.querySelectorAll('tbody tr'));
	votoHeader.addEventListener('click', () => {
		const isAscending = votoHeader.classList.contains('ascending');
		rows.sort((a, b) => {

			const aValue = a.cells[5].textContent.trim();
			const bValue = b.cells[5].textContent.trim();
			return votoOrder[aValue] - votoOrder[bValue];
		});
		if (isAscending) {
			rows.reverse();
			votoHeader.classList.remove('ascending');
			votoHeader.classList.add('descending');
		} else {
			votoHeader.classList.remove('descending');
			votoHeader.classList.add('ascending');
		}

		thList.forEach(header => {
			if (header !== votoHeader) {
				header.classList.remove('ascending', 'descending');
			}
		});
		debugger
		rows.forEach(row => table.querySelector('tbody').appendChild(row));
	})

	filteredThList.forEach((th, index) => {
		th.addEventListener('click', () => {
			const isAscending = th.classList.contains('ascending');
			rows.sort((a, b) => {
				const aValue = a.cells[index].textContent.trim();
				const bValue = b.cells[index].textContent.trim();

				if (index === 0 || index === 5) {
					return +aValue - +bValue;
				}
				return aValue.localeCompare(bValue);
			});

			if (isAscending) {
				rows.reverse();
				th.classList.remove('ascending');
				th.classList.add('descending');
			} else {
				th.classList.remove('descending');
				th.classList.add('ascending');
			}

			thList.forEach(header => {
				if (header !== th) {
					header.classList.remove('ascending', 'descending');
				}
			});

			rows.forEach(row => table.querySelector('tbody').appendChild(row));
		});
	});
}


function generateTable(table, iddom) {
	const existingTable = selectedAppelloSection.querySelector(iddom);
	if (existingTable) {
		selectedAppelloSection.replaceChild(table, existingTable);

	} else {
		selectedAppelloSection.appendChild(table);
	}

}

function updateTable(newTable) {
	const existingTable = selectedAppelloSection.querySelector('#studentTable');
	if (existingTable) {
		selectedAppelloSection.replaceChild(newTable, existingTable);
	} else {
		selectedAppelloSection.appendChild(newTable);
	}
}

