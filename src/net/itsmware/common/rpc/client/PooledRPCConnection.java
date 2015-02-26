/**
 * 
 */
package net.itsmware.common.rpc.client;

import net.itsmware.common.rpc.exception.RPCException;

/**
 * @author James Gao
 *
 */
public class PooledRPCConnection extends RPCConnection {

	private PoolableRPCConnectionFactory poolableRPCConnFactory;
	
	
	/**
	 * @param address
	 * @param port
	 * @throws RPCException 
	 */
	public PooledRPCConnection(String url) throws RPCException {
		super(url);
	}

	
	/**
	 * ǿ�ƹر����ӡ�
	 * @throws Exception 
	 */
	public void foreClose() throws Exception{
		super.close();
	}
	
	/**
	 * ���Ǹ���Ĺر��߼����������ӷ��ص����ӳ��С�
	 */
	@Override
	public void close()throws Exception{
		if(this.poolableRPCConnFactory  != null){
			this.poolableRPCConnFactory.recycle(this);
		}else{
			//������ϵͳ����
			throw new Exception("���ӳ�δ��ȷ������");
		}
	}

	public PoolableRPCConnectionFactory getPoolableRPCConnFactory() {
		return poolableRPCConnFactory;
	}

	public void setPoolableRPCConnFactory(PoolableRPCConnectionFactory poolableRPCConnFactory) {
		this.poolableRPCConnFactory = poolableRPCConnFactory;
	}

}
