/**
 * 
 */
package net.itsmware.common.rpc.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于提供远程调用服务的注册功能。
 * 此服务会生成服务器端Skeleton.
 * @author James Gao
 *
 */
public class ServiceRegistry {

	private Map<String, Object> serviceMap;

	/**
	 * 
	 */
	public ServiceRegistry() {
		this.serviceMap = Collections
				.synchronizedMap(new HashMap<String, Object>());
	}

	/**
	 * 注册一个服务到RPC服务中。
	 * @param serviceName
	 * @param serviceInstance
	 */
	public void registerService(String serviceName, Object serviceInstance) {
		this.serviceMap.put(serviceName, serviceInstance);
	}

	/**
	 * 从RPC服务中移除一个指定的服务。
	 * @param serviceName
	 */
	public void unregisterService(String serviceName) {
		this.serviceMap.remove(serviceName);
	}

	/**
	 * 根据服务名，查询一个服务实例。
	 * @param serviceName
	 * @return
	 */
	public Object getService(String serviceName){
		return this.serviceMap.get(serviceName);
	}
	
	
}
