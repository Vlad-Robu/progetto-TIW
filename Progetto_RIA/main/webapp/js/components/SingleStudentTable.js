export function SingleStudentTable(data) {
	const studentTable = document.createElement('table');
	studentTable.id = 'studentTable'; 
	const thead = document.createElement('thead');
	const tbody = document.createElement('tbody');

	const headerRow = document.createElement('tr');
	const headers = ['Matricola', 'Cognome', 'Nome', 'Email', 'Corso di Laurea', 'Voto', 'Stato di Valutazione', 'Data', 'Nome corso'];
	headers.forEach(headerText => {
		const th = document.createElement('th');
		
		th.textContent = headerText;
		const sortIcon = document.createElement('span');
		sortIcon.classList.add('th-sort-icon');
		th.appendChild(sortIcon);
		headerRow.appendChild(th);
	});
	thead.appendChild(headerRow);

		const row = document.createElement('tr');
		row.innerHTML = `
        <td>${data.matricola}</td>
        <td>${data.cognome}</td>
        <td>${data.nome}</td>
        <td>${data.email}</td>
        <td>${data.corsoDiLaurea}</td>
        <td>${data.voto}</td>
        <td>${data.statoValutazione}</td>
        <td>${data.appelloBean.date}</td>
        <td>${data.appelloBean.courseName}</td>

        `;
		tbody.appendChild(row);


	studentTable.appendChild(thead);
	studentTable.appendChild(tbody);
	return studentTable;
}