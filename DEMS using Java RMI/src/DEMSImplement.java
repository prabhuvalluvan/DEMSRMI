import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.TreeSet;


@SuppressWarnings("serial")
public class DEMSImplement extends UnicastRemoteObject implements DEMSInterface 
{


	HashMap<String,HashMap<String,Integer>> map1= new HashMap<>();
	HashMap<String,HashMap<String,ArrayList<String>>> custdetails1= new HashMap<>();
	//private static final HashMap<String,ArrayList<String>> custbookedevents=new HashMap<>(); 
	
	String servername;
	
	public DEMSImplement() throws RemoteException {
		super();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String addEvent(String eventID, String eventtype, int capacity) throws RemoteException 
	{
		String msg="not successful";
		if(map1.containsKey(eventtype))
		{
			if(map1.get(eventtype).containsKey(eventID))
			{
				map1.get(eventtype).replace(eventID, capacity);
				msg="capcity updated";
			}
			else
			{
				map1.get(eventtype).put(eventID, capacity);
				msg="event added succesfully";
			}
		}
		else
		{
			map1.put(eventtype,new HashMap(){{put(eventID,capacity);}});
			msg="event added succesfully";
		}
		System.out.print(map1);
		return msg;
		
	}

	@Override
	public String removeEvent(String eventID, String eventtype) throws RemoteException, ParseException
	{
		String msg="remove not successful";
		if(eventID.substring(0,3).equals(this.servername.substring(0,3)))
		{
				if(map1.containsKey(eventtype))
				{
					if(map1.get(eventtype).containsKey(eventID))
					{
						ArrayList<String> bookedcustomers=new ArrayList<String>();
						TreeSet<Date> nexteventsdates=new TreeSet<Date>();
						String neweventID = null;
						String nexteventID=null;
						System.out.println("original eventid: "+eventID);
						//booked customers from first own server
						if(!custdetails1.isEmpty())
						{
							for(String keyi:custdetails1.keySet())
							{
					    			for(String keyk:custdetails1.get(keyi).get(eventtype))
							    	{
					    				if(keyk.equals(eventID))
							    		{
					    					bookedcustomers.add(keyi);
							    		}
							    	}
							}
						}
						System.out.println("booked customers of that event: "+bookedcustomers.toString());
						
						//changing the eventid for comparing during next event finding
						if(eventID.charAt(3)=='M') { neweventID=eventID.substring(4)+"09";}
						if(eventID.charAt(3)=='A') { neweventID=eventID.substring(4)+"13";}
						if(eventID.charAt(3)=='E') { neweventID=eventID.substring(4)+"17";}
						System.out.println("changed eventid: "+neweventID);
						Date newdate=new SimpleDateFormat("ddMMyyhh").parse(neweventID); 
						
						//event ids of the given eventtype
						for(String keyi:map1.get(eventtype).keySet())
						{
							String newvalue = null;
							if(keyi.charAt(3)=='M') {newvalue=keyi.substring(4)+"09";}
							if(keyi.charAt(3)=='A') {newvalue=keyi.substring(4)+"13";}
							if(keyi.charAt(3)=='E') {newvalue=keyi.substring(4)+"17";}
							Date nextdate=new SimpleDateFormat("ddMMyyhh").parse(newvalue);
							nexteventsdates.add(nextdate);
						}
						System.out.println("available events of that eventtype: "+nexteventsdates);
						//selecting next available eventid for all booked customers
						if(!bookedcustomers.isEmpty())
						{
							for(String bookedcust:bookedcustomers)
							{
								//nexteventid=null;
								if(!nexteventsdates.isEmpty())
								{
								for(Date nexteventdate:nexteventsdates)
								{
									if(nexteventdate.compareTo(newdate)<0)
									{
										continue;
									}
									if(nexteventdate.equals(newdate))
									{
										continue;
									}
									if(nexteventdate.compareTo(newdate)>0)
									{
										
										Date nedate=nexteventdate;
										DateFormat dateFormat = new SimpleDateFormat("ddMMyyhh");  
										String nextevent = dateFormat.format(nedate);  
										
										String nextevent1 = null;
										
										if(nextevent.endsWith("09")) {nextevent1=this.servername.substring(0,3)+"M"+nextevent.substring(0,nextevent.length()-2);}
										if(nextevent.endsWith("01")) {nextevent1=this.servername.substring(0,3)+"A"+nextevent.substring(0,nextevent.length()-2);}
										if(nextevent.endsWith("05")) {nextevent1=this.servername.substring(0,3)+"E"+nextevent.substring(0,nextevent.length()-2);}
										System.out.println("next eventid: "+nextevent1);
										String result=bookEvent(bookedcust,nextevent1,eventtype);
										//nexteventID=nextevent;
										if(result.equals("event booked succesfully"))
										{
											break;
										}
									}
								}
								}
								
							}
						}
						
						
						
						
						nexteventID="dummy";
						if(!nexteventsdates.isEmpty())
						{
							for(Date nexteventdate:nexteventsdates)
							{
								if(nexteventdate.compareTo(newdate)<0)
								{
									continue;
								}
								if(nexteventdate.equals(newdate))
								{
									continue;
								}
								if(nexteventdate.compareTo(newdate)>0)
								{
									Date nedate=nexteventdate;
									DateFormat dateFormat = new SimpleDateFormat("ddMMyyhh");  
									String nextevent = dateFormat.format(nedate);  
									
									String nextevent1 = null;
									
									if(nextevent.endsWith("09")) {nextevent1=this.servername.substring(0,3)+"M"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("01")) {nextevent1=this.servername.substring(0,3)+"A"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("05")) {nextevent1=this.servername.substring(0,3)+"E"+nextevent.substring(0,nextevent.length()-2);}
									//String result=bookEvent(bookedcust,nextevent,eventtype);
									nexteventID=nextevent1;
									System.out.println("next event id for others:"+nexteventID);
									
									//if(result.equals("event booked succesfully"))
									//{
										break;
									//}
									//booked customers from other server
								}
							}
						}
						String[] senddata=new String[6];
						String fromothers=null;
						
						switch(this.servername)
						{
						case "MTLServer":
							senddata[0]="QUEServer";
							senddata[1]="SHEServer";
							break;
						case "QUEServer":
							senddata[0]="MTLServer";
							senddata[1]="SHEServer";
							break;

						case "SHEServer":
							senddata[0]="MTLServer";
							senddata[1]="QUEServer";
							break;

						}
						
						senddata[2]="removeEvent";
						senddata[3]=eventtype;
						senddata[4]=eventID.concat(nexteventID);  //eventID
						senddata[5]=null;  //CustID
						
						fromothers=this.runUDPClient(senddata);
						System.out.println("from others reponse:"+fromothers);
						
						
						//removing from locals
						map1.get(eventtype).remove(eventID);
						for(String keyi:custdetails1.keySet())
						{
							custdetails1.get(keyi).get(eventtype).remove(eventID);
							if(custdetails1.get(keyi).get(eventtype).isEmpty()) 
							{
								custdetails1.get(keyi).remove(eventtype);
							}
							if(custdetails1.get(keyi).isEmpty()) 
							{
								custdetails1.remove(keyi);
							}
						}
						msg="removed successfully";
					}
					else
					{
						msg="eventid doesnt exist";
					}
				}
				else
				{
					msg ="the event doesnt exist";
				}
		}
		else if(eventID.substring(0,4).equals("temp"))
		{
			
			String removeeventID=eventID.substring(4,14);
			String nexteventID=eventID.substring(14);
			System.out.println("HAI from "+ this.servername); 
			System.out.println("eventID is "+removeeventID );
			System.out.println("nexteventID is "+nexteventID );
			ArrayList<String> bookedcustomers=new ArrayList<String>();
			for(String keyi:custdetails1.keySet())
			{
	    		for(String keyk:custdetails1.get(keyi).get(eventtype))
	    		{
	    			if(keyk.equals(removeeventID))
			    	{
	    				bookedcustomers.add(keyi);
			    	}
			    }
			}
			System.out.println("booked customers of that event: "+bookedcustomers.toString());
			if(!bookedcustomers.isEmpty())
			{
				for(String keyi:custdetails1.keySet())
				{
					custdetails1.get(keyi).get(eventtype).remove(removeeventID);
					if(custdetails1.get(keyi).get(eventtype).isEmpty()) 
					{
						custdetails1.get(keyi).remove(eventtype);
					}
					if(custdetails1.get(keyi).isEmpty()) 
					{
						custdetails1.remove(keyi);
					}
				}
				for(String bookedcust:bookedcustomers)
				{
					if(nexteventID.equals("dummy"))
					{
						return "no1";
					}
					
					else
					{
						String result=bookEvent(bookedcust,nexteventID,eventtype);
						System.out.println(result);
					}
				}
				
				msg="success";
			}
			else
			{
				msg="no2";
			}
			
		}
		else
		{
			msg ="no3";
		}
			
		return msg;
		
	}
	
	
	@Override
	public String listEvent(String eventtype) throws RemoteException 
	{
		StringBuilder finaldata=new StringBuilder();
		StringBuilder fromfirst=new StringBuilder(eventtype+":\n"+this.servername.substring(0,3)+": ") ;
		//if(callee.charAt(3)=='M')
		if(eventtype.equals("SEMINARS")||eventtype.equals("CONFERENCES")||eventtype.equals("TRADESHOWS"))
		{
			if(map1.containsKey(eventtype))
			{
				fromfirst.append(map1.get(eventtype)+",\n");
			}
			else
			{
				fromfirst.append("\n");
			}
			
			finaldata.append(fromfirst);
			System.out.println("data from first server: "+finaldata.toString());
			
			String[] senddata=new String[6];
			String fromothers=null;
		
			String servername=this.servername;
			System.out.println("object servername : "+servername);
			switch(servername)
			{
			case "MTLServer":
				senddata[0]="QUEServer";
				senddata[1]="SHEServer";
				break;
			case "QUEServer":
				senddata[0]="MTLServer";
				senddata[1]="SHEServer";
				break;

			case "SHEServer":
				senddata[0]="MTLServer";
				senddata[1]="QUEServer";
				break;

			}
			
			senddata[2]="listEvent";
			senddata[3]=eventtype;
			senddata[4]=null;  //eventID
			senddata[5]=null;  //CustID
			
			fromothers=this.runUDPClient(senddata);
			System.out.println("data got: "+fromothers);
			
			
			finaldata.append(fromothers);
			System.out.println("final data got: "+finaldata.toString());
			
			return finaldata.toString();
		}
		
		//else if(callee.equals("OtherServer"))
		else if(eventtype.equals("SEMINARStemp")||eventtype.equals("CONFERENCEStemp")||eventtype.equals("TRADESHOWStemp"))
		{
			System.out.println("getting data for other servers");
			String neweventtype=eventtype.substring(0, eventtype.length() - 4);
			if(map1.containsKey(neweventtype))
			{
				System.out.println("CONTENTS: "+map1.get(neweventtype)+", ");
				return this.servername.substring(0,3)+": "+map1.get(neweventtype)+", ";
			}
			else
			{
				return "\n";
			}
		}
		
		else
		{
			return null;
		}
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String bookEvent(String custID, String eventID, String eventtype) throws RemoteException, ParseException 
	{
		
		
		String msg="not successful";
		
		if(custID.substring(0,3).equals((eventID).substring(0,3)))
		{
			if(map1.get(eventtype).containsKey(eventID) && map1.get(eventtype).get(eventID)>0)
			{
				if(custdetails1.containsKey(custID))
				{
					for(String keyi:custdetails1.get(custID).keySet())
					{
						for(String keyj:custdetails1.get(custID).get(keyi))
						{
							if(keyj.substring(3).equals(eventID.substring(3)))
							{
								if(keyj.equals(eventID))
								{
									return "you have booked this event already";
								}
								return "you have booked another event in the same slot";
							}
						}
					}
					if(custdetails1.get(custID).containsKey(eventtype))
					{
						if(custdetails1.get(custID).get(eventtype).contains(eventID.toString()))
						{
							msg="you have booked this event already";
						}
						else
						{
							int temp=(map1.get(eventtype).get(eventID))-1;
							map1.get(eventtype).replace(eventID, temp);
							
							custdetails1.get(custID).get(eventtype).add(eventID.toString());
							msg="event booked succesfully";
						}
					}
					else
					{
						ArrayList<String> al1=new ArrayList<>();
						al1.add(eventID);
						int temp=(map1.get(eventtype).get(eventID))-1;
						map1.get(eventtype).replace(eventID, temp);
					
						custdetails1.get(custID).put(eventtype, al1);
						msg="event booked succesfully";
					}
					
				}
				else
				{
					ArrayList<String> al2=new ArrayList<>();
					al2.add(eventID);
					custdetails1.put(custID,new HashMap(){{put(eventtype,al2);}});
					int temp=(map1.get(eventtype).get(eventID))-1;
				
					map1.get(eventtype).replace(eventID, temp);
					msg="event booked succesfully";
				}
			}
			else
			{
				msg="event is not available";
			}
		}
		else if(!(custID.substring(0,3).equals((eventID).substring(0,3)))&& (eventID.substring(0,3).equals(this.servername.substring(0,3))))
		{
			
			if(map1.get(eventtype).containsKey(eventID) && map1.get(eventtype).get(eventID)>0)
			{
				int temp=(map1.get(eventtype).get(eventID))-1;
				map1.get(eventtype).replace(eventID, temp);
				msg="successfull";
			}
			else
			{
				msg="notsuccessfull";
			}
			
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			String tempdate1=eventID.substring(4);
			Date date1=new SimpleDateFormat("ddMMyy").parse(tempdate1); 
		    cal.setTime(date1);
		    int week1 =cal.get(Calendar.WEEK_OF_YEAR);
		    
		    
		    if(custdetails1.get(custID)!=null && !custdetails1.get(custID).isEmpty())
		    {
		    	int othercitybookings=0;
		    	for(String keyi:custdetails1.get(custID).keySet())
		    	{
		    		for(String keyj:custdetails1.get(custID).get(keyi))
			    	{
			    		if(!(keyj.substring(0,3).equals(this.servername.substring(0,3))))
			    		{
			    			String tempdate2=keyj.substring(4);
			    			Date date2=new SimpleDateFormat("ddMMyy").parse(tempdate2); 
			    			cal.setTime(date2);
			    		    int week2 =cal.get(Calendar.WEEK_OF_YEAR);
			    		    if(week1==week2)
			    		    {
			    		    	othercitybookings++;
			    		    }
			    		}
			    	}
		    	}
		    	if(othercitybookings>=3)
		    	{
		    		msg="you cant book more than 3 events in a week from other cities";
		    		return msg;
		    	}

		    }
		    String[] senddata=new String[6];
    		
			switch(eventID.substring(0,3))
			{
			case "MTL":
				senddata[0]="MTLServer";
				senddata[1]=null;
				break;
			case "QUE":
				senddata[0]="QUEServer";
				senddata[1]=null;
				break;

			case "SHE":
				senddata[0]="SHEServer";
				senddata[1]=null;
				break;

			}
			
			senddata[2]="bookEvent";
			senddata[3]=eventtype; //eventtype
			senddata[4]=eventID;  //eventID
			senddata[5]=custID;  //CustID
			
			if(custdetails1.containsKey(custID))
			{
				for(String keyi:custdetails1.get(custID).keySet())
				{
					for(String keyj:custdetails1.get(custID).get(keyi))
					{
						if(keyj.substring(3).equals(eventID.substring(3)))
						{
							if(keyj.equals(eventID))
							{
								return "you have booked this event already";
							}
							return "you have booked another event in the same slot";
						}
					}
				}
				if(custdetails1.get(custID).containsKey(eventtype))
				{
					if(custdetails1.get(custID).get(eventtype).contains(eventID.toString()))
					{
						msg="you have booked this event already";
					}
					else
					{
						String replymsg=this.runUDPClient(senddata);
						System.out.println("replymessage111111: "+replymsg);
						if(replymsg.trim().equals("successfull"))
						{
							custdetails1.get(custID).get(eventtype).add(eventID.toString());
							msg="event booked succesfully";
						}
						else if(replymsg.trim().equals("notsuccessfull"))
						{
							msg="event is not available";
						}
					}
				}
				else
				{
					String replymsg=this.runUDPClient(senddata);
					System.out.println("replymessage2222222: "+replymsg);
					if(replymsg.trim().equals("successfull"))
					{
						ArrayList<String> al1=new ArrayList<>();
						al1.add(eventID);
						custdetails1.get(custID).put(eventtype, al1);
						System.out.println("IM PUTTING NEW EVENT FOR CUSTOMER");
						msg="event booked succesfully";
					}
					else if(replymsg.trim().equals("notsuccessfull"))
					{
						msg="event is not available";
					}
				}
			}
			else
			{
				String replymsg=this.runUDPClient(senddata);
				System.out.println("replymessage333333: "+replymsg);
				if(replymsg.trim().equals("successfull"))
				{
					ArrayList<String> al2=new ArrayList<>();
					al2.add(eventID);
					System.out.println("IM PUTTING ");
					custdetails1.put(custID,new HashMap(){{put(eventtype,al2);}});
					System.out.println("IM PUTTING NEW CUSTOMER");
					msg="event booked succesfully";
				}
				else if(replymsg.trim().equals("notsuccessfull"))
				{
					msg="event is not available";
				}
			}
		}
		System.out.print(map1);
		System.out.print(custdetails1);
		return msg;
		
	}

	@Override
	public String getSchedule(String custID) throws RemoteException 
	{
		if(custdetails1.containsKey(custID))
		{
			return (custID +" Your upcoming events are:\n" + custdetails1.get(custID));
		}	
		else
		{
		return "you have no event bookings";
		}
		
	}

	@Override
	public String cancelEvent(String custID, String eventID, String eventtype) throws RemoteException 
	{
		String msg="not successful";
		if(custID.substring(0,3).equals((eventID).substring(0,3)))
		{
			if(custdetails1.containsKey(custID))
			{
				if(custdetails1.get(custID).containsKey(eventtype))
				{
					if(custdetails1.get(custID).get(eventtype).contains(eventID.toString()))
					{
					int temp=(map1.get(eventtype).get(eventID))+1;
					map1.get(eventtype).replace(eventID, temp);
					custdetails1.get(custID).get(eventtype).remove(eventID.toString());
					msg="event cancelled succesfully";
					}
					else
					{
						msg="event not already booked by you";
					}
				}
				else
				{
					msg="no such event available in your schedule";
				}	
			}
			else
			{
				msg="event not already booked by you";
			}
		}
		else if(!(custID.substring(0,3).equals((eventID).substring(0,3)))&& (eventID.substring(0,3).equals(this.servername.substring(0,3))))
		{
			
			if(map1.get(eventtype).containsKey(eventID))
			{
				int temp=(map1.get(eventtype).get(eventID))+1;
				map1.get(eventtype).replace(eventID, temp);
				msg="successfull";
			}
			else
			{
				msg="notsuccessfull";
			}
			
		}
		else
		{
			String[] senddata=new String[6];
		
			switch(eventID.substring(0,3))
			{
			case "MTL":
				senddata[0]="MTLServer";
				senddata[1]=null;
				break;
			case "QUE":
				senddata[0]="QUEServer";
				senddata[1]=null;
				break;

			case "SHE":
				senddata[0]="SHEServer";
				senddata[1]=null;
				break;

			}
			
			senddata[2]="cancelEvent";
			senddata[3]=eventtype; //eventtype
			senddata[4]=eventID;  //eventID
			senddata[5]=custID;  //CustID
			
			
			if(custdetails1.containsKey(custID))
			{
				if(custdetails1.get(custID).containsKey(eventtype))
				{
					if(custdetails1.get(custID).get(eventtype).contains(eventID.toString()))
					{
						String replymsg=this.runUDPClient(senddata);
						if(replymsg.trim().equals("successfull"))
						{
							custdetails1.get(custID).get(eventtype).remove(eventID.toString());
							if(custdetails1.get(custID).get(eventtype).isEmpty())
							{
								custdetails1.get(custID).remove(eventtype);
							}
							if(custdetails1.get(custID).isEmpty())
							{
								custdetails1.remove(custID);
							}
							
							msg="event cancelled succesfully";
						}
						else if(replymsg.trim().equals("notsuccessfull"))
						{
							msg="event cant be cancelled";
						}
						
					}
					else
					{
						msg="you haven't booked this event";
					}
				}
				else
				{
					
					msg="no such event available in your schedule";
				}
			}
			else
			{
				msg="event not already booked by you";
			}
	
		}
		System.out.print(map1);
		System.out.print(custdetails1);
		return msg;
	}
	
	

	

	public String runUDPClient(String[] senddata)
	{
		//senddata[0]=firstserver
		//senddata[1]=secondserver
		//senddata[2]=method to call
		//senddata[3]=eventtype
		//senddata[4]=eventID
		//senddata[5]=CustID
		
		String[] newdata=new String[5];
		newdata[1]=senddata[2]; //methodname
		newdata[2]=senddata[3]; //eventtype
		newdata[3]=senddata[4]; //eventID
		newdata[4]=senddata[5]; //custID
		
		StringBuilder requestdata=new StringBuilder();
		StringBuilder returndata=new StringBuilder();
		DatagramSocket aSocket = null;
		byte [] m ;
		int serverport = 0;
		DatagramPacket request;
		ArrayList<Thread> threads=new ArrayList<Thread>();
		ArrayList<getResponse> responses=new ArrayList<getResponse>();
		
		try 
		{
			aSocket = new DatagramSocket(); 
			InetAddress aHost = InetAddress.getByName("localhost");
		
			for(int i=0;i<2;i++)
			{
				if(senddata[i]==null) {
					continue;
				}
				if(senddata[i]=="MTLServer")
				{
					serverport=6789;
					newdata[0]=senddata[i];
				}
				else if(senddata[i]=="QUEServer")
				{
					serverport=6790;
					newdata[0]=senddata[i];
				}
				else if(senddata[i]=="SHEServer")
				{
					serverport=6791;
					newdata[0]=senddata[i];
				}
				
	
				for(String temp:newdata)
				{
					requestdata.append(temp).append(",");
				}
			
				m=requestdata.toString().getBytes();
				request =new DatagramPacket(m,  requestdata.length(), aHost, serverport);
				
				requestdata.setLength(0);
				getResponse resp=new getResponse(aSocket,request);
				responses.add(resp);
				
				Thread t=new Thread(resp);
				t.start();
				threads.add(t);
			}
			
				for(int j=0;j<threads.size();j++)
				{
					while(threads.get(j).isAlive())
					{
						if(responses.get(j).serversdata.length()>0)
						{
							threads.get(j).interrupt();
						}
					}
				}
			
			
			for(getResponse resp: responses)
			{
				returndata.append(resp.serversdata).append("\n");
			}
			
	
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
		catch (IOException e){System.out.println("IO: " + e.getMessage());}
	
		return returndata.toString();

		
	}



}
	
class getResponse implements Runnable
{

	DatagramSocket aSocket;
	DatagramPacket request;
	//ArrayList<String> serversdata=new ArrayList<String>();
	StringBuilder serversdata=new StringBuilder();
	String replymsg;
	public getResponse(DatagramSocket aSocket,DatagramPacket request)
	{
		this.aSocket=aSocket;
		this.request=request;
	}
 	
	
	@Override
	public void run() {
		try
		{
			aSocket.send(request);
			System.out.println("sent to other servers");
			
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
			aSocket.receive(reply);
			System.out.println("reply received");
			replymsg=new String(reply.getData());
			
			System.out.println("reply: "+replymsg);
			
			this.serversdata.append(replymsg);
			//this.serversdata.add(replymsg);
			//Thread.sleep(1000);
			
			
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}
		
	}
	
}

