
package net.itsmware.common.rpc.exception;

/**
 * 调用RPC服务出错时抛出 exception
 *
 * @author aol_aog
 * @date   2014-11-13
 */
public class RPCException extends Exception {

    private static final long serialVersionUID = 7324141364282347199L;

    public RPCException() {
        super();
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCException(String message) {
        super(message);
    }

    public RPCException(Throwable cause) {
        super(cause);
    }
}
