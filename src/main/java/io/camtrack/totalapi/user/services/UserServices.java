// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import io.camtrack.totalapi.entities.Success;
import io.camtrack.totalapi.user.entities.Apiaccess;
import io.camtrack.totalapi.user.entities.Apiaccount;
import io.camtrack.totalapi.user.entities.Camion;
import io.camtrack.totalapi.user.entities.Customer;
import io.camtrack.totalapi.user.entities.Customeraffiliate;
import io.camtrack.totalapi.user.entities.PositionCamion;
import io.camtrack.totalapi.user.entities.Positions;
import io.camtrack.totalapi.user.entities.ResultatAPI;
import io.camtrack.totalapi.user.entities.Transporter;
import io.camtrack.totalapi.user.entities.Vehicle;
import io.camtrack.totalapi.user.metiers.UserMetiers;
import io.camtrack.totalapi.user.repository.ApiaccessRepository;
import io.camtrack.totalapi.user.repository.ApiaccountRepository;
import io.camtrack.totalapi.user.repository.PositionRepository;
import io.camtrack.totalapi.user.repository.VehicleRepository;
import io.camtrack.totalapi.user.utils.UserUtils;
import io.camtrack.totalapi.utils.Utils;
import io.camtrack.totalapi.utils.UtilsString;

@Service
@Repository("userDao")
@PropertySource({ "classpath:sqlqueries.properties" })
public class UserServices implements UserMetiers {
	@Autowired
	ApiaccountRepository apiaccRepo;
	@Autowired
	PositionRepository posiRepo;
	@Autowired
	VehicleRepository truckRepo;
	@Autowired
	ApiaccessRepository apiaccessRepo;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Environment environment;

