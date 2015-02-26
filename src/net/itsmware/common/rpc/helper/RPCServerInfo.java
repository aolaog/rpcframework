/**
 * 
 */
package net.itsmware.common.rpc.helper;

import java.io.Serializable;

/**
 * RPC������������Ϣ��
 * @author James Gao
 *
 */
public class RPCServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9178609139542379857L;

	/**
	 * ��������
	 */
	private String hostname;
	
	/**
	 * �˿ڡ�
	 */
	private int port ;
	
	/**
	 * ���ӳ�ʱʱ�䡣��λ����
	 */
	private int connTimeout;
	
	/**
	 * ������������ʱʱ�䡣��λ����
	 */
	private int requestTimtout; 
	
	/**
	 * ��Ϣ������ֽ�����
	 */
	private int msgSize;
	/**
	 * 
	 */
	public RPCServerInfo() {
	}
	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the connTimeout
	 */
	public int getConnTimeout() {
		return connTimeout;
	}
	/**
	 * @param connTimeout the connTimeout to set
	 */
	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}
	/**
	 * @return the requestTimtout
	 */
	public int getRequestTimtout() {
		return requestTimtout;
	}
	/**
	 * @param requestTimtout the requestTimtout to set
	 */
	public void setRequestTimtout(int requestTimtout) {
		this.requestTimtout = requestTimtout;
	}
	/**
	 * @return the msgSize
	 */
	public int getMsgSize() {
		return msgSize;
	}
	/**
	 * @param msgSize the msgSize to set
	 */
	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}

}
