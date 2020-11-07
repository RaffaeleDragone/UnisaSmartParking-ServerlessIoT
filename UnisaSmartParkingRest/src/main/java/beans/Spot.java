package beans;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rest.RunSmartParkingRest;

import java.util.Calendar;
import java.util.Date;

public class Spot {

    String area, state, x,y;
    Date lastChange;
    Long id;

    public Spot(String area, String state, String fillColor, String x, String y, Date lastChange, Long id) {
        this.area = area;
        this.state = state;
        this.x = x;
        this.y = y;
        this.lastChange = lastChange;
        this.id = id;
    }
    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Spot(){

    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "area='" + area + '\'' +
                ", state='" + state + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", lastChange=" + lastChange +
                ", id=" + id +
                '}';
    }
}
