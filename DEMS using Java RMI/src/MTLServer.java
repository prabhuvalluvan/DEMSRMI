import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class MTLServer
{
	public static void main(String args[]) throws Exception
	{
		DEMSImplement obj1 = new DEMSImplement();
		obj1.servername="MTLServer";
		Registry registry = LocateRegistry.createRegistry(7777);
		registry.bind("MTLServer", obj1);
		System.out.println("Montreal Server is Up & Running");
		
		DatagramSocket aSocket = null;
		try{
			//System.out.println("inside try");
	    	aSocket = new DatagramSocket(6789);
			byte[] buffer = new byte[1000];
			
 			while(true){
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);
  				giveMTLResponse resp=new giveMTLResponse(aSocket,request);
  				Thread t=new Thread(resp);
				t.start();
 
    			
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}
		
		
		
		
		
		
	}


	
}

class giveMTLResponse implements Runnable
{

	DatagramSocket aSocket;
	DatagramPacket request;
	String[] receivedData;
	String getData;
	byte[] m;
	public giveMTLResponse(DatagramSocket aSocket,DatagramPacket request)
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
				//System.out.println("HI LIST EVENT");
				getData=obj.listEvent(receivedData[2].concat("temp"));
				break;
			case "bookEvent":
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
