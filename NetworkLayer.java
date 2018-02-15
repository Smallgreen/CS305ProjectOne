import java.lang.Thread;

import static java.lang.Thread.*;

public class NetworkLayer
{

    private LinkLayer linkLayer;

    private int prop_delay;
    private int trans_delay;

    public NetworkLayer(boolean server,int prop_d, int trans_d)
    {
        linkLayer = new LinkLayer(server);
        prop_delay = prop_d;
        trans_delay = trans_d;
    }
    public void send(byte[] payload)
    {
        try {
            Thread.sleep(prop_delay + trans_delay);
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
