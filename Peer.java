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
import java.util.concurrent.*;
import java.util.Random;

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
    
	long startTime = System.currentTimeMillis();

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
	}

	public static void see(){
		for(int i = 0; i < dictionary.size(); i++){
			System.out.println(dictionary.get(i));
		}
	}

	public static void pull(String regIp) throws Exception{
		Socket nextPeer  = new Socket(InetAddress.getByName(regIp), port);
		PrintWriter   output = new PrintWriter(nextPeer.getOutputStream(), true);
		output.println(String.valueOf("puller " + host));
		output.flush();
		nextPeer.close();
	}

	public static void push(String regIp) throws Exception{
		Socket nextPeer  = new Socket(InetAddress.getByName(regIp), port);
		PrintWriter   output = new PrintWriter(nextPeer.getOutputStream(), true);
		for(int i = 0; i < dictionary.size(); i++){
			output.append(dictionary.get(i) + " ");
		}
		output.flush();
		nextPeer.close();
	}

    @Override
    public void run() {
		try {
			while(true) {
				if(System.currentTimeMillis() - startTime >= 10000){
					Random ran = new Random();
					int r = ran.nextInt(words.length);
					if(!dictionary.contains(words[r]))
						dictionary.add(words[r]);
					startTime = System.currentTimeMillis();
				}			
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
			String ip;
			command = in.readLine();
			/*
			* parse command
			*/
			Scanner sc = new Scanner(command);
			String  op = sc.next();
			/*
			* execute op
			*/

			switch(op) {
				case "register": ip = sc.next(); Server.register(ip); break;
				case "push": ip = sc.next(); Server.push(ip); break;
				case "pull": ip = sc.next(); Server.pull(ip); break;
				case "pushpull": ip = sc.next(); Server.push(ip); Server.pull(ip); break;
				case "see": Server.see(); break;
				default: {
					if(op.equals("registers")){
						ip = sc.next();
						Server.ips.add(ip);
						System.out.println(ip + " adicionado lista");
					}
					else if(op.equals("puller")){
						ip = sc.next();
						Socket nextPeer  = new Socket(InetAddress.getByName(ip), Server.port);
						PrintWriter   output = new PrintWriter(nextPeer.getOutputStream(), true);
						for(int i = 0; i < Server.dictionary.size(); i++){
							output.append(Server.dictionary.get(i) + " ");
						}
						output.flush();
						nextPeer.close();
					}
					else{
						for(String val: command.split(" ")){
							if(!Server.dictionary.contains(val)){
								Server.dictionary.add(val);
							}	
						}
					}
				}
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
					Socket socket  = new Socket(InetAddress.getByName(host), serverPort);
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
