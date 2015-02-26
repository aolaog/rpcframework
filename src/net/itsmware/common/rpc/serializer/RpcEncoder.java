/**
 * 
 */
package net.itsmware.common.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author James Gao
 *
 */
public class RpcEncoder extends MessageToByteEncoder {

	private Class<?> genericClass;

	/**
	 * �����Ϣ���ֽ������Ա����ڴ���������
	 */
	private int msgSize;

	public RpcEncoder(Class<?> genericClass, int msgSize) {
		this.genericClass = genericClass;
		this.msgSize = msgSize;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out)
			throws Exception {
		if (genericClass.isInstance(in)) {
			byte[] data = SerializationUtil.serialize(in);
			// ��鷢�͵���Ϣ��С��
			if (data.length > this.msgSize) {
				throw new Exception("message size(" + data.length
						+ ") exceed max size: " + this.msgSize + ".");
			}
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
}
