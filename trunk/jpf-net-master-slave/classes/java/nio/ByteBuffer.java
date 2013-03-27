package java.nio;

public class ByteBuffer extends Buffer {
	byte[] array;

	public static ByteBuffer allocate(int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
		ByteBuffer newBuffer = new ByteBuffer(i);
		return newBuffer;
	}

	public static ByteBuffer allocateDirect(int capacity) {
		return allocate(capacity);
	}

	private ByteBuffer(int i) {
		capacity = i;
		this.clear();
		array = new byte[i];
	}

	public ByteBuffer duplicate() {
		ByteBuffer copy = new ByteBuffer(capacity);
		copy.array = array;
		return copy;
	}

	public ByteBuffer asReadOnlyBuffer() {
		return duplicate();
	}

	public ByteBuffer slice() {
		int remaining = limit - position;
		ByteBuffer copy = new ByteBuffer(remaining);
		copy.array = array;
		return copy;
	}

	public ByteBuffer put(byte b) {
		if (position >= limit) {
			throw new BufferOverflowException();
		}
		array[position] = b;
		position++;
		return this;
	}

	public ByteBuffer put(int i, byte b) {
		if ((i < 0) || (i >= limit)) {
			throw new IndexOutOfBoundsException();
		}
		array[i] = b;
		return this;
	}

	public ByteBuffer put(ByteBuffer src) {
		if (src == this) {
			throw new IllegalArgumentException();
		}

		int srcRemaining = src.remaining();
		if (srcRemaining > remaining()) {
			throw new BufferOverflowException();
		}

		System.arraycopy(src.array, src.position(), array, position, srcRemaining);

		src.position(src.position() + srcRemaining);
		position(position + srcRemaining);

		return this;
	}

	public ByteBuffer put(byte[] bytes, int offset, int length) {
		if ((offset | length | (offset + length) | (bytes.length - (offset + length))) < 0) {
			throw new IndexOutOfBoundsException();
		}

		if (length > remaining()) {
			throw new BufferOverflowException();
		}

		System.arraycopy(bytes, offset, array, position, length);
		position(position + length);

		return this;
	}

	public byte get() {
		if (position >= limit) {
			throw new BufferUnderflowException();
		}
		position++;
		return array[position-1];
	}

	public byte get(int i) {
		if ((i < 0) || (i >= limit)) {
			throw new IndexOutOfBoundsException();
		}
		return array[i];
	}

	public ByteBuffer get(byte[] bytes) {
		return get(bytes, 0, bytes.length);
	}

	public ByteBuffer get(byte[] bytes, int offset, int length) {
		if ((offset | length | (offset + length) | (bytes.length - (offset + length))) < 0) {
			throw new IndexOutOfBoundsException();
		}

		if (length > remaining()) {
			throw new BufferUnderflowException();
		}

		int end = offset + length;
		for (int i = offset; i < end; i++) {
			bytes[i] = get();
		}
		return this;
	}

	public ByteBuffer order(ByteOrder order) {
		return this;
	}

	public int getInt() {
		if (limit - position < 4) {
			throw new BufferUnderflowException();
		}

		int x3 = (int)((array[position  ]       ) << 24);
		int x2 = (int)((array[position+1] & 0xff) << 16);
		int x1 = (int)((array[position+2] & 0xff) <<  8);
		int x0 = (int)((array[position+3] & 0xff)      );
		position += 4;

		return (x3 | x2 | x1 | x0);
	}

	public ByteBuffer putInt(int x) {
		if (limit - position < 4) {
			throw new BufferOverflowException();
		}

		array[position  ] = (byte)(x >> 24);
		array[position+1] = (byte)(x >> 16);
		array[position+2] = (byte)(x >>  8);
		array[position+3] = (byte)(x      );
		position += 4;

		return this;
	}

	public long getLong() {
		if (limit - position < 8) {
			throw new BufferUnderflowException();
		}

		long x7 = ((long)(array[position  ]       ) << 56);
		long x6 = ((long)(array[position+1] & 0xff) << 48);
		long x5 = ((long)(array[position+2] & 0xff) << 40);
		long x4 = ((long)(array[position+3] & 0xff) << 32);
		long x3 = ((long)(array[position+4] & 0xff) << 24);
		long x2 = ((long)(array[position+5] & 0xff) << 16);
		long x1 = ((long)(array[position+6] & 0xff) <<  8);
		long x0 = ((long)(array[position+7] & 0xff)      );
		position += 8;

		return (x7 | x6 | x5 | x4 | x3 | x2 | x1 | x0);
	}

	public ByteBuffer putLong(long x) {
		if (limit - position < 8) {
			throw new BufferOverflowException();
		}

		array[position  ] = (byte)((x >> 56)       );
		array[position+1] = (byte)((x >> 48) & 0xff);
		array[position+2] = (byte)((x >> 40) & 0xff);
		array[position+3] = (byte)((x >> 32) & 0xff);
		array[position+4] = (byte)((x >> 24) & 0xff);
		array[position+5] = (byte)((x >> 16) & 0xff);
		array[position+6] = (byte)((x >>  8) & 0xff);
		array[position+7] = (byte)((x      ) & 0xff);
		position += 8;

		return this;
	}

	public byte[] array() {
		return array;
	}

	public boolean hasArray() {
		return true;
	}

	public ByteBuffer compact() {
		int pos = position();
		int lim = limit();
		int cap = capacity();
		int rem = lim - pos;

		byte[] newArray = new byte[cap];
		System.arraycopy(array, pos, newArray, 0, rem);
		array = newArray;

		position(rem);
		limit(cap);
		return this;
	}

	public static ByteBuffer wrap(byte[] outMess) {
		ByteBuffer byteBuffer = new ByteBuffer(outMess.length);
		byteBuffer.clear();
		System.arraycopy(outMess, 0 , byteBuffer.array, 0, outMess.length);
		return byteBuffer;
	}
}
