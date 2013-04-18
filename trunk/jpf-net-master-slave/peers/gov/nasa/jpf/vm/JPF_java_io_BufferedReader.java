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

package gov.nasa.jpf.vm;

import java.io.IOException;
//import gov.nasa.jpf.network.cache.*;

public class JPF_java_io_BufferedReader {

	public static int native_readLine_____3B(MJIEnv env, int robj) throws IOException {
//		CacheLayer cache = CacheLayer.getInstance();
		StringBuffer buffer = new StringBuffer();
		byte[] c = new byte[1];
		int inside = env.getReferenceField(robj, "inside");
		int in = env.getReferenceField(inside, "in");
		int id = env.getIntField(in, "id");
		int ret;

//		for (int n = cache.read(id, c); n != -1 && c[0] != '\n'; n = cache.read(id, c)) {
//			buffer.append((char) c[0]);
//		}

		ret = env.newByteArray(buffer.toString().getBytes());

		return ret;
	}

	public static boolean native_ready____Z(MJIEnv env, int robj) throws IOException {
//		CacheLayer cache = CacheLayer.getInstance();
		int inside = env.getReferenceField(robj, "inside");
		int in = env.getReferenceField(inside, "in");
		int id = env.getIntField(in, "id");

//		return !cache.isNextReadBlocked(id);
		return false;
	}

	public static boolean isCacheConnected____Z(MJIEnv env, int robj) throws IOException {
		int inside = env.getReferenceField(robj, "inside");
		int in = env.getReferenceField(inside, "in");

		return env.getClassName(in).contains("CacheLayerInputStream");
	}
	
	public static int buildString___3B__Ljava_lang_String_2(MJIEnv env,
			int robj, int bytesRef) throws IOException {
		return env.newString(bytesRef);
	}
	
}
