import java.io.*;
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
        int delayCnt = 0;

        //create a new transport layer for server (hence true) (wait for client)
        TransportLayer transportLayer = new TransportLayer(true, prop_delay, trans_delay);

        String logPath = "./serverLog";
        File logDirect = new File(logPath);
        if (! logDirect.exists()){
            logDirect.mkdir();
        }

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
                delayCnt++;
            }
            else{
                HTTP response;
                String[] request = str.split("@");
                //if html
                boolean isModified = Boolean.parseBoolean(request[3]);
                //System.out.println("test server "+request[1]);
                if(request[0].equals("GET")){
                    if(!isModified) {
                        //System.out.println("11111111");
                        if(fileList.contains(request[2])){
                        String fileName = request[2];
                        File f = new File("./server_mem/" + fileName);
                        byteArray = Files.readAllBytes(f.toPath());
                        response = new HTTP(false, "200", Double.parseDouble(request[1]), new String(byteArray), isModified);
                        transportLayer.send(response.getResponse().getBytes());
                        delayCnt++;

                    }
                    else{
                           // System.out.println("22222222");
                        response = new HTTP(false,"404",Double.parseDouble(request[1]),"NOT FOUND", isModified);
                        transportLayer.send(response.getResponse().getBytes());
                        delayCnt++;
                    }
                    }
                    else{
                        //System.out.println("3333333");
                        //cache, if not modify; server, if modify
                        response = new HTTP(false,"304",Double.parseDouble(request[1]),"NOT MODIFIED", isModified);
                        transportLayer.send(response.getResponse().getBytes());
                        delayCnt++;
                    }
                }
                else{
                    //System.out.println("4444444");
                    response = new HTTP(false,"200",Double.parseDouble(request[1]),request[2],isModified);

                    transportLayer.send(response.getResponse().getBytes());
                    delayCnt++;
                }


            }

        }

        //System.out.println("delay: "+delayCnt);
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("./result"));
            file.write("prop delay: "+prop_delay+"\n"+"trans delay: "+trans_delay+"\n"+"total delay: "+ delayCnt*(prop_delay + trans_delay));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
