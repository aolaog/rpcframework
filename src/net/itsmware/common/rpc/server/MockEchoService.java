package net.itsmware.common.rpc.server;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 */

/**
 * @author James Gao
 *
 */
public class MockEchoService {
	AtomicLong al ;
	/**
	 * 
	 */
	public MockEchoService() {
		al=new AtomicLong(1);
	}
	
	public String echo(){
		return "我是中国人啊: "+al.incrementAndGet();
	}

}
