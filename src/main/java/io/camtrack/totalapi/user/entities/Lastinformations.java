// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alanic
 */
@Entity
@Table(name = "lastinformations")
@XmlRootElement
@NamedQueries({})
public class Lastinformations implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Long id;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Column(name = "lastlatitude")
	private Double lastlatitude;
	@Column(name = "lastlongitude")
	private Double lastlongitude;
	@Column(name = "speed")
	private Integer speed;
	@Column(name = "isplacetransition")
	private Integer isplacetransition;
	@Column(name = "entryexit")
	private Integer entryexit;
	@Column(name = "lasttimestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lasttimestamp;
	@Column(name = "placeid")
	private BigInteger placeid;

	// lastlatitude as lt,lastlongitude as lg
	@JoinColumn(name = "vehicleid", referencedColumnName = "vehicleid", insertable = false, updatable = false)
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	private Vehicle truck;
	@Column(name = "lastodometer")
	private Long lastodometer;

	@JoinColumn(name = "customid", referencedColumnName = "customerid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customid;
	@JoinColumn(name = "affid", referencedColumnName = "affiliateid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customeraffiliate affid;
	@JoinColumn(name = "trspid", referencedColumnName = "transporterid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Transporter trspid;

	public Customer getCustomid() {
		return customid;
	}

	public void setCustomid(Customer customid) {
		this.customid = customid;
	}

	public Customeraffiliate getAffid() {
		return affid;
	}

	public void setAffid(Customeraffiliate affid) {
		this.affid = affid;
	}

	public Transporter getTrspid() {
		return trspid;
	}

	public void setTrspid(Transporter trspid) {
		this.trspid = trspid;
	}

	public Vehicle getTruck() {
		return truck;
	}

	public void setTruck(Vehicle truck) {
		this.truck = truck;
	}

	public Lastinformations() {
	}

	public Long getLastodometer() {
		return lastodometer;
	}

	public void setLastodometer(Long lastodometer) {
		this.lastodometer = lastodometer;
	}

	public Lastinformations(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLastlatitude() {
		return lastlatitude;
	}

	public void setLastlatitude(Double lastlatitude) {
		this.lastlatitude = lastlatitude;
	}

	public Double getLastlongitude() {
		return lastlongitude;
	}

	public void setLastlongitude(Double lastlongitude) {
		this.lastlongitude = lastlongitude;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Integer getIsplacetransition() {
		return isplacetransition;
	}

	public void setIsplacetransition(Integer isplacetransition) {
		this.isplacetransition = isplacetransition;
	}

	public Integer getEntryexit() {
		return entryexit;
	}

	public void setEntryexit(Integer entryexit) {
		this.entryexit = entryexit;
	}

	public Date getLasttimestamp() {
		return lasttimestamp;
	}

	public void setLasttimestamp(Date lasttimestamp) {
		this.lasttimestamp = lasttimestamp;
	}

	public BigInteger getPlaceid() {
		return placeid;
	}

	public void setPlaceid(BigInteger placeid) {
		this.placeid = placeid;
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
		if (!(object instanceof Lastinformations)) {
			return false;
		}
		Lastinformations other = (Lastinformations) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.camtrack.entities.Lastinformations[ id=" + id + " ]";
	}

}
