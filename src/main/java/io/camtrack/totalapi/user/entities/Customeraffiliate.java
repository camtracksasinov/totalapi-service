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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author alanic
 */
@Entity
@Table(name = "customeraffiliate")
@XmlRootElement
@NamedQueries({})
public class Customeraffiliate implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "affiliateid")
	private Integer affiliateid;
	@Basic(optional = false)
	@Column(name = "name")
	private String name;
	@Column(name = "status")
	private Integer status;
	@JoinColumn(name = "customerid", referencedColumnName = "customerid")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Customer customerid;
	@JsonIgnore
	@OneToMany(mappedBy = "affiliateid", fetch = FetchType.LAZY)
	private List<Transporter> transporterList;
	@JsonIgnore
	@OneToMany(mappedBy = "aff", fetch = FetchType.LAZY)
	private List<Apiaccess> apiaccessList;
	@JsonIgnore
	@OneToMany(mappedBy = "affid", fetch = FetchType.LAZY)
	private List<Lastinformations> lastinformationsList;
	@JsonIgnore
	@OneToMany(mappedBy = "affid", fetch = FetchType.LAZY)
	private List<Positions> positionsList;

	public Customeraffiliate() {
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

	public Integer getAffiliateid() {
		return affiliateid;
	}

	public void setAffiliateid(Integer affiliateid) {
		this.affiliateid = affiliateid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Customer getCustomerid() {
		return customerid;
	}

	public void setCustomerid(Customer customerid) {
		this.customerid = customerid;
	}

	public List<Transporter> getTransporterList() {
		return transporterList;
	}

	public void setTransporterList(List<Transporter> transporterList) {
		this.transporterList = transporterList;
	}

	public List<Apiaccess> getApiaccessList() {
		return apiaccessList;
	}

	public void setApiaccessList(List<Apiaccess> apiaccessList) {
		this.apiaccessList = apiaccessList;
	}

}
