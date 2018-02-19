import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;

//This class represents the server application
public class ServerApp
{

    public static void main(String[] args) throws Exception
    {

        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("animals_logo.art");
        fileList.add("animals.clht");
        fileList.add("cat_logo.art");
        fileList.add("cat.art");
        fileList.add("cat.clht");
        fileList.add("cat2.art");
        fileList.add("cat3.art");
        fileList.add("giraffe_logo.art");
        fileList.add("giraffe.art");
        fileList.add("giraffe.clht");
        fileList.add("giraffe2.art");
        fileList.add("gorilla_logo.art");
        fileList.add("gorilla.art");
        fileList.add("gorilla.clht");
        fileList.add("gorilla2.art");

        System.out.println("Please enter propagation delay and transmission delay");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String readin = reader.readLine();
        String[] delays = readin.split("\\s+");
        int prop_delay = Integer.parseInt(delays[0]);
        int trans_delay = Integer.parseInt(delays[1]);

        //create a new transport layer for server (hence true) (wait for client)
        TransportLayer transportLayer = new TransportLayer(true, prop_delay, trans_delay);

        while( true )
        {
            //receive message from client, and send the "received" message back.
            byte[] byteArray = transportLayer.receive();
            //if client disconnected
            if(byteArray==null)
                break;
            String str = new String ( byteArray );

            if(str.equals("hello")){

                String line = "acknowledged";
                System.out.println(line);
                byteArray = line.getBytes();
                transportLayer.send( byteArray );
            }
            else{
                HTTP response;
                String[] request = str.split("\\s+");
                //if html
                if(request[0].equals("GET") && fileList.contains(request[1])){
                    String fileName = request[1];
                    File f = new File("./server_mem/" + fileName);
                    byteArray = Files.readAllBytes(f.toPath());
                    response = new HTTP(false,"200",Double.parseDouble(request[2]),byteArray.toString());
                    transportLayer.send(response.getResponse().getBytes());
                }
                else{
                    byteArray = request[1].getBytes();
                    transportLayer.send( byteArray );
                }


            }

        }
    }
}
