// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class Success implements Serializable {
	private static final long serialVersionUID = 1L;
	private String status;
	private Integer code;

	public Success(final String status, final Integer code) {
		this.status = status;
		this.code = code;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setCode(final Integer code) {
		this.code = code;
	}
}
