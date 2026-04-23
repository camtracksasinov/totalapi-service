// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import io.camtrack.totalapi.user.entities.Camion;
import io.camtrack.totalapi.user.entities.Lastinformations;
import io.camtrack.totalapi.user.entities.PositionCamion;
import io.camtrack.totalapi.user.entities.Positions;
import io.camtrack.totalapi.user.entities.Vehicle;

public class Utils {
	public static final String DateFormat = "yyyy-MM-dd";
	public static final String DateFormatResponse = "yyyy-MM-dd HH:mm:ss";

	public static Camion truckToCamion(final Vehicle truck, final Integer transporterid) {
		final Camion camion = new Camion();
		Long values;
		try {

			camion.setId(truck.getVehicleid());
			camion.setMtr(truck.getVehicledesc());
			camion.setDt(formatDate(truck.getLastreceived(), "yyyy-MM-dd HH:mm:ss"));
			camion.setVehid(truck.getUnitid());
			camion.setTrpid(transporterid);
			try {
				values = truck.getLastposition().getLastodometer();
				if (values.intValue() == -1) {
					camion.setOdo(notAvaillable());
				} else {
					camion.setOdo("" + values);
				}

			} catch (Exception ex) {
				camion.setOdo(notAvaillable());
			}

		} catch (Exception ex) {

		}
		return camion;
	}

	public static String notAvaillable() {
		return "NA";
	}

	public static PositionCamion positiontoCamion(final Lastinformations position, final Vehicle truck) {
		final PositionCamion positionC = new PositionCamion();
		positionC.setId(truck.getVehicleid());
		positionC.setMtr(truck.getVehicledesc());
		positionC.setLat(position.getLastlatitude());
		positionC.setLon(position.getLastlongitude());
		positionC.setSpd(position.getSpeed());
		positionC.setOdo(position.getLastodometer() + "");
		positionC.setDt(formatDate(position.getLasttimestamp(), "yyyy-MM-dd HH:mm:ss"));
		return positionC;
	}

	public static PositionCamion positionstoCamion(final Positions position, final Vehicle truck) {
		final PositionCamion positionC = new PositionCamion();
		positionC.setId(truck.getVehicleid());
		positionC.setMtr(truck.getVehicledesc());
		positionC.setLat(position.getLatitude());
		positionC.setLon(position.getLongitude());
		positionC.setSpd(position.getSpeed());
		positionC.setOdo(position.getOdometer() + "");
		positionC.setDt(formatDate(position.getEventtimestamp(), "yyyy-MM-dd HH:mm:ss"));
		return positionC;
	}

	public static String formatDate(final Date dates, final String format) {
		try {
			return new SimpleDateFormat(format).format(dates);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Date stringToDate(final String dates, final String format) {
		try {
			return new SimpleDateFormat(format).parse(dates + " 23:59:59");
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static Date stringToDateCheck(final String dates, final String format) {
		try {
			return new SimpleDateFormat(format).parse(dates);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Date stringToDateHour(final String dates, final String hour, final String format) {
		try {
			return new SimpleDateFormat(format).parse(dates + " " + hour);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String generateRandomPassword(final int len) {
		System.out.println("Generating password using random() : ");
		System.out.print("Your new password is : ");
		final String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String Small_chars = "abcdefghijklmnopqrstuvwxyz";
		final String numbers = "0123456789";
		final String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		final Random rndm_method = new Random();
		final char[] password = new char[len];
		for (int i = 0; i < len; ++i) {
			password[i] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(
					rndm_method.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length()));
		}
		return String.valueOf(password);
	}

	public static Integer getNightDrivingID() {
		// TODO Auto-generated method stub
		return 4;
	}
	
	public static Double MINTDIST() {
		// TODO Auto-generated method stub
		return 0.3;
	}
}
