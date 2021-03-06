package com.katekozlova.cargo.data.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
//@Data
@Table(name = "cities")
public class City implements Serializable {

    private static final long serialVersionUID = 8369985237829900115L;

    @Id
    @SequenceGenerator(name = "city_generator", sequenceName = "city_sequence", initialValue = 20)
    @GeneratedValue(generator = "city_generator")
    private long id;

    @Column(name = "city_name")
    private String name;

    @OneToMany(mappedBy = "currentCity", cascade = CascadeType.REMOVE)
    private List<Driver> drivers;

    @OneToMany(mappedBy = "currentCity", cascade = CascadeType.REMOVE)
    private List<Truck> trucks;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<Waypoint> waypoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

