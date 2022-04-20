

import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
import java.lang.*;

class Hotel {
	String id = "SJL Hotel";
	String address = "1845 Fairmount St, Wichita, KS 67260";
}

class User {
	String id = "";
	String userName = "";
}

class Admin {
	String id = "";
}

class CreditCard {
	int cardNumber = 0;
}

class Bookings {
	int[] bookingsNo;
	int totalCost = 0;
	int availableRooms = 0;
	int[] userRoomNumbersArray;
	int [] roomsTakenOrFreeArray;//get our room availability from boolArray file and save in int array for processing
	int[] userFacilitiesArray = new int[4]; //array with jacuzzi, pool etc numbers
		
	
	public void clearScreen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}  
	
	//***********************************************
	public void printRoomNumbersToFile (String userFileName, int[] userRoomNumbersArray ) throws IOException, InterruptedException{ //puts user room numbers in txt file
	
		userFileName = userFileName+".txt";
		File tempFile = new File(userFileName); 
		FileWriter fw = new FileWriter(tempFile,false); //false so it can NOT append to the file
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
			
			for(int i = 0; i<userRoomNumbersArray.length; i++) {
				pw.print(userRoomNumbersArray[i]);
				pw.print(",");
			}
			
		pw.close();
	}
	
	
	public void printUserFacilityNumbersToFile (String userFileName, int totalCost, int[] userFacilitiesArray ) throws IOException, InterruptedException{ //puts user jacuzzi, pool etc in Datatxt file
	
		userFileName = userFileName+"Data.txt";
		File tempFile = new File(userFileName); 
		FileWriter fw = new FileWriter(tempFile,false); //false so it can NOT append to the file
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
			
			for(int i = 0; i<userFacilitiesArray.length; i++) {
				pw.print(userFacilitiesArray[i]);
				pw.print(",");
			}
			pw.print(totalCost);
			pw.print(",");
			
		pw.close();
	}
	//****************************************
	
	public void getUserRoomsAndFacilities(String userFileName) throws IOException, InterruptedException{ //Get user rooms and facilities

		User user = new User();
		user.userName = userFileName;
		Scanner sc1 = new Scanner(System.in);
		System.out.print("\n\nEnter your full name: ");
		user.id = sc1.nextLine(); 
		roomsTakenOrFreeArray = getRoomsBoolArrayFromFile();
		
		System.out.println("\n\nHello, "+user.id);
		
		
		updateAvailableRooms(roomsTakenOrFreeArray);
		Scanner sc = new Scanner(System.in);
		int numOfRooms = 0; // numOfRooms user wants
		do { //numOfRooms cannot be higher than availableRooms
			//System.out.println("\nWe have "+availableRooms+" rooms available");
			System.out.print("How many rooms do you want? : ");
			numOfRooms = sc.nextInt(); 
		}while(numOfRooms > availableRooms);
		
		int totalUserEqs = 5+numOfRooms; //jacuzzi,pool,no of rooms,extrabed,meal plus numOfRooms to save in file
		int numOfRooms2 = -240;
		userRoomNumbersArray = new int[numOfRooms];
		boolean checkIfNumOfRooms2IsInArrayValue = true; //bool to pass to array 
	
	
		
		//HotelReservation.checkIfRoomsAreFree(HotelReservation.boolArray);//print rooms availability
		for (int i = 0; i < numOfRooms; i++) {
			int k = i+1;
			System.out.println("Select room "+k+" number : ");
			numOfRooms2 = sc.nextInt();
			for(int j = 0; j< numOfRooms; j++) {//check if user enters a room twice. decrement i if so. so it will not count that pass
				checkIfNumOfRooms2IsInArrayValue = checkIfNumOfRooms2IsInArray(numOfRooms2,userRoomNumbersArray,roomsTakenOrFreeArray); //it has to check here before the if otherwise it will be false because of the last time it checked in the while loop
				if(userRoomNumbersArray[j] == numOfRooms2 || (checkIfNumOfRooms2IsInArrayValue==true) || (numOfRooms2 > 50) || (numOfRooms2 < 1)) {
					
					while((numOfRooms2 == userRoomNumbersArray[j]) || (checkIfNumOfRooms2IsInArrayValue==true )|| (numOfRooms2 > 50) || (numOfRooms2 < 1)) {
						System.out.println("Room is already taken or unavailable. Please enter another room : ");
						numOfRooms2 = sc.nextInt();
						checkIfNumOfRooms2IsInArrayValue = checkIfNumOfRooms2IsInArray(numOfRooms2,userRoomNumbersArray,roomsTakenOrFreeArray); //this roomsTakenOrFreeArray is the array with the binary for room availability.we want to see if the room has been taken too
						
					}
				}
				
			}
			
			userRoomNumbersArray[i] = numOfRooms2;
		}
	
		updateRoomsTakenOrFreeArray(userRoomNumbersArray);
		updateRoomsBoolArrayTxtWithRoomsTakenOrFreeArray();//update this after user selects room(s)
		updateAvailableRooms(roomsTakenOrFreeArray); //update with roomsTakenOrFreeArray
		printRoomNumbersToFile(userFileName,userRoomNumbersArray); //puts user room numbers in txt file
		getJacuzzisAndOthers(userRoomNumbersArray);
		calculateTotalCost(userRoomNumbersArray.length,userFacilitiesArray);//getJacuzzisAndOthers function updates this array with how many of each facilities user wants
		printUserFacilityNumbersToFile(userFileName,totalCost,userFacilitiesArray);
		makePayment();
		generateReceipt(userFileName);
		
	}
	//---------------------------------------------------------------------------------------------------------------------//
	//---------------------------------------------------------------------------------------------------------------------//
	
	public void updateRoomsTakenOrFreeArray(int [] userRoomNumbersArray) { //use userRoomNumbersArray to update our binary array
		for(int i = 0; i < userRoomNumbersArray.length; i++) {
			roomsTakenOrFreeArray[userRoomNumbersArray[i]] = 0;
		}
		//System.out.println(Arrays.toString(roomsTakenOrFreeArray));
	}
	
	public void updateRoomsBoolArrayTxtWithRoomsTakenOrFreeArray() {
		try {
			File tempFile = new File("roomsBoolArray.txt"); 
			FileWriter fw = new FileWriter(tempFile,false); //false so it can NOT append to the file
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			for(int i = 0; i<51; i++) {
				if(roomsTakenOrFreeArray[i] == 1) {
					pw.print("1");
				}
				else {
					pw.print("0");
				}
				
			}
			
			pw.close();
		}catch(Exception ex) {
			ex.printStackTrace();	
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------//
	//------------------------------------------------------------------------------------------------------------------------------//
	public void updateAvailableRooms(int[] roomsTakenOrFreeArray) {
		for(int i = 0; i < roomsTakenOrFreeArray.length; i++) { //for loop that counts every '1' in roomsTakenOrFreeArray and increments availableRooms
			if(roomsTakenOrFreeArray[i] == 1) {
				availableRooms++;
			}
		}
	}
	
	public void getJacuzzisAndOthers(int[] userRoomNumbersArray) throws IOException, InterruptedException{
		clearScreen();
		Scanner sc = new Scanner(System.in);
		System.out.println("Our Facilities : \n\nJacuzzi     $10\nPool        $20\nMeal        $10\nExtra bed   $5\n\nFor each facility, you cannot add more than the number of rooms you have");
		
		System.out.print("How many Jacuzzis do you want? : ");
		int noOfJacuzzis = sc.nextInt();
		while(noOfJacuzzis > userRoomNumbersArray.length || noOfJacuzzis < 0) {
			System.out.println("You have "+userRoomNumbersArray.length+" room(s). Please choose between 0-"+userRoomNumbersArray.length+" jacuzzis");
			noOfJacuzzis = sc.nextInt();
		}
		System.out.print("How many Pools do you want? : ");
		int noOfPools = sc.nextInt();
		while(noOfPools > userRoomNumbersArray.length || noOfPools < 0) {
			System.out.println("You have "+userRoomNumbersArray.length+" room(s). Please choose between 0-"+userRoomNumbersArray.length+" pools");
			noOfPools = sc.nextInt();
		}
		System.out.print("Do you want meals? : ");
		String mealsYesOrNo = sc.nextLine();
		while(!(mealsYesOrNo.equals("y"))&& !(mealsYesOrNo.equals("n"))) {
			System.out.println("Please enter y or n");
			mealsYesOrNo = sc.nextLine();
		}
		System.out.print("How many extra beds do you want? : ");
		int noOfBeds = sc.nextInt();
		while(noOfBeds > userRoomNumbersArray.length || noOfBeds < 0) {
			System.out.println("You have "+userRoomNumbersArray.length+" room(s). Please choose between 0-"+userRoomNumbersArray.length+" extra beds");
			noOfBeds = sc.nextInt();
		}
		userFacilitiesArray[0] = noOfJacuzzis;
		userFacilitiesArray[1] = noOfPools;
		userFacilitiesArray[3] = noOfBeds;
		if(mealsYesOrNo.equals("y")){
			userFacilitiesArray[2] = 1;
		}
		else {
			userFacilitiesArray[2]= 0;
		}
	}
	
	
	
	public boolean checkIfNumOfRooms2IsInArray(int numOfRooms2, int[] userRoomNumbersArray,int[] roomsTakenOrFreeArray) { //will check user input while in the while loop against other array values. if not, it will only compare the current array element userRoomNumbersArray[j] and not the other ones
		if((numOfRooms2 > 50) || (numOfRooms2 < 1)) { 
			return true;
		}
	
		for (int i = 0; i < userRoomNumbersArray.length; i++) {
			if (Arrays.asList(userRoomNumbersArray[i]).contains(numOfRooms2)) {
				return true;
			}
			
		}
		for(int j = 1; j < 51; j++) {
			if(roomsTakenOrFreeArray[numOfRooms2] == 0) { //if our numOfRooms2 is 0 in the array
				return true;
			}
		}
		return false;
	}
	
	
	public static int[] getRoomsBoolArrayFromFile() { //get roomsBoolArray.txt values and convert to array
		StringBuilder sb = new StringBuilder();
		String copyWordFromFile = "";
		int[] roomsTakenOrFreeFromFile = new int[51];
		try {
			File file = new File("roomsBoolArray.txt");
			FileReader fileReader = new FileReader(file);
			
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;
			
			
			while ((line = reader.readLine()) != null) {
				copyWordFromFile = reader.readLine(); //copyWordFromFile copies everything from opened file
				copyWordFromFile = copyWordFromFile +"\n"+ line; 
				sb.append(line); //so it won't just copy one line. stringbuilder is used just for this method
				
			}
			reader.close();
		} catch(Exception ex) {
			ex.printStackTrace();	
			}
		String sbString = sb.toString(); //change back to string. Easier to work with.

        for(int i = 0; i < 51; i++) {
			//char aChar = sb.charAt(i);
			if(sbString.charAt(i)=='1') {
				roomsTakenOrFreeFromFile[i] = 1; //copy this string to new primitive array
			}	
			else {
				roomsTakenOrFreeFromFile[i] = 0;
			}
		}
		return roomsTakenOrFreeFromFile;
	}
	
	
	
	
	public void checkBookingsFromFile(String word) {
		StringBuilder sb = new StringBuilder();
		String copyWordFromFile = "";
		String userFileName = word+".txt";
		
		try {
			File file = new File(userFileName);
			FileReader fileReader = new FileReader(file);
			
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;
			
			
			while ((line = reader.readLine()) != null) {
				copyWordFromFile = reader.readLine(); //copyWordFromFile copies everything from opened file
				copyWordFromFile = copyWordFromFile +"\n"+ line; 
				sb.append(line); //so it won't just copy one line. stringbuilder is used just for this method
				
			}
			reader.close();
		} catch(Exception ex) {
			ex.printStackTrace();	
			}
		String sbString = sb.toString();
		
		String [] sbStringArray = sbString.split(",");//remove commas gotten from file txt
		int[] sbStringIntArray= new int[sbStringArray.length];
		for(int i = 0; i < sbStringArray.length; i++) {
			sbStringIntArray[i] = Integer.parseInt(sbStringArray[i]); //convert our array which is still a string to int
		}
		System.out.print("You currently have rooms : ");
		for(int i = 0; i < sbStringIntArray.length; i++) {
			if(i == sbStringIntArray.length-1) {
				System.out.println(sbStringIntArray[i]);
			}
			else {
				System.out.print(sbStringIntArray[i]+", ");
			}
			
		}
	}
	
	public int[] checkBookingsFromFile2(String word) {
		StringBuilder sb = new StringBuilder();
		String copyWordFromFile = "";
		String userFileName = word+".txt";
		
		try {
			File file = new File(userFileName);
			FileReader fileReader = new FileReader(file);
			
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;
			
			
			while ((line = reader.readLine()) != null) {
				copyWordFromFile = reader.readLine(); //copyWordFromFile copies everything from opened file
				copyWordFromFile = copyWordFromFile +"\n"+ line; 
				sb.append(line); //so it won't just copy one line. stringbuilder is used just for this method
				
			}
			reader.close();
		} catch(Exception ex) {
			ex.printStackTrace();	
			}
		String sbString = sb.toString();
		
		String [] sbStringArray = sbString.split(",");//remove commas gotten from file txt
		int[] sbStringIntArray= new int[sbStringArray.length];
		for(int i = 0; i < sbStringArray.length; i++) {
			sbStringIntArray[i] = Integer.parseInt(sbStringArray[i]); //convert our array which is still a string to int
		}
		/* System.out.print("You currently have rooms : ");
		for(int i = 0; i < sbStringIntArray.length; i++) {
			if(i == sbStringIntArray.length-1) {
				System.out.println(sbStringIntArray[i]);
			}
			else {
				System.out.print(sbStringIntArray[i]+", ");
			}
			
		} */
		return sbStringIntArray;
	}
	
	public void makePayment() throws InterruptedException {
		System.out.print("\nEnter card cvs to make payment : ");
	
		Scanner sc4 = new Scanner(System.in);
		int cvs = sc4.nextInt();
		System.out.print("\nPayment successful! ");
		Thread.sleep(3000);   
	}
	
	public void generateReceipt(String userFileName) throws IOException, InterruptedException{
		
		StringBuilder sb = new StringBuilder();
		String copyWordFromFile = "";
		String userFileName2 = userFileName;//so I can add it to  method before it is modified
		String userFileName3 = userFileName;//so I can add it to  method before it is modified
		userFileName = userFileName+".txt";
		String sp = "                                                       |";
		String sp2 = "                ";
		clearScreen();
		
		try {
			File file = new File(userFileName);
			FileReader fileReader = new FileReader(file);
			
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;
			
			
			while ((line = reader.readLine()) != null) {
				copyWordFromFile = reader.readLine(); //copyWordFromFile copies everything from opened file
				copyWordFromFile = copyWordFromFile +"\n"+ line; 
				sb.append(line); //so it won't just copy one line. stringbuilder is used just for this method
				
			}
			reader.close();
		} catch(Exception ex) {
			ex.printStackTrace();	
			}
		String sbString = sb.toString();
		
		String [] sbStringArray = sbString.split(",");//remove commas gotten from file txt
		int[] sbStringIntArray= new int[sbStringArray.length];
		for(int i = 0; i < sbStringArray.length; i++) {
			sbStringIntArray[i] = Integer.parseInt(sbStringArray[i]); //convert our array which is still a string to int
		}
		//,,,,,,
		StringBuilder sb2 = new StringBuilder();
		String userFileNameData = userFileName2+"Data.txt";
		try {
			File file2 = new File(userFileNameData);
			FileReader fileReader2 = new FileReader(file2);
			
			BufferedReader reader2 = new BufferedReader(fileReader2);
			String line2 = null;
			
			
			while ((line2 = reader2.readLine()) != null) {
				copyWordFromFile = reader2.readLine(); //copyWordFromFile copies everything from opened file
				copyWordFromFile = copyWordFromFile +"\n"+ line2; 
				sb2.append(line2); //so it won't just copy one line. stringbuilder is used just for this method
				
			}
			reader2.close();
		} catch(Exception ex) {
			ex.printStackTrace();	
			}
		sbString = sb2.toString();
		
		sbStringArray = sbString.split(",");//remove commas gotten from file txt
		int[] sbStringIntArray2= new int[sbStringArray.length];
		for(int i = 0; i < sbStringArray.length; i++) {
			sbStringIntArray2[i] = Integer.parseInt(sbStringArray[i]); //convert our array which is still a string to int
		}
		
		
		for(int j = 0; j < 14; j++) {
			System.out.print("\n ");
		}
		for(int j = 0; j < 21; j++) {
			System.out.print("--");
		}
		System.out.print("\n"+sp2+"************\n"+sp2+"   SJL Hotel\n"+sp2+"************");
		System.out.print("\n| ");
		
		System.out.println(userFileName3+" rented "+sbStringIntArray.length+" rooms : "+Arrays.toString(sbStringIntArray)+"       $"+Room.cost*sbStringIntArray.length+"                  |");
		System.out.println("|"+sp+"\n|Jacuzzi :   "+sbStringIntArray2[0]+"                      $"+Facilities.Jacuzzi.cost*sbStringIntArray2[0]);
		System.out.println("|"+sp+"\n|Pool :      "+sbStringIntArray2[1]+"                      $"+Facilities.Pool.cost*sbStringIntArray2[1]);
		if(sbStringIntArray2[2]==1) {
			System.out.println("|"+sp+"\n|Meal :                             $10");
		}
		else {
			System.out.println("|"+sp+"\n|Meal :                             No");
		}
		System.out.println("|"+sp+"\n|Extra bed : "+sbStringIntArray2[3]+"                      $"+Facilities.Bed.cost*sbStringIntArray2[3]);
		System.out.println("|"); 
		System.out.print("|");
		calculateTotalCost(sbStringIntArray.length,sbStringIntArray2);
		System.out.print("  |");
		System.out.print("\n\n\n");
		for(int k = 0; k < 21; k++) {
			System.out.print("--");
		}
		
		
	}
	
	
	public void createFacilities() {
		
		Room room = new Room();
		int totalCost = HotelReservation.totalCost; //Display  global variable totalCost
		System.out.print("We currently have ");
		room.printRoomNumber();
		System.out.println(" rooms available and your total cost is "+totalCost);
		System.out.println(" How many rooms do you want? Rooms are $50 each ");
		int userRoomNumbers = 0;
		
		Scanner sc = new Scanner(System.in);
		do {
			userRoomNumbers = sc.nextInt();  //Get how many rooms user wants
			System.out.println(" We currently have " + (50-HotelReservation.takenRooms)+" available");
		}while(userRoomNumbers > (50 - HotelReservation.takenRooms));
		
		
		
		
		room.number--; //Decrease the number of available rooms based on how many the guest wants
		 
		System.out.println("You will be getting "+userRoomNumbers+" rooms and your current charge is "+(userRoomNumbers*50));//fuction to calcualte total charge depending on number of rooms that the guest wants

		System.out.print(" Select what you want to add to your room : \n1. Jacuzzi\n2. Pool\n3. Meal\n4. Extra bed\n");
		
		int userOption = sc.nextInt();
		
		
	}
	
	public void calculateTotalCost(int userRoomNumbersArrayLength, int[] userFacilitiesArray) throws IOException, InterruptedException{//calcualte total cost of all guest expenses
		
		totalCost = (50*userRoomNumbersArrayLength)+(10*userFacilitiesArray[0])+(20*userFacilitiesArray[1])+(10*userFacilitiesArray[2])+(5*userFacilitiesArray[3]);
		System.out.println("\nTotal bill is                      $"+totalCost);
	}
	
	static class Facilities {
		static class Jacuzzi {
			int number = 50;
				static int cost = 10;
			}
			
		static class Pool {
			int number = 10;
			static int cost = 20;
		}
			
		static class Meal {
			String typeMeal = "";
			static int cost = 10;
		}
			
		static class Bed {
			String typeBed = "";
			static int cost = 5;
		}
	}
	
	static class Room {
			int number = 50;
			static int cost = 50;
			
			public void printRoomNumber () {
				System.out.println(number);
			}
			
		}
	
}

public class HotelReservation {
	public static int totalCost = 0;
	public static int[] boolArray = Bookings.getRoomsBoolArrayFromFile();	// initialize a boolean array
	//public static boolean [] boolArray2 = Bookings.getRoomsBoolArrayFromFile(); //first will be for admin to clear rooms.
	public static int takenRooms = 0; //to keep track of how many rooms are available
	//ArrayList<String> listOfDeleted = new ArrayList<String>();
	
	
	public static void main (String args[]) throws IOException, InterruptedException{
		Bookings bookings1 = new Bookings();
		bookings1.clearScreen();
		Scanner sc = new Scanner(System.in);
		System.out.print("To Log in, enter ID or username if you are a guest: ");
		String word = "";
		word = sc.nextLine(); //Get user name to help in opening their files
		
		
		if(word.equals("Stephenm") || word.equals("Joshuao") || word.equals("Leoc")) {
			Bookings bookings = new Bookings();
			bookings.clearScreen();
			System.out.println("********************************");
			System.out.println("You are admin!");
			adminMenu();
		}
		else {
			File tempFile = new File("roomsBoolArray.txt"); 
			boolean exists3 = tempFile.exists();
			if(!exists3) {
				printRoomsBoolArrayToFile(boolArray);
			}
			userMenu(word);
			
			//printRoomsBoolArrayToFile(boolArray);
		}
		
	}
	
	public static void adminMenu() throws IOException, InterruptedException{
		Hotel hotel = new Hotel();
		System.out.println(hotel.id+" Management Terminal");
		Bookings bookings = new Bookings();
		Scanner sc = new Scanner(System.in);
		System.out.println("1. See room availability\n2. Check guest out\n3. Generate guest receipt\n4. Empty rooms\n********************************");
		int adminChoice = sc.nextInt();
		if(adminChoice == 4) {
			emptyRooms();
		}
		else if(adminChoice == 3) {
			System.out.print("Enter username of guest to generate receipt for : ");
			Scanner sc2 = new Scanner(System.in);
			String adminChoice2 = sc2.nextLine();
			bookings.generateReceipt(adminChoice2);
		}
		else if(adminChoice == 2) {
			System.out.print("Enter username of guest to check out : ");
			Scanner sc3 = new Scanner(System.in);
			String adminChoice3 = sc3.nextLine();
			deleteFile(adminChoice3);
		}
		else if(adminChoice == 1) {
			checkIfRoomsAreFree(boolArray);
		}
		else {
			System.out.println("\nYou did not selecet a valid number.");
		}
		
	}
	
	public static void userMenu(String word) throws IOException, InterruptedException{
		Hotel hotel = new Hotel();
		
		String userMenuWord = word+".txt";
		File tempFile = new File(userMenuWord); 
		boolean exists = tempFile.exists();//check if user already has a database with us
		

		if(exists) { //if user has a database
//if(listOfDeleted.contains(userMenuWord)) {
				
			//}
			
			Bookings bookings = new Bookings();
			bookings.clearScreen();
			System.out.println("\n\n\n\n\n\n\nHi, "+word+". Welcome back to "+hotel.id);
			bookings.checkBookingsFromFile(word); // call checkBookingsFromFile. It is the method that will open user database with the name they provided
			System.out.println("*************************************\n1. Alert Management \n2. Check rooms availability\n3. View or print receipt\n4. Check out\n********************************");
			
			Scanner sc = new Scanner(System.in);
			int userChoice = sc.nextInt();
			if(userChoice == 1) {
				System.out.println("\n\nManagement alerted. A staff will be with you shortly.");
			}
			else if(userChoice == 3) {
				bookings.generateReceipt(word);
			}
			else if(userChoice == 4) {
				deleteFile(word);
			}
			else if(userChoice == 2) {
				checkIfRoomsAreFree(boolArray);
			}
		}
		else {
			Bookings bookings = new Bookings();
			bookings.clearScreen();
			String sp=("                                                   ");
			System.out.println("Hi, "+word+"!\n"+sp+"*******************************\n"+sp+"    Welcome to "+hotel.id+"\n"+sp+"*******************************\n\n");
			
			if (checkIfRoomsAreFree(boolArray)) {
				Scanner sc = new Scanner(System.in);
				System.out.print("Press y to book room(s) or any other letter to exit : ");
				String yesNo = sc.nextLine(); //Get user name to help in opening their files
				if(yesNo.equals("y")) {
					
					bookings.getUserRoomsAndFacilities(word); //create main user txt
				}
				
			}
			else {
				System.out.println("Sorry, rooms are all taken up");
			}
			
		}
		
		
		
	}
	
	public static void emptyRooms() throws IOException{ //admin only.  empty all rooms 
		System.out.println("\nWARNING! You are about to evacuate all guests and make all rooms empty\nEnter y to proceed or any other character to cancel");
		Scanner sc = new Scanner(System.in);
		String yn = sc.nextLine();
		
		if(yn.equals("y")) {
			System.out.println("\nRooms emptied successfully!  \nAll 50 rooms and facilities now available\n");
			printRoomsBoolArrayToFile(boolArray);
		}
		
	}
	
	public static boolean checkIfRoomsAreFree(int[] boolArray) { //check rooms availability
		for(int i = 1; i < boolArray.length; i++) {
			
			if(i % 6 == 0) {
					System.out.println("\n");
			}
				
			if(boolArray[i] == 0) { //for every taken room, add 1 to takenRooms. If takenRooms is up to 50, that means all rooms are taken
				takenRooms++;
				
				System.out.print("*Room "+i+" is taken*       ");
				
			}
			else {
				System.out.print("[Room "+i+" is available]   ");
			}
			
			
			if(takenRooms > 49) {
				return false;
			}
			
		}
		int freeRooms = 50-takenRooms;
		System.out.println("\n\n\n                We have "+freeRooms+" rooms available ");
		System.out.println("\n\n\n");
		return true;
	}
	
	public static void printRoomsBoolArrayToFile(int [] boolArray) throws IOException { //save room availability to file. 0 means taken
	
		File tempFile = new File("roomsBoolArray.txt"); 
		FileWriter fw = new FileWriter(tempFile,false); //false so it can NOT append to the file
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
			
			for(int i = 0; i<51; i++) {
				if(i==0) {
					pw.print("0");
				}
				pw.print("1");
				
			}
			
		pw.close();
		
	}
	
	public static void deleteFile(String word) { //delete user file and make their room empty
		String userMainFile = word+".txt";
		String userMainFile2 = word+"Data.txt";
		File file = new File(userMainFile);
		File file2 = new File(userMainFile2);
		Bookings bookings = new Bookings();
		int[] userData = bookings.checkBookingsFromFile2(word);
		System.out.println("\nBooked rooms were : "+Arrays.toString(userData));
        
        file.delete();
		file2.delete();
       
        System.out.println("\nChecked out successfully");
		
		int[] roomsBoolArray2 = bookings.getRoomsBoolArrayFromFile(); // just to work with deleting user room(making them 1)
		for(int i = 0; i < userData.length; i++) {
			roomsBoolArray2[userData[i]] = 1;
		} //making a way to pass contents of userData array to roomsBoolArray2
		 //make index of the checked out room 1(free). so we can print it back to roomsBoolArray.txt
		updateRoomsBoolArrayTxtWithRoomsBoolArray2(roomsBoolArray2);
		
	}
	
	public static void updateRoomsBoolArrayTxtWithRoomsBoolArray2(int [] roomsBoolArray2) {
		try {
			File tempFile = new File("roomsBoolArray.txt"); 
			FileWriter fw = new FileWriter(tempFile,false); //false so it can NOT append to the file
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			for(int i = 0; i<51; i++) {
				if(roomsBoolArray2[i] == 1) {
					pw.print("1");
				}
				else {
					pw.print("0");
				}
				
			}
			
			pw.close();
		}catch(Exception ex) {
			ex.printStackTrace();	
		}
	}
	
	
}
