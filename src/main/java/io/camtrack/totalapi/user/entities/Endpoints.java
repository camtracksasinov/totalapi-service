// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "endpoints")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Endpoints.findByDescription", query = "SELECT e FROM Endpoints e WHERE e.description = :description") })
public class Endpoints implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ids")
	private Integer ids;
	@Column(name = "description")
	private String description;
	@JsonIgnore
	@OneToMany(mappedBy = "endpointtid", fetch = FetchType.LAZY)
	private List<Endpointoken> endpointokenList;

	public Endpoints() {
	}

	public Endpoints(final Integer ids) {
		this.ids = ids;
	}

	public Integer getIds() {
		return this.ids;
	}

	public void setIds(final Integer ids) {
		this.ids = ids;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@XmlTransient
	public List<Endpointoken> getEndpointokenList() {
		return (List<Endpointoken>) this.endpointokenList;
	}

	public void setEndpointokenList(final List<Endpointoken> endpointokenList) {
		this.endpointokenList = endpointokenList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += ((this.ids != null) ? this.ids.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Endpoints)) {
			return false;
		}
		final Endpoints other = (Endpoints) object;
		return (this.ids != null || other.ids == null) && (this.ids == null || this.ids.equals(other.ids));
	}

	@Override
	public String toString() {
		return "com.mycompany.mavenproject3.Endpoints[ ids=" + this.ids + " ]";
	}
}
