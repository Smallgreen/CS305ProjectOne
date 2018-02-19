
public class TransportLayer
{

    private NetworkLayer networkLayer;
    private boolean isPersistent;
    //public boolean isConnected;
    private String SYN = "hello";
    private boolean isServer;
    //server is true if the application is a server (should listen) or false if it is a client (should try and connect)
    public TransportLayer(boolean server, int prop_delay, int trans_delay)
    {
        isServer = server;
        networkLayer = new NetworkLayer(server, prop_delay, trans_delay);
        if(!server) {
            checkPersistent();

            if(isPersistent) {
                //build TCP connection
                send(SYN.getBytes());
                receive();
            }
        }
    }

    public void checkPersistent(){
        isPersistent = ClientApp.persistent;
    }

    public void send(byte[] payload)
    {
        if(!isPersistent && !isServer){
            networkLayer.send(SYN.getBytes());
            receive();
        }

        networkLayer.send(payload);
    }

    public byte[] receive()
    {
        byte[] payload = networkLayer.receive();
        return payload;
    }
}
