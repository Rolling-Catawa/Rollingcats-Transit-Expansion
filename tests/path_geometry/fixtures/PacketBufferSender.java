import cn.jstxjf_.rte.bridge.network.RtePacketWriter;
import java.io.*;
public class PacketBufferSender implements RtePacketWriter {
private final ByteArrayOutputStream bytes=new ByteArrayOutputStream();private final DataOutputStream out=new DataOutputStream(bytes);
public byte[] bytes(){return bytes.toByteArray();}
public void writeInt(int v){try{out.writeInt(v);}catch(IOException e){throw new RuntimeException(e);}}
public void writeLong(long v){try{out.writeLong(v);}catch(IOException e){throw new RuntimeException(e);}}
public void writeFloat(float v){try{out.writeFloat(v);}catch(IOException e){throw new RuntimeException(e);}}
public void writeDouble(double v){try{out.writeDouble(v);}catch(IOException e){throw new RuntimeException(e);}}
public void writeBoolean(boolean v){try{out.writeBoolean(v);}catch(IOException e){throw new RuntimeException(e);}}
public void writeString(String v){writeInt(v.length());try{out.writeChars(v);}catch(IOException e){throw new RuntimeException(e);}}
}
