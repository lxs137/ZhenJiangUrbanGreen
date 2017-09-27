package com.nju.urbangreen.zhenjiangurbangreen.util;

import android.util.Log;

import com.nju.urbangreen.zhenjiangurbangreen.attachments.AttachmentRecord;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObjectSug;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.settings.SystemFileItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by lxs on 17-8-10.
 */

public class CacheUtil {
    private static final String UGO_KEY = "HasUGO", UGO_SUG_KEY = "HasUGOSug", UGO_RELATED_KEY="HasRelatedUgo";

    private static ACache m_cache = null;

    private static ACache instance() {
        if(m_cache == null) {
            m_cache = ACache.get(MyApplication.getContext());
        }
        return m_cache;
    }

    public static boolean hasUGOs() {
        return SPUtils.getBool(UGO_KEY, false);
    }

    public static void putUGOs(List<GreenObject> objs) {
        instance().put(UGO_KEY, objs.toArray());
        SPUtils.put(UGO_KEY, true);
    }

    public static List<GreenObject> getUGOs() {
        if(!hasUGOs())
            return null;
        Log.d("test2",instance().<GreenObject>getAsObjectList(UGO_KEY)+"");
        return instance().<GreenObject>getAsObjectList(UGO_KEY);
    }

    public static void removeUGOS() {
        instance().remove(UGO_KEY);
        SPUtils.put(UGO_KEY, false);
    }

    public static boolean hasUGOSug() {
        return SPUtils.getBool(UGO_SUG_KEY, false);
    }

    public static void putUGOSug(List<GreenObjectSug> objs) {
        String Codes[] = new String[objs.size()];
        String Addresses[] = new String[objs.size()];
        int i = 0;
        for(GreenObjectSug obj : objs) {
            Codes[i] = obj.UGO_Ucode;
            Addresses[i] = obj.UGO_Address;
            i++;
        }
        instance().put(UGO_SUG_KEY + "UGO_Code", Codes);
        instance().put(UGO_SUG_KEY + "UGO_Address", Addresses);
        SPUtils.put(UGO_SUG_KEY, true);
    }

    public static String[] getUGOSug(String column) {
        if(!hasUGOSug()) {
            return null;
        }
        switch (column) {
            case "UGO_Code":
                return (String[]) instance().<String>getAsObjectList(UGO_SUG_KEY + "UGO_Code").toArray();
            case "UGO_Address":
                return (String[]) instance().<String>getAsObjectList(UGO_SUG_KEY + "UGO_Address").toArray();
            default:
                return null;
        }
    }

    public static void removeUGOSug() {
        instance().remove(UGO_SUG_KEY + "UGO_Code");
        instance().remove(UGO_SUG_KEY + "UGO_Address");
        SPUtils.put(UGO_SUG_KEY, false);
    }

    public static boolean hasRelatedUgos(){
        return instance().getAsObjectList(UGO_RELATED_KEY)!=null;
    }

    public static void putRelatedUgos(List<GreenObject> objs){
        instance().put(UGO_RELATED_KEY,objs.toArray());
    }

    public static List<GreenObject> getRelatedUgos(){
        if(!hasUGOs()){
            return null;
        }
        return instance().getAsObjectList(UGO_RELATED_KEY);
    }

    public static void removeRelatedUgos(){
        instance().remove(UGO_RELATED_KEY);
    }

    public static String getFileLocalPath(String fileID) {
        return instance().getAsString(fileID);
    }

    public static void putFileLocalPath(String fileID, String localPath) {
        instance().put(fileID, localPath);
    }

    public static void removeFileLocalPath(String fileID) {
        instance().remove(fileID);
    }

    /**
     * 删除本地已上传的的附件记录
     */
    public static void removeAttachRecord(String fileID) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        AttachmentRecord res = realm.where(AttachmentRecord.class)
                .equalTo("fileID", fileID)
                .findFirst();
        if(res != null)
            res.deleteFromRealm();
        realm.commitTransaction();
    }

    /**
     * 只保存未上传的附件记录到本地
     * @param allRecord 所有附件记录
     */
    public static void saveAttachmentRecord(List<AttachmentRecord> allRecord, String parentID) {
        if(allRecord.size() == 0)
            return;
        final List<AttachmentRecord> unUploadRecord = new ArrayList<>();
        for(AttachmentRecord record : allRecord) {
            if(record.atLocal && !record.hasUpload)
                unUploadRecord.add(record);
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<AttachmentRecord> res = realm.where(AttachmentRecord.class)
                .equalTo("parentID", parentID).findAll();
        res.deleteAllFromRealm();
        realm.copyToRealm(unUploadRecord);
        realm.commitTransaction();
    }

    /**
     *  根据附件记录所属的记录ID来返回该记录下的未上传附件
     */
    public static List<AttachmentRecord> getNotUploadAttachmentRecord(String parentID) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<AttachmentRecord> res = realm.where(AttachmentRecord.class)
                .equalTo("parentID", parentID).findAll();
        return realm.copyFromRealm(res);
    }


    /**
     * 获取本地缓存的系统文件信息
     */
    public static List<SystemFileItem> getSystemFiles() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SystemFileItem> res = realm.where(SystemFileItem.class).findAll();
        return realm.copyFromRealm(res);
    }

    public static void saveSystemFiles(List<SystemFileItem> files) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(files);
        realm.commitTransaction();
    }

}
