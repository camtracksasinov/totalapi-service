// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.user.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.camtrack.totalapi.entities.Success;
import io.camtrack.totalapi.user.entities.Apiaccount;
import io.camtrack.totalapi.user.metiers.UserMetiers;
import io.camtrack.totalapi.user.repository.EndpointokenRepository;
import io.camtrack.totalapi.user.utils.UserUtils;
import io.camtrack.totalapi.utils.Utils;
import io.camtrack.totalapi.utils.UtilsString;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class APITotal {
	@Autowired
	
	UserMetiers userMetiers;
	@Autowired
	EndpointokenRepository endpointtokRepo;
	@Autowired
    JdbcTemplate jdbcTemplate;
	@GetMapping({ "/trucks/last-positionsbyid" })
	public ResponseEntity<?> getLastTruckPositionById(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@Parameter(description = "Truck Id") @RequestParam(value = "id", required = false) final Integer id,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LastTruckPosition, Integer.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (!Objects.isNull(id)) {
			return (ResponseEntity<?>) this.userMetiers.getSpecificVehicleLastPositionsById(apiacc, id);
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getAllVehicleLastPositions(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getVehicleLastPositions(apiacc, pages);
	}

	@GetMapping({ "/trucks/last-positions" })
	public ResponseEntity<?> getLastTruckPosition(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) final String key,
			@RequestParam(value = "matricule", required = false) final String matricule,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LastTruckPosition, Integer.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (!Objects.isNull(matricule)) {
			return (ResponseEntity<?>) this.userMetiers.getSpecificVehicleLastPositions(apiacc, matricule);
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getAllVehicleLastPositions(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getVehicleLastPositions(apiacc, pages);
	}

	@GetMapping({ "/trucks" })
	public ResponseEntity<?> getAllLastTruckPerPage(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getListVehicle(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getListVehiclePerPage(apiacc, pages);
	}
	
	@GetMapping({ "/drivers" })
	public ResponseEntity<?> getDrivers(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getListDrivers(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getListDriversPerPage(apiacc, pages);
	}
	
	@GetMapping({ "/transporter" })
	public ResponseEntity<?> getAllTransporterPerPage(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getListTransporter(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getListTransporterPerPage(apiacc, pages);
	}
	
	@GetMapping({ "/affiliate" })
	public ResponseEntity<?> getAllAffiliatePerPage(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "pages", required = false) final Integer pages) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getListAffiliate(apiacc);
		}
		return (ResponseEntity<?>) this.userMetiers.getListAffiliatePerPage(apiacc, pages);
	}

	@PostMapping({ "/nights" })
	public ResponseEntity<?> getAllNightDriving(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "pages", required = false) final Integer pages,@RequestParam(value = "startime", required = true)String startime,@RequestParam(value = "endtime", required = true)String endtime) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("User Token parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		Date date1 = Utils.stringToDate(startime, "yyyy-MM-dd hh:mm:ss");
		Date date2 = Utils.stringToDate(endtime, "yyyy-MM-dd hh:mm:ss");
		if(Objects.isNull(date1)||Objects.isNull(date2))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Wrong Format Date yyyy-MM-dd hh:mm:ss", UtilsString.Error_Wrong_Date_Format));
		}
		if(date2.before(date1))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Intial Data must de lower than end date", UtilsString.Success_Date_Not_Hight_Errors_Int));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (Objects.isNull(pages)) {
			return (ResponseEntity<?>) this.userMetiers.getAllNightDriving(apiacc,startime,endtime);
		}
		return (ResponseEntity<?>) this.userMetiers.getListNightDrivingPerPage(apiacc, pages,startime,endtime);
	}
	
	@PostMapping({ "/summarytrip" })
	public ResponseEntity<?> getAllSummaryTrip(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,@RequestParam(value = "startime", required = true)String startime,@RequestParam(value = "endtime", required = true)String endtime) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("User Token parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		Date date1 = Utils.stringToDate(startime, "yyyy-MM-dd hh:mm:ss");
		Date date2 = Utils.stringToDate(endtime, "yyyy-MM-dd hh:mm:ss");
		if(Objects.isNull(date1)||Objects.isNull(date2))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Wrong Format Date yyyy-MM-dd hh:mm:ss", UtilsString.Error_Wrong_Date_Format));
		}
		if(date2.before(date1))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Intial Data must de lower than end date", UtilsString.Success_Date_Not_Hight_Errors_Int));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		return (ResponseEntity<?>) this.userMetiers.getAllSumaryTrip(apiacc,startime,endtime);
	}
	
	@PostMapping({ "/summaryexception" })
	public ResponseEntity<?> getAllSummaryExceptionDriving(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,@RequestParam(value = "startime", required = true)String startime,@RequestParam(value = "endtime", required = true)String endtime) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("User Token parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		Date date1 = Utils.stringToDate(startime, "yyyy-MM-dd hh:mm:ss");
		Date date2 = Utils.stringToDate(endtime, "yyyy-MM-dd hh:mm:ss");
		if(Objects.isNull(date1)||Objects.isNull(date2))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Wrong Format Date yyyy-MM-dd hh:mm:ss", UtilsString.Error_Wrong_Date_Format));
		}
		if(date2.before(date1))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK)
					.body((Object) new Success("Intial Data must de lower than end date", UtilsString.Success_Date_Not_Hight_Errors_Int));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		return (ResponseEntity<?>) this.userMetiers.getAllSumaryException(apiacc,startime,endtime);
	}
	
	@GetMapping({ "/trucks/{id}" })
	public ResponseEntity<?> getAllLastTruckById(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@Parameter(description = "Truck Id") @PathVariable("id") final Integer id) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.TruckPositionById, Integer.class);
		// final Long b =
		// (Long)mapper.convertValue((Object)UtilsString.TruckPositionById,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		return (ResponseEntity<?>) this.userMetiers.getVehicleById(apiacc, id);
	}

	@Operation(description = "Format Date: yyyy-MM-dd sample 2020-02-18")
	@GetMapping({ "/trucks/{id}/positions" })
	public ResponseEntity<?> getAllTrajetTruckById(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String user_token,
			@RequestParam(value = "pages", required = false) final Integer pages,
			@Parameter(description = "Truck Id") @PathVariable("id") final Integer truck_Id,
			@Parameter(description = "Start Date (yyyy-MM-dd)") @RequestParam(value = "startdate", required = false) final String startdate,
			@Parameter(description = "End Date (yyyy-MM-dd)") @RequestParam(value = "enddate", required = false) final String enddate) {
		if (Objects.isNull(user_token) || Objects.isNull(truck_Id)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key and id truck parameter is required",
							UtilsString.Success_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(user_token, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.TripTruck_HistoryPosition, Integer.class);
		// final Long b =
		// (Long)mapper.convertValue((Object)UtilsString.TripTruck_HistoryPosition,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		if (!Objects.isNull(pages)) {
			if (Objects.isNull(startdate) || Objects.isNull(enddate)) {
				return (ResponseEntity<?>) this.userMetiers.getTrajetVehicleByIdWithPage(apiacc, truck_Id, pages);
			}
			final Date begindate = Utils.stringToDateHour(startdate, UserUtils.HourBegin, "yyyy-MM-dd");
			final Date enddates = Utils.stringToDateHour(enddate, UserUtils.HourEnd, "yyyy-MM-dd");
			if (Objects.isNull(begindate) || Objects.isNull(enddates)) {
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body((Object) new Success("Wrong format date or end date below start date",
								UtilsString.Success_Wrong_Format_Date));
			}
			if (enddates.before(begindate)) {
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body((Object) new Success("Wrong format date or end date below start date",
								UtilsString.Success_Wrong_Format_Date));
			}
			return (ResponseEntity<?>) this.userMetiers.getTrajetVehicleByIdWithPageDateRange(apiacc, truck_Id, pages,
					startdate + " " + UserUtils.HourBegin, enddate + " " + UserUtils.HourEnd);
		} else {
			if (Objects.isNull(startdate) || Objects.isNull(enddate)) {
				return (ResponseEntity<?>) this.userMetiers.getTrajetVehicleById(apiacc, truck_Id);
			}
			final Date begindate = Utils.stringToDateHour(startdate, UserUtils.HourBegin, "yyyy-MM-dd");
			final Date enddates = Utils.stringToDateHour(enddate, UserUtils.HourEnd, "yyyy-MM-dd");
			if (Objects.isNull(begindate) || Objects.isNull(enddates)) {
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body((Object) new Success("Wrong format date or end date below start date",
								UtilsString.Success_Wrong_Format_Date));
			}
			if (enddates.before(begindate)) {
				return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body((Object) new Success("Wrong format date or end date below start date",
								UtilsString.Success_Wrong_Format_Date));
			}
			return (ResponseEntity<?>) this.userMetiers.getTrajetVehicleByIdDateRange(apiacc, truck_Id,
					startdate + " " + UserUtils.HourBegin, enddate + " " + UserUtils.HourEnd);
		}
	}
	
	@RequestMapping(value = { "/allexceptiontype" }, method = { RequestMethod.GET })
    public List<Map<String, Object>> listwhatsappwithlang() throws Exception {
        try {
            final String completeRequest = "select name as nm,parametertypeid as exceptionid, sanctionsalert,sanctionsalarm from parametertype where status=1";
            return (List<Map<String, Object>>)this.jdbcTemplate.queryForList(completeRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
	
	@GetMapping({ "/preventreposhebdo" })
	public ResponseEntity<?> preventreposhebdo(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "nbrdays", required = true) final Integer nbrdays,@RequestParam(value = "sumaryordetails", required = true) final boolean sumaryordetails) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		
		if((nbrdays!=5)&&(nbrdays!=6))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("nbrdays must have only two value 5 or 6", UtilsString.Wrong_NbrDays_Integer));
		}
		
		return (ResponseEntity<?>) this.userMetiers.preventreposhebdo(apiacc, nbrdays,sumaryordetails);
	}
	
	@GetMapping({ "/testpreventreposhebdo" })
	public ResponseEntity<?> testpreventreposhebdo(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,
			@RequestParam(value = "nbrdays", required = true) final Integer nbrdays,@RequestParam(value = "sumaryordetails", required = true) final boolean sumaryordetails) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		
		if((nbrdays!=5)&&(nbrdays!=6))
		{
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("nbrdays must have only two value 5 or 6", UtilsString.Wrong_NbrDays_Integer));
		}
		
		return (ResponseEntity<?>) this.userMetiers.testpreventreposhebdo(apiacc, nbrdays,sumaryordetails);
	}
	/**
	@GetMapping({ "/jourconsecifig" })
	public ResponseEntity<?> jourconsecifig(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key,@RequestParam(value = "sumaryordetails", required = true) final boolean sumaryordetails) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		
		return (ResponseEntity<?>) this.userMetiers.jourconsecutif(apiacc);
	}*/
	
	@GetMapping({ "/lastdaydriving" })
	public ResponseEntity<?> lastdaydriving(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		
		return (ResponseEntity<?>) this.userMetiers.lastdaydriving(apiacc);
	}
	
	@GetMapping({ "/lastdaytransporter" })
	public ResponseEntity<?> lastdaytransporter(
			@Parameter(description = "User Token") @RequestParam(value = "key", required = true) String key) {
		if (Objects.isNull(key)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("Key parameter is required", UtilsString.Success_Key_parameter_Dexists));
		}
		final Date date = new Date();
		final Apiaccount apiacc = this.userMetiers.activeAccount(key, date);
		if (Objects.isNull(apiacc)) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body((Object) new Success("No active key", UtilsString.Success_No_Active_Key));
		}
		final ObjectMapper mapper = new ObjectMapper();
		final Integer b = mapper.convertValue(UtilsString.LIST_TRUCK, Integer.class);
		// final Long b = (Long)mapper.convertValue((Object)UtilsString.LIST_TRUCK,
		// (Class)Long.class);
		if (Objects.isNull(this.endpointtokRepo.findByApiAccountEndPoint(apiacc, b).orElse(null))) {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body((Object) new Success("No Right On EndPoint", UtilsString.Success_NoRightOnEndPoint));
		}
		
		return (ResponseEntity<?>) this.userMetiers.lastdaytransporter(apiacc);
	}
}
