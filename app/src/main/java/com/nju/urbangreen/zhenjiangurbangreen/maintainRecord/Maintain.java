package com.nju.urbangreen.zhenjiangurbangreen.maintainRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lxs on 2016/11/28.
 */
public class Maintain implements Serializable{
    public String MR_ID;
    public String MR_Code;
    public String MR_CompanyID;
    public String MR_MaintainType;
    public String MR_MaintainStaff;
    public String MR_MaintainDate;
    public String MR_MaintainContent;
    public String MR_AssessStatus;
    public String MR_AssessorPID;
    public String MR_AssessTime;
    public String MR_AssessDescription;
    public String MR_LoggerPID;
    public String MR_LogTime;
    public String MR_LastEditorPID;
    public String MR_LastEditTime;
    public String MR_SubmitStatus;
    public String MR_SubmitorPID;
    public String MR_SubmitTime;
    public String UGO_IDs;

    private int state;//0 :未提交；1 :已提交
}
