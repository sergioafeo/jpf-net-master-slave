package jp.ac.nii.masterslavemc.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class NondetThread implements Runnable {
	private Socket mySocket;

	public NondetThread(Socket client) {
		mySocket = client;
	}

	public void run() {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = mySocket.getInputStream();
			output = mySocket.getOutputStream();
		} catch (IOException e1) {
			return;
		}
		int msg = 0;
		while (msg != -1)
			try {
				msg = input.read();
				if (msg == 250) {
					assert (false);
				} else if (msg != -1)
					output.write(msg);
			} catch (IOException e) {
				break;
			}
		
		try {
			mySocket.close();
		} catch (IOException e) {}
	}
}

public class NondetMaster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket s = new ServerSocket(5123);
			Socket client = null;
			client = s.accept();
			new Thread(new NondetThread(client)).start();
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
