import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class QUEServer
{
	public static void main(String args[]) throws Exception
	{
		DEMSImplement obj2 = new DEMSImplement();
		obj2.servername="QUEServer";
		Registry registry = LocateRegistry.getRegistry(7777);
		registry.bind("QUEServer", obj2);
		System.out.println("Quebec Server is Up & Running");
		
	
		
		
		DatagramSocket aSocket = null;
		try{
			//System.out.println("inside try");
	    	aSocket = new DatagramSocket(6790);
			byte[] buffer = new byte[1000];
			
 			while(true){
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);
  				giveQUEResponse resp=new giveQUEResponse(aSocket,request);
  				Thread t=new Thread(resp);
				t.start();
 
    			
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}
		
		
		
		
		
		
	}


	
}

class giveQUEResponse implements Runnable
{

	DatagramSocket aSocket;
	DatagramPacket request;
	String[] receivedData;
	String getData;
	byte[] m;
	public giveQUEResponse(DatagramSocket aSocket,DatagramPacket request)
	{
		this.aSocket=aSocket;
		this.request=request;
	}
 	
	
	@Override
	public void run() {
		receivedData=new String(request.getData()).split("\\,");
		
		DEMSInterface obj;
		try
		{
			Registry registry = LocateRegistry.getRegistry(7777);
			obj= (DEMSInterface) registry.lookup(receivedData[0]);
			switch(receivedData[1])
			{
			case "listEvent":
				getData=obj.listEvent(receivedData[2].concat("temp"));
				System.out.println("Data is: "+getData );
				break;
			case "bookEvent":
				System.out.println("received eventid Data is: "+receivedData[3]);
				System.out.println("received CustID Data is: "+receivedData[4]);
				System.out.println("received eventtype Data is: "+receivedData[2]);
				getData=obj.bookEvent(receivedData[4],receivedData[3],receivedData[2]);
				System.out.println("Data is: "+getData );
				break;
			/*case "getSchedule":
				getData=obj.getSchedule("t".concat(receivedData[4]));
				System.out.println("Data is: "+getData );
				break;*/
			case "cancelEvent":
				getData=obj.cancelEvent(receivedData[4],receivedData[3],receivedData[2]);
				System.out.println("Data is: "+getData );
				break;
			case "removeEvent":
				System.out.println("received eventid Data is: "+receivedData[3]);
				getData=obj.removeEvent("temp".concat(receivedData[3]),receivedData[2]);
				System.out.println("Data is: "+getData );
				break;
				
			}
		}catch (Exception e) {System.out.println("Thread: " + e.getMessage());}
		
			
		try
		{
			m=getData.getBytes();
			DatagramPacket reply =new DatagramPacket(m,  getData.length(), request.getAddress(), request.getPort());
			aSocket.send(reply);
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}
	}
	
}




	
	
	
	
	
	
	
	
	
	
	
	
	
