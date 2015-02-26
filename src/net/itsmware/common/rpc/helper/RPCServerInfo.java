/**
 * 
 */
package net.itsmware.common.rpc.helper;

import java.io.Serializable;

/**
 * RPC服务器连接信息。
 * @author James Gao
 *
 */
public class RPCServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9178609139542379857L;

	/**
	 * 主机名。
	 */
	private String hostname;
	
	/**
	 * 端口。
	 */
	private int port ;
	
	/**
	 * 连接超时时间。单位：秒
	 */
	private int connTimeout;
	
	/**
	 * 请求允许的最大超时时间。单位：秒
	 */
	private int requestTimtout; 
	
	/**
	 * 消息包最大字节数。
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
