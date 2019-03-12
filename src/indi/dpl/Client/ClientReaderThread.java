package indi.dpl.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;


/**
*
*Description:
*@author Karoy
*Start time:2018/8/1
*Version:v0.1 2018/8/1
*
*/

public class ClientReaderThread implements Runnable {

	public ClientReaderThread(Socket socket,BufferedReader br) {
		this.socket=socket;
		this.br=br;
	}
	
	@Override
	public void run() {
		try{
			String content=null;
			while(true)
			{
				content=br.readLine();
				System.out.println(content);
			}
		}
			catch (IOException e) 
			{
				System.out.println("Receive message from Server error");
			}
			finally
			{

				try 
				{
					if(br!=null)
						br.close();
					if(socket!=null)
						socket.close();
				}
				catch (IOException e) 
				{
					System.out.println("fail to relase resource");
				}
			}
	}
	
	private Socket socket=null;
	private BufferedReader br=null;
}
