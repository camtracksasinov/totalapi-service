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

/**
 *
 * @author alanic
 */
@Entity
@Table(name = "apiaccess")
@XmlRootElement
@NamedQueries({})
public class Apiaccess implements Serializable {

	private static final long serialVersionUID = 1L;
	@Column(name = "lastupdated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastupdated;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@JoinColumn(name = "apiaccountdid", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Apiaccount apiaccountdid;
	@JoinColumn(name = "clid", referencedColumnName = "customerid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer clid;
	@JoinColumn(name = "aff", referencedColumnName = "affiliateid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customeraffiliate aff;
	@JoinColumn(name = "trps", referencedColumnName = "transporterid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Transporter trps;

	public Apiaccess() {
	}

	public Apiaccess(Integer id) {
		this.id = id;
	}

	public Date getLastupdated() {
		return lastupdated;
	}

	public void setLastupdated(Date lastupdated) {
		this.lastupdated = lastupdated;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Apiaccount getApiaccountdid() {
		return apiaccountdid;
	}

	public void setApiaccountdid(Apiaccount apiaccountdid) {
		this.apiaccountdid = apiaccountdid;
	}

	public Customer getClid() {
		return clid;
	}

	public void setClid(Customer clid) {
		this.clid = clid;
	}

	public Customeraffiliate getAff() {
		return aff;
	}

	public void setAff(Customeraffiliate aff) {
		this.aff = aff;
	}

	public Transporter getTrps() {
		return trps;
	}

	public void setTrps(Transporter trps) {
		this.trps = trps;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Apiaccess)) {
			return false;
		}
		Apiaccess other = (Apiaccess) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.camtrack.entities.Apiaccess[ id=" + id + " ]";
	}

}
