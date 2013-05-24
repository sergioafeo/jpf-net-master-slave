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

public class ChatServerBug extends ChatServer {
    

	public ChatServerBug() {
	}

	public ChatServerBug(int maxServ, int port) {
		accept(maxServ, port);
	}

	public ChatServerBug(int maxServ) {
		accept(maxServ, PORT);
	}

	/*
	 * [1] Race condition in system may allow client to try to connect to a still-open server socket just before the server itself closes
	 * the socket. Therefore the exception may be of type "Connection reset" rather than "Connection refused", making the behavior more
	 * difficult to test.
	 */

	public static void main(String args[]) throws IOException {
		assert args.length >= 2;
		ChatServer.num_workers = Integer.parseInt(args[1]);
		new ChatServerBug(Integer.parseInt(args[0]));
	}

  /**
   * synchronizing only the notify() but not the rest of our fields will cause a lot of races
   */
	public /* synchronized */ void remove(int n) {
		workers[n] = null;
		activeClients--;
		assert (activeClients >= 0);
		synchronized (this){           // insufficient synchronization
		  notify();
		}
		System.out.println("Client quit, " + activeClients + " client(s) left.");
	}

}
