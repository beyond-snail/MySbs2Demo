package com.mysbs2demo.core;

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

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysbs2demo.common.ToolComfuc;
import com.mysbs2demo.config.Config;
import com.mysbs2demo.config.Constants;
import com.mysbs2demo.modle.ActionCallbackListener;
import com.mysbs2demo.modle.ApiResponse;
import com.mysbs2demo.modle.CouponsResponse;
import com.mysbs2demo.modle.FyMicropayRequest;
import com.mysbs2demo.modle.FyMicropayResponse;
import com.mysbs2demo.modle.FyQueryRequest;
import com.mysbs2demo.modle.FyQueryResponse;
import com.mysbs2demo.modle.FyRefundRequest;
import com.mysbs2demo.modle.FyRefundResponse;
import com.mysbs2demo.modle.LastQueryRequest;
import com.mysbs2demo.modle.LoginApiResponse;
import com.mysbs2demo.modle.MemberTransAmountRequest;
import com.mysbs2demo.modle.MemberTransAmountResponse;
import com.mysbs2demo.modle.TransCancel;
import com.mysbs2demo.modle.TransUploadRequest;
import com.mysbs2demo.modle.TransUploadResponse;
import com.mysbs2demo.modle.ZfQbRequest;
import com.mysbs2demo.modle.ZfQbResponse;
import com.mysbs2demo.myapplication.MyApplication;
import com.mysbs2demo.myokhttp.MyOkHttp;
import com.mysbs2demo.myokhttp.response.GsonResponseHandler;
import com.mysbs2demo.myokhttp.response.JsonResponseHandler;
import com.mysbs2demo.myokhttp.response.RawResponseHandler;
import com.mysbs2demo.utils.EncryptMD5Util;
import com.mysbs2demo.utils.LogUtils;
import com.mysbs2demo.utils.MapUtil;
import com.mysbs2demo.utils.SPUtils;
import com.mysbs2demo.utils.StringUtils;
import com.mysbs2demo.utils.ToastUtils;
import com.mysbs2demo.utils.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**********************************************************
 * *
 * Created by wucongpeng on 2016/11/6.        *
 **********************************************************/


public class SbsAction {

    private static final String TAG = "SbsAction";

