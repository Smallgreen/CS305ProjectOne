
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
    private static int[] experArr = {1, 1, 2, 1, 3, 1}; //visit all the pages

    public static void main(String[] args) throws Exception
    {
        persistent = false;
        double httpVersion = Double.parseDouble(args[0]);

        //http version: 1.0 persistent, 1,1 non
        if(httpVersion == 1.0){
            persistent = true;
        }

        //initiate transport layer
        TransportLayer transportLayer = new TransportLayer(false, 0,0);

        //initiate cache folder
        String PATH = "./cache";
        File directory = new File(PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        //local cache table, filename - date
        Hashtable<String, Integer> localCache = new Hashtable<String, Integer>();

        System.out.println("Select mode: interactive / experiment");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //select mode
        if(line.equals("interactive")){
            isExperi = false;
        }
        else{
            isExperi = true;
        }

        line = reader.readLine();

        //while line is not empty
        while( line != null && !line.equals("") ) {

            HTTP request = new HTTP();
            String[] parseLine = line.split("\\s+");//split the main page address by space
            String[] content;
            String fileName = parseLine[2];
            String cmd = parseLine[0];

            //send http request, save embeded files into arraylist and check cache
            content = sendHelper(localCache, fileName, cmd, request, httpVersion, transportLayer, line);
            while(content == null){
                System.out.println("please input website address again");
                line = reader.readLine();
                parseLine = line.split("\\s+");//split the main page address by space
                fileName = parseLine[2];
                cmd = parseLine[0];
                content = sendHelper(localCache, fileName, cmd, request, httpVersion, transportLayer, line);
            }

            //get webpage list
            ArrayList<String> webpage = new ArrayList<>();
            for(int i=0;i<content.length;i++){
                if(content[i].length() != 0 && !content[i].trim().isEmpty()) {
                    webpage.add(content[i]);
                }
            }

            ArrayList<String> index = new ArrayList<>();
            int indexPage = 0;

            //load all files in current page
            for(int i = 0;i<webpage.size();i++){
                HTTP requestEmbeded;
                String[] parseLineEmbeded = webpage.get(i).split("\\s+");

                if(parseLineEmbeded[0].equals("***")){
                    //if it is link
                    if(parseLineEmbeded[1].equals("href")){
                        indexPage++;
                        index.add(webpage.get(i));
                        if(!isExperi){
                            //show the links
                            System.out.println(indexPage + ". " + parseLineEmbeded[3]);
                        }
                        continue;
                    }
                    //if it is img
                    else {
                        //if it is not in local cache, send http request to get files from server
                        if (!localCache.containsKey(parseLineEmbeded[2])){
                            requestEmbeded = new HTTP(true, "GET", httpVersion, parseLineEmbeded[2], 1);
                        }
                        else{
                            int date = localCache.get(parseLineEmbeded[2]);
                            //check the date of last modified
                            requestEmbeded = new HTTP(true, "GET", httpVersion, parseLineEmbeded[2], date);
                            byte[] byteArr = requestEmbeded.getRequest().getBytes();
                            transportLayer.send(byteArr);
                            byteArr = transportLayer.receive();
                            String str = new String(byteArr);
                            String[] dataSplit = str.split("@");

                            //if it did not modified, load from cache
                            if(Integer.parseInt(dataSplit[0]) == 304 && dataSplit[1].equals("NOT MODIFIED")){

                                File f = new File("./cache/" + parseLineEmbeded[2]);
                                try {
                                    byteArr = Files.readAllBytes(f.toPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(!isExperi) {
                                    System.out.println(new String(byteArr));
                                }
                                continue;
                            }
                            else{
                                //if it is modified, request from server again

                                requestEmbeded = new HTTP(true, "GET", httpVersion, parseLineEmbeded[2], 1);
                                localCache.remove(parseLineEmbeded[2]);
                            }

                        }

                    }

                }else{
                    //if it is text
                    requestEmbeded = new HTTP(true,"TEXT", httpVersion,webpage.get(i),1);
                }

                byte[] byteArrayEmbeded = requestEmbeded.getRequest().getBytes();
                transportLayer.send( byteArrayEmbeded );
                byteArrayEmbeded = transportLayer.receive();

                String strEmbeded = new String ( byteArrayEmbeded );
                String[] response = strEmbeded.split("@");

                if(Integer.parseInt(response[0]) == 200){
                    //saving to local cache
                    if(!localCache.containsKey(parseLineEmbeded[2])){
                        //storeInCache(localCache, parseLineEmbeded[2], response[1], Integer.parseInt(response[3]));
                    }
                    if(!isExperi){
                        System.out.println(response[1]);
                    }
                }
                else{
                    if(!isExperi){
                        System.out.println(response[0]);
                    }
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
                System.out.println("experiment on page. " + experArr[experCnt]);
                if(experCnt == experArr.length - 1){
                    break;
                }
                experCnt++;
            }
        }
    }

    public static void storeInCache(Hashtable<String, Integer> table, String fileName, String content, int modified){

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

    public static String[] sendHelper(Hashtable<String, Integer> localCache, String fileName, String cmd, HTTP request,
                                  double httpVersion, TransportLayer transportLayer, String line){

        byte[] byteArray;
        String[] dataSplit;
        String[] content;

        if (!localCache.containsKey(fileName)) {
            if (cmd.equals("***")) {
                request = new HTTP(true, "GET", httpVersion, fileName, 1);
            } else {
                request = new HTTP(true, "TEXT", httpVersion, line, 1);
            }

            //convert lines into byte array, send to transoport layer and wait for response

            byteArray = request.getRequest().getBytes();

            transportLayer.send(byteArray);
            byteArray = transportLayer.receive();
            String str = new String(byteArray);

            dataSplit = str.split("@");

            if(Integer.parseInt(dataSplit[0]) == 200 ){

                //storeInCache(localCache, fileName, dataSplit[1], Integer.parseInt(dataSplit[3]));
                content = dataSplit[1].split("\\r?\\n");
            }
                else {
                    System.out.println(dataSplit[0]);
                    content = null;
            }
        }
        else{
            int date = localCache.get(fileName);
            if (cmd.equals("***")) {
                request = new HTTP(true, "GET", httpVersion, fileName, date);
            } else {
                request = new HTTP(true, "TEXT", httpVersion, line, date);
            }
            byteArray = request.getRequest().getBytes();

            transportLayer.send(byteArray);
            byteArray = transportLayer.receive();
            String str = new String(byteArray);

            dataSplit = str.split("@");

            if(Integer.parseInt(dataSplit[0]) == 304 && dataSplit[1].equals("NOT MODIFIED")){

                File f = new File("./cache/" + fileName);

                try {
                    byteArray = Files.readAllBytes(f.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                content = new String(byteArray).split("\\r?\\n");

            }
            else{

                if (cmd.equals("***")) {
                    request = new HTTP(true, "GET", httpVersion, fileName, 1);
                } else {
                    request = new HTTP(true, "TEXT", httpVersion, line, 1);
                }

                //convert lines into byte array, send to transoport layer and wait for response

                byteArray = request.getRequest().getBytes();

                transportLayer.send(byteArray);
                byteArray = transportLayer.receive();
                String str2 = new String(byteArray);

                dataSplit = str2.split("@");

                //storeInCache(localCache, fileName, dataSplit[1], Integer.parseInt(dataSplit[3]));
                content = dataSplit[1].split("\\r?\\n");
            }

        }

        return content;

    }

}
