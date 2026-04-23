// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camtrack.totalapi.user.entities.Apiaccess;
import io.camtrack.totalapi.user.entities.Apiaccount;
import io.camtrack.totalapi.user.entities.Customer;
import io.camtrack.totalapi.user.entities.Customeraffiliate;
import io.camtrack.totalapi.user.entities.Transporter;
import io.camtrack.totalapi.user.metiers.UserMetiers;
import io.camtrack.totalapi.user.repository.EndpointokenRepository;
import io.camtrack.totalapi.user.utils.UserUtils;
import io.camtrack.totalapi.utils.Utils;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping({ "/api" })
public class APITotalSumary {
	@Autowired

	UserMetiers userMetiers;
	@Autowired
	EndpointokenRepository endpointtokRepo;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@PostMapping({ "/aggregates/trips/assetspeople" })
	public List<Map<String, Object>> getListAggregateTripPeople(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key,
			@Parameter(description = "Date Initial format AAAAMMJJ") @RequestParam(value = "date1", required = true) @NotEmpty String date1,
			@Parameter(description = "Date FINAL format AAAAMMJJ") @RequestParam(value = "date2", required = true) @NotEmpty String date2) {

		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
		Date date11 = Utils.stringToDateCheck(date1, "yyyyMMdd");
		Date date12 = Utils.stringToDateCheck(date2, "yyyyMMdd");

		if (Objects.isNull(date12) || Objects.isNull(date11)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Wrong Date Format Please Send Date with Format: AAAAMMJJ");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
		String date01 = Utils.formatDate(date11, "yyyy-MM-dd");
		String date02 = Utils.formatDate(date12, "yyyy-MM-dd");
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
		if (!cliendid.isEmpty()) {
			affiliateid = "select custom.affiliateid from customeraffiliate custom where custom.status = 1 and custom.customerid in ("
					+ cliendid + ")";
			String request = "SELECT " + "    COALESCE(va.vehicleid, da.vehicleid) AS assetid, "
					+ "    COALESCE(va.driverid, da.driverid) AS driverid, "
					+ "    COALESCE(va.transporterid, da.transporterid) AS siteid, "
					+ "    COALESCE(va.affiliateid, da.affiliateid) AS groupid, "
					+ "    COALESCE(va.activitydate, da.activitydate) AS datekey, " + "    va.TotalTripDuration, "
					+ "    va.TotalTripCount, " + "    va.TotalTripDistanceKilometres, "
					+ "    va.TotalAssetDrivingTimeSeconds, " + "    da.TotalAssetDrivingTimeSeconds " + "FROM ( "
					+ "    SELECT " + "        vehs.activitydate, " + "        vehs.vehicleid, "
					+ "        vehs.driverid, " + "        vehs.transporterid, " + "        vehs.affiliateid, "
					+ "        SUM(vehs.totaldripduration) AS TotalTripDuration, "
					+ "        SUM(vehs.validduration) AS TotalAssetDrivingTimeSeconds, "
					+ "        SUM(vehs.tripcounts) AS TotalTripCount, "
					+ "        SUM(vehs.totaldistance) AS TotalTripDistanceKilometres "
					+ "    FROM vehicleactivitysummary vehs where " + "    vehs.affiliateid in (" + affiliateid + ")"
					+ "    and vehs.activitydate >= '" + date01 + "' and vehs.activitydate <= '" + date02 + "'"
					+ "    GROUP BY vehs.vehicleid, vehs.driverid, vehs.transporterid, vehs.affiliateid,vehs.activitydate "
					+ ") va " + "LEFT JOIN ( " + "    SELECT " + "        driv.activitydate, "
					+ "        driv.driverid, " + "        driv.vehicleid, " + "        driv.transporterid, "
					+ "        driv.affiliateid, " + "        SUM(driv.validduration) AS TotalAssetDrivingTimeSeconds "
					+ "    FROM driveractivitysummary driv  where " + "    driv.affiliateid in (" + affiliateid + ")"
					+ "    and driv.activitydate >= '" + date01 + "' and driv.activitydate <= '" + date02 + "'"
					+ "    GROUP BY driv.driverid, driv.vehicleid, driv.transporterid, driv.affiliateid,driv.activitydate "
					+ ") da " + "ON va.driverid = da.driverid " + "AND va.vehicleid = da.vehicleid "
					+ "AND va.transporterid = da.transporterid " + "AND va.affiliateid = da.affiliateid "
					+ "AND va.activitydate = da.activitydate";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else if (!affiliateid.isEmpty()) {
			String request = "SELECT " + "    COALESCE(va.vehicleid, da.vehicleid) AS assetid, "
					+ "    COALESCE(va.driverid, da.driverid) AS driverid, "
					+ "    COALESCE(va.transporterid, da.transporterid) AS siteid, "
					+ "    COALESCE(va.affiliateid, da.affiliateid) AS groupid, "
					+ "    COALESCE(va.activitydate, da.activitydate) AS datekey, " + "    va.TotalTripDuration, "
					+ "    va.TotalTripCount, " + "    va.TotalTripDistanceKilometres, "
					+ "    va.TotalAssetDrivingTimeSeconds, " + "    da.TotalAssetDrivingTimeSeconds " + "FROM ( "
					+ "    SELECT " + "        vehs.vehicleid, " + "        vehs.driverid, "
					+ "        vehs.transporterid, " + "        vehs.affiliateid, " + "        vehs.activitydate, "
					+ "        SUM(vehs.totaldripduration) AS TotalTripDuration, "
					+ "        SUM(vehs.validduration) AS TotalAssetDrivingTimeSeconds, "
					+ "        SUM(vehs.tripcounts) AS TotalTripCount, "
					+ "        SUM(vehs.totaldistance) AS TotalTripDistanceKilometres "
					+ "    FROM vehicleactivitysummary vehs  where " + "    vehs.affiliateid in (" + affiliateid + ")"
					+ "    and vehs.activitydate >= '" + date01 + "' and vehs.activitydate <= '" + date02 + "'"
					+ "    GROUP BY vehs.vehicleid, vehs.driverid, vehs.transporterid, vehs.affiliateid,vehs.activitydate "
					+ ") va " + "LEFT JOIN ( " + "    SELECT " + "        driv.driverid, " + "        driv.vehicleid, "
					+ "        driv.transporterid, " + "        driv.affiliateid, " + "        driv.activitydate, "
					+ "        SUM(driv.validduration) AS TotalAssetDrivingTimeSeconds "
					+ "    FROM driveractivitysummary driv  where " + "    driv.affiliateid in (" + affiliateid + ")"
					+ "    and driv.activitydate >= '" + date01 + "' and driv.activitydate <= '" + date02 + "'"
					+ "    GROUP BY driv.driverid, driv.vehicleid, driv.transporterid, driv.affiliateid,driv.activitydate "
					+ ") da " + "ON va.driverid = da.driverid " + "AND va.vehicleid = da.vehicleid "
					+ "AND va.transporterid = da.transporterid " + "AND va.affiliateid = da.affiliateid "
					+ "AND va.activitydate = da.activitydate";

			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);

		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Not Right On a Affiliate");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	@PostMapping("/aggregates/events/assetspeople")
	public List<Map<String, Object>> getListAggregateEventsAssets(
	        @RequestParam(value = "key", required = true) @NotEmpty String key,
	        @RequestParam(value = "date1", required = true) @NotEmpty String date1,
	        @RequestParam(value = "date2", required = true) @NotEmpty String date2) {

	    final Date now = new Date();
	    final Apiaccount apiacc = this.userMetiers.activeAccount(key, now);

	    if (apiacc == null) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("error", "No active key");
	        return Collections.singletonList(map);
	    }

	    Date d1 = Utils.stringToDateCheck(date1, "yyyyMMdd");
	    Date d2 = Utils.stringToDateCheck(date2, "yyyyMMdd");

	    if (d1 == null || d2 == null) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("error", "Wrong Date Format Please Send Date with Format: AAAAMMJJ");
	        return Collections.singletonList(map);
	    }

	    String date01 = Utils.formatDate(d1, "yyyy-MM-dd");
	    String date02 = Utils.formatDate(d2, "yyyy-MM-dd");

	    Set<Long> customerIds = new LinkedHashSet<>();
	    Set<Long> affiliateIds = new LinkedHashSet<>();

	    List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
	    if (listapiAccess != null) {
	        for (Apiaccess apiaccess : listapiAccess) {
	            Customeraffiliate cusaff = apiaccess.getAff();
	            Customer custom = apiaccess.getClid();

	            if (cusaff != null) {
	                affiliateIds.add((long) cusaff.getAffiliateid());
	            } else if (custom != null) {
	                customerIds.add((long) custom.getCustomerid());
	            }
	        }
	    }

	    // If customerIds exist, fetch affiliateIds
	    if (!customerIds.isEmpty() && affiliateIds.isEmpty()) {

	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < customerIds.size(); i++) {
	            sb.append("?");
	            if (i < customerIds.size() - 1) sb.append(",");
	        }

	        String sqlAff =
	                "SELECT ca.affiliateid " +
	                "FROM customeraffiliate ca " +
	                "WHERE ca.status = 1 " +
	                "AND ca.customerid IN (" + sb.toString() + ")";

	        List<Object> params = new ArrayList<>(customerIds);

	        List<Map<String, Object>> affRows =
	                jdbcTemplate.queryForList(sqlAff, params.toArray());

	        for (Map<String, Object> r : affRows) {
	            Object v = r.get("affiliateid");
	            if (v instanceof Number) {
	                affiliateIds.add(((Number) v).longValue());
	            }
	        }
	    }

	    if (affiliateIds.isEmpty()) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("error", "Not Right On a Affiliate");
	        return Collections.singletonList(map);
	    }

