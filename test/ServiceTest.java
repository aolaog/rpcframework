import net.itsmware.common.rpc.client.PoolableRPCConnectionFactory;
import net.itsmware.common.rpc.client.RPCConnection;
import net.itsmware.common.rpc.serializer.RPCReturn;

public class ServiceTest {
	int c = 0;

	public ServiceTest() {
	}

	private void run() {
		final PoolableRPCConnectionFactory prcf = new PoolableRPCConnectionFactory();
		prcf.setUrl("rpc://(localhost:9399,localhost:9398)&clusterPolicy=random&connTimeOut=10&requestTimeout=180&msgSize=1024000");
		prcf.setLifo(true);
		prcf.setMaxIdle(2);
		prcf.setMaxTotal(20);
		prcf.setMaxWaitMillis(60 * 1000L);
		prcf.setTestWhileIdle(true);
		prcf.setMinIdle(1);
		prcf.setTimeBetweenEvictionRunsMillis(10*1000L);

		for (c = 0; c < 1; c++) {

			new Thread() {
				public void run() {
					RPCConnection rpc = null;
					try {
						rpc = prcf.getConnection();
					} catch (Throwable e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						return;
					}
					
					long t = System.currentTimeMillis();
					for (int i = 0; i < 1; i++) {
						try {
							//
							// Thread.sleep(1);
							// long t = System.currentTimeMillis();

							RPCReturn rt = rpc.call("中国人", "echo", new Object[0]);
							System.out.println(rt.getRetVal());
						} catch (Exception ex) {
							ex.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println(c + ":线程调用总耗时："
							+ (System.currentTimeMillis() - t));

					try {
						prcf.recycle(rpc);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();

		}

	}

	public static void main(String[] args) {
		ServiceTest test = new ServiceTest();
		test.run();

	}

}
