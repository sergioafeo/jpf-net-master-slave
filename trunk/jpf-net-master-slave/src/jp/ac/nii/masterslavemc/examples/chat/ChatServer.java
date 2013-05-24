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

/* $Id: ChatServer.java 188 2006-08-16 06:41:09Z cartho $ */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    
    public static final int PORT = 4440;

	Worker workers[];
	static int activeClients = 0;
	static int num_workers = 1;

	public ChatServer() {
	}

	public ChatServer(int maxServ, int port) {
		accept(maxServ, port);
	}

	public ChatServer(int maxServ) {
		accept(maxServ, PORT);
	}

	public void accept(int maxServ, int port) {
		workers = new Worker[num_workers];
		Socket sock;
		try {
			ServerSocket servsock = new ServerSocket(port);

			for (int i = 0; i < maxServ || maxServ == 0; i++) {
				sock = servsock.accept();
				assert (sock.isConnected());

				synchronized (this) {
					int idle = -1;
					for (idle = 0; idle < num_workers; idle++) {
						if (workers[idle] == null)
							break;
					}

					workers[idle] = new Worker(idle, sock, this);
					activeClients++;
					System.out.println(ChatServer.activeClients + " client(s) logged in. ");
					// " client(s) logged in. ");
					new Thread(workers[idle]).start();
					// break;
					
					while (activeClients >= num_workers) {
						try {
							wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}

			synchronized (this) {
				while (activeClients != 0) {
					assert (activeClients >= 0);
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}

			//servsock.close(); // race condition [1], see remark below
		} catch (IOException ioe) {
			System.out.println("Server: " + ioe);
		}
		System.out.println("Server shutting down.");
	}

	/*
	 * [1] Race condition in system may allow client to try to connect to a still-open server socket just before the server itself closes
	 * the socket. Therefore the exception may be of type "Connection reset" rather than "Connection refused", making the behavior more
	 * difficult to test.
	 */

	public static void main(String args[]) throws IOException {
		assert args.length >= 2;
		ChatServer.num_workers = Integer.parseInt(args[1]);
		new ChatServer(Integer.parseInt(args[0]));
	}

	public synchronized void sendAll(String s) throws IOException {
		int i;
		for (i = 0; i < workers.length; i++) {
			if (workers[i] != null) {
				workers[i].send(s);
			}
		}
	}

	public synchronized void remove(int n) {
		workers[n] = null;
		activeClients--;
		assert (activeClients >= 0);
		notify();
		System.out.println("Client quit, " + activeClients + " client(s) left.");
	}

}
