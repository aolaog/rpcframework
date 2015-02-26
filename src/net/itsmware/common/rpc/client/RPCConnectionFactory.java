/**
 * 
 */
package net.itsmware.common.rpc.client;

/**
 * RPC���ӵĹ����ࡣ
 * @author James Gao
 *
 */
public interface RPCConnectionFactory {

	/**
	 * �ӹ��������л�ȡһ�����ӡ�
	 * @return
	 * @throws Throwable
	 */
	public RPCConnection getConnection() throws Throwable ;
	
	/**
	 * �������ӡ�
	 * @param connection
	 * @throws Throwable
	 */
	public void recycle(RPCConnection connection) throws Throwable ;

}
