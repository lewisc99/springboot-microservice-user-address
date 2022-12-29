package com.lewis.address.models.events;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;

public class UserUpdatedEvent {

    private  String name;
    @JsonIgnore
    private OffsetDateTime date = OffsetDateTime.now();

    public UserUpdatedEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
}