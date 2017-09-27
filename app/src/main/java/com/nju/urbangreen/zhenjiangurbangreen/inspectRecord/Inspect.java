package com.nju.urbangreen.zhenjiangurbangreen.inspectRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lxs on 2016/11/20.
 */
public class Inspect implements Serializable{

    private String IR_ID;
    private String IR_Code;
    private String IR_Type;
    private String IR_InspectDate;
    private String IR_CompanyID;
    private String IR_Inspector;
    private String IR_Score;
    private String IR_Content;
    private String IR_InspectOpinion;
    private String IR_LoggerPID;
    private String IR_LogTime;
    private String IR_LastEditorPID;

    public String getUGO_IDs() {
        return UGO_IDs;
    }

    public void setUGO_IDs(String UGO_IDs) {
        this.UGO_IDs = UGO_IDs;
    }

    private String UGO_IDs;

    public Inspect() {
    }

    public Inspect(String IR_ID, String IR_Code, String IR_Type, String IR_InspectDate, String IR_CompanyID, String IR_Inspector, String IR_Score, String IR_Content, String IR_InspectOpinion, String IR_LoggerPID, String IR_LogTime, String IR_LastEditorPID) {
        this.IR_ID = IR_ID;
        this.IR_Code = IR_Code;
        this.IR_Type = IR_Type;
        this.IR_InspectDate = IR_InspectDate;
        this.IR_CompanyID = IR_CompanyID;
        this.IR_Inspector = IR_Inspector;
        this.IR_Score = IR_Score;
        this.IR_Content = IR_Content;
        this.IR_InspectOpinion = IR_InspectOpinion;
        this.IR_LoggerPID = IR_LoggerPID;
        this.IR_LogTime = IR_LogTime;
        this.IR_LastEditorPID = IR_LastEditorPID;
    }

    public Inspect(String IR_ID, String IR_Code) {
        this.IR_ID = IR_ID;
        this.IR_Code = IR_Code;
    }

    public String getIR_ID() {
        return IR_ID;
    }

    public void setIR_ID(String IR_ID) {
        this.IR_ID = IR_ID;
    }

    public String getIR_Code() {
        return IR_Code;
    }

    public void setIR_Code(String IR_Code) {
        this.IR_Code = IR_Code;
    }

    public String getIR_Type() {
        return IR_Type;
    }

    public void setIR_Type(String IR_Type) {
        this.IR_Type = IR_Type;
    }

    public String getIR_InspectDate() {
        return IR_InspectDate;
    }

    public void setIR_InspectDate(String IR_InspectDate) {
        this.IR_InspectDate = IR_InspectDate;
    }

    public String getIR_CompanyID() {
        return IR_CompanyID;
    }

    public void setIR_CompanyID(String IR_CompanyID) {
        this.IR_CompanyID = IR_CompanyID;
    }

    public String getIR_Inspector() {
        return IR_Inspector;
    }

    public void setIR_Inspector(String IR_Inspector) {
        this.IR_Inspector = IR_Inspector;
    }

    public String getIR_Score() {
        return IR_Score;
    }

    public void setIR_Score(String IR_Score) {
        this.IR_Score = IR_Score;
    }

    public String getIR_Content() {
        return IR_Content;
    }

    public void setIR_Content(String IR_Content) {
        this.IR_Content = IR_Content;
    }

    public String getIR_InspectOpinion() {
        return IR_InspectOpinion;
    }

    public void setIR_InspectOpinion(String IR_InspectOpinion) {
        this.IR_InspectOpinion = IR_InspectOpinion;
    }

    public String getIR_LoggerPID() {
        return IR_LoggerPID;
    }

    public void setIR_LoggerPID(String IR_LoggerPID) {
        this.IR_LoggerPID = IR_LoggerPID;
    }

    public String getIR_LogTime() {
        return IR_LogTime;
    }

    public void setIR_LogTime(String IR_LogTime) {
        this.IR_LogTime = IR_LogTime;
    }

    public String getIR_LastEditorPID() {
        return IR_LastEditorPID;
    }

    public void setIR_LastEditorPID(String IR_LastEditorPID) {
        this.IR_LastEditorPID = IR_LastEditorPID;
    }
}
