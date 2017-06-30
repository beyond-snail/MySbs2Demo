package com.mysbs2demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mysbs2demo.common.ToolComfuc;
import com.mysbs2demo.config.Constants;
import com.mysbs2demo.core.SbsAction;
import com.mysbs2demo.modle.ActionCallbackListener;
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
import com.mysbs2demo.utils.LogUtils;
import com.mysbs2demo.utils.SPUtils;
import com.mysbs2demo.utils.StringUtils;
import com.mysbs2demo.utils.ToastUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import static com.mysbs2demo.common.ToolComfuc.getNewClientSn;

public class MainActivity extends BaseActivity {

    private Activity mContext;

    public static final int REQUEST_CAPTURE_WX = 0;
    public static final int REQUEST_CAPTURE_ALY = 1;
    public static final int REQUEST_CAPTURE_QB = 2;

    private String serialNum = "WP00000000001";

    private String orderNo;

    private String qbOldTime;

    private String outOrderNum; //用于异常或者末笔查询
    private String mchnt_order_no; //用于扫码交易成功等待付款，订单查询，或者退款使用

    private String smRefund; //扫码退款订单号。


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        getPermission();


        /**
         * POS 签到
         */
        findViewById(R.id.id_pos_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pos_number = serialNum;
                final String operator_num = "0001";
                final String operator_password = "0000";

                new SbsAction().Login(mContext, pos_number, operator_num, operator_password, new ActionCallbackListener<LoginApiResponse>() {
                    @Override
                    public void onSuccess(LoginApiResponse data) {
                        SPUtils.put(mContext, "operator_num", operator_num);
                        SPUtils.put(mContext, "operator_password", operator_password);
                        SPUtils.put(mContext, "merchantNo", data.getMerchantNo());
                        SPUtils.put(mContext, "terminalNo", data.getTerminalNo());
                        SPUtils.put(mContext, "fyMerchantNo", data.getFyMerchantNo());
                        SPUtils.put(mContext, "activeCode", data.getActiveCode());
                        SPUtils.put(mContext, "sid", data.getSid());
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 会员优惠
         */
        findViewById(R.id.id_pos_benefits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ToolComfuc.startAction(mContext, ActivityMember.class, false);

            }
        });


        /**
         * 会员交易金额计算
         */
        findViewById(R.id.id_pos_tranMoney).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderNo = getNewClientSn(1, StringUtils.getSerial());

                MemberTransAmountRequest request = new MemberTransAmountRequest();
                request.setSid((int) SPUtils.get(mContext, "sid", 0));
                request.setMemberCardNo((String) SPUtils.get(mContext, "memberCardNo", ""));
                //有密码就base64加密 无密码就传null
                request.setPassword(null);//(Base64Utils.getBase64("123456"));
                request.setTradeMoney(20);//当前支付的金额
//                request.setPoint(1);    //当前使用的积分
//                request.setCouponSn(""); //当前使用的优惠券
                request.setMemberName((String) SPUtils.get(mContext, "memberName", ""));
                request.setClientOrderNo(orderNo);


                new SbsAction().memberTransAmount(mContext, request, new ActionCallbackListener<MemberTransAmountResponse>() {
                    @Override
                    public void onSuccess(MemberTransAmountResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 当支付完成后调用该接口，支付方式 刷卡  微信 支付宝 钱包
         * 交易流水上传
         */
        findViewById(R.id.id_pos_transUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransUploadRequest request = new TransUploadRequest();
                request.setSid((int) SPUtils.get(mContext, "sid", 0));
                request.setCardNo((String) SPUtils.get(mContext, "memberCardNo", ""));
//                request.setPassword("");//(Base64Utils.getBase64("123456"));
                request.setCash(0);
                request.setBankAmount(1);
                request.setCouponCoverAmount(0);
                request.setCouponSns("");
                request.setClientOrderNo(orderNo); //会员是上个接口生成的，非会员为当前生成
                request.setActivateCode("123456");
                request.setMerchantNo((String) SPUtils.get(mContext, "merchantNo", ""));
                request.setT(StringUtils.getdate2TimeStamp(StringUtils.getCurTime()));
                request.setTransNo("1234567");
                request.setAuthCode("123456");
                request.setSerialNum("WP00000000001");
                request.setPayType(1);
                request.setPointAmount(0);
                request.setOperator_num("001");

                new SbsAction().transUpload(mContext, request, new ActionCallbackListener<TransUploadResponse>() {
                    @Override
                    public void onSuccess(TransUploadResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 获取打印信息
         */
        findViewById(R.id.id_pos_getPrinter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sid = (int) SPUtils.get(mContext, "sid", 0);
                String clientOrderNo = orderNo;

                new SbsAction().getPrinterData(mContext, sid, clientOrderNo, new ActionCallbackListener<TransUploadResponse>() {
                    @Override
                    public void onSuccess(TransUploadResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });

            }
        });

        /**
         * 钱包支付
         */
        findViewById(R.id.id_pos_qbPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ToolComfuc.startResultAction(MainActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_QB);
            }
        });


        /**
         * 钱包订单查询
         */
        findViewById(R.id.id_pos_qbQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int sid  = (int) SPUtils.get(mContext, "sid", 0);
                String old_orderNo = orderNo;
                String time = qbOldTime;
                String terminalNo = serialNum;
                String operator_num = (String) SPUtils.get(mContext, "operator_num", "");

                new SbsAction().qbQuery(mContext, sid, old_orderNo, time, terminalNo, operator_num, new ActionCallbackListener<ZfQbResponse>() {
                    @Override
                    public void onSuccess(ZfQbResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 退款
         */
        findViewById(R.id.id_pos_transCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TransCancel request = new TransCancel();
                request.setSid((int) SPUtils.get(mContext, "sid", 0));
                request.setOld_trade_order_num(orderNo);
                request.setNew_trade_order_num(getNewClientSn(1, StringUtils.getSerial()));
                request.setAuthCode(smRefund); //模拟支付宝退款成功 上送
                request.setT(StringUtils.getdate2TimeStamp(StringUtils.getCurTime()));
                request.setAction(2);
                request.setPayType(12);
                request.setOperator_num((String) SPUtils.get(mContext, "operator_num", ""));

                new SbsAction().transCancelRefund(mContext, request, new ActionCallbackListener<String>() {
                    @Override
                    public void onSuccess(String data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 扫码支付 WECHAT ALIPAY
         */
        findViewById(R.id.id_pos_smPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolComfuc.startResultAction(MainActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);
            }
        });

        /**
         * 扫码订单查询
         */
        findViewById(R.id.id_pos_smQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FyQueryRequest request = new FyQueryRequest();

                request.setMchnt_cd((String) SPUtils.get(mContext, "fyMerchantNo", ""));
                request.setTerm_id(StringUtils.getTerminalNo(serialNum));
                request.setMchnt_order_no(mchnt_order_no);
                request.setOrder_type("ALIPAY");


                new SbsAction().smQuery(mContext, request, new ActionCallbackListener<FyQueryResponse>() {
                    @Override
                    public void onSuccess(FyQueryResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 扫码末笔订单查询
         */
        findViewById(R.id.id_pos_smLastQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LastQueryRequest request = new LastQueryRequest();

                request.setMchnt_cd((String) SPUtils.get(mContext, "fyMerchantNo", ""));
                request.setTerm_id(StringUtils.getTerminalNo(serialNum));
                request.setAmount("1");
                request.setOutOrderNum(outOrderNum);
                request.setOrder_type("ALIPAY");


                new SbsAction().smLastQuery(mContext, request, new ActionCallbackListener<FyQueryResponse>() {
                    @Override
                    public void onSuccess(FyQueryResponse data) {

                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });


        /**
         * 扫码退款
         */
        findViewById(R.id.id_pos_smRefund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //测试使用支付宝 退款


                FyRefundRequest request = new FyRefundRequest();

                request.setMchnt_cd((String) SPUtils.get(mContext, "fyMerchantNo", ""));
                request.setTerm_id(StringUtils.getTerminalNo(serialNum));
                request.setMchnt_order_no(mchnt_order_no);
                request.setOrder_type("ALIPAY");
                request.setAmount(1);
                request.setRefund_amt(1);


                new SbsAction().smRefund(mContext, request, new ActionCallbackListener<FyRefundResponse>() {
                    @Override
                    public void onSuccess(FyRefundResponse data) {
                        smRefund = data.getRefund_order_no();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAPTURE_WX:
                String result_wx = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_wx);
                smPay("WECHAT", result_wx, Constants.PAY_WAY_WX);
                break;
            case REQUEST_CAPTURE_ALY:
                String result_aly = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_aly);

                smPay("ALIPAY", result_aly, Constants.PAY_WAY_ALY);
                break;
            case REQUEST_CAPTURE_QB:
                String result_qb = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_qb);
//                ZfQbPay1(result_qb);
                qBPay(result_qb);
                break;
            default:
                break;
        }
    }

    private void qBPay(String result_qb) {

        ZfQbRequest request = new ZfQbRequest();
        request.setTranCode("9448");
        request.setQrCode(result_qb);
        request.setMerchantId((int) SPUtils.get(mContext, "sid", 0));
        request.setTerminalNo(serialNum);
        request.setOrgOrderNum(orderNo);

        qbOldTime = StringUtils.getFormatCurTime();

        request.setOrgTranDateTime(qbOldTime);
        request.setSysTraceNum(StringUtils.getFormatCurTime() + StringUtils.createRandomNumStr(5));
        request.setTranAmt(1);
        request.setOrderCurrency("156");
        request.setOperator_num((String) SPUtils.get(mContext, "operator_num", ""));

        new SbsAction().qbPay(mContext, request, new ActionCallbackListener<ZfQbResponse>() {
            @Override
            public void onSuccess(ZfQbResponse data) {

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
            }
        });
    }


    private void smPay(String pay_type, String qrCode, int payType){
        FyMicropayRequest request = new FyMicropayRequest();
        request.setAmount(1);
        request.setAuth_code(qrCode);
        request.setGoods_des("消费");
        request.setGoods_detail("消费物品");
        request.setMchnt_cd((String) SPUtils.get(mContext, "fyMerchantNo", ""));
        request.setTerm_id(StringUtils.getTerminalNo(serialNum));
        request.setOutOrderNum(getNewClientSn(payType, StringUtils.getSerial()));
        request.setType(pay_type);


        new SbsAction().smPay(mContext, request, new ActionCallbackListener<FyMicropayResponse>() {
            @Override
            public void onSuccess(FyMicropayResponse data) {
                mchnt_order_no = data.getMchnt_order_no();
                outOrderNum = data.getOutOrderNum();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
            }
        });
    }




    private void requestPermission() {
        Log.d("requestPermission", "*****" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("requestPermission", "需要获取权限" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
// 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.d("requestPermission", "打开dialog获取权限");
                new AlertDialog.Builder(MainActivity.this).setMessage("申请相机权限").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //申请相机权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                }).show();
            } else {
                //申请相机权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        } else {
//            cameraTv.setText("相机权限已申请");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("requestPermission", "onRequestPermissionsResult反应了吗" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                cameraTv.setText("相机权限已申请");
                Log.d("requestPermission", "onRequestPermissionsResult" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
            } else {
                Log.d("requestPermission", "没有权限，下面在判断下就弹吐司了" + ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));
                //用户勾选了不再询问
                //提示用户手动打开权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "相机权限已被禁止,请在设置中打开", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
    }
    public static final int CAMERA_REQUEST_CODE = 0x01;
}
