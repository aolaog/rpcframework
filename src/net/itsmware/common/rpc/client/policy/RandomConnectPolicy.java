/**
 * 
 */
package net.itsmware.common.rpc.client.policy;

import java.util.Random;

import net.itsmware.common.rpc.exception.RPCException;
import net.itsmware.common.rpc.helper.RPCServerInfo;

/**
 * ���ѡ��һ��rpc host�Ĳ��ԡ�
 * @author James Gao
 *
 */
public class RandomConnectPolicy implements ConnectPolicy {

	/**
	 * ���õķ������б�
	 */
	private RPCServerInfo[] serverInfos;
	/**
	 * @throws RPCException 
	 * 
	 */
	public RandomConnectPolicy(RPCServerInfo[] serverInfos) throws RPCException {
		if (serverInfos == null || serverInfos.length == 0)
			throw new RPCException("rpc server list is empty.");
		this.serverInfos = serverInfos;
	}

	/* (non-Javadoc)
	 * @see net.itsmware.common.rpc.client.policy.ConnectPolicy#select()
	 */
	@Override
	public RPCServerInfo select() {
		Random r = new Random();
		return serverInfos[(int)(r.nextFloat()*10)%serverInfos.length];
	}

}
