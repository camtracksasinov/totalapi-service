// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;

public class Camion implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer trpid;
	private String vehid;
	private String mtr;
	private String dt;
	private String odo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTrpid() {
		return trpid;
	}

	public void setTrpid(Integer trpid) {
		this.trpid = trpid;
	}

	public String getVehid() {
		return vehid;
	}

	public void setVehid(String vehid) {
		this.vehid = vehid;
	}

	public String getMtr() {
		return mtr;
	}

	public void setMtr(String mtr) {
		this.mtr = mtr;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getOdo() {
		return odo;
	}

	public void setOdo(String odo) {
		this.odo = odo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
