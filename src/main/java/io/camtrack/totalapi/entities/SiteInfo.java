package io.camtrack.totalapi.entities;

import java.io.Serializable;

public class SiteInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	// Nom du site ou transporteur
    private String siteName;

    // Identifiant du site
    private String siteId;

    // Identifiant de la filiale
    private String groupId;

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
    
    
}
