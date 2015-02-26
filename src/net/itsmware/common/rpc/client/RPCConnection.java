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
	 * ���ڶϿ��������ӡ�
	 */
	private ScheduledExecutorService executor;

	/**
	 * ��ʶ�Ƿ����ӡ�
	 */
	private boolean connected;

	/**
	 * ���õļ���������Ϊÿ�ε��õı�ʶ��
	 */
	private AtomicLong invokeCounter;

	/**
	 * ͨ����
	 */
	private Channel channel;

	/**
	 * ���÷��صĽ����
	 */
	private RPCReturn rpcReturn;

	/**
	 * ���ڱ�ʶ�����Ƿ��ѷ��ͳɹ���
	 */
	private boolean sendResponseOK;

	/**
	 * ��ʶ�Ƿ�ǿ�ƹرա�
	 */
	private boolean forceClosed;

	/**
	 * �ȴ�������ϡ�
	 */
	private CountDownLatch countDownLatch;

	/**
	 * �����쳣��
	 */
	private Throwable cause;

	/**
	 * ���ӵĵ�ַ��
	 */
	private RPCUrl url;

	/**
	 * ����һ��RPC��������
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
	 * ����һ������
	 * 
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private RPCReturn sendRequest(RPCMessage request) throws Exception {

		sendResponseOK = false;
		// log.debug("-->������Ϣ:"+request);
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
		// �ȴ�������ء�
		countDownLatch.await(this.rpcServer.getRequestTimtout(),
				TimeUnit.SECONDS);
		long t2 = System.currentTimeMillis();
		if (this.rpcReturn == null) { // ˵������ʱ�����׳��쳣��
			if (this.sendResponseOK) {
				throw new Exception(request.getCallID() + "-->����ʱ: "
						+ (t2 - t1) + "ms", cause);
			} else {
				throw new Exception(request.getCallID() + "-->����ʱ����", cause);
			}
		}

		RPCReturn resp = this.rpcReturn;
		this.rpcReturn = null; // ���ȫ�ֵı���ֵ����ֹ�����߳�ʹ�á�
		this.cause = null;

		return resp;
	}

	/**
	 * ���ӵ�Զ�̷���
	 * 
	 * @throws Exception
	 */
	protected void connect() throws Exception {
		if (this.connected)
			return;

		// Ҫ�������Ӳ���
		PolicyFactory pf = new PolicyFactory();
		ConnectPolicy policy = pf.createPolicy(url);
		this.rpcServer = policy.select();

		// �����ǰ����û��ѡ��һ̨RPC���������򲻽������ӣ�������Ҫ����Ϊ��Ч��
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
			// �����첽���Ӳ�����
			b.connect(this.rpcServer.getHostname(), this.rpcServer.getPort())
					.sync();
		} finally {
			// �����������������
			executor.execute(new Runnable() {

				@Override
				public void run() {
					if (forceClosed)
						return; // �����ǿ�ƹر���������������
					try {
						TimeUnit.SECONDS.sleep(5);
						// �����첽���Ӳ�����
						connect();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}

			});
		}
	}

	/**
	 * �ر����ӡ�
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
	 * �Ƿ����ӡ�
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * ͨ���Ƿ�رա�
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

		// ֪ͨ���ó�����Եõ����ؽ���ˡ�
		log.debug(msg.getCallID() + "-->֪ͨ�����̴߳���...");
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
	 * ����Զ�̷��񣬷��ص��ý����
	 * 
	 * @param serviceName
	 *            ������
	 * @param methodName
	 *            ������
	 * @param args
	 *            �����Ĳ���ֵ��
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
