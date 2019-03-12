package indi.dpl.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.BlockingQueue;


import indi.dpl.tools.*;


/**
*
*Description:
*@author Karoy
*Start time:2018/8/1
*Version:v0.1 2018/8/1
*
*/

class ServerReaderThread implements Runnable {

	public ServerReaderThread(Socket socket,BlockingQueue<String> bq,PrintStream ps) {
		this.socket=socket;
		this.bq=bq;
		this.ps=ps;
	}
	
	@Override
	public void run() {
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String content=null;
			while (true) 
			{
				content=br.readLine();
				if(content.startsWith(ProtocolString.USR_ROUND) && content.endsWith(ProtocolString.USR_ROUND))
				{
					content=getRealMsg(content,ProtocolString.PROTOCOL_SIZE);
					if(Server.userMap.map.containsKey(content))
					{
						System.out.println("User "+content+" already log in");
						bq.put(ProtocolString.NAME_REP);
					}
					else
					{
						bq.put(ProtocolString.LOGIN_SUCCESS);
						Server.userMap.put(content, ps);
						break;
					}
				}
			}
			while(true) {
				content=br.readLine();
				if(content.startsWith(ProtocolString.QUERY_ROUND) && content.endsWith(ProtocolString.QUERY_ROUND))
				{
					content=getRealMsg(content,ProtocolString.PROTOCOL_SIZE);
					if(content.equals("usrlst"))
					{
						Set<String> usrSet=Server.userMap.getUserSet();
						String usrOnline="";
						for(String name:usrSet)
							usrOnline=usrOnline+name+" ";
						bq.put(CommandString.ONLINE_USER+usrOnline+CommandString.ONLINE_USER);
					}
				}
				else
				{
					bq.put(content);
				}
			}

		}
		catch (IOException e) {
			System.out.println("Can not receive message from"+socket.getInetAddress());
		} 
		catch (InterruptedException e) {
			System.out.println("Failed to wake up the wirter thread");
		}
		finally
		{
			try {
				Server.userMap.removeFromMap(ps);
				if(socket!=null)
					socket.close();
				if(br!=null)
					br.close();
				if(ps!=null)
					ps.close();
			}
			catch(IOException ex)
			{
				System.out.println("Failed to release resource"); 
			}
		}
		
	}
	
	private String getRealMsg(String content,int size)
	{
		return content.substring(size, content.length()-size);
	}
	

	private Socket socket=null;
	private BufferedReader br=null;
	private BlockingQueue<String>bq;
	private PrintStream ps=null;
		
}