	    StringBuilder inClause = new StringBuilder();
	    for (int i = 0; i < affiliateIds.size(); i++) {
	        inClause.append("?");
	        if (i < affiliateIds.size() - 1) inClause.append(",");
	    }

	    String sql =
	            "SELECT " +
	            "datekey, " +
	            "vehicleid AS \"AssetId\", " +
	            "driverid, " +
	            "eventtypeid AS \"EventTypeId\", " +
	            "transporterid AS \"SiteId\", " +
	            "affiliateid AS \"GroupId\", " +
	            "eventcount AS \"TotalEventOccurrences\", " +
	            "\"minvalue\" AS \"MinEventValue\", " +
	            "\"maxvalue\" AS \"MaxEventValue\", " +
	            "totalvalue AS \"TotalEventValue\", " +
	            "mineventduratioin, " +
	            "maxeventduration, " +
	            "totaleventduration, " +
	            "criticitycount::jsonb AS \"criticitycount\" " +
	            "FROM vehicleeventsummary vehsum " +
	            "WHERE vehsum.affiliateid IN (" + inClause.toString() + ") " +
	            "AND datekey >= ? " +
	            "AND datekey <= ?";

	    List<Object> finalParams = new ArrayList<>(affiliateIds);
	    LocalDate localDate1 = d1.toInstant()
	            .atZone(ZoneId.systemDefault())
	            .toLocalDate();

