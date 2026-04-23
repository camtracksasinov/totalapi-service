// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.metiers;

import java.util.Date;

import org.springframework.http.ResponseEntity;

import io.camtrack.totalapi.user.entities.Apiaccount;

public interface UserMetiers {
	ResponseEntity<?> getVehicleLastPositions(final Apiaccount apiacc, final Integer page);

	ResponseEntity<?> getSpecificVehicleLastPositions(final Apiaccount apiacc, final String immat);

	ResponseEntity<?> getSpecificVehicleLastPositionsById(final Apiaccount apiacc, final Integer id);

	ResponseEntity<?> getAllVehicleLastPositions(final Apiaccount apiacc);

	Apiaccount activeAccount(final String token, final Date date);

	ResponseEntity<?> getListVehicle(final Apiaccount apiacc);

	ResponseEntity<?> getListVehiclePerPage(final Apiaccount apiacc, final Integer page);

	ResponseEntity<?> getVehicleById(final Apiaccount apiacc, final Integer vehicleId);

	ResponseEntity<?> getTrajetVehicleById(final Apiaccount apiacc, final Integer vehicleId);

	ResponseEntity<?> getTrajetVehicleByIdDateRange(final Apiaccount apiacc, final Integer vehicleId,
			final String date1, final String date2);

	ResponseEntity<?> getTrajetVehicleByIdWithPage(final Apiaccount apiacc, final Integer vehicleId,
			final Integer page);

	ResponseEntity<?> getTrajetVehicleByIdWithPageDateRange(final Apiaccount apiacc, final Integer vehicleId,
			final Integer page, final String date1, final String date2);

	ResponseEntity<?> getListTransporter(Apiaccount apiacc);

	ResponseEntity<?> getListTransporterPerPage(Apiaccount apiacc, Integer pages);

	ResponseEntity<?> getListAffiliate(Apiaccount apiacc);

	ResponseEntity<?> getListAffiliatePerPage(Apiaccount apiacc, Integer pages);

	ResponseEntity<?> getAllNightDriving(Apiaccount apiacc, String startime, String endtime);

	ResponseEntity<?> getListNightDrivingPerPage(Apiaccount apiacc, Integer pages, String startime, String endtime);

	ResponseEntity<?> getListDrivers(Apiaccount apiacc);

	ResponseEntity<?> getListDriversPerPage(Apiaccount apiacc, Integer pages);

	ResponseEntity<?> getAllSumaryException(Apiaccount apiacc, String startime, String endtime);

	ResponseEntity<?> getAllSumaryTrip(Apiaccount apiacc, String startime, String endtime);

	ResponseEntity<?> preventreposhebdo(Apiaccount apiacc, Integer nbrdays,boolean sumaryordetails);

	ResponseEntity<?> jourconsecutif(Apiaccount apiacc);

	ResponseEntity<?> lastdaydriving(Apiaccount apiacc);

	ResponseEntity<?> lastdaytransporter(Apiaccount apiacc);

	ResponseEntity<?> testpreventreposhebdo(Apiaccount apiacc, Integer nbrdays, boolean sumaryordetails);
}
