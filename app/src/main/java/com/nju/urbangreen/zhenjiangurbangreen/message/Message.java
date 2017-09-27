package com.nju.urbangreen.zhenjiangurbangreen.message;
import java.io.Serializable;

/**
 * Created by Kyle on 2017/8/29.
 */

public class Message implements Serializable {

    private String QM_ID;
    private String QM_CreateTime;
    private String PFullName_From;
    private String QuickMessage;
    private boolean QM_IsShown;

    public Message(String QM_ID,String QM_CreateTime,String PFullName_From,String QuickMessage, boolean QM_IsShown){
        this.QM_ID=QM_ID;
        this.QM_CreateTime=QM_CreateTime;
        this.PFullName_From=PFullName_From;
        this.QuickMessage=QuickMessage;
        this.QM_IsShown=QM_IsShown;

    }
    public String getQM_ID() {
        return QM_ID;
    }

    public String getQM_CreateTime() {
        return QM_CreateTime;
    }

    public String getPFullName_From() {
        return PFullName_From;
    }

    public String getQuickMessage() {
        return QuickMessage;
    }

    public boolean getQM_IsShown() {
        return QM_IsShown;
    }

    public void setQM_ID(String QM_ID) {
        this.QM_ID = QM_ID;
    }

    public void setQM_CreateTime(String QM_CreateTime) {
        this.QM_CreateTime = QM_CreateTime;
    }

    public void setQuickMessage(String quickMessage) {
        QuickMessage = quickMessage;
    }

    public void setQM_IsShown(boolean QM_IsShown) {
        this.QM_IsShown = QM_IsShown;
    }

    public void setPFullName_From(String PFullName_From) {
        this.PFullName_From = PFullName_From;
    }
}
