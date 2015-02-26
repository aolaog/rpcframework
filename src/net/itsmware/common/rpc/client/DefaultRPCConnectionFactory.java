package net.itsmware.common.rpc.client;

import net.itsmware.common.rpc.helper.RPCUrl;

/**
 * 默认的RPC连接工厂类。
 * 
 * @author James Gao
 *
 */
public class DefaultRPCConnectionFactory implements RPCConnectionFactory {
	/**
	 * 连接的地址。
	 */
	private String url;

	@Override
	public RPCConnection getConnection() throws Exception {
		return new RPCConnection(url);
	}

	@Override
	public void recycle(RPCConnection connection) throws Exception {
		if (connection != null) {
			connection.close();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
