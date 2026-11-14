package cn.jstxjf_.rte.mapping.holder;

public final class PacketBuffer extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.network.FriendlyByteBuf> {

    public PacketBuffer(net.minecraft.network.FriendlyByteBuf p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.PacketBuffer cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new PacketBuffer((net.minecraft.network.FriendlyByteBuf) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.network.FriendlyByteBuf;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeDate(java.util.Date p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeDate(p0));
    }

    public PacketBuffer(io.netty.buffer.ByteBuf p0) {
        super(new net.minecraft.network.FriendlyByteBuf(p0));
    }

    public io.netty.buffer.ByteBuf asReadOnly() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).asReadOnly();
    }

    public byte getByte(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getByte(p0);
    }

    public long readUnsignedIntLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedIntLE();
    }

    public int readInt() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readInt();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos readBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.network.FriendlyByteBuf) this.data).readBlockPos());
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeUuid(java.util.UUID p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeUUID(p0));
    }

    public int getUnsignedShort(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedShort(p0);
    }

    public int nioBufferCount() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).nioBufferCount();
    }

    public int writableBytes() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writableBytes();
    }

    public java.util.Date readDate() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readDate();
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeVarInt(int p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeVarInt(p0));
    }

    public java.util.UUID readUuid() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUUID();
    }

    public boolean isReadable() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isReadable();
    }

    public boolean isReadable(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isReadable(p0);
    }

    public int readVarInt() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readVarInt();
    }

    public long readVarLong() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readVarLong();
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeLongArray(long[] p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeLongArray(p0));
    }

    public boolean isWritable() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isWritable();
    }

    public boolean isWritable(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isWritable(p0);
    }

    public char readChar() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readChar();
    }

    public io.netty.buffer.ByteBuf setFloatLE(int p0, float p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setFloatLE(p0, p1);
    }

    public long getLongLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getLongLE(p0);
    }

    public float readFloat() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readFloat();
    }

    public boolean getBoolean(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getBoolean(p0);
    }

    public java.lang.CharSequence readCharSequence(int p0, java.nio.charset.Charset p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readCharSequence(p0, p1);
    }

    public int readUnsignedShortLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedShortLE();
    }

    public java.nio.ByteBuffer[] nioBuffers(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).nioBuffers(p0, p1);
    }

    public java.nio.ByteBuffer[] nioBuffers() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).nioBuffers();
    }

    public int ensureWritable(int p0, boolean p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).ensureWritable(p0, p1);
    }

    public int readableBytes() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readableBytes();
    }

    public io.netty.buffer.ByteBuf readBytes(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readBytes(p0);
    }

    public int getUnsignedMedium(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedMedium(p0);
    }

    public int readBytes(java.nio.channels.FileChannel p0, long p1, int p2) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readBytes(p0, p1, p2);
    }

    public int readBytes(java.nio.channels.GatheringByteChannel p0, int p1) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readBytes(p0, p1);
    }

    public io.netty.buffer.ByteBuf retainedSlice() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).retainedSlice();
    }

    public io.netty.buffer.ByteBuf retainedSlice(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).retainedSlice(p0, p1);
    }

    public java.lang.String toString(int p0, int p1, java.nio.charset.Charset p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).toString(p0, p1, p2);
    }

    public java.lang.String toString(java.nio.charset.Charset p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).toString(p0);
    }

    public boolean readBoolean() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readBoolean();
    }

    public long[] readLongArray(long[] p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readLongArray(p0);
    }

    public long[] readLongArray(long[] p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readLongArray(p0, p1);
    }

    public short readShort() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readShort();
    }

    public cn.jstxjf_.rte.mapping.holder.BlockHitResult readBlockHitResult() {
        return new cn.jstxjf_.rte.mapping.holder.BlockHitResult(((net.minecraft.network.FriendlyByteBuf) this.data).readBlockHitResult());
    }

    public int getBytes(int p0, java.nio.channels.FileChannel p1, long p2, int p3) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getBytes(p0, p1, p2, p3);
    }

    public int getBytes(int p0, java.nio.channels.GatheringByteChannel p1, int p2) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getBytes(p0, p1, p2);
    }

    public io.netty.buffer.ByteBuf duplicate() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).duplicate();
    }

    public cn.jstxjf_.rte.mapping.holder.Identifier readIdentifier() {
        return new cn.jstxjf_.rte.mapping.holder.Identifier(((net.minecraft.network.FriendlyByteBuf) this.data).readResourceLocation());
    }

    public int writerIndex() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writerIndex();
    }

    public int getUnsignedShortLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedShortLE(p0);
    }

    public int writeCharSequence(java.lang.CharSequence p0, java.nio.charset.Charset p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeCharSequence(p0, p1);
    }

    public java.nio.ByteBuffer nioBuffer() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).nioBuffer();
    }

    public java.nio.ByteBuffer nioBuffer(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).nioBuffer(p0, p1);
    }

    public int[] readIntArray(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readVarIntArray(p0);
    }

    public int[] readIntArray() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readVarIntArray();
    }

    public io.netty.buffer.ByteBufAllocator alloc() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).alloc();
    }

    public int readUnsignedShort() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedShort();
    }

    public int getMedium(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getMedium(p0);
    }

    public char getChar(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getChar(p0);
    }

    public double getDouble(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getDouble(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeVarLong(long p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeVarLong(p0));
    }

    public float getFloat(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getFloat(p0);
    }

    public int maxWritableBytes() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).maxWritableBytes();
    }

    public short readUnsignedByte() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedByte();
    }

    public java.nio.ByteBuffer internalNioBuffer(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).internalNioBuffer(p0, p1);
    }

    public int hashCode() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).hashCode();
    }

    public int setCharSequence(int p0, java.lang.CharSequence p1, java.nio.charset.Charset p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setCharSequence(p0, p1, p2);
    }

    public short getUnsignedByte(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedByte(p0);
    }

    public <T extends java.lang.Enum<T>> T readEnumConstant(java.lang.Class<T> p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readEnum(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeString(java.lang.String p0, int p1) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeUtf(p0, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeString(java.lang.String p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeUtf(p0));
    }

    public boolean release(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).release(p0);
    }

    public boolean release() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).release();
    }

    public io.netty.buffer.ByteBuf order(java.nio.ByteOrder p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).order(p0);
    }

    public java.nio.ByteOrder order() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).order();
    }

    public io.netty.buffer.ByteBuf readRetainedSlice(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readRetainedSlice(p0);
    }

    public io.netty.buffer.ByteBuf slice() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).slice();
    }

    public io.netty.buffer.ByteBuf slice(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).slice(p0, p1);
    }

    public int getMediumLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getMediumLE(p0);
    }

    public int maxCapacity() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).maxCapacity();
    }

    public int bytesBefore(int p0, int p1, byte p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).bytesBefore(p0, p1, p2);
    }

    public int bytesBefore(int p0, byte p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).bytesBefore(p0, p1);
    }

    public int bytesBefore(byte p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).bytesBefore(p0);
    }

    public io.netty.buffer.ByteBuf writeDoubleLE(double p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeDoubleLE(p0);
    }

    public int getInt(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getInt(p0);
    }

    public int capacity() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).capacity();
    }

    public boolean hasMemoryAddress() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).hasMemoryAddress();
    }

    public io.netty.buffer.ByteBuf copy(int p0, int p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).copy(p0, p1);
    }

    public io.netty.buffer.ByteBuf copy() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).copy();
    }

    public short readShortLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readShortLE();
    }

    public float getFloatLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getFloatLE(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeIntArray(int[] p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeVarIntArray(p0));
    }

    public int readUnsignedMedium() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedMedium();
    }

    public boolean isReadOnly() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isReadOnly();
    }

    public double readDouble() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readDouble();
    }

    public io.netty.buffer.ByteBuf readSlice(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readSlice(p0);
    }

    public java.lang.String readString() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUtf();
    }

    public java.lang.String readString(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUtf(p0);
    }

    public int getIntLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getIntLE(p0);
    }

    public int forEachByteDesc(io.netty.util.ByteProcessor p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).forEachByteDesc(p0);
    }

    public int forEachByteDesc(int p0, int p1, io.netty.util.ByteProcessor p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).forEachByteDesc(p0, p1, p2);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeByteArray(byte[] p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeByteArray(p0));
    }

    public long memoryAddress() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).memoryAddress();
    }

    public boolean hasArray() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).hasArray();
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeIdentifier(cn.jstxjf_.rte.mapping.holder.Identifier p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeResourceLocation(((net.minecraft.resources.ResourceLocation) p0.data)));
    }

    public boolean isDirect() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).isDirect();
    }

    public int readMediumLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readMediumLE();
    }

    public long getLong(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getLong(p0);
    }

    public long readLong() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readLong();
    }

    public byte[] readByteArray(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readByteArray(p0);
    }

    public byte[] readByteArray() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readByteArray();
    }

    public int compareTo(io.netty.buffer.ByteBuf p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).compareTo(p0);
    }

    public java.lang.CharSequence getCharSequence(int p0, int p1, java.nio.charset.Charset p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getCharSequence(p0, p1, p2);
    }

    public io.netty.buffer.ByteBuf unwrap() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).unwrap();
    }

    public int readerIndex() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readerIndex();
    }

    public int indexOf(int p0, int p1, byte p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).indexOf(p0, p1, p2);
    }

    public long getUnsignedInt(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedInt(p0);
    }

    public int refCnt() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).refCnt();
    }

    public long getUnsignedIntLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedIntLE(p0);
    }

    public cn.jstxjf_.rte.mapping.holder.CompoundTag readNbt() {
        final var v0 = ((net.minecraft.network.FriendlyByteBuf) this.data).readNbt();
        return (v0 == null ? null : new cn.jstxjf_.rte.mapping.holder.CompoundTag(v0));
    }

    public long readUnsignedInt() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedInt();
    }

    public short getShortLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getShortLE(p0);
    }

    public float readFloatLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readFloatLE();
    }

    public double getDoubleLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getDoubleLE(p0);
    }

    public io.netty.buffer.ByteBuf retainedDuplicate() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).retainedDuplicate();
    }

    public int writeBytes(java.nio.channels.ScatteringByteChannel p0, int p1) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeBytes(p0, p1);
    }

    public int writeBytes(java.io.InputStream p0, int p1) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeBytes(p0, p1);
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeEnumConstant(java.lang.Enum<?> p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeEnum(p0));
    }

    public int readMedium() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readMedium();
    }

    public int writeBytes(java.nio.channels.FileChannel p0, long p1, int p2) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeBytes(p0, p1, p2);
    }

    public int forEachByte(io.netty.util.ByteProcessor p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).forEachByte(p0);
    }

    public int forEachByte(int p0, int p1, io.netty.util.ByteProcessor p2) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).forEachByte(p0, p1, p2);
    }

    public io.netty.buffer.ByteBuf setDoubleLE(int p0, double p1) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setDoubleLE(p0, p1);
    }

    public io.netty.buffer.ByteBuf writeFloatLE(float p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).writeFloatLE(p0);
    }

    public int arrayOffset() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).arrayOffset();
    }

    public int readIntLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readIntLE();
    }

    public double readDoubleLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readDoubleLE();
    }

    public int readUnsignedMediumLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readUnsignedMediumLE();
    }

    public long readLongLE() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readLongLE();
    }

    public cn.jstxjf_.rte.mapping.holder.PacketBuffer writeBlockPos(cn.jstxjf_.rte.mapping.holder.BlockPos p0) {
        return new cn.jstxjf_.rte.mapping.holder.PacketBuffer(((net.minecraft.network.FriendlyByteBuf) this.data).writeBlockPos(((net.minecraft.core.BlockPos) p0.data)));
    }

    public byte[] array() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).array();
    }

    public int setBytes(int p0, java.io.InputStream p1, int p2) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setBytes(p0, p1, p2);
    }

    public int setBytes(int p0, java.nio.channels.FileChannel p1, long p2, int p3) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setBytes(p0, p1, p2, p3);
    }

    public int setBytes(int p0, java.nio.channels.ScatteringByteChannel p1, int p2) throws java.io.IOException {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).setBytes(p0, p1, p2);
    }

    public short getShort(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getShort(p0);
    }

    public byte readByte() {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).readByte();
    }

    public void writeBlockHitResult(cn.jstxjf_.rte.mapping.holder.BlockHitResult p0) {
        ((net.minecraft.network.FriendlyByteBuf) this.data).writeBlockHitResult(((net.minecraft.world.phys.BlockHitResult) p0.data));
    }

    public int getUnsignedMediumLE(int p0) {
        return ((net.minecraft.network.FriendlyByteBuf) this.data).getUnsignedMediumLE(p0);
    }
}