    /**
     * 商博士-签到
     *
     * @param posNum   设备序列号
     * @param listener 回调
     */
    public void Login(final Context context, String posNum, String username, String password, final ActionCallbackListener<LoginApiResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取配置信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("pos_number", posNum);
        paramsMap.put("operator_num", username);
        paramsMap.put("operator_password", password);
        String data = ToolComfuc.getJsonStr("posSignIn", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<LoginApiResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<LoginApiResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }
        });
    }


    /**
     * 商博士-获取会员信息
     *
     * @param sid
     * @param mobile
     * @param tradeMoney
     * @param listener
     */
    public void getMemberInfo(final Context context, int sid, String mobile, int tradeMoney, String operator_num, String serialNum, String icCardNo,
                              final ActionCallbackListener<CouponsResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在获取会员信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("mobile", mobile);
        paramsMap.put("tradeMoney", tradeMoney);
        paramsMap.put("operator_num", operator_num);
        paramsMap.put("serialNum", serialNum);
        paramsMap.put("icCardNo", icCardNo);
        String data = ToolComfuc.getJsonStr("benefits", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<CouponsResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<CouponsResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }


    /**
     * 商博士-会员交易金额计算
     *
     * @param request
     * @param listener
     */
    public void memberTransAmount(final Context context, MemberTransAmountRequest request,
                                  final ActionCallbackListener<MemberTransAmountResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在会员交易计算...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("memberCardNo", request.getMemberCardNo());
        paramsMap.put("password", request.getPassword());
        paramsMap.put("tradeMoney", request.getTradeMoney());
        paramsMap.put("point", request.getPoint());
        paramsMap.put("couponSn", request.getCouponSn());
        paramsMap.put("memberName", request.getMemberName());
        paramsMap.put("clientOrderNo", request.getClientOrderNo());
        String data = ToolComfuc.getJsonStr("tranMoney", paramsMap, "verify", Config.md5_key);

        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<MemberTransAmountResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<MemberTransAmountResponse> response) {

                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }

    /**
     * 商博士-交易流水上送
     *
     * @param request
     * @param listener
     */
    public void transUpload(final Context context, TransUploadRequest request,
                            final ActionCallbackListener<TransUploadResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("交易流水上送...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("cardNo", request.getCardNo());
        paramsMap.put("password", request.getPassword());
        paramsMap.put("cash", request.getCash());
        paramsMap.put("bankAmount", request.getBankAmount());
        paramsMap.put("couponCoverAmount", request.getCouponCoverAmount());
        paramsMap.put("pointCoverAmount", request.getPointCoverAmount());
        paramsMap.put("couponSns", request.getCouponSns());
        paramsMap.put("clientOrderNo", request.getClientOrderNo());
        paramsMap.put("activateCode", request.getActivateCode());
        paramsMap.put("merchantNo", request.getMerchantNo());
        paramsMap.put("t", request.getT());
        paramsMap.put("transNo", request.getTransNo());
        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("serialNum", request.getSerialNum());
        paramsMap.put("payType", request.getPayType());
        paramsMap.put("pointAmount", request.getPointAmount());
        paramsMap.put("operator_num", request.getOperator_num());
        String data = ToolComfuc.getJsonStr("tranUploadPos", paramsMap, "verify", Config.md5_key);

        LogUtils.e(TAG, Config.SBS_URL);


        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {

                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        listener.onSuccess(response.getResult());
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }


    /**
     * 商博士-获取打印信息
     *
     * @param sid
     * @param clientOrderNo
     * @param listener
     */
    public void getPrinterData(final Context context, int sid, String clientOrderNo,
                               final ActionCallbackListener<TransUploadResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("获取打印信息...");
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", sid);
        paramsMap.put("clientOrderNo", clientOrderNo);
        String data = ToolComfuc.getJsonStr("getPrintData", paramsMap, "verify", Config.md5_key);

        LogUtils.e(TAG, Config.SBS_URL);


        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new GsonResponseHandler<ApiResponse<TransUploadResponse>>() {
            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

            @Override
            public void onSuccess(int statusCode, ApiResponse<TransUploadResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        if (response.getResult() != null) {
                            listener.onSuccess(response.getResult());
                        } else {
                            listener.onFailure("", "结果数据返回为空");
                        }
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }
        });
    }

    /**
     * 退款
     *
     * @param context
     * @param request
     * @param listener
     */
    public void transCancelRefund(final Context context, TransCancel request,
                                  final ActionCallbackListener<String> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在上送退款信息...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("sid", request.getSid());
        paramsMap.put("old_trade_order_num", request.getOld_trade_order_num());
        paramsMap.put("new_trade_order_num", request.getNew_trade_order_num());
        paramsMap.put("action", request.getAction());
        paramsMap.put("payType", request.getPayType() + "");
        paramsMap.put("authCode", request.getAuthCode());
        paramsMap.put("t", request.getT());
        paramsMap.put("operator_num", request.getOperator_num());
        String data = ToolComfuc.getJsonStr("trade_cancel", paramsMap, "verify", Config.md5_key);

        LogUtils.e(TAG, Config.SBS_URL);


        MyOkHttp.get().postJson(context, Config.SBS_URL, data, new JsonResponseHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                dialog.dismiss();
                if (response == null) {
                    listener.onFailure("", "链接服务器异常");
                } else {
                    LogUtils.e(TAG, response.toString());
                    try {
                        String code = response.getString("code");
                        String msg = response.getString("msg");

                        if (code.equals("A00006")) {
                            listener.onSuccess(msg);
                        } else {
                            listener.onFailure("", msg);
                        }

                    } catch (JSONException e) {
                        listener.onFailure("", "数据解析失败");
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }


        });

    }


    /**
     * 商博士-钱包支付
     *
     * @param request
     * @param listener
     */
    public void qbPay(final Context context, ZfQbRequest request, final ActionCallbackListener<ZfQbResponse> listener) {
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在钱包支付...");

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("TranCode", request.getTranCode());
        paramsMap.put("qrCode", request.getQrCode());
        paramsMap.put("MerchantId", request.getMerchantId());
        paramsMap.put("TerminalNo", request.getTerminalNo());
        paramsMap.put("OrgOrderNum", request.getOrgOrderNum());
        paramsMap.put("OrgTranDateTime", request.getOrgTranDateTime());
        paramsMap.put("SysTraceNum", request.getSysTraceNum());
        paramsMap.put("TranAmt", request.getTranAmt());
        paramsMap.put("OrderCurrency", request.getOrderCurrency());
        paramsMap.put("operator_num", request.getOperator_num());

        String data = ToolComfuc.getJsonStr("qbPay", paramsMap, "verify", Config.md5_key);

        LogUtils.e("URL", Config.SBS_URL_QB);


        MyOkHttp.get().postJson(context, Config.SBS_URL_QB, data, new GsonResponseHandler<ApiResponse<ZfQbResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<ZfQbResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        if (response.getResult() != null) {
                            listener.onSuccess(response.getResult());
                        } else {
                            listener.onFailure("", "结果数据返回为空");
                        }
                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

        });
    }


    /**
     * 钱包订单查询
     *
     * @param context
     * @param sid
     * @param orderNo
     * @param time
     * @param terminalNo
     * @param operator_num
     * @param listener
     */
    public void qbQuery(Context context, int sid, String orderNo, String time, String terminalNo, String operator_num, final ActionCallbackListener<ZfQbResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在查询...");
        dialog.setCancelable(false);

        Map<String, Object> paramsMap = new HashMap<String, Object>();

        paramsMap.put("MerchantId", sid);
        paramsMap.put("TerminalNo", terminalNo);
        paramsMap.put("OrgOrderNum", orderNo);
        paramsMap.put("OrgTranDateTime", time);
        paramsMap.put("operator_num", operator_num);

        String data = ToolComfuc.getJsonStr("qbQuery", paramsMap, "verify", Config.md5_key);
        LogUtils.e("URL", Config.SBS_URL_QB);


        MyOkHttp.get().postJson(context, Config.SBS_URL_QB, data, new GsonResponseHandler<ApiResponse<ZfQbResponse>>() {
            @Override
            public void onSuccess(int statusCode, ApiResponse<ZfQbResponse> response) {
                dialog.dismiss();
                if (response != null) {
                    if (response.getCode().equals("A00006")) {
                        if (StringUtils.isEquals(response.getResult().getTransState(), "0000")) {
                            listener.onSuccess(response.getResult());
                        } else {
                            listener.onFailure(response.getCode(), response.getMsg());
                        }

                    } else {
                        listener.onFailure(response.getCode(), response.getMsg());
                    }
                } else {
                    listener.onFailure("", "链接服务器异常");
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                listener.onFailure("" + statusCode, error_msg);
            }

        });
    }


    /**
     * 扫码支付
     *
     * @param context
     * @param request
     * @param listener
     */
    public void smPay(final Context context, final FyMicropayRequest request, final ActionCallbackListener<FyMicropayResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在查询...");
        dialog.setCancelable(false);

//        request.setMchnt_cd(request.getMchnt_cd());//MyApplication.getInstance().getLoginData().getFyMerchantNo());
//        request.setTerm_id(request.getTerm_id());//StringUtils.getTerminalNo(StringUtils.getSerial()));
//        request.setOutOrderNum(request.getOutOrderNum());//outOrderNo);
//        request.setType(request.getType());
//        request.setGoods_des(request.getGoods_des());//("消费");
//        request.setGoods_detail(request.getGoods_detail());//("消费物品");
//        request.setAmount(request.getAmount());//amount);
//        request.setAuth_code(request.getAuth_code());//result);


        // 除去sign组成map 按字典排序
        Map<String, String> tempMap = MapUtil
                .order(MapUtil.objectToMap(request, "sign"));
        MapUtil.removeNullValue(tempMap);
        String src = MapUtil.mapJoin(tempMap, false, false);
        String signRes = EncryptMD5Util.MD5(src);
        tempMap.put("sign", signRes);
//        LogUtils.e("tempMap", tempMap.toString());
        String url = "http://" + SPUtils.get(context, Constants.FY_IP, Constants.DEFAULT_FY_IP) + "/ZFTiming/api/fyTrade/pay";
        LogUtils.e("url", url);


        MyOkHttp.get().post(context, url, tempMap, new RawResponseHandler() {

            @Override
            public void onFailure(int statusCode, String error_msg) {
//                LogUtils.e(TAG, statusCode + "#" + error_msg);
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, String response) {
                dialog.dismiss();
//                LogUtils.e(TAG, statusCode + "#" + response);
                if (statusCode == 200 && !StringUtils.isEmpty(response)) {
                    Type type = new TypeToken<FyMicropayResponse>() {
                    }.getType();
                    Gson gson = new Gson();
                    FyMicropayResponse result = gson.fromJson(response, type);
                    if (result.getResult_code().equals("000000")) {
                        listener.onSuccess(result);
                    } else if (result.getResult_code().equals("030010")) { //用户使用支付密码
                        listener.onSuccess(result);
                    } else {

                        ToastUtils.CustomShow(context, result.getResult_code() + "#" + result.getResult_msg());
                    }
                } else {

                    ToastUtils.CustomShow(context, statusCode + "#" + response);

                }
            }
        });


    }


    /**
     * 订单查询（富友） 最好是在支付返回成功后，进行轮询查询，因为可能会有 等待用户支付的情况，所以该接口需要轮询
     *
     * @param type       （支付方式：ALIPAY , WECHAT）

     */
    public void smQuery(final Context context, FyQueryRequest request, final ActionCallbackListener<FyQueryResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在查询...");
        dialog.setCancelable(false);

        // 除去sign组成map 按字典排序
        Map<String, String> tempMap = MapUtil
                .order(MapUtil.objectToMap(request, "sign"));
        MapUtil.removeNullValue(tempMap);
        String src = MapUtil.mapJoin(tempMap, false, false);
        String signRes = EncryptMD5Util.MD5(src);
        tempMap.put("sign", signRes);
        LogUtils.e("tempMap", tempMap.toString());

        String url = "http://" + SPUtils.get(context, Constants.FY_IP, Constants.DEFAULT_FY_IP) + "/ZFTiming/api/fyTrade/query";
        LogUtils.e("url", url);
        MyOkHttp.get().post(context, url, tempMap, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                dialog.dismiss();
                LogUtils.e(TAG, statusCode + "#" + response);
                if (statusCode == 200 && !StringUtils.isEmpty(response)) {
                    Type type = new TypeToken<FyQueryResponse>() {
                    }.getType();
                    Gson gson = new Gson();
                    FyQueryResponse result = gson.fromJson(response, type);
                    if (result.getResult_code().equals("000000")) {
                        if (result.getTrans_stat().equals("SUCCESS")) {

                        } else { //用户支付中。。
                            ToastUtils.CustomShow(context, result.getResult_code() + "#" + result.getResult_msg());
                        }
                    } else {
                        if (result.getResult_code().equals("999999") && result.getTrans_stat().equals("PAYERROR")) {
                            ToastUtils.CustomShow(context, "用户未支付");
                        } else {
                            ToastUtils.CustomShow(context, result.getResult_code() + "#" + result.getResult_msg());
                        }
                    }
                } else {
                    ToastUtils.CustomShow(context, statusCode + "#" + response);
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                ToastUtils.CustomShow(context, statusCode + "#" + error_msg);

            }
        });
    }


    /**
     * 订单查询（富友）
     *
     * @param type （支付方式：ALIPAY , WECHAT）
     */
    public void smLastQuery(final Context context, LastQueryRequest request, final ActionCallbackListener<FyQueryResponse> listener) {

        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在末笔查询...");
        dialog.setCancelable(false);

//        LastQueryRequest lastQuery = new LastQueryRequest();
//
//        lastQuery.setAmount(request.getAmount());
//        lastQuery.setMchnt_cd(request.getMchnt_cd());
//        lastQuery.setOrder_type(request.getOrder_type());
//        lastQuery.setTerm_id(request.getTerm_id());
//        lastQuery.setOutOrderNum(request.getOutOrderNum());


        // 除去sign组成map 按字典排序
        Map<String, String> tempMap = MapUtil
                .order(MapUtil.objectToMap(request, "sign"));
        MapUtil.removeNullValue(tempMap);
        String src = MapUtil.mapJoin(tempMap, false, false);
        LogUtils.e("before", src);
        String signRes = EncryptMD5Util.MD5(src);
        LogUtils.e("after", signRes);
        tempMap.put("sign", signRes);
        LogUtils.e("tempMap", tempMap.toString());


        String url = "http://" + SPUtils.get(context, Constants.FY_IP, Constants.DEFAULT_FY_IP) + "/ZFTiming/api/fyTrade/lastQuery";
        LogUtils.e("url", url);
        MyOkHttp.get().post(context, url, tempMap, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                LogUtils.e(TAG, statusCode + "#" + response);
                dialog.dismiss();
                if (statusCode == 200 && !StringUtils.isEmpty(response)) {
                    Type type = new TypeToken<FyQueryResponse>() {
                    }.getType();
                    Gson gson = new Gson();
                    FyQueryResponse result = gson.fromJson(response, type);
                    if (result.getResult_code().equals("000000")) {
                        if (result.getTrans_stat().equals("SUCCESS")) {


                        } else {
                            ToastUtils.CustomShow(context, "交易失败");
                        }
                    } else {
                        ToastUtils.CustomShow(context, result.getResult_code() + "#" + result.getResult_msg());
                    }
                } else {
                    ToastUtils.CustomShow(context, statusCode + "#" + response);
                }

            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                ToastUtils.CustomShow(context, statusCode + "#" + error_msg);
                dialog.dismiss();
            }
        });


    }


    /**
     * 退款
     *
     * @param context
     * @param request
     * @param listener
     */
    public void smRefund(final Context context, FyRefundRequest request, final ActionCallbackListener<FyRefundResponse> listener) {

        request.setMchnt_cd(request.getMchnt_cd());
        request.setTerm_id(request.getTerm_id());
        request.setMchnt_order_no(request.getMchnt_order_no());
        request.setOrder_type(request.getOrder_type());
        request.setAmount(request.getAmount());
        request.setRefund_amt(request.getRefund_amt());


        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show("正在退款...");
        dialog.setCancelable(false);


        // 除去sign组成map 按字典排序
        Map<String, String> tempMap = MapUtil
                .order(MapUtil.objectToMap(request, "sign", "reserved_sub_appid", "reserved_limit_pay"));
        MapUtil.removeNullValue(tempMap);
        String src = MapUtil.mapJoin(tempMap, false, false);
        String signRes = EncryptMD5Util.MD5(src);
        tempMap.put("sign", signRes);
        LogUtils.e("tempMap", tempMap.toString());

        String url = "http://" + SPUtils.get(context, Constants.FY_IP, Constants.DEFAULT_FY_IP) + "/ZFTiming/api/fyTrade/refund";
        LogUtils.e("url", url);

        MyOkHttp.get().post(context, url, tempMap, new RawResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                dialog.dismiss();
                LogUtils.e(TAG, statusCode + "#" + response);
                if (statusCode == 200 && !StringUtils.isEmpty(response)) {
                    Type type = new TypeToken<FyRefundResponse>() {
                    }.getType();
                    Gson gson = new Gson();
                    FyRefundResponse result = gson.fromJson(response, type);
                    if (result.getResult_code().equals("000000")) {
                        ToastUtils.CustomShow(context, "退款成功");
                        listener.onSuccess(result);

                    } else {
                        ToastUtils.CustomShow(context, result.getResult_code() + "#" + result.getResult_msg());
                    }
                } else {
                    ToastUtils.CustomShow(context, statusCode + "#" + response);
                }
            }

            @Override
            public void onFailure(int statusCode, String error_msg) {
                dialog.dismiss();
                ToastUtils.CustomShow(context, statusCode + "#" + error_msg);
            }
        });


    }


}
