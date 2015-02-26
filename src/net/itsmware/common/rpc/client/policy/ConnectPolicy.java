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
	 * 从一组RPC服务器中选择一个可用的服务器返回。
	 * @return
	 */
	public RPCServerInfo select();

}
