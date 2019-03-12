package indi.dpl.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import indi.dpl.tools.ProtocolString;

/**
 *
 * Description:chat room ,client
 * 
 * @author Karoy
 *         Start time:2018/8/1
 *         Version:v0.1 2018/8/1
 *         v0.2 2018/8/1:增加广播、私聊与查询在线用户
 */

public class Client {

	public void init() {
		try {
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			ps = new PrintStream(socket.getOutputStream());
			bufferKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				System.out.println("Input username:");
				name = bufferKeyBoard.readLine();
				if (name.equals("")) {
					System.out.println("You must input user name");
					continue;
				}
				ps.println(ProtocolString.USR_ROUND + name + ProtocolString.USR_ROUND);
				String fromServer = br.readLine();
				if (fromServer.equals(ProtocolString.NAME_REP)) {
					System.out.println("User already log in");
				}
				if (fromServer.equals(ProtocolString.LOGIN_SUCCESS)) {
					System.out.println("Log in successfully!");
					break;
				}
			}
			new Thread(new ClientReaderThread(socket, br)).start();
			String content = null;
			while (true) {
				content = bufferKeyBoard.readLine();
				if (content.equals("")) {
					System.out.println("Input something");
					continue;
				}
				if (content.equals("usrlst")) {
					ps.println(ProtocolString.QUERY_ROUND + content + ProtocolString.QUERY_ROUND);
				} else if (content.indexOf(":") > 0 && content.startsWith("//")) {
					content = content.substring(2);
					ps.println(ProtocolString.PRAVITE_ROUND + content.split(":")[0] + ProtocolString.SPLIT_SIGN
							+ content.split(":")[1] + ProtocolString.PRAVITE_ROUND);
				} else {
					ps.println(
							ProtocolString.GROUP_ROUND + "From " + name + " : " + content + ProtocolString.GROUP_ROUND);
				}
			}
		} catch (UnknownHostException ex) {
			System.out.println("Can not find the server,please check IP adddress and port number!");
			closeRes();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("network error!");
			closeRes();
			System.exit(1);
		}
	}

	private void closeRes() {
		try {
			if (bufferKeyBoard != null)
				bufferKeyBoard.close();
			if (br != null)
				br.close();
			if (ps != null)
				ps.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			System.out.println("failed to close");
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.init();

	}

	private Socket socket = null;
	private PrintStream ps = null;
	private BufferedReader bufferKeyBoard = null;
	private BufferedReader br = null;
	private String SERVER_ADDRESS = "127.0.0.1";
	private int SERVER_PORT = 8080;
	private String name = null;
}
