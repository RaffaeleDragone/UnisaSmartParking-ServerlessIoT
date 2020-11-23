package rest;


import beans.Spot;
import com.mongodb.MongoClient;
import com.mongodb.client.model.geojson.GeoJsonObjectType;
import connection.ConnectionPool;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("api")
public class ParkingRest {
    Logger logger=Logger.getLogger(ParkingRest.class);
    @GET
    @Produces("application/json")
    @Path("/getArea/area={area}")
    public String getSensorsFromArea(@PathParam("area") String area) {
        List<Spot> spots = ConnectionPool.getSpotsFromArea(area);
        JSONArray jsArr=new JSONArray(spots);
        logger.info("Size array : "+jsArr.length());
        return jsArr.toString();
    }

    @PUT
    @Produces("application/json")
    @Path("/updateState")
    public String updateState(String json_obj) {
        logger.info("Enter Update Function");
        JSONObject json_spot = new JSONObject(json_obj);
        if(json_spot!=null && json_spot.get("id_sensor")!=null){
            Spot sp=new Spot();
            sp.setId(Long.valueOf(json_spot.get("id_sensor")+""));
            sp.setState(json_spot.get("state")+"");
            sp.setArea(json_spot.get("area")+"");
            ConnectionPool.changeSpotState(sp);
            logger.info("Updated");
            return "{response:'ok'}";
        }
        return "{response:'ko'}";
    }
}
