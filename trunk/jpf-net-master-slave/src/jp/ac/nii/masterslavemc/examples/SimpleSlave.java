package jp.ac.nii.masterslavemc.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleSlave {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String msgs[] = {"FIRST", "TRY", "CHECK", "FAIL"};
		try {
			Socket s = new Socket("localhost", 5123);
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter output = new PrintWriter(s.getOutputStream());
			
			for (String msg : msgs){
				output.println(msg);
				String reply = input.readLine();
				if (!reply.equals(msg)) {
					assert(false);
				}
			} 
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
