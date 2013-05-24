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

/* $Id: Worker.java 183 2006-08-13 03:37:16Z tatsumi $ */

import java.io.*;
import java.net.*;

class Worker implements Runnable {
    Socket sock;
    PrintWriter out;
    BufferedReader in;
    ChatServer chatServer;
    int n;

    public Worker(int n, Socket s, ChatServer cs) {
        this.n = n;
        chatServer = cs;
        sock = s;
        in = null;
        // FIX: out reference must be valid before constructor has
        // terminated!
        // reason: other threads may use send via sendAll and thus
        // call out.println before out is initialized.
        try {
            out = new PrintWriter(sock.getOutputStream());
            assert(out != null);
        } catch(IOException ioe) {
            System.err.println("Worker thread " + n + ": " + ioe);
            chatServer.remove(n);
        }
    }

	public void run() {
    	String s;
        //System.out.println("Thread running: " + Thread.currentThread());
		try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while ((s = in.readLine()) != null) {
		String msg = "[" + n + "] " + s;
		System.out.println(msg);
                chatServer.sendAll(msg);
            }
			chatServer.remove(n);
			assert (!sock.isClosed());
			sock.close();
		} catch (IOException ioe) {
			chatServer.remove(n);
		}
	}

    public void send(String s) throws IOException {
        out.println(s);
    }
}
