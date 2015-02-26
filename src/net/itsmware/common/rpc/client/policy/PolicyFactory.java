/**
 * 
 */
package net.itsmware.common.rpc.client.policy;

import net.itsmware.common.rpc.exception.RPCException;
import net.itsmware.common.rpc.helper.RPCUrl;

/**
 * @author James Gao
 *
 */
public class PolicyFactory {

	/**
	 * 
	 */
	public PolicyFactory() {
	}

	/**
	 * 根据URL中指定项创建一个连接策略。
	 * @param url
	 * @return
	 * @throws RPCException
	 */
	public ConnectPolicy createPolicy(RPCUrl url) throws RPCException{
		if(RPCUrl.RANDOM_POLICY.equalsIgnoreCase(url.getClusterPolicy())){
			return new RandomConnectPolicy(url.getServerInfo());
		}else if(RPCUrl.RANDOM_POLICY.equalsIgnoreCase(url.getClusterPolicy())){
			return new OrderConnectPolicy(url.getServerInfo());
		}else{
			throw new RPCException("not support connect policy: "+url.getClusterPolicy());
		}
	}
}
