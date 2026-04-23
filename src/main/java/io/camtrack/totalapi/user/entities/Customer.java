package io.camtrack.totalapi.user.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "customer")
@XmlRootElement
@NamedQueries({})
public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "customerid")
	private Integer customerid;
	@Basic(optional = false)
	@Column(name = "name")
	private String name;
	@OneToMany(mappedBy = "clid", fetch = FetchType.LAZY)
	private List<Apiaccess> apiaccessList;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "customerid", fetch = FetchType.LAZY)
	private List<Customeraffiliate> customeraffiliateList;
	@JsonIgnore
	@OneToMany(mappedBy = "customid", fetch = FetchType.LAZY)
	private List<Lastinformations> lastinformationsList;
	@JsonIgnore
	@OneToMany(mappedBy = "customid", fetch = FetchType.LAZY)
	private List<Positions> positionsList;

	public Customer() {
	}

	public List<Customeraffiliate> getCustomeraffiliateList() {
		return customeraffiliateList;
	}

	public void setCustomeraffiliateList(List<Customeraffiliate> customeraffiliateList) {
		this.customeraffiliateList = customeraffiliateList;
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

	public Integer getCustomerid() {
		return customerid;
	}

	public void setCustomerid(Integer customerid) {
		this.customerid = customerid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Apiaccess> getApiaccessList() {
		return apiaccessList;
	}

	public void setApiaccessList(List<Apiaccess> apiaccessList) {
		this.apiaccessList = apiaccessList;
	}

}
