/**
 * 
 */
package net.itsmware.common.rpc.helper;

import java.io.Serializable;

import net.itsmware.common.rpc.exception.RPCException;

/**
 * RPC的URL字符串。
 * <p>
 * 一般URL如下：rpc://(localhost:9300,localhost:9400)&clusterPolicy=random&
 * connTimeOut=10&requestTimeout=180&msgSize=1024000
 * </p>
 * 
 * @author James Gao,create on 2015-1-28
 *
 */
public class RPCUrl implements Serializable {

	/**
	 * serialUID.
	 */
	private static final long serialVersionUID = -6477861387591823671L;

	/**
	 * 随机选取一个服务。
	 */
	public static final String RANDOM_POLICY = "random";

	/**
	 * 按照URL中配置的顺序选取一个服务。
	 */
	public static final String SEQUENCE_POLICY = "order";

	/**
	 * 集群中服务的选取策略。
	 * 
	 * @see RPCUrl#RANDOM_POLICY
	 */
	private String clusterPolicy;

	/**
	 * 要连接的服务器信息。
	 */
	private RPCServerInfo[] serverInfo;

	/**
	 * 连接的超时时间。单位：秒
	 */
	private int connTimeout;

	/**
	 * 请求的超时时间。单位：秒
	 */
	private int requestTimeout;

	/**
	 * 请求包的最大大小。
	 */
	private int msgSize;

	/**
	 * @throws Exception
	 * 
	 */
	public RPCUrl(String url) throws RPCException {
		this.serverInfo = parseUrl(url);
	}

	/**
	 * 解析RPC URL字符串。 URL的格式：
	 * 
	 * @param url
	 * @return
	 * @throws RPCException
	 */
	private RPCServerInfo[] parseUrl(String url) throws RPCException {
		if (url == null || url.trim().isEmpty()) {
			throw new RPCException("rpc url is empty.");
		}

		if (!url.startsWith("rpc://")) {
			throw new RPCException("rpc url doesn't start with :\"rpc://\".");

		}
		// 去掉url://
		url = url.substring(6);

		String tmp[] = url.split("&");
		if (tmp == null || tmp.length == 0l) {
			throw new RPCException("rpc url is incorrect.");
		}

		for (int i = 1; i < tmp.length; i++) {
			String[] kk = tmp[i].split("=");
			if (kk == null || kk.length < 2) {
				throw new RPCException("rpc url is incorrect.");
			}
			if (kk[0].equalsIgnoreCase("clusterPolicy")) {
				if (kk[1].equalsIgnoreCase(RANDOM_POLICY)) {
					clusterPolicy = RANDOM_POLICY;

				} else if (kk[1].equalsIgnoreCase(SEQUENCE_POLICY)) {
					clusterPolicy = SEQUENCE_POLICY;

				} else {
					throw new RPCException(
							"rpc url's parameter: clusterPolicy is incorrect.");
				}
			} else if (kk[0].equalsIgnoreCase("connTimeOut")) {
				this.connTimeout = Integer.parseInt(kk[1].trim());
			} else if (kk[0].equalsIgnoreCase("requestTimeout")) {
				this.requestTimeout = Integer.parseInt(kk[1].trim());
			} else if (kk[0].equalsIgnoreCase("msgSize")) {
				this.msgSize = Integer.parseInt(kk[1].trim());
			} else {
				throw new RPCException("Invalidate rpc url's parameter: "
						+ kk[1]);
			}
		}

		if (connTimeout <= 0) {
			this.connTimeout = 10;
		}

		if (requestTimeout <= 0) {
			this.requestTimeout = 60;
		}

		String hostlist = tmp[0];
		// 去掉前后()
		if (!hostlist.startsWith("(") && !hostlist.endsWith(")")) {
			throw new RPCException("rpc url is incorrect.");
		}

		hostlist = hostlist.substring(1, hostlist.length() - 1);
		String[] hls = hostlist.split(",");
		if (hls == null || hls.length == 0) {
			throw new RPCException("no rpc servers are set.");
		}

		RPCServerInfo[] tt = new RPCServerInfo[hls.length];
		int i = 0;
		for (String hl : hls) {
			tt[i] = new RPCServerInfo();
			tt[i].setConnTimeout(connTimeout);
			tt[i].setRequestTimtout(requestTimeout);
			tt[i].setMsgSize(msgSize);
			String[] ss = hl.split(":");
			tt[i].setHostname(ss[0].trim());
			tt[i].setPort(Integer.parseInt(ss[1].trim()));

			i++;
		}

		return tt;
	}

	/**
	 * @return the clusterPolicy
	 */
	public String getClusterPolicy() {
		return clusterPolicy;
	}

	/**
	 * @return the serverInfo
	 */
	public RPCServerInfo[] getServerInfo() {
		return serverInfo;
	}

	/**
	 * @return the connTimeout
	 */
	public int getConnTimeout() {
		return connTimeout;
	}

	/**
	 * @return the requestTimeout
	 */
	public int getRequestTimeout() {
		return requestTimeout;
	}

	/**
	 * @param requestTimeout
	 *            the requestTimeout to set
	 */
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	/**
	 * @return the msgSize
	 */
	public int getMsgSize() {
		return msgSize;
	}

	/**
	 * @param msgSize
	 *            the msgSize to set
	 */
	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}
}
