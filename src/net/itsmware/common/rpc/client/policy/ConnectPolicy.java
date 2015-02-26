/**
 * 
 */
package net.itsmware.common.rpc.client.policy;

import net.itsmware.common.rpc.helper.RPCServerInfo;

/**
 * @author James Gao
 *
 */
public interface ConnectPolicy {

	/**
	 * ��һ��RPC��������ѡ��һ�����õķ��������ء�
	 * @return
	 */
	public RPCServerInfo select();

}
