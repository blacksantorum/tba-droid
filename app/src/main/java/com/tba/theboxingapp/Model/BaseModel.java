package com.tba.theboxingapp.Model;

import java.util.Date;

/**
 * Created by christibbs on 9/16/14.
 */
public class BaseModel {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int id;
    public Date createdAt;
    public Date updatedAt;


}
