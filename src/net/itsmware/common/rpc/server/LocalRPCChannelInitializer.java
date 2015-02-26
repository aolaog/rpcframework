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
 * RPC调用的通道初始化类。
 * 
 * @author James Gao
 *
 */
public class LocalRPCChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	/**
	 * 超时时间，默认为60秒。
	 */
	private int timeout;

	/**
	 * 最大消息为1M。保护内存。
	 */
	private int msgSize;

	private LocalServiceCallHandler serviceHandler;

	/**
	 * 用指定的超时时间和消息的大小构造通道初始化类。
	 * 
	 * @param timeout
	 *            超时时间。
	 * @param msgSize
	 *            最大消息字节数。
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
		
		ch.pipeline().addLast(new RpcDecoder(RPCMessage.class,this.msgSize)); // 将 RPC
																// 响应进行解码（为了处理响应）
		ch.pipeline().addLast(new RpcEncoder(RPCReturn.class,this.msgSize)); // 将 RPC
																	// 请求进行编码（为了发送请求）
		// 最终由LocalRPCServerHandler来执行调用本地的服务。
		ch.pipeline().addLast(new LocalRPCServerHandler(serviceHandler));
	}

}
