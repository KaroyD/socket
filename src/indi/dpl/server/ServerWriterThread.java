package indi.dpl.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
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

class ServerWriterThread implements Runnable {

	public ServerWriterThread(Socket socket,BlockingQueue<String>bq,PrintStream ps) {
		this.socket=socket;
		this.bq=bq;
		this.ps=ps;
	}
	@Override
	public void run() {
		try {
			String message=null;
			while(true){
				message=bq.take();
				if(message.equals(ProtocolString.NAME_REP))
				{
					ps.println(ProtocolString.NAME_REP);
				}
				else if(message.equals(ProtocolString.LOGIN_SUCCESS)){
					ps.println(ProtocolString.LOGIN_SUCCESS);
					break;
				}
			}
			while(true)
			{
				message=bq.take();
				if(message.startsWith(CommandString.ONLINE_USER) && message.endsWith(CommandString.ONLINE_USER))
				{
					message=getRealMsg(message, CommandString.COMMANDASTRING_SIZE);
					ps.println(message);
				}
				else if(message.startsWith(ProtocolString.GROUP_ROUND) && message.endsWith(ProtocolString.GROUP_ROUND))
				{
					message=getRealMsg(message, ProtocolString.PROTOCOL_SIZE);
					for(PrintStream usrPs:Server.userMap.getValueSet())
						usrPs.println("(Broadcast) "+message);
				}
				else if(message.startsWith(ProtocolString.PRAVITE_ROUND) && message.endsWith(ProtocolString.PRAVITE_ROUND))
				{
					String nameAndMsg=getRealMsg(message, ProtocolString.PROTOCOL_SIZE);
					String sendto=nameAndMsg.split(ProtocolString.SPLIT_SIGN)[0];
					String mString=nameAndMsg.split(ProtocolString.SPLIT_SIGN)[1];
					for(String usrName:Server.userMap.getUserSet())
					{
						if(sendto.equals(usrName))
						{
							PrintStream tmpps=Server.userMap.map.get(sendto);
							tmpps.println("(Personally) From "+Server.userMap.getKeyByValue(ps)+" : "+mString);
							break;
						}
					}
				}
			}
		}
		catch (InterruptedException e) {
			System.out.println("Failed to take message from blockqueue");
		}
		finally{
			try {
				if(socket!=null)
					socket.close();
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
	private PrintStream ps=null;
	private BlockingQueue<String>bq;
}
