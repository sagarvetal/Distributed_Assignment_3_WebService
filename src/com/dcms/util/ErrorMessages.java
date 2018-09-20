package com.dcms.util;

public class ErrorMessages {

	//Error Messages
	public static String RECORD_ID_GENERATION_FAILED = "Error occurred in record id generation.";
	public static String RECORD_CREATION_FAILED = "Record Creation Failed : %s";
	public static String RECORD_EDIT_FAILED = "Record Editing Failed : %s";
    public static String RECORD_NOT_FOUND = "Failed to Fetch Record: Record %s Not Found.";
    public static String RECORD_COUNT_FAILED = "Failed to get record count : %s";
    public static String RECORD_TRANSFER_FAILED = "Failed to transfer record : %s";
    public static String RECORD_TRANSFER_IN_PROGRESS = "Record Id - %s is already being transferred by Manager Id - %s";

    //Validation Errors
    public static String INVALID_VALUES = "Invalid values for field(s) : %s";
    public static String FIELD_NOT_FOUND = "Failed to Update Record: Field %s is Invalid";
    public static String INVALID_REMOTE_SERVER = "Invalid remote server name : %s, [Record Id - %s]";
    public static String SAME_REMOTE_SERVER = "Remote server name(%s) and source server name(%s) are same, [Record Id - %s]";
}
