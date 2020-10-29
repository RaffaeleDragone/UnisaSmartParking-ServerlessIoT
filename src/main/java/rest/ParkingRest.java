package rest;


import beans.Spot;
import com.mongodb.MongoClient;
import com.mongodb.client.model.geojson.GeoJsonObjectType;
import connection.ConnectionPool;
import org.json.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("testrest")
public class ParkingRest {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces("application/json")
    @Path("/getArea/area={area}")
    public String getSensorsFromArea(@PathParam("area") String area) {
        List<Spot> spots = ConnectionPool.getSpotsFromArea(area);
        for(Spot s : spots){
            System.out.println(s);
        }
        JSONArray jsArr=new JSONArray(spots);
        return jsArr.toString();
    }

    @PUT
    @Produces("application/json")
    @Path("/updateState/area={area}&id={id}&state={state}")
    public String updateState(@PathParam("area") String area, @PathParam("id") String id,@PathParam("state") String state) {

        Spot spot=new Spot();
        spot.setId(Long.valueOf(id));
        spot.setArea(area);
        spot.setState(state);

        ConnectionPool.changeSpotState(spot);
        return "{response:'ok'}";
    }


}
