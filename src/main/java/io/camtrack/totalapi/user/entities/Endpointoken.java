// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "endpointoken")
@XmlRootElement
@NamedQueries({})
public class Endpointoken implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ids")
	private Integer ids;
	@Column(name = "createddate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createddate;
	@Column(name = "updatedate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedate;
	@JoinColumn(name = "apiaccountdid", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Apiaccount apiaccountdid;
	@JoinColumn(name = "endpointtid", referencedColumnName = "ids")
	@ManyToOne(fetch = FetchType.LAZY)
	private Endpoints endpointtid;

	public Endpointoken() {
	}

	public Endpointoken(final Integer ids) {
		this.ids = ids;
	}

	public Integer getIds() {
		return this.ids;
	}

	public void setIds(final Integer ids) {
		this.ids = ids;
	}

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(final Date createddate) {
		this.createddate = createddate;
	}

	public Date getUpdatedate() {
		return this.updatedate;
	}

	public void setUpdatedate(final Date updatedate) {
		this.updatedate = updatedate;
	}

	public Apiaccount getApiaccountdid() {
		return this.apiaccountdid;
	}

	public void setApiaccountdid(final Apiaccount apiaccountdid) {
		this.apiaccountdid = apiaccountdid;
	}

	public Endpoints getEndpointtid() {
		return this.endpointtid;
	}

	public void setEndpointtid(final Endpoints endpointtid) {
		this.endpointtid = endpointtid;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += ((this.ids != null) ? this.ids.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Endpointoken)) {
			return false;
		}
		final Endpointoken other = (Endpointoken) object;
		return (this.ids != null || other.ids == null) && (this.ids == null || this.ids.equals(other.ids));
	}

	@Override
	public String toString() {
		return "com.mycompany.mavenproject3.Endpointoken[ ids=" + this.ids + " ]";
	}
}
