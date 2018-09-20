package com.dcms.util;

public class LocationConstants {
    public static final String MONTREAL = "MTL";
    public static final String LAVAL = "LVL";
    public static final String DOLLARD = "DDO";
    public static final String MONTREAL_DESC = "Montreal";
    public static final String LAVAL_DESC = "Laval";
    public static final String DOLLARD_DESC = "Dollard-des-Ormeaux";
    public static final String SERVER = "Server";
    
    //Address for Servers
    public static final String MONTREAL_ADDRESS = "http://localhost:8080/service/" + LocationConstants.MONTREAL;
    public static final String LAVAL_ADDRESS = "http://localhost:8081/service/" + LocationConstants.LAVAL;
    public static final String DOLLARD_ADDRESS = "http://localhost:8082/service/" + LocationConstants.DOLLARD;
    
    //WSDL URLS
    public static final String MONTREAL_WSDL_URL = LocationConstants.MONTREAL_ADDRESS + "?wsdl";
    public static final String LAVAL_WSDL_URL = LocationConstants.LAVAL_ADDRESS + "?wsdl";
    public static final String DOLLARD_WSDL_URL = LocationConstants.DOLLARD_ADDRESS + "?wsdl";
}
