import java.io.*;
import java.net.*;

public class Talk {
	//Inner class to receive message from server
	class RecieveFromServer implements Runnable{
		Socket socket =null; // Socket used by client
		//Constructor
		public RecieveFromServer(Socket sock) {
			this.socket = sock;
		}
		public void run() {
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));//Making object of BufferedReader
				String message = null;
				while((message = in.readLine())!= null){
					System.out.println("[remote]:" + message); //Displaying message on client console
				}
			}
			catch(IOException e){
				System.out.println("Read Failed.");
				System.exit(-1);
			}
		}
	}
	//Inner class to send message to server
	class SendToServer implements Runnable{
		Socket socket=null;
		PrintWriter out=null;
		//Constructor
		public SendToServer(Socket sock){
			this.socket = sock;
		}
		public void run(){
			try{
				if(socket.isConnected()){
					System.out.println("Client connected to "+socket.getInetAddress() + " on port "+socket.getPort());
					this.out = new PrintWriter(socket.getOutputStream(), true);	////Making object of PrintWriter
					try
					{
						while(true){
							BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //Making object of BufferedReader
							String message =null;
							message = in.readLine();//Reading entered value by the client
							//If message to be sent is STATUS
							if(message.equals("STATUS")) {
								System.out.println("Remote Port number: "+socket.getPort());
								System.out.println("Local port number: "+socket.getLocalPort());
								System.out.println("Local address: "+socket.getLocalAddress());
								System.out.println("Endpoint address: "+socket.getLocalSocketAddress());
								System.out.println("Inet address: "+socket.getInetAddress());
								System.out.println("Socket time-out time: "+socket.getSoTimeout());
								System.out.println("Receive buffer size: "+socket.getReceiveBufferSize());
								System.out.println("Send buffer size: "+socket.getSendBufferSize());
								
							}
							else
							{
							this.out.println(message); //Sending message to server
							this.out.flush(); //Flush the PrintWriter
							}
							
						}
					}
					//Exception to handle unknown host
					catch(UnknownHostException e)
					{
						System.out.println("Unknown host:"+ socket.getInetAddress());
						System.exit(1);
					}
					//Exception if no I/O is available
					catch(IOException e)
					{
						System.out.println("No I/O");
						System.exit(1);
					}
				}
			
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
	}
	//Inner class to receive message from client
	class RecieveFromClient implements Runnable{
		Socket socket =null; //Socket used by server
		//Constructor
		public RecieveFromClient(Socket clientSocket){
			this.socket = clientSocket;
		}
		public void run(){
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));//Making object of BufferedReader	
				String message;
				while(true){
					while((message = in.readLine())!= null){
						System.out.println("[remote]:" + message); //Displaying message on server console
					}
					this.socket.close();
					System.exit(0);
				}
			}
			catch(IOException ex){
				System.out.println("Read filed.");
				System.exit(-1);
			}
		}
	}
	//Inner class to send message to client
	class SendToClient implements Runnable{
		Socket socket = null;
		//Constructor
		public SendToClient(Socket clientSock){
			this.socket = clientSock;
		}
		public void run(){
			try{
				PrintWriter out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));//Making object of PrintWriter
				while(true)
				{
					String message = null;
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //Making object of BufferedReader
					message = in.readLine();//Reading entered value by the server
					//If message to be sent is STATUS
					if(message.equals("STATUS")) {
						System.out.println("Remote Port number: "+socket.getPort());
						System.out.println("Local port number: "+socket.getLocalPort());
						System.out.println("Local address: "+socket.getLocalAddress());
						System.out.println("Endpoint address: "+socket.getLocalSocketAddress());
						System.out.println("Inet address: "+socket.getInetAddress());
						System.out.println("Socket time-out time: "+socket.getSoTimeout());
						System.out.println("Receive buffer size: "+socket.getReceiveBufferSize());
						System.out.println("Send buffer size: "+socket.getSendBufferSize());
						
					}
					else
					{
						out.println(message); //Sending message to client 
						out.flush(); //Flush the PrintWriter
					}
				}
			}
			//Exception to handle unknown host
			catch(UnknownHostException e){
				System.out.println("Unknown host:"+ socket.getInetAddress());
				System.exit(1);
			}
			//Exception if no I/O is available
			catch(IOException ex){
				System.out.println("NO I/O");
				System.exit(1);
			}
			
		}
	}
	
	public static void main(String [] args) throws IOException{
		if(args.length>0){
			String mode = args[0]; //Reading mode
			switch(mode){	
			//-h mode
			case "-h":{
					String serverName="localhost";//Default server name
					int serverPortNumber=12987;	//Default port number
					//If 2 arguments given
					if(args.length == 2){
						//If second argument is -p and no port given after -p
						if(args[1].equals("-p")) {
							System.out.println("Please provide a port number.");
							System.exit(-1);
						}
						//Second argument is server name/ IP address
						else {
							serverName = args[1];
						}
					}
					////If 3 arguments given
					if(args.length == 3){
						//If second argument is -p
						if(args[1].equals("-p")){
							try{
								serverPortNumber = Integer.parseInt(args[2]); //Assign argument after -p as port number
							}
							//If port number is not numeric
							catch(NumberFormatException e){
								System.out.println("Please provide a valid port number.");
								System.exit(-1);
							}
							catch(IllegalArgumentException e) {
								System.out.println("Port "+ serverPortNumber +" is out of range.");
								System.exit(-1);
							}
						}
						//If second argument is not -p
						else{
							serverName = args[1];
							//Third argument must be -p (If there is)
							if(!args[2].equals("-p")){
								System.out.println("Invalid argument.");
								System.exit(-1);
							}
						}
						// If third argument is -p and fourth argument is not given
						if(args[2].equals("-p")){
							System.out.println("Please provide the port number.");
							System.exit(-1);
						}
					}
					//If 4 arguments given
					if(args.length == 4) {
						//If second argument is -p
						if(args[1].equals("-p")){
							System.out.println("Invalid Argument");
							System.exit(-1);
						}
						//Second argument is server name
						else{
							serverName = args[1];
						}
						//If third argument is -p
						if(args[2].equals("-p")){
							try{
								serverPortNumber = Integer.parseInt(args[3]); //Assign argument after -p as port number
							}
							//If port number is not numeric
							catch(NumberFormatException e){
								System.out.println("Please provide a valid port number.");
								System.exit(-1);
							}
							
						}
						//Anything else than -p
						else{
							System.out.println("Invalid Argument");
							System.exit(-1);
						}
					}
					if(args.length > 4) {
						System.out.println("Too many arguments given.");
						System.exit(-1);
					}
					try{
						if(serverPortNumber == 22) {
							System.out.println("Port 22 is reserved for SSH.");
							System.exit(-1);
						}
						else
						{
							Socket sock = new Socket(serverName,serverPortNumber); //Creating socket
							Talk obj = new Talk(); //Object of class Talk
							Talk.SendToServer sendThread = obj.new SendToServer(sock); //Object of SendToServer thread
							Thread Send = new Thread(sendThread);
							Send.start();//Running SendToServer thread
							Talk.RecieveFromServer recieveThread = obj.new RecieveFromServer(sock);//Object of RecieveFromServer thread
							Thread Recieve =new Thread(recieveThread);
							Recieve.start();//Running RecieveFromServer thread
						}
					} 
					catch(IllegalArgumentException e) {
						System.out.println("Port "+ serverPortNumber +" is out of range.");
						System.exit(-1);
					}
					//Exception to handle unknown host
					catch (UnknownHostException e){
						System.out.println("Unknown host:" +serverName);
						System.exit(1);
					} 
					//Exception if no I/O is available
					catch (IOException e){
						System.out.println("No I/O");
						System.exit(1);
					} 
					//Any other exception
					catch (Exception e){
						System.out.println(e.getMessage());
						System.exit(1);
					} 
					break;
				}
				//-s mode
				case "-s":
				{					
					int serverPortNumber=12987; //Default port number
					//If more than one arguments given
					if(args.length > 1 && args.length < 4){
						//If first argument is -p
						if (args[1].equals("-p")){
							if(args.length > 2 &&  args[2]!=" "){
								try{
									serverPortNumber = Integer.parseInt(args[2]);//Assign argument after -p as port number
								}
								//If port number is not numeric
								catch(NumberFormatException e){
									System.out.println("Please provide a valid port number.");
									System.exit(-1);
								}
							}
							//If no port number is given after -p
							else{
								System.out.println("Please provide a port number.");
								System.exit(0);
							}
						}
						//Anything else than -p
						else{
							System.out.println("Invalid argument given.");
							System.exit(0);
						}
					}
					//If more than 3 arguments given
					if(args.length > 3)
					{
						System.out.println("Too many arguments given.");
						System.exit(0);
					}
					
					Talk obj = new Talk(); //Object of class Talk
					ServerSocket server = null; //Socket of server
					Socket clientSocket = null; //Socket of client
					try {
						server = new ServerSocket(serverPortNumber); //Creating server socket
						System.out.println("Server is active on port number: "+serverPortNumber);
					}
					catch(IOException e)
					{
						System.out.println("Could not listen on port: "+serverPortNumber);
						System.exit(-1);
					}
					catch(IllegalArgumentException e) {
						System.out.println("Port "+ serverPortNumber +" is out of range.");
						System.exit(-1);
					}
					try {
						clientSocket = server.accept(); //Accepting client socket
						System.out.println("Server is connected to "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
					}
					catch(IOException e)
					{
						System.out.println("Accept fail on port "+serverPortNumber);
						System.exit(-1);
					}
					
					Talk.SendToClient send = obj.new SendToClient(clientSocket);//Object of SendToClient thread
					Thread Send = new Thread(send);
					Send.start(); //Running SendToClient thread
					Talk.RecieveFromClient recieve = obj.new RecieveFromClient(clientSocket); //Object of RecieveFromClient thread
					Thread Recieve = new Thread(recieve);
					Recieve.start();//Running RecieveFromClient thread
					
					server.close();
					break;
				}
				
				
				case "-a":{
					String Name="localhost";//Default name	
					int PortNumber=12987;//Default port number	
					//If 2 arguments given
					if(args.length == 2){	
						//If second argument is -p and no port given after -p
						if(args[1].equals("-p")) {
							System.out.println("Please provide a port number.");
							System.exit(-1);
						}
						//Second argument is server name/ IP address
						else {
							Name = args[1];
						}
					}
					//If 3 arguments given
					if(args.length == 3){
						//If second argument is -p
						if(args[1].equals("-p")){
							try{
								PortNumber = Integer.parseInt(args[2]); //Assign argument after -p as port number
							}
							//If port number is not numeric
							catch(NumberFormatException e){
								System.out.println("Please provide a valid port number.");
								System.exit(-1);
							}
						}
						//If second argument is not -p
						else{
							Name = args[1];//Assigning second argument as Name
							//If third argument is not -p
							if(!args[2].equals("-p")){
								System.out.println("Invalid argument.");
								System.exit(-1);
							}
						}
						//If third argument is -p
						if(args[2].equals("-p")){
							System.out.println("Please provide the port number.");
							System.exit(-1);
						}
							
					}
					//If 4 arguments given
					if(args.length == 4){
						// If second argument is -p
						if(args[1].equals("-p")){
							System.out.println("Invalid Argument");
							System.exit(-1);
						}
						else{
							Name = args[1];//Assigning second argument as Name
						}
						//If third argument is -p
						if(args[2].equals("-p")){
							try{
								PortNumber = Integer.parseInt(args[3]); //Assigning fourth argument as port number
							}
							//If port number is not numeric
							catch(NumberFormatException e){
								System.out.println("Please provide a valid port number.");
								System.exit(-1);
							}
						}
						else{
							System.out.println("Invalid Argument");
							System.exit(-1);
						}
					}
					//If more than 4 arguments given
					if(args.length > 4) {
						System.out.println("Too many arguments given");
						System.exit(-1);
					}
					System.out.println("NAME: "+Name);
					System.out.println("PORT: "+PortNumber);
					
					
					
					int flag = 0;
					//Try to connect it as client
						try{
							if(PortNumber == 22){
								System.out.println("Port 22 is reserved for SSH.");
								System.exit(-1);
							}
							else{
								Socket sock = null; 
								sock = new Socket(Name,PortNumber);
								Talk obj = new Talk();
								Talk.SendToServer sendThread = obj.new SendToServer(sock); //Object of SendToServer thread
								Thread Send = new Thread(sendThread);
								Send.start();//Running SendToServer thread
								Talk.RecieveFromServer recieveThread = obj.new RecieveFromServer(sock); //Object of RecieveFromServer thread
								Thread Recieve =new Thread(recieveThread);
								Recieve.start();//Running RecieveFromServer thread
								System.out.println("Connected as client!");
							}
						} 
						catch(IllegalArgumentException e) {
							System.out.println("Port "+ PortNumber +" is out of range.");
							System.exit(-1);
						}
						//Exception to handle unknown host
						catch (UnknownHostException e){
							System.out.println("Unknown host:" +Name);
							System.exit(1);
						} 
						//Exception if no I/O is available (It means server is not established.)
						catch (IOException e){
							flag =1;
						} 
						//Any other exception
						catch (Exception e){
							System.out.println(e.getMessage());
							System.exit(1);
						} 
					
					//Try to connect it as a server
					if(flag == 1) {
						System.out.println("Starting this as server!");
						Talk obj = new Talk();
						ServerSocket server = null;
						Socket clientSocket = null;
						try {
							server = new ServerSocket(PortNumber); //Starting server socket
							System.out.println("Server is active on port number: "+PortNumber);
						}
						catch(IOException e1){
							System.out.println("Could not listen on port "+PortNumber);
							System.exit(-1);
						}
						try {
							clientSocket = server.accept();//Accepting client socket
							System.out.println("Server is connected to "+clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
						}
						catch(IOException e1){
							System.out.println("Accept fail on port "+PortNumber);
							System.exit(-1);
						}
						
						//Talk obj = new Talk();
						Talk.SendToClient send = obj.new SendToClient(clientSocket);//Object of SendToClient thread
						Thread Send = new Thread(send);
						Send.start();//Running SendToClient thread
						Talk.RecieveFromClient recieve = obj.new RecieveFromClient(clientSocket);//Object of RecieveFromClient thread
						Thread Recieve = new Thread(recieve);
						Recieve.start();//Running RecieveFromClient thread
						server.close();
					}
						
					break;
				}
				//-help mode
				case "-help":{
					String line ="-----------------------------------------------------------------------------------------------------------------------------";
					String dash = "-";
					String s1 = "WELCOME TO THE HELP MENU";
					String Name = "Rujit Raval";
					String ID = "1512338";
					String Subject = "Computer Networks";
					String project = "Project 1";
					String program = "A Bidirectional Talk Program";
					System.out.println();
					System.out.println(line);
					System.out.format("%75s", s1);
					System.out.println();
					System.out.println(line);
					System.out.format("%68s", Name);
					System.out.println();
					System.out.format("%66s", ID);
					System.out.println();
					System.out.format("%72s", Subject);
					System.out.println();
					System.out.format("%67s", project);
					System.out.println();
					System.out.format("%77s", program);
					System.out.println();		
					System.out.println(line);
					System.out.print("Talk "+dash+"h [hostname | IPaddress] ["+dash+"p portnumber]");
					System.out.print("\t\t");
					System.out.format("%30s","It will try to connect as client.");
					System.out.println("");
					System.out.format("%106s","[hostname | IPaddress] = Server name / IP address.");
					System.out.println("");
					System.out.format("%100s","["+dash+"p portnumber] = Port number of the server.");
					System.out.println("");
					System.out.println(line);
					System.out.print("Talk "+dash+"s ["+dash+"p portnumber]");
					System.out.print("\t\t");
					System.out.format("%57s","It will try to connect as server.");
					System.out.println("");
					System.out.format("%100s","["+dash+"p portnumber] = Port number of the server.");
					System.out.println("");
					System.out.println(line);
					System.out.print("Talk "+dash+"a [hostname|IPaddress] ["+dash+"p portnumber]");
					System.out.print("\t\t");
					System.out.format("%30s","It will try to connect as client first.");
					System.out.println("");
					System.out.format("%99s","[hostname | IPaddress] = name / IP address.");
					System.out.println("");
					System.out.format("%86s","["+dash+"p portnumber] = Port number.");
					System.out.println("");
					System.out.format("%110s","If server is enabled it will try to connect as client.");
					System.out.println("");
					System.out.format("%98s","If no server is enabled it will be server.");
					System.out.println("");
					System.out.println(line);
					System.out.println("");
					System.out.println("# INSTRUCTIONS:");
					System.out.println("");
					System.out.println("All the arguments in the breackets([]) are optional.");
					System.out.println("");
					System.out.println("In case a hostname or IPaddress is not provided their default value will be localhost.");
					System.out.println("If a portnumber is not provided its default value will be 12987.");
					System.out.println("");
					System.out.println("String STATUS is considered to be a keyword and will not be transmitted.");
					System.out.println("STATUS will print information about the state of the connection (IP numbers, remote and local ports etc.).");
					System.out.println(line);
					break;
				}
				//Any other argument than -h, -s, -a or -help 
				default:{	
					System.out.println("Invalid argument given.");
					
					break;
				}
			}
		}
		else{
			System.out.println("Please provide required argument.");
		}

	}
}