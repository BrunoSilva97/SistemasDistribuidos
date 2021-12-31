package ds.trabalho.parte2;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.LinkedList;

public class Peer {
    String host;

    public Peer(String hostname) {
		host   = hostname;
    }
    
    public static void main(String[] args) throws Exception {
		Peer peer = new Peer(args[0]);
		System.out.printf("new peer @ host=%s\n", args[0]);
		new Thread(new Server(args[0], Integer.parseInt(args[1]))).start();
		new Thread(new Client(args[0], Integer.parseInt(args[1]))).start();
    }
}

class Server implements Runnable {
    static String       host;
    static int          port;
    ServerSocket server;
	static LinkedList<String> ips = new LinkedList<String>();
	static LinkedList<String> dictionary = new LinkedList<String>();
	static String[] words = {"ability","able","about","above","accept","according","account","across","act","action","activity","actually","add","address","administration","admit","adult","affect","after","again","against","age","agency","agent","ago","agree","agreement","ahead","air","all","allow","almost","alone","along","already","also","although","always","American","among","amount","analysis","and","animal","another","answer","any","anyone","anything","appear","apply","approach","area","argue","arm","around","arrive","art","article","artist","as","ask","assume","at","attack","attention","attorney","audience","author","authority","available","avoid","away","baby","back","bad","bag","ball","bank","bar","base","be","beat","beautiful","because","become","bed","before","begin","behavior","behind","believe","benefit","best","better","between","beyond","big","bill","billion","bit","black","blood","blue","board","body","book","born","both","box","boy","break","bring","brother","budget","build","building","business","but","buy","by","call","camera","campaign","can"};
    
    public Server(String host, int port) throws Exception {
		this.host   = host;
		this.port   = port;
        server = new ServerSocket(port, 1, InetAddress.getByName(host));
    }

	public static void register(String regIp) throws Exception{
		ips.add(regIp);
		Socket nextPeer  = new Socket(InetAddress.getByName(regIp), port);
		PrintWriter   output = new PrintWriter(nextPeer.getOutputStream(), true);
		output.println(String.valueOf("registers " + host));
		output.flush();
		nextPeer.close();
		System.out.println("REGISTER");
	}

	public static void registers(String regIp) throws Exception{
		ips.add(regIp);
		System.out.println("AQUI");
	}

	public static void pull(){
		System.out.println("PULL");
	}

	public static void push(){
		System.out.println("PUSH");
	}

    @Override
    public void run() {
		try {
			while(true) {
				try {
					Socket client = server.accept();
					String clientAddress = client.getInetAddress().getHostAddress();
					new Thread(new Connection(clientAddress, client)).start();
				}
				catch(Exception e) {
					e.printStackTrace();
				}    
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}

class Connection implements Runnable {
    String clientAddress;
    Socket clientSocket;

    public Connection(String clientAddress, Socket clientSocket) {
		this.clientAddress = clientAddress;
		this.clientSocket  = clientSocket;
    }

    @Override
    public void run() {
		/*
		* prepare socket I/O channels
		*/
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			String command;
			command = in.readLine();
			/*
			* parse command
			*/
			Scanner sc = new Scanner(command);
			String  op = sc.next();
			int port = Integer.parseInt(sc.next());    
			/*
			* execute op
			*/
			switch(op) {
				case "register": Server.register(regIp); break;
				case "push": Server.push(); break;
				case "pull": Server.pull(); break;
				case "pushpull": Server.push(); Server.pull(); break;
				case "registers": Server.registers(regIp); break;
			}  
			/*
			* send result
			*/
			//out.println(String.valueOf(result));
			//out.flush();
			/*
			* close connection
			*/
			clientSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class Client implements Runnable {
    String  host;
	int serverPort;
    Scanner scanner;
    
    public Client(String host, int serverPort) throws Exception {
		this.host    = host;
		this.serverPort = serverPort; 
		this.scanner = new Scanner(System.in);
    }

    @Override 
    public void run() {
		try {
			while (true) {
				try {
					/*
					* read command
					*/
					System.out.print("$ ");
					String command = scanner.nextLine();
					/* 
					* make connection
					*/
					Socket socket  = new Socket(InetAddress.getByName("localhost"), serverPort);
					/*
					* prepare socket I/O channels
					*/
					PrintWriter   out = new PrintWriter(socket.getOutputStream(), true);    
					/*
					* send command
					*/
					out.println(command);
					out.flush();	    
					/*
					* receive result
					*/
					/*
					* close connection
					*/
					socket.close();
				} catch(Exception e) {
					e.printStackTrace();
				}   
			}
		} catch(Exception e) {
			e.printStackTrace();
		}   	    
    }
}
