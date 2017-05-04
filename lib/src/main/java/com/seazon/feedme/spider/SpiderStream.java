package com.seazon.feedme.spider;

import java.util.ArrayList;
import java.util.List;

public class SpiderStream extends Entity {

    public String continuation;
    public List<SpiderItem> items = new ArrayList<>();

}
