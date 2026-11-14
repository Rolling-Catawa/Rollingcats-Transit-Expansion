import cn.jstxjf_.rte.bridge.network.RtePacketReader;
import java.nio.ByteBuffer;
public class PacketBufferReceiver implements RtePacketReader {
private final ByteBuffer in;public PacketBufferReceiver(byte[] b){in=ByteBuffer.wrap(b);}
public int readInt(){return in.getInt();}public long readLong(){return in.getLong();}public float readFloat(){return in.getFloat();}public double readDouble(){return in.getDouble();}public boolean readBoolean(){if(!in.hasRemaining())throw new IndexOutOfBoundsException();return in.get()!=0;}public char readChar(){return in.getChar();}
public String readString(){int n=readInt();StringBuilder b=new StringBuilder(n);for(int i=0;i<n;i++)b.append(readChar());return b.toString();}
}
