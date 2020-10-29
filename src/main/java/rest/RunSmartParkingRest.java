package rest;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connection.ConnectionPool;
import org.bson.Document;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;
import java.io.IOException;
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
    /*
    public static void main_old(String[] args) throws IOException, SQLException {
        String dbName="parking.db";
        createNewDatabase(dbName);
        ConnectionPool.getConnection(dbName);


        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
        boolean res = deleteDatabase(dbName);
        System.out.println(res);
    }
    */
    public static void main(String[] args) throws IOException, SQLException {
        deleteDbNoSQL(DB_NAME);
        createDbNoSQL(DB_NAME);
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();

    }

    private static void deleteDbNoSQL(String name_db) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase(name_db);
        if(database!=null){
            database.drop();
        }
        mongoClient.close();
    }

    private static void createDbNoSQL(String name_db) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase(name_db);

        String[] areas=new String[]{"1","2"};
        Integer[] spots=new Integer[]{20,25};
        initUnisaParkingDb(database,areas,spots);

        mongoClient.close();
    }

    private static void initUnisaParkingDb(MongoDatabase database, String[] areas, Integer[] spots) {
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
                        spotsCollection.insertOne(spot);
                    }
                }
            }
        }
    }

}

