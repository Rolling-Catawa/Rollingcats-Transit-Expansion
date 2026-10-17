package cn.jstxjf_.rte.bridge.network;

import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

final class MtrPacketBuffers {

    private MtrPacketBuffers() {
    }

    static RtePacketReader reader(PacketBufferReceiver receiver) {
        return new RtePacketReader() {
            @Override
            public boolean readBoolean() {
                return receiver.readBoolean();
            }

            @Override
            public char readChar() {
                return receiver.readChar();
            }

            @Override
            public int readInt() {
                return receiver.readInt();
            }

            @Override
            public long readLong() {
                return receiver.readLong();
            }

            @Override
            public float readFloat() {
                return receiver.readFloat();
            }

            @Override
            public double readDouble() {
                return receiver.readDouble();
            }

            @Override
            public String readString() {
                return receiver.readString();
            }
        };
    }

    static RtePacketWriter writer(PacketBufferSender sender) {
        return new RtePacketWriter() {
            @Override
            public void writeBoolean(boolean value) {
                sender.writeBoolean(value);
            }

            @Override
            public void writeInt(int value) {
                sender.writeInt(value);
            }

            @Override
            public void writeLong(long value) {
                sender.writeLong(value);
            }

            @Override
            public void writeFloat(float value) {
                sender.writeFloat(value);
            }

            @Override
            public void writeDouble(double value) {
                sender.writeDouble(value);
            }

            @Override
            public void writeString(String value) {
                sender.writeString(value);
            }
        };
    }
}
