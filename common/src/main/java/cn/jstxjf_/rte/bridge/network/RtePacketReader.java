package cn.jstxjf_.rte.bridge.network;

public interface RtePacketReader {

    boolean readBoolean();

    char readChar();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readString();
}
