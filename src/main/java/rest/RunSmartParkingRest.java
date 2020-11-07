package rest;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connection.ConnectionPool;
import org.apache.log4j.xml.DOMConfigurator;
import org.bson.Document;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class.
 *
 */
public class RunSmartParkingRest {
    // Base URI the Grizzly HTTP server will listen on

    public static final String BASE_URI = "http://localhost:8080/unisasmartparkingrest/";
    public static final String DB_NAME="unisaSmartParking";
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.example package
        final ResourceConfig rc = new ResourceConfig().packages("rest");
        rc.register(new CORSFilter());
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException, SQLException {
        DOMConfigurator.configure("myLog");

        org.json.simple.JSONArray sensorsArea1= getConfigSensors("area1.json") ;
        org.json.simple.JSONArray sensorsArea2= getConfigSensors("area2.json") ;

        deleteDbNoSQL(DB_NAME);
        createDbNoSQL(DB_NAME,sensorsArea1,sensorsArea2);

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80));
        System.out.println(socket.getLocalAddress());

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();

    }

    private static JSONArray getConfigSensors(String jsonFile) {
        JSONArray jsonSensors=null;

        if(jsonFile!=null ){
            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(jsonFile))
            {
                //Read JSON file
                Object obj = jsonParser.parse(reader);
                jsonSensors= (org.json.simple.JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonSensors;
    }

    private static void deleteDbNoSQL(String name_db) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase(name_db);
        if(database!=null){
            database.drop();
        }
        mongoClient.close();
    }

    private static void createDbNoSQL(String name_db, JSONArray sensorsArea1, JSONArray sensorsArea2) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase(name_db);

        String[] areas=new String[]{"1","2"};
        Integer[] spots=new Integer[]{30,48};
        initUnisaParkingDb(database,areas,spots,sensorsArea1,sensorsArea2);

        mongoClient.close();
    }

    private static void initUnisaParkingDb(MongoDatabase database, String[] areas, Integer[] spots, JSONArray sensorsArea1, JSONArray sensorsArea2) {
        if(database!=null){
            if(areas.length==spots.length){ // gli array hanno stesso num elementi (  2 areas, 2 spots associate alle 2 aree ).
                int n = areas.length;
                MongoCollection<Document> spotsCollection = database.getCollection("spot");
                if(spotsCollection==null){
                    database.createCollection("spot");
                }
                for(int i=0; i<n;++i){
                    for(int j=1; j<=spots[i];++j){
                        Document spot=new Document();
                        spot.put("area",areas[i]);
                        spot.put("id",j);
                        spot.put("state","free");
                        spot.put("lastChange", Calendar.getInstance().getTime());
                        if(i==0) {
                            spot.put("x", ((JSONObject) sensorsArea1.get(j - 1)).get("x"));
                            spot.put("y", ((JSONObject) sensorsArea1.get(j - 1)).get("y"));
                        }
                        else if(i==1){
                            spot.put("x", ((JSONObject) sensorsArea2.get(j - 1)).get("x"));
                            spot.put("y", ((JSONObject) sensorsArea2.get(j - 1)).get("y"));
                        }
                        spotsCollection.insertOne(spot);
                    }
                }
            }
        }
    }

}

