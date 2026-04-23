package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class TripSummary implements Serializable{

	private static final long serialVersionUID = 1L;

	// Identifiant du site ou transporteur
	private String siteId;

	// Identifiant unique du véhicule
	private String assetId;

	// Identifiant unique du conducteur
	private String driverId;

	// Nombre total de trajets effectués
	private Integer totalTripCount;

	// Distance totale parcourue (km)
	private Double totalTripDistanceKilometres;

	// Carburant total consommé (litres)
	private Double totalFuelUsedLitres;

	// Énergie totale consommée (kWh)
	private Double totalEnergyUsedKwh;

	// Temps total passé au ralenti (secondes)
	private Integer totalIdleTimeSeconds;

	// Temps de conduite du véhicule (secondes)
	private Integer totalAssetDrivingTimeSeconds;

	// Temps de conduite du conducteur (secondes)
	private Integer totalDriverDrivingTimeSeconds;

	// Temps total de fonctionnement du moteur (secondes)
	private Integer totalEngineSecond;

	// Durée totale des trajets (secondes)
	private Integer totalTripDuration;

	// --- Getters & Setters ---

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

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public Integer getTotalTripCount() {
		return totalTripCount;
	}

	public void setTotalTripCount(Integer totalTripCount) {
		this.totalTripCount = totalTripCount;
	}

	public Double getTotalTripDistanceKilometres() {
		return totalTripDistanceKilometres;
	}

	public void setTotalTripDistanceKilometres(Double totalTripDistanceKilometres) {
		this.totalTripDistanceKilometres = totalTripDistanceKilometres;
	}

	public Double getTotalFuelUsedLitres() {
		return totalFuelUsedLitres;
	}

	public void setTotalFuelUsedLitres(Double totalFuelUsedLitres) {
		this.totalFuelUsedLitres = totalFuelUsedLitres;
	}

	public Double getTotalEnergyUsedKwh() {
		return totalEnergyUsedKwh;
	}

	public void setTotalEnergyUsedKwh(Double totalEnergyUsedKwh) {
		this.totalEnergyUsedKwh = totalEnergyUsedKwh;
	}

	public Integer getTotalIdleTimeSeconds() {
		return totalIdleTimeSeconds;
	}

	public void setTotalIdleTimeSeconds(Integer totalIdleTimeSeconds) {
		this.totalIdleTimeSeconds = totalIdleTimeSeconds;
	}

	public Integer getTotalAssetDrivingTimeSeconds() {
		return totalAssetDrivingTimeSeconds;
	}

	public void setTotalAssetDrivingTimeSeconds(Integer totalAssetDrivingTimeSeconds) {
		this.totalAssetDrivingTimeSeconds = totalAssetDrivingTimeSeconds;
	}

	public Integer getTotalDriverDrivingTimeSeconds() {
		return totalDriverDrivingTimeSeconds;
	}

	public void setTotalDriverDrivingTimeSeconds(Integer totalDriverDrivingTimeSeconds) {
		this.totalDriverDrivingTimeSeconds = totalDriverDrivingTimeSeconds;
	}

	public Integer getTotalEngineSecond() {
		return totalEngineSecond;
	}

	public void setTotalEngineSecond(Integer totalEngineSecond) {
		this.totalEngineSecond = totalEngineSecond;
	}

	public Integer getTotalTripDuration() {
		return totalTripDuration;
	}

	public void setTotalTripDuration(Integer totalTripDuration) {
		this.totalTripDuration = totalTripDuration;
	}
}
