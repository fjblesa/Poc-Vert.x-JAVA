package net.aqualogybs.crud.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by vrubinat on 20/02/2016.
 */
@Entity
@Table(name="TEST_USER")
public class User implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    @Column(name="Nomnbre")
    private  String name;

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getId(){return this.id;}
    public void setId(int id){this.id = id;}
}
