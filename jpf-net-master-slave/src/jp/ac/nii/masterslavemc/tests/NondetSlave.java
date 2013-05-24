package jp.ac.nii.masterslavemc.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NondetSlave {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int msgs[] = {0, 1, 2, 252, 3, 4, 5, 10, 250, 8};
		try {
			Socket s = new Socket("localhost", 5123);
			InputStream input = s.getInputStream();
			OutputStream output = s.getOutputStream();
			//output.write(100);
			for (int msg : msgs){
				output.write(msg);
				int reply = input.read();
				if (reply != msg) {
					// assert(false);
				}
			} 
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
