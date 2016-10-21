package net.aqualogybs.crud.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Created by vrubinat on 20/02/2016.
 */
@Entity
@Table(name="TEST_COCHE")
public class Car implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "type")
    private String type;
    @Column(name ="color")
    private String color;

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getColor(){
        return this.color;
    }
    public void setColor(String color){
        this.color = color;
    }



}
