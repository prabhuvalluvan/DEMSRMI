import java.rmi.*;
import java.text.ParseException;


public interface DEMSInterface extends Remote{
	
	public String addEvent(String eventID, String eventtype, int capacity) throws RemoteException;
	public String removeEvent(String eventID, String eventtype) throws RemoteException, ParseException;
	public String listEvent(String eventtype) throws RemoteException;
	public String bookEvent(String custID, String eventID, String eventtype) throws RemoteException, ParseException;
	public String getSchedule(String custID) throws RemoteException;
	public String cancelEvent(String custID, String eventID, String eventtype) throws RemoteException;

}
