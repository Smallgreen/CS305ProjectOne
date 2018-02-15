
public class TransportLayer
{

    private NetworkLayer networkLayer;
    public boolean isPersistent;
    //server is true if the application is a server (should listen) or false if it is a client (should try and connect)
    public TransportLayer(boolean server, int prop_delay, int trans_delay)
    {

        networkLayer = new NetworkLayer(server, prop_delay, trans_delay);
        checkPersistent();
    }

    public void checkPersistent(){
        isPersistent = ClientApp.persistent;
    }

    public void send(byte[] payload)
    {

        networkLayer.send( payload );
    }

    public byte[] receive()
    {
        byte[] payload = networkLayer.receive();    
        return payload;
    }
}
