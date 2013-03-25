package jp.ac.nii.masterslavemc.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleMaster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket s = new ServerSocket(5123);
			Socket client = null;
			BufferedReader input = null;
			PrintWriter output = null;
			while (true){
				client = s.accept();
				input = new BufferedReader(new InputStreamReader(client.getInputStream()));
				output = new PrintWriter(client.getOutputStream());
				
				String msg = input.readLine();
				if (msg.equals("FAIL")) {assert(false);}
				else output.println(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
