import java.lang.Thread;

import static java.lang.Thread.*;

public class NetworkLayer
{

    private LinkLayer linkLayer;

    private static final int PROP_DELAY = 500;
    private int transDelay;
    private static final int TRAN_RATE = 2000;

    public NetworkLayer(boolean server)
    {
        linkLayer = new LinkLayer(server);

    }
    public void send(byte[] payload)
    {
        transDelay = payload.length / TRAN_RATE;
        try {
            Thread.sleep(PROP_DELAY);
            //delay trans
            linkLayer.send(payload);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public byte[] receive()
    {
        byte[] payload = linkLayer.receive();
        return payload;
    }
}
