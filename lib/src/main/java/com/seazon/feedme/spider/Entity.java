package com.seazon.feedme.spider;

import com.google.gson.Gson;

public class Entity {

    public String toJsonString() {
        return new Gson().toJson(this);
    }

}
