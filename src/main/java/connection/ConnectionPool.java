package connection;

import beans.Spot;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import rest.RunSmartParkingRest;

import java.util.*;

public class ConnectionPool {

    private static MongoClient mongoClient = new MongoClient();
    private static MongoDatabase database = mongoClient.getDatabase(RunSmartParkingRest.DB_NAME);

    public static void changeSpotState(Spot spot){

        MongoCollection<Document> spotsCollection = database.getCollection("spot");
        if(spotsCollection==null){
            database.createCollection("spot");
            spotsCollection=database.getCollection("spot");
        }

        HashMap<String,Object> filterMap=new HashMap<>();
        filterMap.put("area",spot.getArea());
        filterMap.put("id",spot.getId());
        Bson filter = new Document(filterMap);
        FindIterable<Document> docIterator = spotsCollection.find().filter(filter);

        Document dSpot= docIterator.iterator().next();

        if(dSpot!=null){
            Document dNew=new Document();
            String state = dSpot.getString("state");
            dNew.put("state",spot.getState());
            dNew.put("lastChange",Calendar.getInstance().getTime());

            Bson bUpdate = new Document(dNew);
            Bson updateDocument = new Document("$set", bUpdate);
            spotsCollection.updateOne(dSpot,updateDocument);
        }

    }

    public static List<Spot> getSpotsFromArea(String area){
        List<Spot> list=null;
        MongoCollection<Document> spotsCollection = database.getCollection("spot");
        if(spotsCollection==null){
            return null;
        }
        HashMap<String,Object> filterMap=new HashMap<>();
        filterMap.put("area",area);
        Bson filter = new Document(filterMap);
        FindIterable<Document> docIterator = spotsCollection.find().filter(filter);
        if(docIterator.iterator().hasNext()){
            list=new Vector<>();
            MongoCursor<Document> mongoCursor = docIterator.iterator();
            while(mongoCursor.hasNext()){
                Document dSpot = mongoCursor.next();
                Spot spot=new Spot();
                spot.setArea( dSpot.getString("area"));
                spot.setId(Long.valueOf(dSpot.getInteger("id")));
                spot.setState(dSpot.getString("state"));
                spot.setLastChange(dSpot.getDate("lastChange"));
                list.add(spot);
            }
        }
        return list;
    }
}
