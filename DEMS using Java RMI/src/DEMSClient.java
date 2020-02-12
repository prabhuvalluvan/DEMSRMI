
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class DEMSClient 
{
	static Logger log=Logger.getLogger(DEMSClient.class.getName());
	static FileHandler file;
	static SimpleFormatter formatter = new SimpleFormatter();
	@SuppressWarnings("unused")
	public static void main(String args[]) throws Exception
	{
		Scanner sc=new Scanner(System.in);
		String managerID,custID,eventID,eventtype,tempID;
		int capacity,option;
		Registry registry = LocateRegistry.getRegistry(7777);
		DEMSInterface obj=null;
		MTLServer tempobj=null;
		
		
		
		//file=new FileHandler("/home/vijay/java/SAMPLEDEMSRMI/src/clientlogging",true);
		//log.addHandler(file);
		//SimpleFormatter formatter = new SimpleFormatter();  
		//file.setFormatter(formatter); 
		
		System.out.println("Enter your ID: ");
		tempID=sc.nextLine();
		
		if(tempID.contains("MTL"))
		{
			//registry = LocateRegistry.getRegistry(7777);
			obj = (DEMSInterface) registry.lookup("MTLServer");
		}
		else if(tempID.contains("QUE"))
		{
			//registry = LocateRegistry.getRegistry(7777);
			obj = (DEMSInterface) registry.lookup("QUEServer");
		}
		else if(tempID.contains("SHE"))
		{
			//registry = LocateRegistry.getRegistry(9999);
			obj = (DEMSInterface) registry.lookup("SHEServer");
		}
		
	
		
		if(tempID.charAt(3)=='M')
		{
			managerID=tempID;
			file=new FileHandler("/home/vijay/java/SAMPLEDEMSRMI/src/client"+managerID+".txt",true);
			log.addHandler(file);
			file.setFormatter(formatter); 
		while(true)
		{
			System.out.println("\nEnter the necessary operation:\n 1.Add Event\n 2.Remove Event\n 3.List Event Availability\n 4.Do customer service\n 5.EXIT ");
			option=sc.nextInt();
			
		
			switch(option)
			{
			case 1:
				System.out.println("Enter the Event Type: ");
				eventtype=sc.next();
				System.out.println("Enter the Event ID: ");
				eventID=sc.next();
				System.out.println("Enter the Capacity: ");
				capacity=sc.nextInt();
				if(managerID.substring(0,3).equals(eventID.substring(0,3)))
				{
					String msg=obj.addEvent(eventID, eventtype, capacity);
					log.info("\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\nCapacity:"+capacity+"\n"+msg);
					System.out.println(msg);
				}
				else
				{
					String msg="you can not add events for other cities";
					log.info(msg);
					System.out.println(msg);
				}
				
				break;
				
			case 2:
				System.out.print("Enter the Event Type: ");
				eventtype=sc.next();
				System.out.print("Enter the Event ID: ");
				eventID=sc.next();
				if(managerID.substring(0,3).equals(eventID.substring(0,3)))
				{
					String msg=obj.removeEvent(eventID, eventtype);
					log.info("\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg);
					System.out.println(msg);
				}
				else
				{
					String msg="you can not remove events from other cities";
					log.info(msg);
					System.out.println(msg);
				}
				
				break;
				
			case 3:
				System.out.print("Enter the Event Type: ");
				eventtype=sc.next();
				//String callee="manager";
				String msg=obj.listEvent(eventtype);
				log.info("\nEvent Type:"+eventtype+"\nlist of events available displayed");
				System.out.println(msg);
				break;
				
			case 4:
				System.out.print("Enter the Customer ID: ");
				custID=sc.next();
				if(!(custID.substring(0,3).equals(managerID.substring(0,3))))
				{
					String msg1="You cant perform service for other city customers";
					log.info(msg1);
					System.out.println(msg1);
					break;
				}
				else
				{
					while(true)
					{
						String msg1=customerfun(custID,obj);
					
					//System.out.println(msg1+" customers operations");
						if(msg1.equals("completed"))
						{
							break;
						}
					}
					log.info("\nCustomer ID:"+custID+"\nServices performed");
				}
			case 5:
				System.exit(0);
				
			}
		}
		}

		else if(tempID.charAt(3)=='C')
		{
			custID=tempID;
			file=new FileHandler("/home/vijay/java/SAMPLEDEMSRMI/src/client"+custID+".txt",true);
			log.addHandler(file);
			file.setFormatter(formatter); 
			while(true)
			{
				String msg1=customerfun(custID,obj);
				System.out.println(msg1);
				if(msg1.equals("completed"))
				{
					System.exit(0);
				}
			}
		}

		sc.close();
	}
	
	
	public static String customerfun(String custID,DEMSInterface obj) throws RemoteException, ParseException
	{
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(System.in);

		String eventID,eventtype;
		
			System.out.println("\nEnter the necessary operation:\n 1.Book Event\n 2. Get Schedule\n 3.cancel Event\n 4.EXIT ");
			int option=sc.nextInt();
			
		
			switch(option)
			{
			case 1:
				System.out.println("Enter the Event Type: ");
				eventtype=sc.next();
				System.out.println("Enter the Event ID: ");
				eventID=sc.next();
				String msg= obj.bookEvent(custID,eventID, eventtype);
				log.info("\nCustomer ID:"+custID+"\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg);
				return msg;
				
			case 2:
				String msg1= obj.getSchedule(custID);
				log.info("\nCustomer ID:"+custID+"\nschedule displayed");
				return msg1;
				
			case 3:
				System.out.println("Enter the Event Type: ");
				eventtype=sc.next();
				System.out.println("Enter the Event ID: ");
				eventID=sc.next();
				String msg2= obj.cancelEvent(custID,eventID, eventtype);
				log.info("\nCustomer ID:"+custID+"\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg2);
				return msg2;
			case 4:
				return "completed";
			}
			return "not done anything";
		
	}
	
	
	
	
	
	


		
}



