package com.dcms.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.dcms.util.LocationConstants;
import com.dcms.util.RecordTypeConstants;

public class SingleClientTest {
	
    public static void main(String[] args) {
    	
        while (true) {
            try {
            	final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter Manager Id: ");
                final String managerId = br.readLine();
                
				if (isValidManagerId(managerId)) {
                	
					final ClientManager clientManager = new ClientManager(managerId);
					displayServerName(clientManager.getServerName());
					boolean logout = false;
					while (true) {
						try {
							showOperations();
							System.out.print("Enter Your Choice: ");
							final String userChoice = br.readLine();
							switch (userChoice) {
							case "1":
								System.out.print("Enter First Name: ");
								final String firstName = br.readLine();
								System.out.print("Enter Last Name: ");
								final String lastName = br.readLine();
								System.out.print("Enter Courses (like maths,french,science): ");
								final String courses = br.readLine();
								System.out.print("Enter Status (Active/InActive): ");
								final String status = br.readLine();
								System.out.print("Enter Status Date (dd-MM-yyyy): ");
								final String statusDate = br.readLine();
								clientManager.createSRecord(firstName, lastName, courses, status, statusDate);
								break;
							case "2":
								System.out.print("Enter First Name: ");
								final String fName = br.readLine();
								System.out.print("Enter Last Name: ");
								final String lName = br.readLine();
								System.out.print("Enter Address: ");
								final String address = br.readLine();
								System.out.print("Enter Phone No: ");
								final String phoneNo = br.readLine();
								System.out.print("Enter Specialization: ");
								final String specialization = br.readLine();
								System.out.print("Enter Location code (MTL/LVL/DDO): ");
								final String location = br.readLine();
								clientManager.createTRecord(fName, lName, address, phoneNo, specialization, location);
								break;
							case "3":
								System.out.print("Enter Record Id: ");
								final String recordId = br.readLine();
								String fieldsToBeModified = "Enter field name";
								if(recordId.startsWith(RecordTypeConstants.STUDENT_RECORD)) {
									fieldsToBeModified += " (E.g. courses registered, status): ";
								} else if(recordId.startsWith(RecordTypeConstants.TEACHER_RECORD)){
									fieldsToBeModified += " (E.g. address, phone, location): ";
								}
								System.out.print(fieldsToBeModified);
								final String fields = br.readLine();
								System.out.print("Enter New Value: ");
								final String value = br.readLine();
								clientManager.editRecord(recordId, fields, value);
								break;
							case "4":
								clientManager.getRecordCounts();
								break;
							case "5":
								System.out.print("Enter Record Id: ");
								final String recordID = br.readLine();
								clientManager.displayRecord(recordID);
								break;
							case "6":
								System.out.print("Enter Record Id: ");
								final String recordid = br.readLine();
								System.out.print("Enter Remote Server Name: ");
								final String remoteServer = br.readLine();
								clientManager.transferRecord(recordid, remoteServer);
								break;
							case "7":
								System.out.println("\n" + managerId + " has been successfully logged out.\n");
								logout = true;
								break;
							case "8":
								System.out.println("\nProgram has been terminated..!");
								System.out.println("\n================ Thank You ================");
								System.exit(0);
								break;
							default:
								System.out.println("\nPlease enter proper choice.");
								//showOperations();
							}

							if (logout)
								break;
							
						} catch (Exception e) {
							System.out.println("ERROR!!!" + e.getMessage());
						}
					}
					
                } else {
                	System.out.println("\nERROR => Invalid Manager Id\n");
                }
                
            } catch (Exception e) {
                System.out.println("ERROR =>" + e.getMessage());
            }
        }
    }
    
    private static void displayServerName(final String serverName) {
    	System.out.println("\n*************** Welcome to " + serverName + " DCMS ***************\n");
    }

    private static void showOperations(){
        System.out.println("\n****** Operations ******");
        System.out.println("1. Add a Student");
        System.out.println("2. Add a Teacher");
        System.out.println("3. Edit a Record");
        System.out.println("4. Get Records Count");
        System.out.println("5. Display a Record");
        System.out.println("6. Transfer a Record");
        System.out.println("7. Log Out");
        System.out.println("8. Exit\n");
    }
    
    private static boolean isValidManagerId(final String managerId) {
    	if(managerId == null || managerId.isEmpty()) {
    		return false;
    	}
    	if (!managerId.startsWith(LocationConstants.MONTREAL) &&
				!managerId.startsWith(LocationConstants.LAVAL) &&
				!managerId.startsWith(LocationConstants.DOLLARD)) {
    		return false;
    	}
    	if(managerId.substring(3, managerId.length()).length() != 4) {
    		return false;
    	}
    	if(!isInteger(managerId.substring(3, managerId.length()))) {
    		return false;
    	}
    	return true;
    }
    	
    private static boolean isInteger(final String str) {
    	try {
    		int num = Integer.parseInt(str);
    		return true;
    	} catch(NumberFormatException e) {
    		return false;
    	}
    }

}