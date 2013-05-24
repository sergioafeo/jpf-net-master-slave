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

import java.io.*;
import java.net.Socket;

public class CoupledLoopBackSocket extends Socket {
    PipedInputStream pair1_input, pair2_input;
    PipedOutputStream pair1_output, pair2_output;

    public CoupledLoopBackSocket() {
        try {
            pair1_input = new PipedInputStream();
            pair1_output = new PipedOutputStream(pair1_input);
            pair2_input = new PipedInputStream();
            pair2_output = new PipedOutputStream(pair2_input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        return (InputStream)pair1_input;
    }

    public OutputStream getOutputStream() {
        return (OutputStream)pair2_output;
    }

    public InputStream getInputStreamExternal() {
        return (InputStream)pair2_input;
    }

    public OutputStream getOutputStreamExternal() {
        return (OutputStream)pair1_output;
    }

    public void close() {
        try {
            pair1_output.close();
            pair2_output.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
