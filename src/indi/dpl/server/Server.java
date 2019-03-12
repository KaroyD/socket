package indi.dpl.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
*
*Description:chat room program, Server class
*@author Karoy
*Start time:2018/8/1
*Version: v0.1 2018/8/1
**		  v0.2 2018/8/2:增加广播、私聊与查询在线用户
*							
*/

public class Server {

	public void init()
	{
		try(ServerSocket ss=new ServerSocket(SERVER_PORT))
		{
			System.out.println("Waiting for connectiong...");
			while(true)
			{
				Socket socket=ss.accept();
				BlockingQueue<String> bq=new ArrayBlockingQueue<>(blockqueuesize);
				System.out.println("Connection request from: "+socket.getInetAddress());
				System.out.println("New thread start up");
				PrintStream ps=new PrintStream(socket.getOutputStream());
				new Thread(new ServerReaderThread(socket,bq,ps)).start();
				new Thread(new ServerWriterThread(socket,bq,ps)).start();
			}
		}
		catch (IOException e) 
		{
			System.out.println("Server initialization failed.Please check the PORT");
		}
	}		
	
	public static void main(String []args)
	{
		Server server=new Server();
		server.init();
	}
	
	private final static int SERVER_PORT=8080;
	public static UserMap<String, PrintStream>userMap=new UserMap<>();
	private int blockqueuesize=5;
}
