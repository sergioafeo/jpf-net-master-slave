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

/* $Id: LoopBackSocket.java 159 2006-08-03 06:34:35Z cartho $ */

import java.io.*;
import java.net.Socket;

public class LoopBackSocket extends Socket {
    PipedInputStream istr;
    PipedOutputStream ostr;

    public LoopBackSocket() {
        super();
        try {
            istr = new PipedInputStream();
            ostr = new PipedOutputStream(istr);
        } catch (IOException e) {
            istr = null;
            ostr = null;
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        return (InputStream)istr;
    }

    public OutputStream getOutputStream() {
        return (OutputStream)ostr;
    }
}
