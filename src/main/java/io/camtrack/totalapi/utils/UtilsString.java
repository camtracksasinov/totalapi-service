// 
// Decompiled by Procyon v0.5.30
// 

package io.camtrack.totalapi.utils;

public class UtilsString {
	public static Integer LIST_TRUCK;
	public static Integer TripTruck_HistoryPosition;
	public static Integer LastTruckPosition;
	public static Integer TruckPositionById;
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static Integer Success_Failure;
	public static Integer Success_Token_D_exist;
	public static Integer Success_Token_Already_exist;
	public static Integer Success_Wrong_Format_Date;
	public static String Success_Wrong_Format_Date_Error = "Wrong format date or date below day date";
	public static String Success_Wrong_Format_Date_User_Error = "Wrong format date or end date below start date";
	public static Integer Success_Ok;
	public static Integer Success_Update_Int;
	public static String Success_Insert = "Success Insert";
	public static String Success_Update = "Success Update";
	public static String Success_Failure_AdminTokenError = "No valid Token";
	public static String Success_Failure_TokenExist = "Token Description Exist";
	public static String Success_Token_D_Exists = "Token Not Exists or it's expired";
	public static String SEPARATEUR_FILLIALE_TOKEN = ",";
	public static Integer Success_No_Active_Key;
	public static String Success_No_Active_Key_Error = "No active key";
	public static Integer Success_VehicleName_Not_Exists;
	public static String Success_VehicleName_Not_Exists_Error = "This vehicle doesn't exists";
	public static Integer Success_Not_Right_Vehicle;
	public static String Success_Not_Right_Vehicle_Error = "No rith on this vehicle";
	public static Integer Success_Page_Must_Positive;
	public static String Success_Page_Must_Positive_Error = "Page value is strictly  positive";
	public static Integer Success_Key_parameter_Dexists;
	public static String Success_Key_parameter_Dexists_Error = "Key parameter is required";
	public static Integer Success_parameter_Dexists;
	public static String Success_parameter_Dexists_Error = "Key and id truck parameter is required";
	public static Integer Success_Other_parameter_Dexists;
	public static String Success_Other_parameter_Dexists_Error = "pages,  parameter is required";
	public static Integer Success_Date_Inf;
	public static String Success_Date_Inf_Error = "Date is expire please update date or create new Token";
	public static Integer Success_Date_Not_Hight;
	public static String Success_Date_Not_Hight_Error = "The New Date must be higer than last date";
	public static String Success_Date_Not_Hight_Errors = "The New Initial date must be lower than the end  date";
	public static Integer Success_Date_Not_Hight_Errors_Int;
	public static Integer Success_LoginIncorrect;
	public static String Success_LoginIncorrect_String = "Login or password Incorrect";
	public static Integer Success_NoRightOnEndPoint;
	public static String Success_NoRightOnEndPoint_String = "No Right On EndPoint";
	public static Integer Success_NoCreate;
	public static String Success_NoCreate_String = "Token No Create please check List Endpoint and Affiliate";
	public static Integer Success_Pages_Positif;
	public static String Success_Pages_Positif_String = "Page must be positive integer";
	public static Integer Wrong_NbrDays_Integer;
	public static int Error_Wrong_Date_Format;

	static {
		UtilsString.LIST_TRUCK = 1;
		UtilsString.TripTruck_HistoryPosition = 3;
		UtilsString.LastTruckPosition = 2;
		UtilsString.TruckPositionById = 4;
		UtilsString.Success_Failure = -1;
		UtilsString.Success_Token_D_exist = -2;
		UtilsString.Success_Token_Already_exist = -3;
		UtilsString.Success_Wrong_Format_Date = -4;
		UtilsString.Success_Ok = 1;
		UtilsString.Success_Update_Int = 2;
		UtilsString.Success_No_Active_Key = -5;
		UtilsString.Success_VehicleName_Not_Exists = -6;
		UtilsString.Success_Not_Right_Vehicle = -7;
		UtilsString.Success_Page_Must_Positive = -8;
		UtilsString.Success_Key_parameter_Dexists = -9;
		UtilsString.Success_parameter_Dexists = -10;
		UtilsString.Success_Other_parameter_Dexists = -11;
		UtilsString.Success_Date_Inf = -12;
		UtilsString.Success_Date_Not_Hight = -13;
		UtilsString.Success_LoginIncorrect = -14;
		UtilsString.Success_NoRightOnEndPoint = -15;
		UtilsString.Success_NoCreate = -16;
		UtilsString.Success_Pages_Positif = -17;
		UtilsString.Error_Wrong_Date_Format = -18;
		UtilsString.Success_Date_Not_Hight_Errors_Int = -19;
		UtilsString.Wrong_NbrDays_Integer = -20;
	}
}
