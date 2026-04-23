package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class GroupInfo implements Serializable{
	 private static final long serialVersionUID = 1L;

	// Identifiant de la filiale
    private String groupId;

    // Nom du groupe
    private String name;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
