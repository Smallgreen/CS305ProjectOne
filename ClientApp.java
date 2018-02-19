
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

        TransportLayer transportLayer = new TransportLayer(false, 0,0);

        //initiate cache, new folder, new file when receive

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard


        //while line is not empty
        while( line != null && !line.equals("") )
        {
            HTTP request;
            String[] parseLine = line.split("\\s+");

            if(parseLine[0].equals("***")){
                request = new HTTP(true,"GET",httpVersion,parseLine[2]);
            }else{
                request = new HTTP(true,"TEXT", httpVersion,line);
            }

            //convert lines into byte array, send to transoport layer and wait for response
            byte[] byteArray = request.getRequest().getBytes();


            transportLayer.send( byteArray );
            byteArray = transportLayer.receive();
            //new arrlist


            String str = new String ( byteArray );

            System.out.println( str );

            //read next line
            line = reader.readLine();
        }
    }

    

}
