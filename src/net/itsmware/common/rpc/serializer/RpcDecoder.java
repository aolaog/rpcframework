/**
 * 
 */
package net.itsmware.common.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import net.itsmware.common.rpc.server.LocalRPCServerHandler;

import org.apache.log4j.Logger;

/**
 * @author James Gao
 *
 */
public class RpcDecoder extends ByteToMessageDecoder {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(RpcDecoder.class);

	private Class<?> genericClass;

	/**
	 * �����Ϣ���ֽ������Ա����ڴ���������
	 */
	private int msgSize;

	public RpcDecoder(Class<?> genericClass, int msgSize) {
		this.genericClass = genericClass;
		this.msgSize = msgSize;
	}

	@Override
	public final void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (dataLength < 0) {
			ctx.close();
		}

		// ��鴫���������ݵĴ�С�Ƿ�̫��
		if (dataLength > this.msgSize) {
			log.warn("message length exceed " + this.msgSize
					+ "bytes and will be discard.");
			ctx.close();
		}
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}

		byte[] data = new byte[dataLength];
		in.readBytes(data);

		Object obj = SerializationUtil.deserialize(data, genericClass);
		out.add(obj);
	}
}
