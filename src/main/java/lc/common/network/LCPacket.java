package lc.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Generic packet implementation
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCPacket {

	protected enum PrimType {
		NULL, BOOLEAN, SHORT, INTEGER, FLOAT, DOUBLE;
	}

	/**
	 * Encode a packet into the network stream.
	 *
	 * @param ctx
	 *            The handler context
	 * @param buffer
	 *            The write buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	protected void encodePrimitiveInto(ByteBuf buffer, Object prim) throws IOException {
		if (prim == null) {
			buffer.writeByte(PrimType.NULL.ordinal());
		} else if (prim instanceof Boolean) {
			buffer.writeByte(PrimType.BOOLEAN.ordinal());
			buffer.writeBoolean((Boolean) prim);
		} else if (prim instanceof Short) {
			buffer.writeByte(PrimType.SHORT.ordinal());
			buffer.writeShort((Short) prim);
		} else if (prim instanceof Integer) {
			buffer.writeByte(PrimType.INTEGER.ordinal());
			buffer.writeInt((Integer) prim);
		} else if (prim instanceof Float) {
			buffer.writeByte(PrimType.FLOAT.ordinal());
			buffer.writeFloat((Float) prim);
		} else if (prim instanceof Double) {
			buffer.writeByte(PrimType.DOUBLE.ordinal());
			buffer.writeDouble((Double) prim);
		} else
			throw new IOException("Unknown primitive type " + prim.getClass().getName());
	}

	protected void encodePrimitiveArrayInto(ByteBuf buffer, Object[] arr) throws IOException {
		buffer.writeInt(arr.length);
		for (int i = 0; i < arr.length; i++)
			encodePrimitiveInto(buffer, arr[i]);
	}

	/**
	 * Decode a packet from the network stream.
	 *
	 * @param ctx
	 *            The handler context
	 * @param buffer
	 *            The read buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	protected Object decodePrimitiveFrom(ByteBuf buffer) throws IOException {
		byte typeof = buffer.readByte();
		switch (PrimType.values()[typeof]) {
		case BOOLEAN:
			return buffer.readBoolean();
		case DOUBLE:
			return buffer.readDouble();
		case FLOAT:
			return buffer.readFloat();
		case INTEGER:
			return buffer.readInt();
		case NULL:
			return null;
		case SHORT:
			return buffer.readShort();
		}
		throw new IOException("Unknown primitive type " + typeof);
	}

	protected Object[] decodePrimitiveArrayFrom(ByteBuf buffer) throws IOException {
		int sz = buffer.readInt();
		Object[] prims = new Object[sz];
		for (int i = 0; i < sz; i++)
			prims[i] = decodePrimitiveFrom(buffer);
		return prims;
	}

}
