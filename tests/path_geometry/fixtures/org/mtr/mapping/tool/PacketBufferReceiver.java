package org.mtr.mapping.tool;
import java.nio.ByteBuffer;
public class PacketBufferReceiver {
private final ByteBuffer in;public PacketBufferReceiver(byte[] b){in=ByteBuffer.wrap(b);}
public int readInt(){return in.getInt();}public long readLong(){return in.getLong();}public double readDouble(){return in.getDouble();}public boolean readBoolean(){if(!in.hasRemaining())throw new IndexOutOfBoundsException();return in.get()!=0;}public char readChar(){return in.getChar();}
}