	    LocalDate localDate2 = d2.toInstant()
	            .atZone(ZoneId.systemDefault())
	            .toLocalDate();

	    finalParams.add(localDate1);
	    finalParams.add(localDate2);
	    //finalParams.add(date01);
	   // finalParams.add(date02);

	    return jdbcTemplate.queryForList(sql, finalParams.toArray());
	}



	@PostMapping({ "/dimensions/organisations" })
	public List<Map<String, Object>> getListOrganisationsDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key) {

		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
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
		if (!cliendid.isEmpty()) {
			String request = "select aff.affiliateid as GroupId, aff.\"name\" as \"name\",(aff.status = 1) as IsActive from customeraffiliate aff where  aff.status = 1 and  aff.customerid in ("
					+ cliendid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else if (!affiliateid.isEmpty()) {
			String request = "select aff.affiliateid as GroupId, aff.\"name\" as \"name\",(aff.status = 1) as IsActive from customeraffiliate aff where aff.status = 1 and  aff.affiliateid in ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Not Right On a Affiliate");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	@PostMapping({ "/dimensions/sites" })
	public List<Map<String, Object>> getListTransporterDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key) {

		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
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
		if (!cliendid.isEmpty()) {
			affiliateid = "select custom.affiliateid from customeraffiliate custom where custom.status = 1 and  custom.customerid in ("
					+ cliendid + ")";
			String request = "select tr.transporterid as SiteId, tr.affiliateid as GroupId, tr.\"name\" as \"name\", (tr.status = 1) as IsActive from transporter tr where tr.affiliateid in ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else if (!affiliateid.isEmpty()) {
			String request = "select tr.transporterid as SiteId, tr.affiliateid as GroupId, tr.\"name\" as \"name\", (tr.status = 1) as IsActive from transporter tr where tr.affiliateid in ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Not Right On a Affiliate");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	@PostMapping({ "/dimensions/people" })
	public List<Map<String, Object>> getListDriverDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key) {

		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
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
		if (!cliendid.isEmpty()) {
			affiliateid = "select custom.affiliateid from customeraffiliate custom where custom.status = 1 and  custom.customerid in ("
					+ cliendid + ")";
			String request = "select  dr.transporterid as SiteId, dr.driverid as DriverId, dr.\"name\" as \"name\", (dr.status = 1) as IsActive from driver dr where dr.affiliateid in ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else if (!affiliateid.isEmpty()) {
			String request = "select  dr.transporterid as SiteId, dr.driverid as DriverId, dr.\"name\" as \"name\", (dr.status = 1) as IsActive from driver dr where dr.affiliateid in ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Not Right On a Affiliate");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	@PostMapping({ "/dimensions/assets" })
	public List<Map<String, Object>> getListVehicleDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key) {

		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
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
		if (!cliendid.isEmpty()) {
			affiliateid = "select custom.affiliateid from customeraffiliate custom where custom.status = 1 and  custom.customerid in ("
					+ cliendid + ")";
			String request = "SELECT vh.vehicleid AS AssetId, vh.transporterid AS SiteId, vh.vehicledesc AS RegistrationNumber, trans.affiliateid AS GroupId, ctry.\"name\" AS country, (vh.status = 1) AS IsAvailable, vht.names AS VehicleType, vh.make, vh.model, vh.vinnumber, vh.fueltype FROM vehicle vh LEFT JOIN vehicletype vht ON vht.ids = vh.vehicletypeid LEFT JOIN transporter trans ON trans.transporterid = vh.transporterid LEFT JOIN customeraffiliate aff ON aff.affiliateid = trans.affiliateid LEFT JOIN country ctry ON ctry.countryid = aff.countryid WHERE trans.affiliateid IN ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else if (!affiliateid.isEmpty()) {
			String request = "SELECT vh.vehicleid AS AssetId, vh.transporterid AS SiteId, vh.vehicledesc AS RegistrationNumber, trans.affiliateid AS GroupId, ctry.\"name\" AS country, (vh.status = 1) AS IsAvailable, vht.names AS VehicleType, vh.make, vh.model, vh.vinnumber, vh.fueltype FROM vehicle vh LEFT JOIN vehicletype vht ON vht.ids = vh.vehicletypeid LEFT JOIN transporter trans ON trans.transporterid = vh.transporterid LEFT JOIN customeraffiliate aff ON aff.affiliateid = trans.affiliateid LEFT JOIN country ctry ON ctry.countryid = aff.countryid WHERE trans.affiliateid IN ("
					+ affiliateid + ")";
			return (List<Map<String, Object>>) this.jdbcTemplate.queryForList(request);
		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Not Right On a Affiliate");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	@PostMapping({ "/dimensions/event-descriptions" })
	public List<Map<String, Object>> getListEventDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) @NotEmpty String key) {
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "No active key");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
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
		if (!cliendid.isEmpty()) {
			affiliateid = "select custom.affiliateid from customeraffiliate custom where custom.status = 1 and custom.customerid in ("
					+ cliendid + ")";
			String request = "select paramid.eventcode,paramid.parametertypeid as EventTypeId,paramid.name as EventName,param.clientaffiliateid as groupid,json_build_object('record', level.recordlevel,'alert', level.alertlevel,'alarm', level.alarmlevel)::text AS criticite from parametertype paramid,parameterconfig param,allalertlevel level  where paramid.parametertypeid = param.parametertypeid and  param.clientaffiliateid = level.affiliateid and param.clientaffiliateid in ("
					+ affiliateid + ")";
			List<Map<String, Object>> result = this.jdbcTemplate.queryForList(request);

			ObjectMapper mapper = new ObjectMapper();

			for (Map<String, Object> row : result) {
				String criticiteJson = (String) row.get("criticite");
				if (criticiteJson != null) {
					try {
						row.put("criticite", mapper.readValue(criticiteJson, Map.class));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return result;
		} else if (!affiliateid.isEmpty()) {
			String request = "select paramid.eventcode,paramid.parametertypeid as EventTypeId,paramid.name as EventName,param.clientaffiliateid as groupid,json_build_object('record', level.recordlevel,'alert', level.alertlevel,'alarm', level.alarmlevel)::text AS criticite from parametertype paramid,parameterconfig param,allalertlevel level  where paramid.parametertypeid = param.parametertypeid and  param.clientaffiliateid = level.affiliateid and param.clientaffiliateid in ("
					+ affiliateid + ")";
			List<Map<String, Object>> result = this.jdbcTemplate.queryForList(request);

			ObjectMapper mapper = new ObjectMapper();

			for (Map<String, Object> row : result) {
				String criticiteJson = (String) row.get("criticite");
				if (criticiteJson != null) {
					try {
						row.put("criticite", mapper.readValue(criticiteJson, Map.class));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return result;
		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", "Affiliate Empty");
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result.add(map);
			return result;
		}
	}

	/**
	 * @PostMapping({ "/dimensions/event-details" }) public List<Map<String,
	 * Object>> getListDetailsEventDescription(@Parameter(description = "User
	 * Token") @RequestParam(value = "key", required = true) @NotEmpty String
	 * key,@Parameter(description = "Date Initial format
	 * AAAAMMJJ") @RequestParam(value = "date1", required = true) @NotEmpty String
	 * date1,@Parameter(description = "Date FINAL format
	 * AAAAMMJJ") @RequestParam(value = "date2", required = true) @NotEmpty String
	 * date2){
	 * 
	 * final Date date = new Date(); final Apiaccount apiacc =
	 * this.userMetiers.activeAccount(key, date); if (Objects.isNull(apiacc)) {
	 * Map<String, Object> map=new HashMap<String, Object>(); map.put("error", "No
	 * active key"); List<Map<String, Object>> result=new
	 * ArrayList<Map<String,Object>>(); result.add(map); return result; } Date
	 * date11=Utils.stringToDateCheck(date1, "yyyyMMdd"); Date
	 * date12=Utils.stringToDateCheck(date2, "yyyyMMdd");
	 * 
	 * 
	 * if(Objects.isNull(date12)||Objects.isNull(date11)) { Map<String, Object>
	 * map=new HashMap<String, Object>(); map.put("error", "Wrong Date Format Please
	 * Send Date with Format: AAAAMMJJ"); List<Map<String, Object>> result=new
	 * ArrayList<Map<String,Object>>(); result.add(map); return result; } String
	 * date01 = Utils.formatDate(date11, "yyyy-MM-dd"); String date02 =
	 * Utils.formatDate(date12, "yyyy-MM-dd"); List<Apiaccess> listapiAccess =
	 * apiacc.getApiaccessList(); String cliendid = ""; List<Integer>
	 * affiliateid=new ArrayList<Integer>(); String transporterid = ""; Transporter
	 * trans; Customeraffiliate cusaff; Customer custom; for (final Apiaccess
	 * apiaccess : listapiAccess) { trans = apiaccess.getTrps(); cusaff =
	 * apiaccess.getAff(); custom = apiaccess.getClid(); if (!Objects.isNull(trans))
	 * { if (!transporterid.isEmpty()) { transporterid = transporterid + "," +
	 * trans.getTransporterid(); } else { transporterid = trans.getTransporterid() +
	 * ""; } } else if (!Objects.isNull(cusaff)) { /**if (!affiliateid.isEmpty()) {
	 * affiliateid = affiliateid + "," + cusaff.getAffiliateid(); } else {
	 * affiliateid = cusaff.getAffiliateid() + ""; }
	 *//*
		 * affiliateid.add(cusaff.getAffiliateid()); } else if (!Objects.isNull(custom))
		 * { if (!cliendid.isEmpty()) { cliendid = cliendid + "," +
		 * custom.getCustomerid(); } else { cliendid = custom.getCustomerid() + ""; } }
		 * } if(!cliendid.isEmpty()) { //String request0 = "select from " String
		 * affiliateID_Final=""; String affiliatesid =
		 * "select distinct custom.affiliateid from customeraffiliate custom where custom.status = 1 and custom.customerid in ("
		 * +cliendid+")"; List<Integer> allaffiliateID =
		 * jdbcTemplate.queryForList(affiliatesid,Integer.class); int k=0; for (Integer
		 * affid : allaffiliateID) { if(k==0) { affiliateID_Final =
		 * "  where bigexception.startdatetime >='"
		 * +date01+"' and bigexception.enddatetime<='"
		 * +date02+"' and (bigexception.affiliateid = "
		 * +affid+" AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
		 * +affid+"))"; } else { affiliateID_Final = affiliateID_Final
		 * +" or ("+"bigexception.affiliateid = "
		 * +affid+" AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
		 * +affid+"))"; } k++; } String
		 * request="select bigexception.vehicleid as assetid,bigexception.transporterid as siteid,bigexception.driverid,bigexception.exceptiontype as EventTypeId,bigexception.affiliateid as groupid,bigexception.level AS criticity,true AS classification,bigexception.startdatetime as startevent,bigexception.enddatetime as endevent from exception bigexception "
		 * ; String end = request+affiliateID_Final; List<Map<String, Object>> result=
		 * this.jdbcTemplate.queryForList(end);
		 * 
		 * return result; } else if(!affiliateid.isEmpty()) {
		 * 
		 * String affiliateID_Final=""; //List<Integer> allaffiliateID =
		 * jdbcTemplate.queryForList(affiliateid,Integer.class); int k=0; for (Integer
		 * affid : affiliateid) { if(k==0) { affiliateID_Final =
		 * "  where bigexception.startdatetime >='"
		 * +date01+"' and bigexception.enddatetime<='"
		 * +date02+"' and (bigexception.affiliateid = "
		 * +affid+" AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
		 * +affid+"))"; } else { affiliateID_Final = affiliateID_Final
		 * +" or ("+"bigexception.affiliateid = "
		 * +affid+" AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
		 * +affid+"))"; } k++; } String
		 * request="select bigexception.vehicleid as assetid,bigexception.transporterid as siteid,bigexception.driverid,bigexception.exceptiontype as EventTypeId,bigexception.affiliateid as groupid,bigexception.level AS criticity,true AS classification,bigexception.startdatetime as startevent,bigexception.enddatetime as endevent from exception bigexception "
		 * ; List<Map<String, Object>> result=
		 * this.jdbcTemplate.queryForList(request+affiliateID_Final);
		 * 
		 * return result; } else {
		 * 
		 * Map<String, Object> map=new HashMap<String, Object>(); map.put("error",
		 * "Affiliate Empty"); List<Map<String, Object>> result=new
		 * ArrayList<Map<String,Object>>(); result.add(map); return result; } }
		 */

	@PostMapping("/dimensions/event-details")
	public Map<String, Object> getListDetailsEventDescription(
			@Parameter(description = "User Token") @RequestParam(value = "key") @NotEmpty String key,
			@Parameter(description = "Date Initial format AAAAMMJJ") @RequestParam(value = "date1") @NotEmpty String date1,
			@Parameter(description = "Date FINAL format AAAAMMJJ") @RequestParam(value = "date2") @NotEmpty String date2,
			@Parameter(description = "Page number (default 0)") @RequestParam(defaultValue = "0") int page) {

		Map<String, Object> response = new HashMap<>();
		int size = UserUtils.MaxReturnLine;
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);

		if (Objects.isNull(apiacc)) {
			response.put("error", "No active key");
			return response;
		}

		Date date11 = Utils.stringToDateCheck(date1, "yyyyMMdd");
		Date date12 = Utils.stringToDateCheck(date2, "yyyyMMdd");

		if (Objects.isNull(date11) || Objects.isNull(date12)) {
			response.put("error", "Wrong Date Format. Expected AAAAMMJJ");
			return response;
		}

		String date01 = Utils.formatDate(date11, "yyyy-MM-dd");
		String date02 = Utils.formatDate(date12, "yyyy-MM-dd");

		List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
		String cliendid = "";
		List<Integer> affiliateid = new ArrayList<>();

		for (Apiaccess apiaccess : listapiAccess) {
			if (apiaccess.getAff() != null) {
				affiliateid.add(apiaccess.getAff().getAffiliateid());
			} else if (apiaccess.getClid() != null) {
				if (!cliendid.isEmpty())
					cliendid += ",";
				cliendid += apiaccess.getClid().getCustomerid();
			}
		}

		StringBuilder whereClause = new StringBuilder();
		List<Integer> finalAffiliates = new ArrayList<>();

		if (!cliendid.isEmpty()) {
			String affiliatesid = "SELECT DISTINCT custom.affiliateid FROM customeraffiliate custom WHERE custom.status = 1 AND custom.customerid IN ("
					+ cliendid + ")";
			finalAffiliates = jdbcTemplate.queryForList(affiliatesid, Integer.class);
		} else {
			finalAffiliates = affiliateid;
		}

		if (finalAffiliates.isEmpty()) {
			response.put("error", "Affiliate Empty");
			return response;
		}

		// Build WHERE clause
		whereClause.append(" WHERE bigexception.startdatetime >= '" + date01 + "' ")
				.append(" AND bigexception.enddatetime <= '" + date02 + "' AND (");

		int i = 0;
		for (Integer affid : finalAffiliates) {
			if (i > 0)
				whereClause.append(" OR ");
			whereClause.append("(bigexception.affiliateid = " + affid + " AND bigexception.level IN ("
					+ "   SELECT unnest(ARRAY_REMOVE(ARRAY[" + "       CASE WHEN alls.recordlevel THEN 1 END,"
					+ "       CASE WHEN alls.alertlevel THEN 2 END," + "       CASE WHEN alls.alarmlevel THEN 3 END"
					+ "   ], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = " + affid + "))");
			i++;
		}

		whereClause.append(")");

		String baseQuery = "SELECT bigexception.vehicleid AS assetid," + " bigexception.transporterid AS siteid,"
				+ " bigexception.driverid," + " bigexception.exceptiontype AS eventtypeid,"
				+ " bigexception.affiliateid AS groupid," + " bigexception.level AS criticity,"
				+ " true AS classification," + " bigexception.startdatetime AS startevent,"
				+ " bigexception.enddatetime AS endevent " + "FROM exception bigexception ";

		int offset = page * size;

		// PAGINATED QUERY
		String paginatedQuery = baseQuery + whereClause + " ORDER BY bigexception.startdatetime DESC " + " LIMIT "
				+ size + " OFFSET " + offset;

		List<Map<String, Object>> data = jdbcTemplate.queryForList(paginatedQuery);

		// TOTAL COUNT QUERY
		String countQuery = "SELECT COUNT(*) FROM exception bigexception " + whereClause;
		int total = jdbcTemplate.queryForObject(countQuery, Integer.class);

		response.put("page", page);
		response.put("size", size);
		response.put("total", total);
		response.put("data", data);

		return response;
	}

	/*
	 * @PostMapping({ "/dimensions/invalidatevent-details" }) public
	 * List<Map<String, Object>> getListInvalidateDetailsEventDescription(
	 * 
	 * @Parameter(description = "User Token") @RequestParam(value = "key", required
	 * = true) @NotEmpty String key,
	 * 
	 * @Parameter(description = "Date Initial format AAAAMMJJ") @RequestParam(value
	 * = "date1", required = true) @NotEmpty String date1,
	 * 
	 * @Parameter(description = "Date FINAL format AAAAMMJJ") @RequestParam(value =
	 * "date2", required = true) @NotEmpty String date2) {
	 * 
	 * final Date date = new Date(); final Apiaccount apiacc =
	 * this.userMetiers.activeAccount(key, date); if (Objects.isNull(apiacc)) {
	 * Map<String, Object> map = new HashMap<String, Object>(); map.put("error",
	 * "No active key"); List<Map<String, Object>> result = new
	 * ArrayList<Map<String, Object>>(); result.add(map); return result; } Date
	 * date11 = Utils.stringToDateCheck(date1, "yyyyMMdd"); Date date12 =
	 * Utils.stringToDateCheck(date2, "yyyyMMdd");
	 * 
	 * if (Objects.isNull(date12) || Objects.isNull(date11)) { Map<String, Object>
	 * map = new HashMap<String, Object>(); map.put("error",
	 * "Wrong Date Format Please Send Date with Format: AAAAMMJJ"); List<Map<String,
	 * Object>> result = new ArrayList<Map<String, Object>>(); result.add(map);
	 * return result; } String date01 = Utils.formatDate(date11, "yyyy-MM-dd");
	 * String date02 = Utils.formatDate(date12, "yyyy-MM-dd"); List<Apiaccess>
	 * listapiAccess = apiacc.getApiaccessList(); String cliendid = "";
	 * List<Integer> affiliateid = new ArrayList<Integer>(); String transporterid =
	 * ""; Transporter trans; Customeraffiliate cusaff; Customer custom; for (final
	 * Apiaccess apiaccess : listapiAccess) { trans = apiaccess.getTrps(); cusaff =
	 * apiaccess.getAff(); custom = apiaccess.getClid(); if (!Objects.isNull(trans))
	 * { if (!transporterid.isEmpty()) { transporterid = transporterid + "," +
	 * trans.getTransporterid(); } else { transporterid = trans.getTransporterid() +
	 * ""; } } else if (!Objects.isNull(cusaff)) {
	 *//**
		 * if (!affiliateid.isEmpty()) { affiliateid = affiliateid + "," +
		 * cusaff.getAffiliateid(); } else { affiliateid = cusaff.getAffiliateid() + "";
		 * }
		 *//*
			 * affiliateid.add(cusaff.getAffiliateid()); } else if (!Objects.isNull(custom))
			 * { if (!cliendid.isEmpty()) { cliendid = cliendid + "," +
			 * custom.getCustomerid(); } else { cliendid = custom.getCustomerid() + ""; } }
			 * } if (!cliendid.isEmpty()) { // String request0 = "select from " String
			 * affiliateID_Final = ""; String affiliatesid =
			 * "select distinct custom.affiliateid from customeraffiliate custom where custom.status = 1 and custom.customerid in ("
			 * + cliendid + ")"; List<Integer> allaffiliateID =
			 * jdbcTemplate.queryForList(affiliatesid, Integer.class); int k = 0; for
			 * (Integer affid : allaffiliateID) { if (k == 0) { affiliateID_Final =
			 * "  where bigexception.startdatetime >='" + date01 +
			 * "' and bigexception.enddatetime<='" + date02 +
			 * "' and (bigexception.affiliateid = " + affid +
			 * " AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
			 * + affid + "))"; } else { affiliateID_Final = affiliateID_Final + " or (" +
			 * "bigexception.affiliateid = " + affid +
			 * " AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
			 * + affid + "))"; } k++; } String request =
			 * "select bigexception.vehicleid as assetid,bigexception.transporterid as siteid,bigexception.driverid,bigexception.exceptiontype as EventTypeId,bigexception.affiliateid as groupid,bigexception.level AS criticity,bigexception.comments AS classification,bigexception.startdatetime as startevent,bigexception.enddatetime as endevent from invalidateexception bigexception "
			 * ; List<Map<String, Object>> result = this.jdbcTemplate.queryForList(request +
			 * affiliateID_Final);
			 * 
			 * return result; } else if (!affiliateid.isEmpty()) {
			 * 
			 * String affiliateID_Final = ""; // List<Integer> allaffiliateID = //
			 * jdbcTemplate.queryForList(affiliateid,Integer.class); int k = 0; for (Integer
			 * affid : affiliateid) { if (k == 0) { affiliateID_Final =
			 * "  where bigexception.startdatetime >='" + date01 +
			 * "' and bigexception.enddatetime<='" + date02 +
			 * "' and (bigexception.affiliateid = " + affid +
			 * " AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
			 * + affid + "))"; } else { affiliateID_Final = affiliateID_Final + " or (" +
			 * "bigexception.affiliateid = " + affid +
			 * " AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[CASE WHEN alls.recordlevel THEN 1 END, CASE WHEN alls.alertlevel THEN 2 END, CASE WHEN alls.alarmlevel THEN 3 END], NULL)) FROM allalertlevel alls WHERE alls.affiliateid = "
			 * + affid + "))"; } k++; } String request =
			 * "select bigexception.vehicleid as assetid,bigexception.transporterid as siteid,bigexception.driverid,bigexception.exceptiontype as EventTypeId,bigexception.affiliateid as groupid,bigexception.level AS criticity,bigexception.comments AS classification,bigexception.startdatetime as startevent,bigexception.enddatetime as endevent from invalidateexception bigexception "
			 * ; List<Map<String, Object>> result = this.jdbcTemplate.queryForList(request +
			 * affiliateID_Final);
			 * 
			 * return result; } else {
			 * 
			 * Map<String, Object> map = new HashMap<String, Object>(); map.put("error",
			 * "Affiliate Empty"); List<Map<String, Object>> result = new
			 * ArrayList<Map<String, Object>>(); result.add(map); return result; } }
			 */
	@PostMapping({ "/dimensions/invalidatevent-details" })
	public Map<String, Object> getListInvalidateDetailsEventDescription(
	        @RequestParam(value = "key") @NotEmpty String key,
	        @Parameter(description = "Date Initial format AAAAMMJJ") @RequestParam(value = "date1") @NotEmpty String date1,
			@Parameter(description = "Date FINAL format AAAAMMJJ") @RequestParam(value = "date2") @NotEmpty String date2,
	        @RequestParam(defaultValue = "0") int page) {

	    Map<String, Object> response = new HashMap<>();
	    int size = UserUtils.MaxReturnLine;
	    final Date date = new Date();
	    final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
	    if (Objects.isNull(apiacc)) {
	        response.put("error", "No active key");
	        response.put("data", Collections.emptyList());
	        return response;
	    }

	    Date date11 = Utils.stringToDateCheck(date1, "yyyyMMdd");
	    Date date12 = Utils.stringToDateCheck(date2, "yyyyMMdd");

	    if (Objects.isNull(date12) || Objects.isNull(date11)) {
	        response.put("error", "Wrong Date Format Please Send Date with Format: AAAAMMJJ");
	        response.put("data", Collections.emptyList());
	        return response;
	    }

	    String date01 = Utils.formatDate(date11, "yyyy-MM-dd");
	    String date02 = Utils.formatDate(date12, "yyyy-MM-dd");

	    List<Apiaccess> listapiAccess = apiacc.getApiaccessList();
	    List<Integer> affiliateid = new ArrayList<>();
	    String cliendid = "";

	    for (final Apiaccess apiaccess : listapiAccess) {
	        if (apiaccess.getAff() != null) {
	            affiliateid.add(apiaccess.getAff().getAffiliateid());
	        } else if (apiaccess.getClid() != null) {
	            if (!cliendid.isEmpty())
	                cliendid += ",";
	            cliendid += apiaccess.getClid().getCustomerid();
	        }
	    }

	    String finalFilterSql = "";

	    if (!cliendid.isEmpty()) {

	        String affiliatesQuery =
	            "SELECT DISTINCT custom.affiliateid FROM customeraffiliate custom WHERE custom.status = 1 AND custom.customerid IN (" + cliendid + ")";
	        List<Integer> allaffiliateID = jdbcTemplate.queryForList(affiliatesQuery, Integer.class);

	        finalFilterSql = buildAffiliateFilter(allaffiliateID, date01, date02);

	    } else if (!affiliateid.isEmpty()) {

	        finalFilterSql = buildAffiliateFilter(affiliateid, date01, date02);

	    } else {
	        response.put("error", "Affiliate Empty");
	        response.put("data", Collections.emptyList());
	        return response;
	    }

	    // PAGE & LIMIT
	    int offset = page * size;
	    String limitOffsetSql = " LIMIT " + size + " OFFSET " + offset;

	    String baseSql =
	            "SELECT bigexception.vehicleid AS assetid," +
	            "       bigexception.transporterid AS siteid," +
	            "       bigexception.driverid," +
	            "       bigexception.exceptiontype AS EventTypeId," +
	            "       bigexception.affiliateid AS groupid," +
	            "       bigexception.level AS criticity," +
	            "       bigexception.comments AS classification," +
	            "       bigexception.startdatetime AS startevent," +
	            "       bigexception.enddatetime AS endevent " +
	            "FROM invalidateexception bigexception ";

	    List<Map<String, Object>> data =
	            this.jdbcTemplate.queryForList(baseSql + finalFilterSql + limitOffsetSql);

	    // Format de la réponse paginée
	    response.put("page", page);
	    response.put("size", size);
	    response.put("count", data.size());
	    response.put("data", data);

	    return response;
	}

	private String buildAffiliateFilter(List<Integer> affiliateIds, String date01, String date02) {
	    StringBuilder sb = new StringBuilder(" WHERE (");
	    int i = 0;

	    for (Integer affid : affiliateIds) {
	        if (i > 0) sb.append(" OR ");

	        sb.append("(bigexception.startdatetime >= '").append(date01)
	          .append("' AND bigexception.enddatetime <= '").append(date02)
	          .append("' AND bigexception.affiliateid = ").append(affid)
	          .append(" AND bigexception.level IN (SELECT unnest(ARRAY_REMOVE(ARRAY[")
	          .append("CASE WHEN alls.recordlevel THEN 1 END, ")
	          .append("CASE WHEN alls.alertlevel THEN 2 END, ")
	          .append("CASE WHEN alls.alarmlevel THEN 3 END], NULL)) ")
	          .append("FROM allalertlevel alls WHERE alls.affiliateid = ").append(affid).append("))");

	        i++;
	    }

	    sb.append(")");
	    return sb.toString();
	}

}
