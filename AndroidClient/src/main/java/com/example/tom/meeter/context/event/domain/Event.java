package com.example.tom.meeter.context.event.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.tom.meeter.context.user.domain.User;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity
public class Event {

  @PrimaryKey
  @NonNull
  private String id;
  private String name;
  private String description;
  private double latitude;
  private double longitude;

  @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "creatorId",
      onDelete = CASCADE)
  private String creatorId;
  private String created;
  private String starting;
  private String ending;

  public Event(@NonNull String id, String name, String description, double latitude,
      double longitude, String creatorId, String created, String starting,
      String ending) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.latitude = latitude;
    this.longitude = longitude;
    this.creatorId = creatorId;
    this.created = created;
    this.starting = starting;
    this.ending = ending;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getStarting() {
    return starting;
  }

  public void setStarting(String starting) {
    this.starting = starting;
  }

  public String getEnding() {
    return ending;
  }

  public void setEnding(String ending) {
    this.ending = ending;
  }
}
