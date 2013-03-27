package jp.ac.nii.masterslavemc.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


class WorkerThread implements Runnable {
	private Socket mySocket;

	public WorkerThread(Socket client) {
		mySocket = client;
	}

	public void run() {
		BufferedReader input = null;
		PrintWriter output = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					mySocket.getInputStream()));
			output = new PrintWriter(mySocket.getOutputStream());
		} catch (IOException e1) {
			return;
		}

		while (true)
			try {
				String msg = input.readLine();
				if (msg.equals("FAIL")) {
					assert (false);
				} else
					output.println(msg);
			} catch (IOException e) {
				break;
			}
		
		try {
			mySocket.close();
		} catch (IOException e) {}
	}
}

public class SimpleMaster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket s = new ServerSocket(5123);
			Socket client = null;

			while (true) {
				client = s.accept();
				new Thread(new WorkerThread(client)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
