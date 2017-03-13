import java.io.IOException;

/**
 * Created by myrao_000 on 2017-03-11.
 */

public class Main {
    public static void main (String[] args) throws IOException
    {
        RDPRelayServer rdpRelayServer = RDPRelayServer.getInstance();
        rdpRelayServer.run();
    }
}


