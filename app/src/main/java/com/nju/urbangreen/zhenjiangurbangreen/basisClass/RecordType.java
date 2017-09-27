package com.nju.urbangreen.zhenjiangurbangreen.basisClass;

import com.nju.urbangreen.zhenjiangurbangreen.events.OneEvent;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.Inspect;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.Maintain;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by tommy on 2017/9/23.
 */

//列表类型
public class RecordType {

    private String type;
    private String title;
    private Class nextActivity;
    private Map<String,Integer> spinners;
    private List<Maintain> maintainList;
    private List<Inspect> inspectList;
    private List<OneEvent> eventList;

    public RecordType(String type, String title, Class nextActivity, Map<String, Integer> spinners) {
        this.type = type;
        this.title = title;
        this.nextActivity = nextActivity;
        this.spinners = spinners;
    }

    public RecordType(String title, Class nextActivity, Map<String, Integer> spinners) {
        this.title = title;
        this.nextActivity = nextActivity;
        this.spinners = spinners;
    }


    public String getTitle() {
        return title;
    }

    public Class getNextActivity() {
        return nextActivity;
    }

    public Map<String, Integer> getSpinners() {
        return spinners;
    }


}
