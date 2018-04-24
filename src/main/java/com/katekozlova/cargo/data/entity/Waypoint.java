package com.katekozlova.cargo.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "waypoints")
public class Waypoint {

    @Id
    @SequenceGenerator(name = "waypoint_generator", sequenceName = "waypoint_sequence", initialValue = 21)
    @GeneratedValue(generator = "waypoint_generator")
    private long id;

    @ManyToOne
    private City city;

    @ManyToOne
    private Cargo cargo;

    @ManyToOne
    private Order order;

    @Column(name = "waypoint_type")
    @Enumerated(EnumType.ORDINAL)
    private WaypointType waypointType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public WaypointType getWaypointType() {
        return waypointType;
    }

    public void setWaypointType(WaypointType waypointType) {
        this.waypointType = waypointType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "id=" + id +
//                ", city=" + city +
//                ", cargo=" + cargo +
//                ", order=" + order +
//                ", waypointType=" + waypointType +
                '}';
    }
}
