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
	 * �����ע������
	 */
	private ServiceRegistry serviceReg;

	/**
	 * 
	 */
	public LocalServiceCallHandler(ServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public RPCReturn exec(RPCMessage msg) {
		// �ֽ��������Ȼ��ִ�б��صķ�������󷵻ؽ����
		
		String callID = msg.getCallID();
		String mn = msg.getInvokeMethodName();
		String sn = msg.getServiceName();
		Object[] args = msg.getArgs();
		// ���ҷ���
		Object service = this.serviceReg.getService(sn);
		RPCReturn rpcRet = new RPCReturn();
		rpcRet.setCallID(callID);
		rpcRet.setRequestTime(msg.getRequestTime());
		if (service == null) {
			rpcRet.setThrowing(true);
			rpcRet.setRetVal(new Exception("�Ҳ���ָ���ķ���" + sn));
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
				rpcRet.setRetVal(new NoSuchMethodException("�Ҳ���ָ���ķ�����" + mn));
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
				//System.out.println("-----���أ�"+rpcRet);
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
