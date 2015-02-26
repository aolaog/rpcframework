/**
 * 
 */
package net.itsmware.common.rpc.handler;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.itsmware.common.rpc.serializer.RPCMessage;
import net.itsmware.common.rpc.serializer.RPCReturn;
import net.itsmware.common.rpc.server.ReflectionCache;

import org.apache.log4j.Logger;

/**
 * @author James Gao
 *
 */
public class LocalServiceCallHandler {

	/**
	 * logger.
	 */
	private Logger log = Logger.getLogger(LocalServiceCallHandler.class);
	/**
	 * 服务的注册器。
	 */
	private ServiceRegistry serviceReg;

	/**
	 * 
	 */
	public LocalServiceCallHandler(ServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public RPCReturn exec(RPCMessage msg) {
		// 分解请求包，然后执行本地的方法，最后返回结果。
		
		String callID = msg.getCallID();
		String mn = msg.getInvokeMethodName();
		String sn = msg.getServiceName();
		Object[] args = msg.getArgs();
		// 查找服务。
		Object service = this.serviceReg.getService(sn);
		RPCReturn rpcRet = new RPCReturn();
		rpcRet.setCallID(callID);
		rpcRet.setRequestTime(msg.getRequestTime());
		if (service == null) {
			rpcRet.setThrowing(true);
			rpcRet.setRetVal(new Exception("找不到指定的服务：" + sn));
		} else {
			Class<?>[] paramClasses = null;
			if (args == null) {
				paramClasses = new Class[0];
			} else {
				paramClasses = new Class[args.length];
				for(int i =0;i < args.length; i++){
					paramClasses[i] = args[i].getClass();
				}
			}
			
			Method invokeMethod = null;
			try {
				invokeMethod = ReflectionCache.getMethod(service.getClass()
						.getName(), mn, paramClasses);
			} catch (Exception ex) {
				// ignore this exception.
			}
			if (invokeMethod == null) {
				rpcRet.setThrowing(true);
				rpcRet.setRetVal(new NoSuchMethodException("找不到指定的方法：" + mn));
			} else {
				try {
					Object rv = invokeMethod.invoke(service, args);
					rpcRet.setRetVal((Serializable) rv);
					rpcRet.setThrowing(false);					
				} catch (Exception ex) {
					rpcRet.setThrowing(true);
					rpcRet.setRetVal(ex);
					log.error(ex.getMessage(), ex);

				}
				//System.out.println("-----返回："+rpcRet);
			}
		}
		return rpcRet;
	}

	public ServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(ServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}
}
