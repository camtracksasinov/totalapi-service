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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
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
@Table(name = "transporter")
@XmlRootElement
@NamedQueries({})
public class Transporter implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "transporterid")
	private Integer transporterid;
	@Column(name = "name")
	private String name;
	@Column(name = "countryid")
	private Integer countryid;
	@Column(name = "status")
	private Integer status;
	@Column(name = "transportuniqueid")
	private String transportuniqueid;
	@Column(name = "createdon")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdon;
	@JsonIgnore
	@OneToMany(mappedBy = "transporterid", fetch = FetchType.LAZY)
	private List<Vehicle> vehicleList;

	@JoinColumn(name = "affiliateid", referencedColumnName = "affiliateid")
	@ManyToOne(fetch = FetchType.LAZY)
	private Customeraffiliate affiliateid;
	@JsonIgnore
	@OneToMany(mappedBy = "trspid", fetch = FetchType.LAZY)
	private List<Lastinformations> lastinformationsList;
	@JsonIgnore
	@OneToMany(mappedBy = "trspid", fetch = FetchType.LAZY)
	private List<Positions> positionsList;

	public Transporter() {
	}

	public Customeraffiliate getAffiliateid() {
		return affiliateid;
	}

	public void setAffiliateid(Customeraffiliate affiliateid) {
		this.affiliateid = affiliateid;
	}

	public List<Lastinformations> getLastinformationsList() {
		return lastinformationsList;
	}

	public void setLastinformationsList(List<Lastinformations> lastinformationsList) {
		this.lastinformationsList = lastinformationsList;
	}

	public List<Positions> getPositionsList() {
		return positionsList;
	}

	public void setPositionsList(List<Positions> positionsList) {
		this.positionsList = positionsList;
	}

	public Integer getTransporterid() {
		return transporterid;
	}

	public void setTransporterid(Integer transporterid) {
		this.transporterid = transporterid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCountryid() {
		return countryid;
	}

	public void setCountryid(Integer countryid) {
		this.countryid = countryid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTransportuniqueid() {
		return transportuniqueid;
	}

	public void setTransportuniqueid(String transportuniqueid) {
		this.transportuniqueid = transportuniqueid;
	}

	public Date getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}

	public List<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(List<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

}
