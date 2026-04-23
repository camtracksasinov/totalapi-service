// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "apiaccount")
@XmlRootElement
@NamedQueries({})
public class Apiaccount implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "description")
	private String description;
	@Column(name = "token")
	private String token;
	@Column(name = "validdate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validdate;
	@Column(name = "actives")
	private boolean actives;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@JsonIgnore
	@OneToMany(mappedBy = "apiaccountdid", fetch = FetchType.LAZY)
	private List<Endpointoken> endpointokenList;
	@JsonIgnore
	@OneToMany(mappedBy = "apiaccountdid", fetch = FetchType.LAZY)
	private List<Apiaccess> apiaccessList;

	public Apiaccount() {
	}

	public Date getValiddate() {
		return this.validdate;
	}

	public void setValiddate(final Date validdate) {
		this.validdate = validdate;
	}

	public Apiaccount(final Integer id) {
		this.id = id;
	}

	public boolean isActives() {
		return this.actives;
	}

	public void setActives(final boolean actives) {
		this.actives = actives;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@XmlTransient
	public List<Endpointoken> getEndpointokenList() {
		return (List<Endpointoken>) this.endpointokenList;
	}

	public void setEndpointokenList(final List<Endpointoken> endpointokenList) {
		this.endpointokenList = endpointokenList;
	}

	@XmlTransient
	public List<Apiaccess> getApiaccessList() {
		return (List<Apiaccess>) this.apiaccessList;
	}

	public void setApiaccessList(final List<Apiaccess> apiaccessList) {
		this.apiaccessList = apiaccessList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += ((this.id != null) ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Apiaccount)) {
			return false;
		}
		final Apiaccount other = (Apiaccount) object;
		return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
	}

	@Override
	public String toString() {
		return "com.mycompany.mavenproject3.Apiaccount[ id=" + this.id + " ]";
	}
}
