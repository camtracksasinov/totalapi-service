// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;

public class PositionCamion implements Serializable {
	private static final long serialVersionUID = 1L;
	private Double lat;
	private Double lon;
	private Integer spd;
	private String dt;
	private Integer id;
	private String mtr;
	private String odo;

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Integer getSpd() {
		return spd;
	}

	public void setSpd(Integer spd) {
		this.spd = spd;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMtr() {
		return mtr;
	}

	public void setMtr(String mtr) {
		this.mtr = mtr;
	}

	public String getOdo() {
		return odo;
	}

	public void setOdo(String odo) {
		this.odo = odo;
	}

}
