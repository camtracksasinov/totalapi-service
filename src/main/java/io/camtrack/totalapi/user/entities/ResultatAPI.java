// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.List;

public class ResultatAPI implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<?> resultat;
	private Boolean hasmoreElements;
	private Integer size;

	public ResultatAPI(final List<?> resultat, final Boolean hasmoreElements, final Integer size) {
		this.hasmoreElements = false;
		this.resultat = resultat;
		this.hasmoreElements = hasmoreElements;
		this.size = size;
	}

	public List<?> getResultat() {
		return this.resultat;
	}

	public void setResultat(final List<?> resultat) {
		this.resultat = resultat;
	}

	public Boolean getHasmoreElements() {
		return this.hasmoreElements;
	}

	public void setHasmoreElements(final Boolean hasmoreElements) {
		this.hasmoreElements = hasmoreElements;
	}

	public Integer getSize() {
		return this.size;
	}

	public void setSize(final Integer size) {
		this.size = size;
	}
}
