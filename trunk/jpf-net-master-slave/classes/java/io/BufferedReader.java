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

package java.io;

public class BufferedReader extends Reader {

	Reader inside;

	public BufferedReader(Reader in) {
		inside = in;
	}

	public native int read(char[] cbuf, int off, int len) throws IOException;

	public native byte[] native_readLine() throws IOException;

	public native boolean native_ready() throws IOException;

	public boolean ready() throws IOException {
		return native_ready();
	}

	public void close() throws IOException {
	}

	public String readLine() throws IOException {
		boolean cacheConnected = isCacheConnected();

		// if the internal Reader is connecting to the cache, access the cache.
		if (cacheConnected) {
			byte[] line = native_readLine();
			String ret;
			
			return line.length == 0 ? null : buildString(line);
		}
		// otherwise, call read() on the internal Reader until the end of line.
		else {
			StringBuffer buffer = new StringBuffer();
			
			for(int c = inside.read(); c != -1 && c != '\n';c = inside.read()) {
				buffer.append((char) c);
			}
			
			return buffer.toString();
		}
	}

	// return true if the InputStream in "inside" is CacheLayerInputStream.
	private native boolean isCacheConnected();

	/**
	 * Statement "new String" cannot be safely used in model classes. Use this
	 * method instead.
	 * 
	 * @param bytes
	 * @return
	 */
	private native String buildString(byte[] bytes);

}
