package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class AssetInfo implements Serializable{

    private static final long serialVersionUID = 1L;

	// Identifiant de l'affiliate
    private String groupId;

    // Identifiant du transporter
    private String siteId;

    // Identifiant du véhicule
    private String assetId;

    // Type du véhicule
    private String assetType;

    // Description du véhicule
    private String description;

    // Numéro VIN
    private String vinNumber;

    // Numéro de flotte
    private String fleetNumber;

    // Marque
    private String make;

    // Modèle
    private String model;

    // Type de carburant
    private String fuelType;

    // Pays d’immatriculation
    private String country;

    // Numéro d’immatriculation
    private String registrationNumber;

    // Disponibilité du véhicule
    private Boolean isAvailable;

    // Année de fabrication
    private Integer year;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public String getFleetNumber() {
		return fleetNumber;
	}

	public void setFleetNumber(String fleetNumber) {
		this.fleetNumber = fleetNumber;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
    
    
}
