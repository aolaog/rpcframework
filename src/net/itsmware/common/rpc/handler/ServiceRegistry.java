/**
 * 
 */
package net.itsmware.common.rpc.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * �����ṩԶ�̵��÷����ע�Ṧ�ܡ�
 * �˷�������ɷ�������Skeleton.
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
	 * ע��һ������RPC�����С�
	 * @param serviceName
	 * @param serviceInstance
	 */
	public void registerService(String serviceName, Object serviceInstance) {
		this.serviceMap.put(serviceName, serviceInstance);
	}

	/**
	 * ��RPC�������Ƴ�һ��ָ���ķ���
	 * @param serviceName
	 */
	public void unregisterService(String serviceName) {
		this.serviceMap.remove(serviceName);
	}

	/**
	 * ���ݷ���������ѯһ������ʵ����
	 * @param serviceName
	 * @return
	 */
	public Object getService(String serviceName){
		return this.serviceMap.get(serviceName);
	}
	
	
}
