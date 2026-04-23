package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class EventInfo implements Serializable{

    private static final long serialVersionUID = 1L;

	// Nom de l’événement
    private String eventName;

    // Identifiant du type d’événement
    private String eventTypeId;

    // Identifiant de la filiale
    private String groupId;

    // Catégorie de l’événement (ex: True, False, Excused, etc.)
    private String classification;

    // Niveau de criticité
    private String criticity;

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(String eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getCriticity() {
		return criticity;
	}

	public void setCriticity(String criticity) {
		this.criticity = criticity;
	}
    
    
}

