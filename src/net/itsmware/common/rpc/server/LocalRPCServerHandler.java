/**
 * 
 */
package net.itsmware.common.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import net.itsmware.common.rpc.handler.LocalServiceCallHandler;
import net.itsmware.common.rpc.serializer.RPCMessage;
import net.itsmware.common.rpc.serializer.RPCReturn;

import org.apache.log4j.Logger;

/**
 * RPC调用服务的处理类，主要完成RPC包头和包体分解，查找本地的服务并调用。
 * 
 * @author James Gao
 *
 */
public class LocalRPCServerHandler extends
		SimpleChannelInboundHandler<RPCMessage> {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(LocalRPCServerHandler.class);

	/**
	 * 调用次数的计数器。
	 */
	private AtomicLong invokeCounter;

	private LocalServiceCallHandler serviceHandler;

	/**
	 * 
	 */
	public LocalRPCServerHandler(LocalServiceCallHandler serviceHandler) {
		this.serviceHandler = serviceHandler;
		invokeCounter = new AtomicLong(0L);

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RPCMessage msg)
			throws Exception {
		long t0 = System.currentTimeMillis();
		long t1 = System.nanoTime();
		/*
		  if (log.isDebugEnabled()) { log.debug("Received request message: " +
		  msg); }*/
		 
		invokeCounter.incrementAndGet();
		RPCReturn rt = this.serviceHandler.exec(msg);
		long t2 = System.nanoTime();
		rt.setRequestTime(msg.getRequestTime());
		rt.setResponseTime1(t0);
		rt.setResponseTime2(System.currentTimeMillis());

		ctx.writeAndFlush(rt);

		if (log.isDebugEnabled()) {
			log.debug(msg.getCallID() + "--本次调用耗时：" + (t2 - t1) / 1000000.0
					+ "毫秒。");
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		try {
			ctx.flush();
		} catch (Exception ex) {
			log.error("channel Read Complete failed.");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof ReadTimeoutException) {

			log.info("客户端在指定时间内没有数据发送过来，断开连接." + cause.getMessage());
			try {
				ctx.close();
			} catch (Exception e) {
				log.error("关闭通道时出错：" + e.getMessage(), e);
			}
		} else if (cause instanceof IOException) {
			log.info("通讯出错，断开连接: " + cause.getMessage());
			try {
				ctx.close();
			} catch (Exception e) {
				log.error("关闭通道时出错：" + e.getMessage(), e);
			}
		} else {
			log.error("系统内部异常：" + cause.getMessage(), cause);
		}

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("Remote RPCClient comming in: " + ctx.name());

		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("Remote RPCClient disconnected: " + ctx.name());
		super.channelInactive(ctx);
	}

}
