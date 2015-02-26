/**
 * 
 */
package net.itsmware.common.rpc.client.policy;

import net.itsmware.common.rpc.exception.RPCException;
import net.itsmware.common.rpc.helper.RPCServerInfo;

/**
 * 按顺序选择一个服务器的策略。
 * 
 * @author James Gao
 *
 */
public class OrderConnectPolicy implements ConnectPolicy {
	/**
	 * 可用的服务器列表。
	 */
	private RPCServerInfo[] serverInfos;

	/**
	 * 上次选择的服务器。
	 */
	private int last = -1;

	/**
	 * 当前选择的服务器。
	 */
	private int current = -1;

	/**
	 * @throws Exception
	 * 
	 */
	public OrderConnectPolicy(RPCServerInfo[] serverInfos) throws RPCException {
		if (serverInfos == null || serverInfos.length == 0)
			throw new RPCException("server list is empty.");
		this.serverInfos = serverInfos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.itsmware.common.rpc.client.policy.ConnectPolicy#select()
	 */
	@Override
	public RPCServerInfo select() {
		//没有可用的服务器。
		if  (this.serverInfos.length == 0) {
			this.last = -1;
			this.current = -1;
			return null;
		}
		//首次选举
		if (this.last <=0) {
			if (this.serverInfos.length != 0) {
				this.last = 0;
				this.current = 0;
				return this.serverInfos[this.current];
			} 
		} else {
			if(this.last >= this.serverInfos.length){
				this.last=0;
				this.current=0;
				return this.serverInfos[this.current];
			}else{
				int tmp = this.last+1;
				this.last = this.current;
				this.current = tmp;
				return this.serverInfos[this.current];
			}
		}

		return null;
	}

}
