package cn.jstxjf_.rte.bridge.network;
public abstract class RtePacket {
protected abstract void write(RtePacketWriter writer);
protected void handleClient(){}
public void encode(RtePacketWriter writer){write(writer);}
public void runClient(){handleClient();}
}
