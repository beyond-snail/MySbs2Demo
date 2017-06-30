package com.mysbs2demo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mysbs2demo.core.SbsAction;
import com.mysbs2demo.modle.ActionCallbackListener;
import com.mysbs2demo.modle.CouponsResponse;
import com.mysbs2demo.utils.SPUtils;
import com.mysbs2demo.utils.StringUtils;
import com.mysbs2demo.utils.ToastUtils;

public class ActivityMember extends BaseActivity {


    private EditText etPhone;
    private EditText etIcCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_member);


        etPhone = (EditText) findViewById(R.id.id_phoneNo);
        etIcCard = (EditText) findViewById(R.id.id_icCard);
        etPhone.setText("14782108169");

        findViewById(R.id.id_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (StringUtils.isEmpty(etPhone.getText().toString().trim())) {
                    ToastUtils.CustomShow(mContext, "请输入手机号");
                    return;
                }



                int sid = 1;
                String mobile = etPhone.getText().toString().trim();
                int tradeMoney = 1;
                String operator_num = (String) SPUtils.get(mContext, "operator_num", "");
                String serialNum = "WP00000000001";
                String icCardNo = etIcCard.getText().toString().trim();


                new SbsAction().getMemberInfo(mContext, sid, mobile, tradeMoney, operator_num, serialNum, icCardNo, new ActionCallbackListener<CouponsResponse>() {
                    @Override
                    public void onSuccess(CouponsResponse data) {
                        SPUtils.put(mContext, "memberCardNo", data.getMemberCardNo());
//                        SPUtils.put(mContext, "memberName", data.getMemberName());
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        ToastUtils.CustomShow(mContext, message);
                    }
                });
            }
        });
    }
}
