package com.dcms.util;

public class PortConstants {
   
   public static final int MTL_UDP_PORT = 9080;
   public static final int LVL_UDP_PORT = 9081;
   public static final int DDO_UDP_PORT = 9082;
   
   public static int getUdpPort(final String serverLocation) {
	   if(LocationConstants.MONTREAL.equalsIgnoreCase(serverLocation)) {
		   return MTL_UDP_PORT;
	   } else if(LocationConstants.LAVAL.equalsIgnoreCase(serverLocation)) {
		   return LVL_UDP_PORT;
	   } else if(LocationConstants.DOLLARD.equalsIgnoreCase(serverLocation)) {
		   return DDO_UDP_PORT;
	   } 
	   return 0;
   }
   
}