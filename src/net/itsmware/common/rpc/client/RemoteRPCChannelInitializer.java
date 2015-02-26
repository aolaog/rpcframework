/**
 * 
 */
package net.itsmware.common.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
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
public class RemoteRPCChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	/**
	 * 最大消息的字节数，以保护内存避免溢出。
	 */
	private int msgSize;
	
	/**
	 * 用来处理远程服务调用。
	 */
	private RemoteRPCServerHandler servCallHandler;

	/**
	 * 用指定的超时时间和消息的大小构造通道初始化类。
	 * 
	 * @param msgSize
	 *            最大消息字节数。
	 */
	public RemoteRPCChannelInitializer(int msgSize,RemoteRPCServerHandler servCallHandler) {

		if (msgSize < 1024)
			msgSize = 1024 * 1024 * 1;
		this.msgSize = msgSize;
		this.servCallHandler = servCallHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		/*ch.pipeline().addLast(
				new ObjectDecoder(msgSize, ClassResolvers
						.weakCachingConcurrentResolver(this.getClass()
								.getClassLoader())));
		ch.pipeline().addLast(new ObjectEncoder());*/
		ch.pipeline().addLast(new RpcEncoder(RPCMessage.class,this.msgSize)); // 将 RPC 请求进行编码（为了发送请求）
		ch.pipeline().addLast(new RpcDecoder(RPCReturn.class,this.msgSize)); // 将 RPC 响应进行解码（为了处理响应）
		// 最终由RemoteRPCServerHandler来执行调用远程的服务。
		ch.pipeline().addLast(this.servCallHandler);
	}

}
