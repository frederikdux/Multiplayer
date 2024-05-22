package Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerListDTO implements Serializable {
    private static final long serialVersionUID = 6521685098267257690L;

    List<ServerData> serverDataList;

    public ServerListDTO(List<ServerData> serverDataList) {
        this.serverDataList = serverDataList;
    }

    public List<ServerData> getServerDataList() {
        return serverDataList;
    }

    public void setServerDataList(List<ServerData> serverDataList) {
        this.serverDataList = serverDataList;
    }
}
