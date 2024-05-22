package Entity;

import java.io.Serial;
import java.io.Serializable;

public class ServerData implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267257690L;

    private Integer id;

    private String serverName;

    private String addr;

    private int port;


    public ServerData(String serverName, String addr, int port) {
        this.serverName = serverName;
        this.addr = addr;
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
