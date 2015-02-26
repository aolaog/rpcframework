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
 * RPC���÷���Ĵ����࣬��Ҫ��ɷ��͵�����Ϣ��������ˣ�Ȼ���ȡ���õķ��ؽ����
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
			log.info("ͨѶ�����Ͽ�����: "+cause.getMessage());
			try {
				ctx.close();
			} catch (Exception e) {
				log.error("�ر�ͨ��ʱ����" + e.getMessage(), e);
			}
		}else{
			log.error("ϵͳ�ڲ��쳣��"+cause.getMessage(), cause);
		}
		
	}

}
