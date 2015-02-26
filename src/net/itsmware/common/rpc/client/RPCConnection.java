package net.itsmware.common.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.itsmware.common.rpc.client.policy.ConnectPolicy;
import net.itsmware.common.rpc.client.policy.PolicyFactory;
import net.itsmware.common.rpc.exception.RPCException;
import net.itsmware.common.rpc.helper.RPCServerInfo;
import net.itsmware.common.rpc.helper.RPCUrl;
import net.itsmware.common.rpc.serializer.RPCMessage;
import net.itsmware.common.rpc.serializer.RPCReturn;

import org.apache.log4j.Logger;

/**
 * 
 * @author James Gao
 *
 */
public class RPCConnection extends RemoteRPCServerHandler {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(RPCConnection.class);

	/**
	 * 用于断开后重连接。
	 */
	private ScheduledExecutorService executor;

	/**
	 * 标识是否连接。
	 */
	private boolean connected;

	/**
	 * 调用的计数器，做为每次调用的标识。
	 */
	private AtomicLong invokeCounter;

	/**
	 * 通道。
	 */
	private Channel channel;

	/**
	 * 调用返回的结果。
	 */
	private RPCReturn rpcReturn;

	/**
	 * 用于标识请求是否已发送成功。
	 */
	private boolean sendResponseOK;

	/**
	 * 标识是否强制关闭。
	 */
	private boolean forceClosed;

	/**
	 * 等待处理完毕。
	 */
	private CountDownLatch countDownLatch;

	/**
	 * 请求异常。
	 */
	private Throwable cause;

	/**
	 * 连接的地址。
	 */
	private RPCUrl url;

	/**
	 * 代表一个RPC服务器。
	 */
	private RPCServerInfo rpcServer;

	/**
	 * 
	 * @param url
	 * @throws RPCException
	 */
	public RPCConnection(String url) throws RPCException {
		this.url = new RPCUrl(url);
		this.executor = Executors.newScheduledThreadPool(1);
		invokeCounter = new AtomicLong(0L);
		forceClosed = false;
		sendResponseOK = false;

	}

	/**
	 * 发送一个请求。
	 * 
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private RPCReturn sendRequest(RPCMessage request) throws Exception {

		sendResponseOK = false;
		// log.debug("-->发送消息:"+request);
		countDownLatch = new CountDownLatch(1);
		long t1 = System.currentTimeMillis();
		channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
					sendResponseOK = true;
				} else {
					cause = f.cause();
					sendResponseOK = false;

				}
			}
		}).sync();
		// 等待结果返回。
		countDownLatch.await(this.rpcServer.getRequestTimtout(),
				TimeUnit.SECONDS);
		long t2 = System.currentTimeMillis();
		if (this.rpcReturn == null) { // 说明请求超时，则抛出异常。
			if (this.sendResponseOK) {
				throw new Exception(request.getCallID() + "-->请求超时: "
						+ (t2 - t1) + "ms", cause);
			} else {
				throw new Exception(request.getCallID() + "-->请求时出错", cause);
			}
		}

		RPCReturn resp = this.rpcReturn;
		this.rpcReturn = null; // 清除全局的变量值，防止其它线程使用。
		this.cause = null;

		return resp;
	}

	/**
	 * 连接到远程服务。
	 * 
	 * @throws Exception
	 */
	protected void connect() throws Exception {
		if (this.connected)
			return;

		// 要按照连接策略
		PolicyFactory pf = new PolicyFactory();
		ConnectPolicy policy = pf.createPolicy(url);
		this.rpcServer = policy.select();

		// 如果当前策略没有选定一台RPC服务器，则不进行连接，此连接要设置为无效。
		if (this.rpcServer == null) {
			this.connected = false;
			this.forceClosed = true;
			this.channel = null;
			throw new RPCException("No rpc server is availiable.");
		}

		EventLoopGroup group = new NioEventLoopGroup();

		RemoteRPCChannelInitializer handler = new RemoteRPCChannelInitializer(
				this.rpcServer.getMsgSize(), this);
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.option(ChannelOption.TCP_NODELAY, true);
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
				this.rpcServer.getConnTimeout() * 1000);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(handler);
		try {
			// 发起异步连接操作。
			b.connect(this.rpcServer.getHostname(), this.rpcServer.getPort())
					.sync();
		} finally {
			// 如果有问题则重连。
			executor.execute(new Runnable() {

				@Override
				public void run() {
					if (forceClosed)
						return; // 如果是强制关闭连接则不再生连。
					try {
						TimeUnit.SECONDS.sleep(5);
						// 发起异步连接操作。
						connect();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}

			});
		}
	}

	/**
	 * 关闭连接。
	 * 
	 * @throws Throwable
	 */
	public void close() throws Exception {
		this.connected = false;
		forceClosed = true;
		if (null != channel) {
			channel.close().sync();
		}
	}

	/**
	 * 是否连接。
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * 通道是否关闭。
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return (null == channel || !channel.isActive() || !channel.isOpen() || !channel
				.isWritable());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext cxt, RPCReturn msg)
			throws Exception {

		/*if (log.isDebugEnabled()) {
			log.debug("Received response message: " + msg);
		}*/

		this.rpcReturn = msg;

		// 通知调用程序可以得到返回结果了。
		log.debug(msg.getCallID() + "-->通知调用线程处理...");
		this.countDownLatch.countDown();

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("Connected to: " + ctx.name());
		channel = ctx.channel();
		this.connected = true;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("Disconnect from: " + ctx.name());
		this.connected = false;
		channel = null;		
	}

	@Override
	public boolean isSharable() {
		return true;
	}

	/**
	 * 调用远程服务，返回调用结果。
	 * 
	 * @param serviceName
	 *            服务名
	 * @param methodName
	 *            方法名
	 * @param args
	 *            方法的参数值。
	 * @return
	 */
	public RPCReturn call(String serviceName, String methodName, Object[] args)
			throws Exception {
		if (!isConnected() || this.isClosed()) {
			throw new IllegalStateException("not connected");
		}
		RPCMessage rpcMsg = new RPCMessage();

		rpcMsg.setServiceName(serviceName);
		rpcMsg.setInvokeMethodName(methodName);

		rpcMsg.setArgs(args);
		rpcMsg.setCallID(serviceName + "-" + methodName + "-"
				+ this.invokeCounter.incrementAndGet());

		return this.sendRequest(rpcMsg);

	}

}
