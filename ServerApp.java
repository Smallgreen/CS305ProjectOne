import java.io.*;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

//This class represents the server application
public class ServerApp
{

    public static void main(String[] args) throws Exception
    {

        Hashtable<String, Integer> serverCache = new Hashtable<>();
        serverCache.put("animals_logo.art", 0);
        serverCache.put("animals.clht", 0);
        serverCache.put("cat_logo.art", 0);
        serverCache.put("cat.art", 0);
        serverCache.put("cat.clht", 0);
        serverCache.put("cat2.art", 0);
        serverCache.put("cat3.art", 0);
        serverCache.put("giraffe_logo.art", 0);
        serverCache.put("giraffe.art", 0);
        serverCache.put("giraffe.clht", 0);
        serverCache.put("giraffe2.art", 0);
        serverCache.put("gorilla_logo.art", 0);
        serverCache.put("gorilla.art", 0);
        serverCache.put("gorilla.clht", 0);
        serverCache.put("gorilla2.art", 0);

        //hashtable for storing contents
        Hashtable<String, String> checkModified = new Hashtable<>();

        String files;
        Set<String> keys = serverCache.keySet();
        Iterator<String> itr = keys.iterator();
        //iterate through serverCache and save the file
        while (itr.hasNext()) {
            // Getting Key
            files = itr.next();
            File f = new File("./server_mem/" + files);
            byte[] curContent = Files.readAllBytes(f.toPath());

            checkModified.put(files,new String((curContent)));
            //System.out.println(checkModified.get(files));
        }

        System.out.println("Please enter propagation delay and transmission delay");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String readin = reader.readLine();
        String[] delays = readin.split("\\s+");
        int prop_delay = Integer.parseInt(delays[0]);
        int trans_delay = Integer.parseInt(delays[1]);
        double delayCnt = 0;

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
                delayCnt = delayCnt + 0.25;//present difference between ack and data
            }
            else{
                HTTP response;
                String[] request = str.split("@");
                //if html
                int isModified = Integer.parseInt(request[3]);

                if(request[0].equals("GET")){
                    if(isModified != 0) {

                        if(serverCache.containsKey(request[2])){
                        String fileName = request[2];
                        response = new HTTP(false, "200", Double.parseDouble(request[1]), checkModified.get(fileName), 0);
                        transportLayer.send(response.getResponse().getBytes());
                        delayCnt++;

                    }
                    else{
                        response = new HTTP(false,"404",Double.parseDouble(request[1]),"NOT FOUND", 0);
                        transportLayer.send(response.getResponse().getBytes());
                        delayCnt = delayCnt+0.25;

                    }
                    }
                    else{
                        //cache, if not modify; server, if modify
                        //check file is modified or not
                        String fileName = request[2];
                        File f = new File("./server_mem/" + fileName);
                        byteArray = Files.readAllBytes(f.toPath());
                        if(!checkModified.get(fileName).equals(new String(byteArray))){
                            serverCache.put(fileName, 2);//change date
                            checkModified.put(fileName, new String(byteArray));//upgrade file content
                        }

                        //if date is same, not modified
                        if(isModified == serverCache.get(request[2])){
                            response = new HTTP(false,"304",Double.parseDouble(request[1]),"NOT MODIFIED", 0);
                            delayCnt = delayCnt+0.25;
                        }
                        else{
                            //sent back another code, reset server date
                            response = new HTTP(false,"304",Double.parseDouble(request[1]),"MODIFIED", 0);
                            serverCache.put(fileName, 0);
                            delayCnt = delayCnt+0.25;

                        }
                        transportLayer.send(response.getResponse().getBytes());

                    }
                }
                else{
                    response = new HTTP(false,"200",Double.parseDouble(request[1]),request[2],0);

                    transportLayer.send(response.getResponse().getBytes());
                    if(Double.parseDouble(request[1]) == 1.1){
                        delayCnt = delayCnt - 0.25;
                    }

                }


            }

        }

        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("./result"));
            file.write("prop delay: "+prop_delay+"\n"+"trans delay: "+trans_delay+"\n"+"total delay: "+ delayCnt*(prop_delay + trans_delay));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
