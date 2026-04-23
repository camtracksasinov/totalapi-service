package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class DriverInfo implements Serializable{

    private static final long serialVersionUID = 1L;

	// Identifiant du transporter
    private String siteId;

    // Identifiant du conducteur
    private String driverId;

    // Nom du conducteur
    private String name;

    // Numéro d’employé
    private String employeeNumber;

    // Conducteur système (virtuel) ou conducteur réel
    private Boolean isSystemDriver;

    // Statut actif du conducteur
    private Boolean isActive;

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Boolean getIsSystemDriver() {
		return isSystemDriver;
	}

	public void setIsSystemDriver(Boolean isSystemDriver) {
		this.isSystemDriver = isSystemDriver;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
    
    
}

