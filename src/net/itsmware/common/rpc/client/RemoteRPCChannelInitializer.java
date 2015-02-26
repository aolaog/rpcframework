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
 * RPC���õ�ͨ����ʼ���ࡣ
 * 
 * @author James Gao
 *
 */
public class RemoteRPCChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	/**
	 * �����Ϣ���ֽ������Ա����ڴ���������
	 */
	private int msgSize;
	
	/**
	 * ��������Զ�̷�����á�
	 */
	private RemoteRPCServerHandler servCallHandler;

	/**
	 * ��ָ���ĳ�ʱʱ�����Ϣ�Ĵ�С����ͨ����ʼ���ࡣ
	 * 
	 * @param msgSize
	 *            �����Ϣ�ֽ�����
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
		ch.pipeline().addLast(new RpcEncoder(RPCMessage.class,this.msgSize)); // �� RPC ������б��루Ϊ�˷�������
		ch.pipeline().addLast(new RpcDecoder(RPCReturn.class,this.msgSize)); // �� RPC ��Ӧ���н��루Ϊ�˴�����Ӧ��
		// ������RemoteRPCServerHandler��ִ�е���Զ�̵ķ���
		ch.pipeline().addLast(this.servCallHandler);
	}

}
