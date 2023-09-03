export function StudentsVerbaleTable(data) {
	const studentTable = document.createElement('table');
	studentTable.id = 'studentTable';
	const thead = document.createElement('thead');
	const tbody = document.createElement('tbody');

	const headerRow = document.createElement('tr');
	const headers = ['Matricola', 'Cognome', 'Nome', 'Email', 'Corso di Laurea', 'Voto'];
	headers.forEach(headerText => {
		const th = document.createElement('th');
		
		th.textContent = headerText;
		const sortIcon = document.createElement('span');
		sortIcon.classList.add('th-sort-icon');
		th.appendChild(sortIcon);
		headerRow.appendChild(th);
	});
	thead.appendChild(headerRow);

	data.datiStud.forEach(student => {
		const row = document.createElement('tr');
		row.innerHTML = `
            <td>${student.matricola}</td>
            <td>${student.cognome}</td>
            <td>${student.nome}</td>
            <td>${student.email}</td>
            <td>${student.corsoDiLaurea}</td>
            <td>${student.voto}</td>
        `;
		tbody.appendChild(row);
	});

	studentTable.appendChild(thead);
	studentTable.appendChild(tbody);
	return studentTable;
}