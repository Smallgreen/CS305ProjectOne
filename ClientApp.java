

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

            //System.out.println("the str received!!!!!   " + str);

            String[] dataSplit = str.split("@");
            String[] content = dataSplit[1].split("\\r?\\n");
           System.out.println("data split: "+dataSplit[1]);
//            System.out.println("length: "+content.length);

            ArrayList<String> webpage = new ArrayList<>();
            for(int i=0;i<content.length;i++){
                if(content[i].length() != 0) {
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
                        System.out.println(indexPage + ". " + parseLineEmbeded[3]);
                        continue;

                    }
                    else {
                        requestEmbeded = new HTTP(true, "GET", httpVersion, parseLineEmbeded[2], false);
                    }

                }else{
                    //WTFFF
                    //isText = true;
                    requestEmbeded = new HTTP(true,"TEXT", httpVersion,webpage.get(i),false);
                    //System.out.println("html "+parseLine[2]);
                }

                byte[] byteArrayEmbeded = requestEmbeded.getRequest().getBytes();


                transportLayer.send( byteArrayEmbeded );
                //System.out.println(new String(byteArrayEmbeded));
                byteArrayEmbeded = transportLayer.receive();

                String strEmbeded = new String ( byteArrayEmbeded );


                    String[] response = strEmbeded.split("@");
                    //you wen ti
                    System.out.println(response[1]);


            }
            System.out.println("Please enter the page number you would like to view: ");




            //read next line
            line = reader.readLine();

            line = index.get(Integer.parseInt(line)-1);
            webpage.clear();
            index.clear();
        }
    }

    

}
