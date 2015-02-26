/**
 * 
 */
package net.itsmware.common.rpc.client.policy;

import net.itsmware.common.rpc.exception.RPCException;
import net.itsmware.common.rpc.helper.RPCServerInfo;

/**
 * ��˳��ѡ��һ���������Ĳ��ԡ�
 * 
 * @author James Gao
 *
 */
public class OrderConnectPolicy implements ConnectPolicy {
	/**
	 * ���õķ������б�
	 */
	private RPCServerInfo[] serverInfos;

	/**
	 * �ϴ�ѡ��ķ�������
	 */
	private int last = -1;

	/**
	 * ��ǰѡ��ķ�������
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
		//û�п��õķ�������
		if  (this.serverInfos.length == 0) {
			this.last = -1;
			this.current = -1;
			return null;
		}
		//�״�ѡ��
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
