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

    @Override public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof BaseModel)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        BaseModel lhs = (BaseModel) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return lhs.id == this.id;
    }

    public int id;
    public Date createdAt;
    public Date updatedAt;


}
