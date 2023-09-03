import { getSession, sendGetRequest } from './utils.js'
export class Studente {
	constructor() {
		this.user = getSession()
	}

	load(callaback) {
		sendGetRequest('GoToHome', (listaCorsi) => {
			this.listaCorsi = listaCorsi
			callaback(listaCorsi)
		})
	}

	getAppelliByCorso(corso, callback) {
		const setListaAppelliAndCallCallback = (listaAppelli) => {
			this.listaAppelli = listaAppelli
			callback(listaAppelli)
		}
		
		sendGetRequest('SelezionaAppello', setListaAppelliAndCallCallback , { corso })
	}
}