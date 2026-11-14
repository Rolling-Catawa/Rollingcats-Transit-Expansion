package cn.jstxjf_.rte.bridge.network;

public interface RtePacketWriter {

    void writeBoolean(boolean value);

    void writeInt(int value);

    void writeLong(long value);

    void writeFloat(float value);

    void writeDouble(double value);

    void writeString(String value);
}
