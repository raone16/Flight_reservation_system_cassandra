package FBS;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import FBS.FlightDetails;
public class BookingSystem {
	static Scanner input = new Scanner(System.in);
        static List<FlightDetails> l = new ArrayList<FlightDetails>();
        static void addflight() {
        	FlightDetails f =new FlightDetails();
        	l.add(f);
        }
        static FlightDetails flightNo(int flightNo) {
        	FlightDetails f;
        
	 		try {
	 			f=l.get(flightNo-101);
	 		}
	 		catch(Exception e) {
	 			return null;
	 		}
	 		
	 		return f;
        	
        }
        static FlightDetails flightNo() {
        	FlightDetails f;
        
        	int flightNo;
        	System.out.println("Enter the flight no");
	 		flightNo=input.nextInt();
	 		try {
	 			f=l.get(flightNo-101);
	 		}
	 		catch(Exception e) {
	 			return null;
	 		}
	 		
	 		return f;
        	
        }
        static List<Integer> availableFlight(){
        	List<Integer> availflight = new ArrayList<Integer>();
        	FlightDetails f;
        	for(int i=0;i<l.size();i++) {
        		f=l.get(i);
        		availflight.add(f.getFlightNo());
        		
        	}
        	return availflight;
            
        } 
        static void bookingDetail(int bId) {
        	boolean flag;
        	for(FlightDetails f: l) {
        		flag=f.displayBookingSummary(bId);
        		if(flag)
        			return;
        	}
        	System.out.println("invalid booking id");
        	
        	
        }
        
        public static Session connectToDb() {
        	BasicConfigurator.configure();
    		Cluster cluster;
    		Session session;
    		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
    		session = cluster.connect("flight");
    		return session;
    		}
	public static void updateOld(Session con) {
		
		FlightDetails f=null;
		int tempId;
		
	    String query ="SELECT * from booking";
	    
	 
	    try {
	        com.datastax.driver.core.ResultSet rs=con.execute(query);
	        for (Row row: rs) {
	      
	        	tempId = row.getInt("flightNo")-101;
	        	//System.out.println("flight id"+tempId);
	        	for(int i=0;i<=tempId;i++) {
	        	try{f=l.get(i);}
    			catch(Exception e) {
    				addflight();
    				//System.out.println(i);
    				f=l.get(i);
    			}
	        	}
	        	List<Integer> seats=row.getList("seats", Integer.class);
    			
  			//System.out.println(seats+" "+rs.getDouble("cost")+" "+(rs.getInt("meal")==1 ? true:false)+" "+(seats.get(0)<6)+" "+(rs.getInt("status")==1 ? true:false));
  			f.makeBooking(seats,row.getDouble("cost"),(row.getBool("meal")),(seats.get(0)<6),row.getBool("status"),row.getInt("BookId"));
	    	
	    }}
	    catch(Exception e) {
	    	e.printStackTrace();
	    	
	    	System.out.println(e.getMessage());
	    }
	    
		
	}
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		int bookingid,ch=1;
		int noOfSeats;
		int Pref;
		boolean mealPref,classPref;
		FlightDetails f;
		Session con =connectToDb();
		
		if (con != null) {
 			System.out.println("nSuccessfullly connected to Oracle DB");
 		
 		} else {
 			System.out.println("nFailed to connect to Oracle DB");
 			
 		}
		updateOld(con);
		
			
	while(ch!=0) {
		System.out.print("1.addFilght 2.checkAvailablity \n"
				+ "3.BookTicket 4.cancelTicket 5.mealPref\n 6.BookingSummary"
				+ " 7.Avialble FlightNo 8.Booking Details or enter 0 to end");
		 ch = input.nextInt();
		 
		 switch(ch) {
		 	case 0:
			    con.close();
			    return;
		 		//break;
		 	case 1:
		 		addflight();
		 		break;
		 	case 2:
		 		f=flightNo();
		 		if(f==null) {
		 			System.out.println("invalid flight no");break;
		 		}
		 		System.out.println(f.availability());
		 		break;
		 		
		 	case 3:
		 		f=flightNo();
		 		if(f==null) {
		 			System.out.println("invalid flight no");break;
		 		}
		 		System.out.println("Enter no of seats");
		 		noOfSeats = input.nextInt();
		 		System.out.println("Enter 1 for Business 0 for Economy");
		 		System.out.println("seat no 0-5 Business class 6-14 EconomyClass");
		 		Pref = input.nextInt();
		 		classPref = (Pref==1)? true:false;
		 		System.out.println("Enter 1 for meal 0 for not");
		 		Pref = input.nextInt();
		 		mealPref = (Pref==1)? true:false;
		 		//List<Integer>seats;
		 		f.bookTicket(noOfSeats, classPref, mealPref);
		 		//writeToDb(con,seats);
		 		break;
		 	case 4:

		 		f=flightNo();
		 		if(f==null) {
		 			System.out.println("invalid flight no");break;
		 		}
		 		System.out.println("Enter the booking id");
		 		bookingid = input.nextInt();
		 		f.cancelBooking(bookingid);
		 		break;
		 	case 5:
		 		f=flightNo();
		 		if(f==null) {
		 			System.out.println("invalid flight no");break;
		 		}
		 		System.out.println("Meal Prefernce"+f.mealPref());
		 		break;
		 	case 6:
		 		f= flightNo();
		 		if(f==null) {
		 			System.out.println("invalid flight no");break;
		 		}
		 		f.displayBookingSummary();
		 		break;
		 	case 7:
		 		System.out.println(availableFlight());
		 		break;
		 	case 8:
		 		 System.out.println("Enter the booking ID");
		 		 bookingid = input.nextInt();
		 		 bookingDetail(bookingid);
		 		 
		 		 break;
		 	default:
		 		System.out.println("invalid choice");
		 		
		 }
		 
	}
	input.close();
	}

}
