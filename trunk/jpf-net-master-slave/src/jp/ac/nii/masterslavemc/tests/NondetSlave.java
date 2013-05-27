package jp.ac.nii.masterslavemc.tests;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import gov.nasa.jpf.vm.Verify;

public class NondetSlave {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int msgs[] = {0, 1, 2, 152, 3, 4, 5, 10, 150, 8};
		try {
			Socket s = new Socket("localhost", 5123);
			InputStream input = s.getInputStream();
			OutputStream output = s.getOutputStream();
			//output.write(100);
			for (int msg : msgs){
				output.write(Verify.getInt(msg, msg+5));
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
