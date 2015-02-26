/**
 * 
 */
package net.itsmware.common.rpc.client;

/**
 * RPC连接的工厂类。
 * @author James Gao
 *
 */
public interface RPCConnectionFactory {

	/**
	 * 从工厂方法中获取一个连接。
	 * @return
	 * @throws Throwable
	 */
	public RPCConnection getConnection() throws Throwable ;
	
	/**
	 * 回收连接。
	 * @param connection
	 * @throws Throwable
	 */
	public void recycle(RPCConnection connection) throws Throwable ;

}
