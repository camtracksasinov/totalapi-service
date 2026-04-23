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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alanic
 */
@Entity
@Table(name = "positions")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Positions.findAll", query = "SELECT p FROM Positions p"),
		@NamedQuery(name = "Positions.findById", query = "SELECT p FROM Positions p WHERE p.id = :id"),
		@NamedQuery(name = "Positions.findByLatitude", query = "SELECT p FROM Positions p WHERE p.latitude = :latitude"),
		@NamedQuery(name = "Positions.findByLongitude", query = "SELECT p FROM Positions p WHERE p.longitude = :longitude"),
		@NamedQuery(name = "Positions.findByEventtimestamp", query = "SELECT p FROM Positions p WHERE p.eventtimestamp = :eventtimestamp"),
		@NamedQuery(name = "Positions.findBySpeed", query = "SELECT p FROM Positions p WHERE p.speed = :speed"),
		@NamedQuery(name = "Positions.findByUtclastupdated", query = "SELECT p FROM Positions p WHERE p.utclastupdated = :utclastupdated") })
public class Positions implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Long id;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "longitude")
	private Double longitude;
	@Basic(optional = false)
	@Column(name = "eventtimestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventtimestamp;
	@Column(name = "speed")
	private Integer speed;
	@Column(name = "utclastupdated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date utclastupdated;
	@JoinColumn(name = "vehicleid", referencedColumnName = "vehicleid")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Vehicle vehicleid;
	@Column(name = "odometer")
	private Long odometer;

	@JoinColumn(name = "customid", referencedColumnName = "customerid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customid;
	@JoinColumn(name = "affid", referencedColumnName = "affiliateid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customeraffiliate affid;
	@JoinColumn(name = "trspid", referencedColumnName = "transporterid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Transporter trspid;

	public Positions() {
	}

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

	public Long getOdometer() {
		return odometer;
	}

	public void setOdometer(Long odometer) {
		this.odometer = odometer;
	}

	public Positions(Long id) {
		this.id = id;
	}

	public Positions(Long id, Date eventtimestamp) {
		this.id = id;
		this.eventtimestamp = eventtimestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getEventtimestamp() {
		return eventtimestamp;
	}

	public void setEventtimestamp(Date eventtimestamp) {
		this.eventtimestamp = eventtimestamp;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Date getUtclastupdated() {
		return utclastupdated;
	}

	public void setUtclastupdated(Date utclastupdated) {
		this.utclastupdated = utclastupdated;
	}

	public Vehicle getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(Vehicle vehicleid) {
		this.vehicleid = vehicleid;
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
		if (!(object instanceof Positions)) {
			return false;
		}
		Positions other = (Positions) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.camtrack.entities.Positions[ id=" + id + " ]";
	}

}
