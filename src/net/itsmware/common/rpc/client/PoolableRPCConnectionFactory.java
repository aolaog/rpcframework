/**
 * 
 */
package net.itsmware.common.rpc.client;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * 创建具有池化功能的连接工厂。
 * 
 * @author James Gao
 *
 */
public class PoolableRPCConnectionFactory implements RPCConnectionFactory,
		PooledObjectFactory<RPCConnection> {
	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(PoolableRPCConnectionFactory.class);
	/**
	 * 连接池。
	 */
	private GenericObjectPool<RPCConnection> pool;

	/**
	 * 连接的地址。
	 */
	private String url;

	/**
	 * 创建一个RPC的连接池。
	 */
	public PoolableRPCConnectionFactory() {
		this.pool = new GenericObjectPool<RPCConnection>(this);		
	}

	@Override
	public RPCConnection getConnection() throws Exception {
		return this.pool.borrowObject();
	}

	@Override
	public void recycle(RPCConnection connection) throws Exception {
		if (null != connection) {
			pool.returnObject(connection);
			log.debug("将连接返回到连接池中。");
		}

	}

	@Override
	public void activateObject(PooledObject<RPCConnection> po) throws Exception {
		if(!po.getObject().isConnected()){
			po.getObject().connect();
			log.debug("连接到服务器。");
		}
	}

	@Override
	public void destroyObject(PooledObject<RPCConnection> po) throws Exception {
		po.getObject().close();
		log.debug("已关闭到服务器的连接。");
	}

	@Override
	public PooledObject<RPCConnection> makeObject() throws Exception {
		// 检查必要的参数是否已经设置。
		if(this.url == null  ){
			throw new IllegalArgumentException("rpc url is not set.");
		}
		PooledRPCConnection poolConn = new PooledRPCConnection(url);
		poolConn.setPoolableRPCConnFactory(this);
		return new DefaultPooledObject<RPCConnection>(poolConn);
	}

	@Override
	public void passivateObject(PooledObject<RPCConnection> conn)
			throws Exception {
	}

	@Override
	public boolean validateObject(PooledObject<RPCConnection> conn) {
		return !conn.getObject().isClosed() && conn.getObject().isConnected();
	}

	/**
	 * 关闭整个连接池。
	 */
	public void close() {
		log.info("关闭RPC连接池...");
		this.pool.close();
		this.pool.clear();
		log.info("RPC连接池关闭完毕。");
	}

	public void setLifo(boolean lifo) {
		pool.setLifo(lifo);
	}

	public void setMaxTotal(int maxActive) {
		if (maxActive < 0) {
			pool.setMaxTotal(10);
		} else {
			pool.setMaxTotal(maxActive);
		}

	}

	public void setMaxIdle(int maxIdle) {
		if (maxIdle < 0) {
			pool.setMaxIdle(1);
		} else
			pool.setMaxIdle(maxIdle);
	}

	public void setMaxWaitMillis(long maxWait) {
		if (maxWait < 0) {
			pool.setMaxWaitMillis(30 * 1000L);
		} else
			pool.setMaxWaitMillis(maxWait);
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		pool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	public void setMinIdle(int minIdle) {
		pool.setMinIdle(minIdle);
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}

	public void setSoftMinEvictableIdleTimeMillis(
			long softMinEvictableIdleTimeMillis) {
		pool.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		pool.setTestOnBorrow(testOnBorrow);
	}

	public void setTestOnReturn(boolean testOnReturn) {
		pool.setTestOnReturn(testOnReturn);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		pool.setTestWhileIdle(testWhileIdle);
	}

	public void setTimeBetweenEvictionRunsMillis(
			long timeBetweenEvictionRunsMillis) {
		pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	public void setWhenExhaustedAction(boolean blockWhenExhausted) {
		pool.setBlockWhenExhausted(blockWhenExhausted);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	

}
