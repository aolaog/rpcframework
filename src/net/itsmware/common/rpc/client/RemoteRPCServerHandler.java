/**
 * 
 */
package net.itsmware.common.rpc.client;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.itsmware.common.rpc.serializer.RPCReturn;

import org.apache.log4j.Logger;

/**
 * RPC调用服务的处理类，主要完成发送调用消息包到服务端，然后读取调用的返回结果。
 * 
 * @author James Gao
 *
 */
public abstract class RemoteRPCServerHandler extends
		SimpleChannelInboundHandler<RPCReturn> {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(RemoteRPCServerHandler.class);


	

	/**
	 * 
	 */
	public RemoteRPCServerHandler() {
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		try {
			ctx.flush();
		} catch (Exception ex) {
			log.error("channelReadComplete");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof IOException) {
			log.info("通讯出错，断开连接: "+cause.getMessage());
			try {
				ctx.close();
			} catch (Exception e) {
				log.error("关闭通道时出错：" + e.getMessage(), e);
			}
		}else{
			log.error("系统内部异常："+cause.getMessage(), cause);
		}
		
	}

}
