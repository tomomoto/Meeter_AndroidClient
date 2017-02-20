package com.example.tom.meeter.Infrastructure;

/**
 * Created by Tom on 10.02.2017.
 */

public class Event {
    Integer EID;
    String Name;
    String Description;
    Integer OID;
    String Created;
    String Starting;
    String Ending;

    public Event(Integer EID, String name, String description, Integer OID, String created, String starting, String ending) {
        this.EID = EID;
        Name = name;
        Description = description;
        this.OID = OID;
        Created = created;
        Starting = starting;
        Ending = ending;
    }
}
