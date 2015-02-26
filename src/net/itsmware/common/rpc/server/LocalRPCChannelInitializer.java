/**
 * 
 */
package net.itsmware.common.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.itsmware.common.rpc.handler.LocalServiceCallHandler;
import net.itsmware.common.rpc.serializer.RPCMessage;
import net.itsmware.common.rpc.serializer.RPCReturn;
import net.itsmware.common.rpc.serializer.RpcDecoder;
import net.itsmware.common.rpc.serializer.RpcEncoder;

/**
 * RPC���õ�ͨ����ʼ���ࡣ
 * 
 * @author James Gao
 *
 */
public class LocalRPCChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	/**
	 * ��ʱʱ�䣬Ĭ��Ϊ60�롣
	 */
	private int timeout;

	/**
	 * �����ϢΪ1M�������ڴ档
	 */
	private int msgSize;

	private LocalServiceCallHandler serviceHandler;

	/**
	 * ��ָ���ĳ�ʱʱ�����Ϣ�Ĵ�С����ͨ����ʼ���ࡣ
	 * 
	 * @param timeout
	 *            ��ʱʱ�䡣
	 * @param msgSize
	 *            �����Ϣ�ֽ�����
	 */
	public LocalRPCChannelInitializer(int timeout, int msgSize,
			LocalServiceCallHandler serviceHandler) {
		if (timeout < 0)
			timeout = 60;
		this.timeout = timeout;
		if (msgSize < 1024)
			msgSize = 1024 * 1024 * 1;
		this.msgSize = msgSize;
		this.serviceHandler = serviceHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		/*
		 * ch.pipeline().addLast("readTimeoutHandler", new
		 * ReadTimeoutHandler(this.timeout));
		 */
		/*
		 * ch.pipeline().addLast( new ObjectDecoder(msgSize, ClassResolvers
		 * .weakCachingConcurrentResolver(this.getClass() .getClassLoader())));
		 * ch.pipeline().addLast(new ObjectEncoder());
		 */
		
		ch.pipeline().addLast(new RpcDecoder(RPCMessage.class,this.msgSize)); // �� RPC
																// ��Ӧ���н��루Ϊ�˴�����Ӧ��
		ch.pipeline().addLast(new RpcEncoder(RPCReturn.class,this.msgSize)); // �� RPC
																	// ������б��루Ϊ�˷�������
		// ������LocalRPCServerHandler��ִ�е��ñ��صķ���
		ch.pipeline().addLast(new LocalRPCServerHandler(serviceHandler));
	}

}
