// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author alanic
 */
@Entity
@Table(name = "vehicle")
@XmlRootElement
@NamedQueries({})
public class Vehicle implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "vehicleid")
	private Integer vehicleid;
	@Column(name = "vehicledesc")
	private String vehicledesc;
	@Column(name = "status")
	private Integer status;
	@Column(name = "unitid")
	private String unitid;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicleid", fetch = FetchType.LAZY)
	private List<Positions> positionsList;
	@JoinColumn(name = "transporterid", referencedColumnName = "transporterid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Transporter transporterid;
	@Column(name = "lastreceived")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastreceived;
	/**
	 * @JsonIgnore
	 * @OneToMany(mappedBy = "vehicleid", fetch = FetchType.LAZY) private
	 *                     List<Lastinformations> lastinformationsList;
	 */
	// vehicleid as vclid,vehicledesc as vclenm,transporterid as trpid, unitid as
	// unid
	@OneToOne(cascade = { CascadeType.ALL }, mappedBy = "truck", fetch = FetchType.LAZY)
	private Lastinformations lastposition;

	public Vehicle() {
	}

	public Lastinformations getLastposition() {
		return lastposition;
	}

	public void setLastposition(Lastinformations lastposition) {
		this.lastposition = lastposition;
	}

	public Date getLastreceived() {
		return lastreceived;
	}

	public void setLastreceived(Date lastreceived) {
		this.lastreceived = lastreceived;
	}

	public Integer getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(Integer vehicleid) {
		this.vehicleid = vehicleid;
	}

	public String getVehicledesc() {
		return vehicledesc;
	}

	public void setVehicledesc(String vehicledesc) {
		this.vehicledesc = vehicledesc;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUnitid() {
		return unitid;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	public List<Positions> getPositionsList() {
		return positionsList;
	}

	public void setPositionsList(List<Positions> positionsList) {
		this.positionsList = positionsList;
	}

	public Transporter getTransporterid() {
		return transporterid;
	}

	public void setTransporterid(Transporter transporterid) {
		this.transporterid = transporterid;
	}

}
