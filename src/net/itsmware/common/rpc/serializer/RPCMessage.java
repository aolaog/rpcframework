package net.itsmware.common.rpc.serializer;

import java.io.Serializable;

/**
 * 代表一个服务调用传递的消息数据。
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
	 * 调用的ID。
	 */
	private String callID;

	/**
	 * 要调用的远程服务名。
	 */
	private String serviceName;

	/**
	 * 要调用的服务方法。
	 */
	private String invokeMethodName;

	/**
	 * 要调用时传递的方法。
	 */
	private Object[] args;

	/**
	 * 请求时的时间.
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
