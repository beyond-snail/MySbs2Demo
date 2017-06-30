package com.mysbs2demo.common;

////////////////////////////////////////////////////////////////////
//                          _ooOoo_                               //
//                         o8888888o                              //
//                         88" . "88                              //
//                         (| ^_^ |)                              //
//                         O\  =  /O                              //
//                      ____/`---'\____                           //
//                    .'  \\|     |//  `.                         //
//                   /  \\|||  :  |||//  \                        //
//                  /  _||||| -:- |||||-  \                       //
//                  |   | \\\  -  /// |   |                       //
//                  | \_|  ''\---/''  |   |                       //
//                  \  .-\__  `-`  ___/-. /                       //
//                ___`. .'  /--.--\  `. . ___                     //
//              ."" '<  `.___\_<|>_/___.'  >'"".                  //
//            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
//            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
//      ========`-.____`-.___\_____/___.-`____.-'========         //
//                           `=---='                              //
//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
//              佛祖保佑       永无BUG     永不修改                  //
//                                                                //
//          佛曰:                                                  //
//                  写字楼里写字间，写字间里程序员；                   //
//                  程序人员写程序，又拿程序换酒钱。                   //
//                  酒醒只在网上坐，酒醉还来网下眠；                   //
//                  酒醉酒醒日复日，网上网下年复年。                   //
//                  但愿老死电脑间，不愿鞠躬老板前；                   //
//                  奔驰宝马贵者趣，公交自行程序员。                   //
//                  别人笑我忒疯癫，我笑自己命太贱；                   //
//                  不见满街漂亮妹，哪个归得程序员？                   //
////////////////////////////////////////////////////////////////////

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mysbs2demo.utils.EncryptMD5Util;
import com.mysbs2demo.utils.LogUtils;
import com.mysbs2demo.utils.StringUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**********************************************************
 * *
 * Created by wucongpeng on 2017/2/28.        *
 **********************************************************/


public class ToolComfuc {



    /**
     * 排序
     * @param a
     * @return
     */
    public static ArrayList<String> getSortAsc(ArrayList<String> a) {
        String temp = "";
        int i;
        for (i = 0; i < a.size(); i++) {
            for (int j = a.size() - 1; j > i; j--) {
                if (a.get(j).compareTo(a.get(j - 1)) < 0) {
                    temp = a.get(j);
                    a.set(j, a.get(j - 1));
                    a.set(j - 1, temp);
                }
            }
        }
        return a;
    }


    /**
     * 获取到JSON字串
     * @param cmd
     * @param paramsMap
     * @return
     */
    public static String getJsonStr(String cmd, Map<String, Object> paramsMap, String sign, String key) {
        JSONObject jsonParams = null;

        ArrayList<String> keys = new ArrayList<String>();
        for (Iterator<String> it = paramsMap.keySet().iterator(); it.hasNext(); ) {
            keys.add(it.next());
        }
        String tmp = "";
        keys = getSortAsc(keys);
        for (int i = 0; i < keys.size(); i++) {
            Object obj = paramsMap.get(keys.get(i));
            if (obj != null) {
                tmp = tmp + obj.toString();
            } else {
                LogUtils.e("getJsonStr", "getSortAsc obj is null");
            }
        }
        LogUtils.e(tmp);
        String verify = EncryptMD5Util.MD5(tmp + key);
        paramsMap.put(sign, verify);


        Map<String, Object> final_params = new HashMap<String, Object>();
        final_params.put("cmd", cmd);
        final_params.put("params", paramsMap);

        jsonParams = new JSONObject(final_params);

        return jsonParams.toString();
    }


    /**
     * 获取订单号
     * @param PayType
     * @param serialNum
     * @return
     */
    public static String getNewClientSn(int PayType, String serialNum) {
        String device = "2"; // 1:手机 2:Pos机
        String payType = "" + PayType;
        String timestamp = StringUtils.getFormatCurTime();
        String randomNum = StringUtils.createRandomNumStr(3);
        String serial_no = serialNum;

        return device + payType + timestamp + randomNum + serial_no;
    }



    public static void startAction(Activity context, Class<?> cls, boolean flag) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
        if (flag){
            context.finish();
        }
    }

    public static void startAction(Activity context, Class<?> cls, Bundle bundle, boolean flag) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (flag){
            context.finish();
        }
    }

    public static void startResultAction(Activity context, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (bundle != null){
            intent.putExtras(bundle);
        }

        context.startActivityForResult(intent, requestCode);
    }
}
