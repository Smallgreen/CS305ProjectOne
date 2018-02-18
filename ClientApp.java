
import java.io.BufferedReader;
import java.io.InputStreamReader;

//This class represents the client application
public class ClientApp
{


    public static boolean persistent;

    public static void main(String[] args) throws Exception
    {
        persistent = false;
        double httpVersion = Double.parseDouble(args[0]);

        if(httpVersion == 1.0){
            persistent = true;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        TransportLayer transportLayer = new TransportLayer(false, 0,0);

        //while line is not empty
        while( line != null && !line.equals("") )
        {
            //convert lines into byte array, send to transoport layer and wait for response
            byte[] byteArray = line.getBytes();
            transportLayer.send( byteArray );
            byteArray = transportLayer.receive();
            String str = new String ( byteArray );
            System.out.println( str );
            //read next line
            line = reader.readLine();
        }
    }
    

}