	public ResponseEntity<?> getVehicleLastPositions(final Apiaccount apiacc, Integer page) {
		if (page < 0) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("Page value is strictly  positive", UtilsString.Success_Page_Must_Positive));
		}
		final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}
		// final List<Transporter> listtransporter = apiaccess.getTrps();
		/*
		 * for (final Transporter transporter : listtransporter) { final List<Vehicle>
		 * listtruck = (List<Vehicle>)transporter.getVehicleList(); for (final Vehicle
		 * truck : listtruck) { try { Lastinformations lastposi =
		 * truck.getLastposition(); if (Objects.isNull(lastposi)) { continue; }
		 * lastPosition.add(Utils.positiontoCamion(lastposi, truck)); } catch (Exception
		 * ex) { ex.printStackTrace(); } } }
		 */

		// resultlastPosition =
		// lastPosition.stream().sorted(Comparator.comparing(PositionCamion::getMtr)).collect(Collectors.toList());
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "trspid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or trspid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affid in (" + affiliateid + ")";
			} else {
				SQL = SQL + " or affid in (" + affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customid in (" + cliendid + ")";
			} else {
				SQL = SQL + " or customid in (" + cliendid + ")";
			}
		}
		String completeRequest = environment.getRequiredProperty("lastposition.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			Integer sizes = size.intValue(); // = result.size();
			completeRequest = environment.getRequiredProperty("lastposition.select");
			SQL = SQL + " order by lasttimestamp limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);

			if (sizes <= minValue) {
				maxValue = sizes.intValue();
				minValue = sizes - UserUtils.MaxReturnLine;
				if (minValue < 0) {
					minValue = 0;
				}
			}
			if (sizes < maxValue) {
				maxValue = sizes;
			}
			boolean hasmoreElements = sizes > maxValue;
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
		} else {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new ResultatAPI(result, false, 0));
		}

	}

	public ResponseEntity<?> getSpecificVehicleLastPositions(final Apiaccount apiacc, String vcleid) {
		final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findByImmat(vcleid).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		Transporter trans2 = truck.getTransporterid();
		Integer tranid = trans2.getTransporterid();
		Customeraffiliate aff2 = trans2.getAffiliateid();
		Integer affid = aff2.getAffiliateid();
		Customer custom2 = aff2.getCustomerid();
		Integer cusid = custom2.getCustomerid();
		Transporter trans;
		Customeraffiliate aff;
		Customer custom;
		boolean trouve = false;
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		Apiaccess apiaccess;
		Integer size = listapiAccess.size();
		Integer k = 0;
		while ((!trouve) && (k < size)) {
			apiaccess = listapiAccess.get(k);
			trans = apiaccess.getTrps();
			aff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans) && trans.getTransporterid() == tranid) {
				trouve = true;
			} else if (!Objects.isNull(aff) && aff.getAffiliateid() == affid) {
				trouve = true;
			} else if (!Objects.isNull(custom) && custom.getCustomerid() == cusid) {
				trouve = true;
			}
			k++;
		}

		if (!trouve) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle));
		}

		String completeRequest = environment.getRequiredProperty("lastposition.selectonevehicle") + ""
				+ truck.getVehicleid();
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(this.jdbcTemplate.queryForList(completeRequest), false, 1));
	}

	public ResponseEntity<?> getSpecificVehicleLastPositionsById(final Apiaccount apiacc, Integer vcleid) {
		final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vcleid).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		Transporter trans2 = truck.getTransporterid();
		Integer tranid = trans2.getTransporterid();
		Customeraffiliate aff2 = trans2.getAffiliateid();
		Integer affid = aff2.getAffiliateid();
		Customer custom2 = aff2.getCustomerid();
		Integer cusid = custom2.getCustomerid();
		Transporter trans;
		Customeraffiliate aff;
		Customer custom;
		boolean trouve = false;
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		Apiaccess apiaccess;
		Integer size = listapiAccess.size();
		Integer k = 0;
		while ((!trouve) && (k < size)) {
			apiaccess = listapiAccess.get(k);
			trans = apiaccess.getTrps();
			aff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans) && trans.getTransporterid() == tranid) {
				trouve = true;
			} else if (!Objects.isNull(aff) && aff.getAffiliateid() == affid) {
				trouve = true;
			} else if (!Objects.isNull(custom) && custom.getCustomerid() == cusid) {
				trouve = true;
			}
			k++;
		}

		if (!trouve) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle));
		}
		String completeRequest = environment.getRequiredProperty("lastposition.selectonevehicle") + "" + vcleid;
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(this.jdbcTemplate.queryForList(completeRequest), false, 1));
	}

	/**
	 * public ResponseEntity<?> getSpecificVehicleLastPositionsById(final Apiaccount
	 * apiacc, final Long id) { final List<PositionCamion> lastPosition = new
	 * ArrayList<PositionCamion>(); if (Objects.isNull(apiacc)) { return
	 * (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
	 * Success("No active key", UtilsString.Success_No_Active_Key)); } final Vehicle
	 * truck = this.truckRepo.findById(id).orElse(null); if (Objects.isNull(truck))
	 * { return
	 * (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
	 * Success("This vehicle doesn't exists",
	 * UtilsString.Success_VehicleName_Not_Exists)); } final Platformaccount
	 * platform = truck.getTransporterid().getPlatformaccountid(); final Apiaccess
	 * apiacess = this.apiaccessRepo.findByPlatformAndApiaccount(apiacc,
	 * platform).orElse(null); if (Objects.isNull(apiacess)) { return
	 * (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
	 * Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle)); }
	 * lastPosition.add(Utils.positiontoCamion(truck.getLastposition(), truck));
	 * return
	 * (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
	 * ResultatAPI(lastPosition, false, lastPosition.size())); }
	 */

	public ResponseEntity<?> getAllVehicleLastPositions(Apiaccount apiacc) {
		final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}
		// final List<Transporter> listtransporter = apiaccess.getTrps();
		/*
		 * for (final Transporter transporter : listtransporter) { final List<Vehicle>
		 * listtruck = (List<Vehicle>)transporter.getVehicleList(); for (final Vehicle
		 * truck : listtruck) { try { Lastinformations lastposi =
		 * truck.getLastposition(); if (Objects.isNull(lastposi)) { continue; }
		 * lastPosition.add(Utils.positiontoCamion(lastposi, truck)); } catch (Exception
		 * ex) { ex.printStackTrace(); } } }
		 */

		// resultlastPosition =
		// lastPosition.stream().sorted(Comparator.comparing(PositionCamion::getMtr)).collect(Collectors.toList());
		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "trspid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or trspid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affid in (" + affiliateid + ")";
			} else {
				SQL = SQL + " or affid in (" + affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customid in (" + cliendid + ")";
			} else {
				SQL = SQL + " or customid in (" + cliendid + ")";
			}
		}
		String completeRequest = environment.getRequiredProperty("lastposition.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			Integer sizes = size.intValue(); // = result.size();
			completeRequest = environment.getRequiredProperty("lastposition.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);

			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new ResultatAPI(result, false, sizes));
		} else {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new ResultatAPI(result, false, 0));
		}

	}

	public Apiaccount activeAccount(final String token, final Date date) {
		return this.apiaccRepo.findByActiveToken(token, date).orElse(null);
	}

	public ResponseEntity<?> getListVehicle(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (" + affiliateid
						+ "))";
			} else {
				SQL = SQL + " or transporterid in (select transporterid from transporter where affiliateid in ("
						+ affiliateid + "))";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			} else {
				SQL = SQL
						+ " or transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("vehicle.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	public ResponseEntity<?> getListVehiclePerPage(final Apiaccount apiacc, final Integer page) {

		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (" + affiliateid
						+ "))";
			} else {
				SQL = SQL + " or transporterid in (select transporterid from transporter where affiliateid in ("
						+ affiliateid + "))";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			} else {
				SQL = SQL
						+ " or transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("vehicle.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("vehicle.select");
			SQL = SQL + " order by vehicleid limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("vehicle.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}
	
	
	
	
	
	public ResponseEntity<?> getListTransporter(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("transporter.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	public ResponseEntity<?> getListTransporterPerPage(final Apiaccount apiacc, final Integer page) {

		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("transporter.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("vehicle.select");
			SQL = SQL + " order by transporterid limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("transporter.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}
	
	
	
	public ResponseEntity<?> getListAffiliate(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customerid in ("
						+ cliendid + ")";
			} else {
				SQL = SQL
						+ " or customerid in ("
						+ cliendid + ")";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("affiliate.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	public ResponseEntity<?> getListAffiliatePerPage(final Apiaccount apiacc, final Integer page) {

		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customerid in ("
						+ cliendid + ")";
			} else {
				SQL = SQL
						+ " or customerid in ("
						+ cliendid + ")";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("affiliate.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("vehicle.select");
			SQL = SQL + " order by affiliate limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("affiliate.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}

	public ResponseEntity<?> getVehicleById(final Apiaccount apiacc, final Integer vehicleId) {
		final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vehicleId).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		Transporter trans2 = truck.getTransporterid();
		Integer tranid = trans2.getTransporterid();
		Customeraffiliate aff2 = trans2.getAffiliateid();
		Integer affid = aff2.getAffiliateid();
		Customer custom2 = aff2.getCustomerid();
		Integer cusid = custom2.getCustomerid();
		Transporter trans;
		Customeraffiliate aff;
		Customer custom;
		boolean trouve = false;
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		Apiaccess apiaccess;
		Integer size = listapiAccess.size();
		Integer k = 0;
		while ((!trouve) && (k < size)) {
			apiaccess = listapiAccess.get(k);
			trans = apiaccess.getTrps();
			aff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans) && trans.getTransporterid() == tranid) {
				trouve = true;
			} else if (!Objects.isNull(aff) && aff.getAffiliateid() == affid) {
				trouve = true;
			} else if (!Objects.isNull(custom) && custom.getCustomerid() == cusid) {
				trouve = true;
			}
			k++;
		}

		if (!trouve) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle));
		}

		String completeRequest = environment.getRequiredProperty("vehicle.selectonevehicle") + "" + vehicleId;
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(this.jdbcTemplate.queryForList(completeRequest), false, 1));
	}

	public ResponseEntity<?> getTrajetVehicleByIdWithPage(final Apiaccount apiacc, final Integer vehicleId,
			final Integer page) {
		if (page < 0) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("Page value is strictly  positive", UtilsString.Success_Page_Must_Positive));
		}
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		// List<Positions> resultPosition = new ArrayList<Positions>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vehicleId).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "trspid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or trspid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affid in (" + affiliateid + ")";
			} else {
				SQL = SQL + " or affid in (" + affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customid in (" + cliendid + ")";
			} else {
				SQL = SQL + " or customid in (" + cliendid + ")";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("position.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("position.select");
			SQL = SQL + " order by vehicleid limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("position.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}

	public ResponseEntity<?> getTrajetVehicleById(final Apiaccount apiacc, final Integer vehicleId) {
		final List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		List<Positions> resultPosition = new ArrayList<Positions>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vehicleId).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		Transporter trans2 = truck.getTransporterid();
		Integer tranid = trans2.getTransporterid();
		Customeraffiliate aff2 = trans2.getAffiliateid();
		Integer affid = aff2.getAffiliateid();
		Customer custom2 = aff2.getCustomerid();
		Integer cusid = custom2.getCustomerid();
		Transporter trans;
		Customeraffiliate aff;
		Customer custom;
		boolean trouve = false;
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		Apiaccess apiaccess;
		Integer size = listapiAccess.size();
		Integer k = 0;
		while ((!trouve) && (k < size)) {
			apiaccess = listapiAccess.get(k);
			trans = apiaccess.getTrps();
			aff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans) && trans.getTransporterid() == tranid) {
				trouve = true;
			} else if (!Objects.isNull(aff) && aff.getAffiliateid() == affid) {
				trouve = true;
			} else if (!Objects.isNull(custom) && custom.getCustomerid() == cusid) {
				trouve = true;
			}
			k++;
		}

		if (!trouve) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle));
		}
		String completeRequest = environment.getRequiredProperty("position.selectonecountvehicle") + "" + vehicleId;
		Long sizes = jdbcTemplate.queryForObject(completeRequest, Long.class);
		size = sizes.intValue();
		completeRequest = environment.getRequiredProperty("position.selectonevehicle") + "" + vehicleId;

		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(this.jdbcTemplate.queryForList(completeRequest), false, size));
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(resultlastPosition, false, resultlastPosition.size()));
	}

	public ResponseEntity<?> getTrajetVehicleByIdDateRange(final Apiaccount apiacc, final Integer vehicleId,
			final String date1, final String date2) {
		final List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		List<Positions> resultPosition = new ArrayList<Positions>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vehicleId).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		Transporter trans2 = truck.getTransporterid();
		Integer tranid = trans2.getTransporterid();
		Customeraffiliate aff2 = trans2.getAffiliateid();
		Integer affid = aff2.getAffiliateid();
		Customer custom2 = aff2.getCustomerid();
		Integer cusid = custom2.getCustomerid();
		Transporter trans;
		Customeraffiliate aff;
		Customer custom;
		boolean trouve = false;
		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		Apiaccess apiaccess;
		Integer size = listapiAccess.size();
		Integer k = 0;
		while ((!trouve) && (k < size)) {
			apiaccess = listapiAccess.get(k);
			trans = apiaccess.getTrps();
			aff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans) && trans.getTransporterid() == tranid) {
				trouve = true;
			} else if (!Objects.isNull(aff) && aff.getAffiliateid() == affid) {
				trouve = true;
			} else if (!Objects.isNull(custom) && custom.getCustomerid() == cusid) {
				trouve = true;
			}
			k++;
		}

		if (!trouve) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No rith on this vehicle", UtilsString.Success_Not_Right_Vehicle));
		}

		String completeRequest = environment.getRequiredProperty("position.selectonecountvehicle") + "" + vehicleId
				+ " and  eventtimestamp >= '" + date1 + "' and eventtimestamp <='" + date2 + "'";
		Long sizes = jdbcTemplate.queryForObject(completeRequest, Long.class);
		size = sizes.intValue();

		completeRequest = environment.getRequiredProperty("position.selectonevehicle") + " " + vehicleId
				+ " and  eventtimestamp >= '" + date1 + "' and eventtimestamp <='" + date2 + "'";
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(this.jdbcTemplate.queryForList(completeRequest), false, size));
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(resultlastPosition, false, resultlastPosition.size()));
	}

	public ResponseEntity<?> getTrajetVehicleByIdWithPageDateRange(final Apiaccount apiacc, final Integer vehicleId,
			final Integer page, final String date1, final String date2) {
		if (page < 0) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("Page value is strictly  positive", UtilsString.Success_Page_Must_Positive));
		}
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		// List<Positions> resultPosition = new ArrayList<Positions>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final Vehicle truck = this.truckRepo.findById(vehicleId).orElse(null);
		if (Objects.isNull(truck)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK).body(
					(Object) new Success("This vehicle doesn't exists", UtilsString.Success_VehicleName_Not_Exists));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "trspid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or trspid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affid in (" + affiliateid + ")";
			} else {
				SQL = SQL + " or affid in (" + affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "customid in (" + cliendid + ")";
			} else {
				SQL = SQL + " or customid in (" + cliendid + ")";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("position.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			SQL = " eventtimestamp >= '" + date1 + "' and eventtimestamp <='" + date2 + "' and (" + SQL + ")";
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("position.select");
			SQL = SQL + " order by vehicleid limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("position.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}
	
	
	
	public ResponseEntity<?> getAllNightDriving(final Apiaccount apiacc,String startdate,String endate) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String idalert=" and bigexception.level in (2,3) ";
			String completeRequest = environment.getRequiredProperty("nightdriving.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			completeRequest = completeRequest.replaceAll("STARTDATSED", startdate);
			completeRequest = completeRequest.replaceAll("ENDDATSED", endate);
			completeRequest = completeRequest.replaceAll("EXCEPTIONTYPE", Utils.getNightDrivingID()+"");
			completeRequest = completeRequest.replaceAll("ALTIONEXCEPERT", idalert);
			String request = completeRequest + " and " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	public ResponseEntity<?> getListNightDrivingPerPage(final Apiaccount apiacc, final Integer page,String startime,String endtime) {

		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		String idalert=" and bigexception.level in (2,3) ";
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("nightdriving.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " and " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("vehicle.select");
			SQL = SQL + " order by exceptionid limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("nightdriving.select");
			completeRequest = completeRequest.replaceAll("STARTDATSED", startime);
			completeRequest = completeRequest.replaceAll("ENDDATSED", endtime);
			completeRequest = completeRequest.replaceAll("EXCEPTIONTYPE", Utils.getNightDrivingID()+"");
			completeRequest = completeRequest.replaceAll("ALTIONEXCEPERT", idalert);
			
			result = this.jdbcTemplate.queryForList(completeRequest + " and " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}
	
	public ResponseEntity<?> getListDrivers(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (" + affiliateid
						+ "))";
			} else {
				SQL = SQL + " or transporterid in (select transporterid from transporter where affiliateid in ("
						+ affiliateid + "))";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			} else {
				SQL = SQL
						+ " or transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("driver.select");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	public ResponseEntity<?> getListDriversPerPage(final Apiaccount apiacc, final Integer page) {

		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		// List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (" + affiliateid
						+ "))";
			} else {
				SQL = SQL + " or transporterid in (select transporterid from transporter where affiliateid in ("
						+ affiliateid + "))";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			} else {
				SQL = SQL
						+ " or transporterid in (select transporterid from transporter where affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + ")))";
			}
		}
		// List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		String completeRequest = environment.getRequiredProperty("driver.count");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int maxValue = UserUtils.MaxReturnLine * (page + 1);
		int minValue = UserUtils.MaxReturnLine * page;
		Integer sizes = 0;
		if (!SQL.isEmpty()) {
			Long size = jdbcTemplate.queryForObject(completeRequest + " " + SQL, Long.class);
			sizes = size.intValue(); // = result.size();
			// completeRequest = environment.getRequiredProperty("vehicle.select");
			SQL = SQL + " order by driver limit " + UserUtils.MaxReturnLine + " OFFSET " + minValue;
			completeRequest = environment.getRequiredProperty("driver.select");

			result = this.jdbcTemplate.queryForList(completeRequest + " " + SQL);
		}
		// return
		// (ResponseEntity<?>)ResponseEntity.status(HttpStatus.OK).body((Object)new
		// ResultatAPI(lastPosition, false, lastPosition.size()));

		// final int sizes = resultlastPosition.size();
		boolean hasmoreElements = false;
		if (sizes <= minValue) {
			maxValue = sizes;
			minValue = sizes - UserUtils.MaxReturnLine;
			if (minValue < 0) {
				minValue = 0;
			}
		}
		if (sizes < maxValue) {
			maxValue = sizes;
		}
		hasmoreElements = (sizes > maxValue);
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, hasmoreElements, sizes));
	}
	
	
	public ResponseEntity<?> getAllSumaryTrip(final Apiaccount apiacc, String startime, String endtime) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("driver.sumarytrip");
			completeRequest = completeRequest.replaceAll("STARTDATSED", startime);
			completeRequest = completeRequest.replaceAll("ENDDATSED", endtime);
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
	
	public ResponseEntity<?> getAllSumaryException(final Apiaccount apiacc, String startime, String endtime) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest = environment.getRequiredProperty("driver.sumaryexception");
			completeRequest = completeRequest.replaceAll("STARTDATSED", startime);
			completeRequest = completeRequest.replaceAll("ENDDATSED", endtime);
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
	
	public ResponseEntity<?> preventreposhebdo(final Apiaccount apiacc,Integer nbrdays,boolean sumaryordetails) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest;
			if(sumaryordetails)
			{
				if(nbrdays == 6)
				{
			      completeRequest = environment.getRequiredProperty("weeklypreventionsumary");
				}else
				{
					completeRequest = environment.getRequiredProperty("weeklypreventionsumary5");
				}
			}
			else
			{
			   completeRequest = environment.getRequiredProperty("weeklypreventiondetails");
			}
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			Date date = new Date();
			Calendar dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			String datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -nbrdays);
			String dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			
			dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -(nbrdays+1));
			String dateint0 = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTZDATSED", dateint0);
			
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", nbrdays+"");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
	
	public ResponseEntity<?> testpreventreposhebdo(final Apiaccount apiacc,Integer nbrdays,boolean sumaryordetails) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest;
			if(sumaryordetails)
			{
				if(nbrdays == 6)
				{
			      completeRequest = environment.getRequiredProperty("weeklypreventionsumary");
				}else
				{
				  completeRequest = environment.getRequiredProperty("weeklypreventionsumary5");
				}
			}
			else
			{
			   completeRequest = environment.getRequiredProperty("weeklypreventiondetails");
			}
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			Date date = new Date();
			Calendar dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			String datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -nbrdays);
			String dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			//completeRequest = completeRequest.replaceAll("STARTZDATSED", dateint0);
			dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -(nbrdays+1));
			String dateint0 = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTZDATSED", dateint0);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", nbrdays+"");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
	@Override
	public ResponseEntity<?> jourconsecutif(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest;
			completeRequest = environment.getRequiredProperty("weeklysumary");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			Date date = new Date();
			Calendar dal=Calendar.getInstance();
			dal.setTime(date);
			String datend = Utils.formatDate(date, "yyyy-MM-dd");
			dal.add(Calendar.DAY_OF_MONTH, -5);
			String dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			result = this.jdbcTemplate.queryForList(request);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
	
	@Override
	public ResponseEntity<?> lastdaydriving(final Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result2 = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest;
			//completeRequest = environment.getRequiredProperty("weeklysumary");
			completeRequest = environment.getRequiredProperty("lastdaydriving");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			Date date = new Date();
			Calendar dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			String datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -5);
			String dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", "5");
			
			
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			//String data = "select Z.driverid,Z.nbr from (select MAX(Q.activitydate),count(*) as nbr,Q.driverid from (select count(*),d.activitydate,d.driverid from driveractivitysummary d where d.totaldistance >= MINTDIST and d.activitydate >= 'INITTDATE' and d.activitydate <= 'ENDTDATE' and XXXXXXXXXXXXXXXX group by d.driverid ,d.activitydate)Q group by Q.driverid)Z where Z.nbr <= 6 and Z.nbr >= 5";
			result = this.jdbcTemplate.queryForList(request);
			
			
			completeRequest = environment.getRequiredProperty("lastdaydriving");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			 datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -6);
			 dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", "6");
			
			
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			//String data = "select Z.driverid,Z.nbr from (select MAX(Q.activitydate),count(*) as nbr,Q.driverid from (select count(*),d.activitydate,d.driverid from driveractivitysummary d where d.totaldistance >= MINTDIST and d.activitydate >= 'INITTDATE' and d.activitydate <= 'ENDTDATE' and XXXXXXXXXXXXXXXX group by d.driverid ,d.activitydate)Q group by Q.driverid)Z where Z.nbr <= 6 and Z.nbr >= 5";
			result2 = this.jdbcTemplate.queryForList(request);
			
			result.addAll(result2);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}

	@Override
	public ResponseEntity<?> lastdaytransporter(Apiaccount apiacc) {
		final List<Camion> lastPosition = new ArrayList<Camion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final List<Apiaccess> listapiAccess = (List<Apiaccess>) apiacc.getApiaccessList();
		// final List<PositionCamion> lastPosition = new ArrayList<PositionCamion>();
		List<PositionCamion> resultlastPosition = new ArrayList<PositionCamion>();
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		// List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		String affiliateid = "";
		String transporterid = "";
		Transporter trans;
		Customeraffiliate cusaff;
		Customer custom;
		for (final Apiaccess apiaccess : listapiAccess) {
			trans = apiaccess.getTrps();
			cusaff = apiaccess.getAff();
			custom = apiaccess.getClid();
			if (!Objects.isNull(trans)) {
				if (!transporterid.isEmpty()) {
					transporterid = transporterid + "," + trans.getTransporterid();
				} else {
					transporterid = trans.getTransporterid() + "";
				}
			} else if (!Objects.isNull(cusaff)) {
				if (!affiliateid.isEmpty()) {
					affiliateid = affiliateid + "," + cusaff.getAffiliateid();
				} else {
					affiliateid = cusaff.getAffiliateid() + "";
				}
			} else if (!Objects.isNull(custom)) {
				if (!cliendid.isEmpty()) {
					cliendid = cliendid + "," + custom.getCustomerid();
				} else {
					cliendid = custom.getCustomerid() + "";
				}
			}
		}

		String SQL = "";
		if (!transporterid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "transporterid in (" + transporterid + ")";
			} else {
				SQL = SQL + " or transporterid in (" + transporterid + ")";
			}
		}

		if (!affiliateid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (" + affiliateid
						+ ")";
			} else {
				SQL = SQL + " or affiliateid in ("
						+ affiliateid + ")";
			}
		}

		if (!cliendid.isEmpty()) {
			if (SQL.isEmpty()) {
				SQL = "affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			} else {
				SQL = SQL
						+ " or affiliateid in (select affiliateid from customeraffiliate where customerid in ("
						+ cliendid + "))";
			}
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result2 = new ArrayList<Map<String, Object>>();
		if (!SQL.isEmpty()) {
			String completeRequest;
			//completeRequest = environment.getRequiredProperty("weeklysumary");
			completeRequest = environment.getRequiredProperty("lastdaytransporter");
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			Date date = new Date();
			Calendar dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			String datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -5);
			String dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", 5+"");
			
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			String request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			//String request = completeRequest + " " + SQL;
			//String data = "select Z.driverid,Z.nbr from (select MAX(Q.activitydate),count(*) as nbr,Q.driverid from (select count(*),d.activitydate,d.driverid from driveractivitysummary d where d.totaldistance >= MINTDIST and d.activitydate >= 'INITTDATE' and d.activitydate <= 'ENDTDATE' and XXXXXXXXXXXXXXXX group by d.driverid ,d.activitydate)Q group by Q.driverid)Z where Z.nbr <= 6 and Z.nbr >= 5";
			result = this.jdbcTemplate.queryForList(request);
			
			
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			dal.add(Calendar.DAY_OF_MONTH, -1);
			 datend = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			
			 date = new Date();
			 dal=Calendar.getInstance();
			dal.setTime(date);
			// datend = Utils.formatDate(date, "yyyy-MM-dd");
			//dal.add(Calendar.DAY_OF_MONTH, -(nbrdays-1));
			dal.add(Calendar.DAY_OF_MONTH, -6);
			 dateint = Utils.formatDate(dal.getTime(), "yyyy-MM-dd");
			completeRequest = completeRequest.replaceAll("STARTDATSED", dateint);
			completeRequest = completeRequest.replaceAll("ENDDATSED", datend);
			completeRequest = completeRequest.replaceAll("MINTDIST", Utils.MINTDIST()+"");
			completeRequest = completeRequest.replaceAll("DAYTNUMBER", 6+"");
			
			// SQL=SQL+" order by lasttimestamp limit "+UserUtils.MaxReturnLine+" OFFSET
			// "+minValue;
			 request = completeRequest .replaceAll("XXXXXXXXXXXXX", SQL);
			 
			 result2 = this.jdbcTemplate.queryForList(request);
			 
			 result.addAll(result2);
		}
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
				.body((Object) new ResultatAPI(result, false, lastPosition.size()));
	}
}