package net.itsmware.common.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.atomic.AtomicBoolean;

import net.itsmware.common.rpc.handler.LocalServiceCallHandler;
import net.itsmware.common.rpc.handler.ServiceRegistry;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * The RPC server. This object listen to a specific port and when a client
 * connects it delegates the connection to a
 * {@link net.itsmware.common.rpc.handler.ConnectionHandler ConnectionHandler}.
 *
 * @author aol_aog
 * @date 2014-11-13
 */
public class RPCServer {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(RPCServer.class);

	/**
	 * 服务的端口。
	 */
	private int port;

	/**
	 * 超时时间，默认为60秒。
	 */
	private int timeout;

	/**
	 * 最大消息字节数，用于保护内存避免溢出。
	 */
	private int msgSize;

	/**
	 * 本地方法调用处理器类。
	 */
	private LocalServiceCallHandler localServiceHandler;

	/**
	 * 服务注册表，用于查找服务。
	 */
	private ServiceRegistry serviceRegistry;

	/**
	 * 标识是否已经停止。
	 */
	private AtomicBoolean stopped = new AtomicBoolean(false);

	/**
	 * 启动RPC服务器。
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		log.info("Starting RPCServer, bind on port: " + port + "...");

		// 启动本地Socket server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(20);

		ServerBootstrap bootstrap = new ServerBootstrap();
		try {
			this.localServiceHandler = new LocalServiceCallHandler(
					serviceRegistry);
			LocalRPCChannelInitializer ci = new LocalRPCChannelInitializer(
					timeout, msgSize, this.localServiceHandler);

			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(ci);
			// 绑定服务端口，等待连接进入。
			Channel channel = bootstrap.bind(port).channel();
			log.info("RPC Server start successfully!");
			waitForShutdownCommand();

			log.info("Stopping RPC Server...");
			ChannelFuture cf = channel.close();
			cf.awaitUninterruptibly();
			log.info("RPC Server stopped.");
		} finally {
			// 关闭线程池
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	public void stop() {
		stopped.set(true);
		synchronized (stopped) {
			stopped.notifyAll();
		}
	}

	private void waitForShutdownCommand() {
		synchronized (stopped) {
			while (!stopped.get()) {
				try {
					stopped.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the msgSize
	 */
	public int getMsgSize() {
		return msgSize;
	}

	/**
	 * @param msgSize the msgSize to set
	 */
	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}

	public static void main(String[] args) {
		BasicConfigurator.resetConfiguration();
		DOMConfigurator.configure("./src/log4j.xml");
		final RPCServer server = new RPCServer();

		new Thread() {
			public void run() {
				server.port = 9399;
				server.timeout = 60;
				ServiceRegistry sr = new ServiceRegistry();
				sr.registerService("中国人", new MockEchoService());
				server.setServiceRegistry(sr);
				try {
					server.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();

		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("关闭了....");

		server.stop();*/
	}
}
