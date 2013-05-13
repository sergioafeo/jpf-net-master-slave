package jp.ac.nii.masterslavemc.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class MessengerThread implements Runnable{
	@Override
	public void run() {
		while (ThreadedSlave.hasMsg()){
			try {
				int msg = ThreadedSlave.getMsg();
				ThreadedSlave.output.write(msg);
				int response = ThreadedSlave.input.read();
				if (response != msg){
					assert (false);
				}
			} catch (IOException e) {
				return;
			}
		}
	}
}
public class ThreadedSlave {
	
	public static int[] msgs = {1,2};
	public static int msgIndex = 0;
	public static Socket s = new Socket("localhost", 5123);
	public static InputStream input;
	public static OutputStream output;
	
	static{
		try {
			input = s.getInputStream();
			output = s.getOutputStream();
		} catch (IOException e) {
			input = null;
			output = null;
			e.printStackTrace();
		}
	}
	
	synchronized public static int getMsg(){
		return msgs[msgIndex++];
	}
	
	public static boolean hasMsg() {
		return msgIndex < msgs.length;
	}
	
	public static void main(String[] args){
		if (input != null && output != null){
			Thread t1 = new Thread(new MessengerThread());
			Thread t2 = new Thread(new MessengerThread());
			t1.start();
			t2.start();
		}
	}
}
