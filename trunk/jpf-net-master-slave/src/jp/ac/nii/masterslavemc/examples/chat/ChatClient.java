//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package jp.ac.nii.masterslavemc.examples.chat;

/* $Id: ChatClient.java 160 2006-08-05 17:29:49Z tatsumi $ */

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class ChatClient extends Thread {

	static int currID = 0;

	int id;

	int num_read;

	public ChatClient(int num_read) {
		synchronized (getClass()) {
			id = currID++;
		}

		this.num_read = num_read;
	}

	public void run() {
		OutputStream out;
		int message = 0;

		try {
			Socket socket = new Socket();
			InetSocketAddress addr = new InetSocketAddress("localhost", ChatServer.PORT);
			socket.connect(addr);
			System.out.println("Client " + id + " connected.");
			InputStream istr = socket.getInputStream();

			out = socket.getOutputStream();
			System.out.println("[CLIENT] " + id + " writes " + message);
			out.write(message);
			out.flush();

			// read input
			for (int i = 0; i < num_read; i++) {
				System.out.println("[CLIENT " + id + "] receive " + istr.read() + " at i=" + i);
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Client " + id + ": " + e);
		}
	}

	public static void main(String[] args) {
		new ChatClient(Integer.parseInt(args[0])).start();
	}
}
