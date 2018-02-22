
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;

//This class represents the client application
public class ClientApp
{
    public static boolean persistent;
    public static boolean isExperi;
    public static int experCnt = 0;
    private static int[] experArr = {1, 1, 2, 1, 3, 1};//1, 1, 2, 1, 3, 1

    public static void main(String[] args) throws Exception
    {
        persistent = false;
        double httpVersion = Double.parseDouble(args[0]);

        if(httpVersion == 1.0){
            persistent = true;
        }

        TransportLayer transportLayer = new TransportLayer(false, 0,0);

        //initiate cache, new folder, new file when receive

        String PATH = "./cache";
        File directory = new File(PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        Hashtable<String, Integer> localCache = new Hashtable<String, Integer>();

        //map: name - date
        //map in server: read from log file
        //map in client: fill with msg from server

        //store the list of file stored in local cache
        //ArrayList<String> cacheList = new ArrayList<>();

        System.out.println("Select mode: interactive / experiment");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        if(line.equals("interactive")){
            isExperi = false;
        }
        else{
            isExperi = true;
        }

        line = reader.readLine();

        //while line is not empty
        while( line != null && !line.equals("") ) {

            HTTP request;
            String[] parseLine = line.split("\\s+");
            byte[] byteArray;
            String[] dataSplit;
            String[] content;

            System.out.print("contains: "+ localCache.containsKey(parseLine[2]));

            if (!localCache.containsKey(parseLine[2])) {
                if (parseLine[0].equals("***")) {
                    request = new HTTP(true, "GET", httpVersion, parseLine[2], 1);
                } else {
                    request = new HTTP(true, "TEXT", httpVersion, line, 1);
                }

            //convert lines into byte array, send to transoport layer and wait for response

            byteArray = request.getRequest().getBytes();

            transportLayer.send(byteArray);
            byteArray = transportLayer.receive();
            String str = new String(byteArray);

            dataSplit = str.split("@");

            System.out.println("currentKey: " + Integer.parseInt(dataSplit[3]));

            storeInCache(localCache, parseLine[2], dataSplit[1], Integer.parseInt(dataSplit[3]));
            content = dataSplit[1].split("\\r?\\n");
        }
        else{
               int date = localCache.get(parseLine[2]);
                if (parseLine[0].equals("***")) {
                    request = new HTTP(true, "GET", httpVersion, parseLine[2], date);
                } else {
                    request = new HTTP(true, "TEXT", httpVersion, line, date);
                }
                byteArray = request.getRequest().getBytes();

                transportLayer.send(byteArray);
                byteArray = transportLayer.receive();
                String str = new String(byteArray);
                dataSplit = str.split("@");

                if(Integer.parseInt(dataSplit[0]) == 304){
                    System.out.println("300004");
                    File f = new File("./cache/" + parseLine[2]);
                    byteArray = Files.readAllBytes(f.toPath());
                    content = new String(byteArray).split("\\r?\\n");
                }
                else{

                    if (parseLine[0].equals("***")) {
                        request = new HTTP(true, "GET", httpVersion, parseLine[2], 1);
                    } else {
                        request = new HTTP(true, "TEXT", httpVersion, line, 1);
                    }

                    //convert lines into byte array, send to transoport layer and wait for response

                    byteArray = request.getRequest().getBytes();

                    transportLayer.send(byteArray);
                    byteArray = transportLayer.receive();
                    String str2 = new String(byteArray);

                    dataSplit = str2.split("@");

                    storeInCache(localCache, parseLine[2], dataSplit[1], Integer.parseInt(dataSplit[3]));
                    content = dataSplit[1].split("\\r?\\n");
                }

            }

            //get webpage list

           //System.out.println("data split: "+dataSplit[1]);
//            System.out.println("length: "+content.length);

            ArrayList<String> webpage = new ArrayList<>();
            for(int i=0;i<content.length;i++){
                if(content[i].length() != 0 && !content[i].trim().isEmpty()) {
                    webpage.add(content[i]);
                }
            }

            //boolean isText = false;
            ArrayList<String> index = new ArrayList<>();
            int indexPage = 0;
            for(int i = 0;i<webpage.size();i++){
                HTTP requestEmbeded;
                String[] parseLineEmbeded = webpage.get(i).split("\\s+");
                //System.out.println("client get "+webpage.get(i));

                if(parseLineEmbeded[0].equals("***")){
                    if(parseLineEmbeded[1].equals("href")){
                        indexPage++;
                        index.add(webpage.get(i));
                        if(!isExperi){
                            System.out.println(indexPage + ". " + parseLineEmbeded[3]);
                        }
                        continue;

                    }
                    else {
                        requestEmbeded = new HTTP(true, "GET", httpVersion, parseLineEmbeded[2], 1);
                    }

                }else{
                    //WTFFF
                    //isText = true;
                    requestEmbeded = new HTTP(true,"TEXT", httpVersion,webpage.get(i),1);
                    //System.out.println("html "+parseLine[2]);
                }

                byte[] byteArrayEmbeded = requestEmbeded.getRequest().getBytes();


                transportLayer.send( byteArrayEmbeded );
                //System.out.println(new String(byteArrayEmbeded));
                byteArrayEmbeded = transportLayer.receive();

                String strEmbeded = new String ( byteArrayEmbeded );


                    String[] response = strEmbeded.split("@");
                    if(!isExperi){
                        System.out.println(response[1]);
                    }


            }
            if(!isExperi){
                System.out.println("Please enter the page number you would like to view: ");
                //read next line
                line = reader.readLine();

                if(line == null || line.equals("") ){
                    break;
                }
                line = index.get(Integer.parseInt(line)-1);
            }
            else{
                line = index.get(experArr[experCnt] - 1);
                System.out.println("expe" + experArr[experCnt]);
                if(experCnt == experArr.length - 1){
                    break;
                }
                experCnt++;
            }
//            webpage.clear();
//            index.clear();
        }
    }

    public static void storeInCache(Hashtable<String, Integer> table, String fileName, String content, int modified){
    //store, put in hashmap, deal with modified date
        //before request http, search in cache, send date

        if(!table.contains(fileName)){
            table.put(fileName, modified);
        }

        try {
            BufferedWriter file = new BufferedWriter(new FileWriter("./cache/"+fileName));
            file.write(content);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void sendHelper(){

    }


    

}
