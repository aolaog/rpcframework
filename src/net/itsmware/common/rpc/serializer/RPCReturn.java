package net.itsmware.common.rpc.serializer;

import java.io.Serializable;

/**
 * Class that holds method return information.
 *
 * @author aol_aog
 * @date 2014-11-13
 */
public class RPCReturn implements Serializable {

	private static final long serialVersionUID = -2353656699817180281L;

	private long t;

	private long t1, t2;

	public String getCallID() {
		return callID;
	}

	public void setCallID(String callID) {
		this.callID = callID;
	}

	public boolean isThrowing() {
		return throwing;
	}

	public void setThrowing(boolean throwing) {
		this.throwing = throwing;
	}

	public Serializable getRetVal() {
		return retVal;
	}

	public void setRetVal(Serializable retVal) {
		this.retVal = retVal;
	}

	public void setRequestTime(long t) {
		this.t = t;
	}

	public long getRequestTime() {
		return this.t;
	}

	public void setResponseTime1(long t) {
		this.t1 = t;
	}

	public long getResponseTime1() {
		return this.t1;
	}

	public void setResponseTime2(long t) {
		this.t2 = t;
	}

	public long getResponseTime2() {
		return this.t2;
	}

	/**
	 * 调用的ID。
	 */
	private String callID;
	/**
	 * 代表是否有异常抛出。
	 */
	private boolean throwing;

	/**
	 * 代表返回的值。
	 */
	private Serializable retVal;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(callID + "-");

		sb.append(this.retVal);
		return sb.toString();
	}
}
