package io.camtrack.totalapi.entities;

import java.io.Serializable;
import java.util.Date;

public class EventSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	// Clé de date au format AAAAMMJJ
	private Date dateKey;

	// Identifiant de l'affiliate
	private String groupId;

	// Identifiant du site ou transporteur
	private String siteId;

	// Identifiant unique du véhicule
	private String assetId;

	// Identifiant unique du conducteur
	private String driverId;

	// Nombre total d’occurrences de l’événement
	private Integer totalEventOccurrences;

	// Identifiant du type d’événement
	private String eventTypeId;

	// Valeur minimale enregistrée pour l’événement
	private Double minEventValue;

	// Valeur maximale enregistrée
	private Double maxEventValue;

	// Somme des valeurs de l’événement
	private Double totalEventValue;

	// Durée minimale (secondes)
	private Integer minEventDuration;

	// Durée maximale (secondes)
	private Integer maxEventDuration;

	// Durée totale cumulée (secondes)
	private Integer totalEventDuration;

	// ---- Getters & Setters ----

	public Date getDateKey() {
		return dateKey;
	}

	public void setDateKey(Date dateKey) {
		this.dateKey = dateKey;
	}

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

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public Integer getTotalEventOccurrences() {
		return totalEventOccurrences;
	}

	public void setTotalEventOccurrences(Integer totalEventOccurrences) {
		this.totalEventOccurrences = totalEventOccurrences;
	}

	public String getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(String eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public Double getMinEventValue() {
		return minEventValue;
	}

	public void setMinEventValue(Double minEventValue) {
		this.minEventValue = minEventValue;
	}

	public Double getMaxEventValue() {
		return maxEventValue;
	}

	public void setMaxEventValue(Double maxEventValue) {
		this.maxEventValue = maxEventValue;
	}

	public Double getTotalEventValue() {
		return totalEventValue;
	}

	public void setTotalEventValue(Double totalEventValue) {
		this.totalEventValue = totalEventValue;
	}

	public Integer getMinEventDuration() {
		return minEventDuration;
	}

	public void setMinEventDuration(Integer minEventDuration) {
		this.minEventDuration = minEventDuration;
	}

	public Integer getMaxEventDuration() {
		return maxEventDuration;
	}

	public void setMaxEventDuration(Integer maxEventDuration) {
		this.maxEventDuration = maxEventDuration;
	}

	public Integer getTotalEventDuration() {
		return totalEventDuration;
	}

	public void setTotalEventDuration(Integer totalEventDuration) {
		this.totalEventDuration = totalEventDuration;
	}
}
