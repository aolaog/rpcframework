package net.itsmware.common.rpc.serializer;

import java.io.Serializable;

/**
 * ����һ��������ô��ݵ���Ϣ���ݡ�
 *
 * @author aol_aog
 * @date 2014-11-13
 */
public class RPCMessage implements Serializable {

	/**
	 * serialUID.
	 */
	private static final long serialVersionUID = -3779455092391695774L;

	/**
	 * ���õ�ID��
	 */
	private String callID;

	/**
	 * Ҫ���õ�Զ�̷�������
	 */
	private String serviceName;

	/**
	 * Ҫ���õķ��񷽷���
	 */
	private String invokeMethodName;

	/**
	 * Ҫ����ʱ���ݵķ�����
	 */
	private Object[] args;

	/**
	 * ����ʱ��ʱ��.
	 */
	private long t;

	public RPCMessage() {
		t = System.currentTimeMillis();
	}

	public long getRequestTime() {
		return this.t;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInvokeMethodName() {
		return invokeMethodName;
	}

	public void setInvokeMethodName(String invokeMethodName) {
		this.invokeMethodName = invokeMethodName;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getCallID() {
		return callID;
	}

	public void setCallID(String callID) {
		this.callID = callID;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(callID).append(":");
		if(this.args != null){
			sb.append("{");
			for(Object o : args){
				sb.append("[").append(o).append("]");
			}
			sb.append("}");
		}else{
			sb.append("{empty}");
		}
		
		return sb.toString();
	}
}
