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
	 * 强制关闭连接。
	 * @throws Exception 
	 */
	public void foreClose() throws Exception{
		super.close();
	}
	
	/**
	 * 覆盖父类的关闭逻辑，将此连接返回到连接池中。
	 */
	@Override
	public void close()throws Exception{
		if(this.poolableRPCConnFactory  != null){
			this.poolableRPCConnFactory.recycle(this);
		}else{
			//可能有系统错误。
			throw new Exception("连接池未正确创建。");
		}
	}

	public PoolableRPCConnectionFactory getPoolableRPCConnFactory() {
		return poolableRPCConnFactory;
	}

	public void setPoolableRPCConnFactory(PoolableRPCConnectionFactory poolableRPCConnFactory) {
		this.poolableRPCConnFactory = poolableRPCConnFactory;
	}

}
