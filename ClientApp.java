
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

        //while line is not empty
        while( line != null && !line.equals("") )
        {
            HTTP request;
            String[] parseLine = line.split("\\s+");

            if(parseLine[0].equals("***")){
                request = new HTTP(true,"GET",httpVersion,parseLine[2],false);
            }else{
                request = new HTTP(true,"TEXT", httpVersion,line,false);
            }

            //convert lines into byte array, send to transoport layer and wait for response
            byte[] byteArray = request.getRequest().getBytes();


            transportLayer.send( byteArray );
            byteArray = transportLayer.receive();
            String str = new String ( byteArray );
            //System.out.println(str);

            String[] dataSplit = str.split(",");
            String[] content = dataSplit[1].split("\\r?\\n");


            ArrayList<String> webpage = new ArrayList<>();
            for(int i=0;i<content.length;i++){
                if(content[i].length() != 0) {
                    webpage.add(content[i]);
                }
            }
            for(int i = 0;i<webpage.size();i++){
                HTTP requestEmbeded;
                String[] parseLineEmbeded = webpage.get(i).split("\\s+");
                System.out.println(webpage.get(i));

                if(parseLineEmbeded[0].equals("***")){
                    requestEmbeded = new HTTP(true,"GET",httpVersion,parseLine[2],false);
                }else{
                    requestEmbeded = new HTTP(true,"TEXT", httpVersion,line,false);
                }

                byte[] byteArrayEmbeded = requestEmbeded.getRequest().getBytes();


                transportLayer.send( byteArrayEmbeded );
                byteArrayEmbeded = transportLayer.receive();
                String strEmbeded = new String ( byteArrayEmbeded );
                //System.out.println(strEmbeded);
            }



            //read next line
            line = reader.readLine();
        }
    }

    

}
