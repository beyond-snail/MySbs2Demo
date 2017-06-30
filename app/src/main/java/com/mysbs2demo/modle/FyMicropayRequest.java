package com.mysbs2demo.modle;

public class FyMicropayRequest {

	private String mchnt_cd; // 商户号， 富友分配给二级商户的商户号
	private String term_id; // 终端号，富友分配的终端设备号
	private String outOrderNum; //外部订单号
	private String type; // 订单类型：ALYPAY, WECHAT
	private String goods_des; // 商品描述，商品或支付单简要描述
	private String goods_detail; // 商品详情， 商品名称明细
	private int amount; // 总金额, 订单总金额，单位为分
	private String auth_code; // 扫码支付授权码，设备读取用户的条码或者二维码 信息

	public String getMchnt_cd() {
		return mchnt_cd;
	}

	public void setMchnt_cd(String mchnt_cd) {
		this.mchnt_cd = mchnt_cd;
	}

	public String getTerm_id() {
		return term_id;
	}

	public void setTerm_id(String term_id) {
		this.term_id = term_id;
	}

	public String getOutOrderNum() {
		return outOrderNum;
	}

	public void setOutOrderNum(String outOrderNum) {
		this.outOrderNum = outOrderNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGoods_des() {
		return goods_des;
	}

	public void setGoods_des(String goods_des) {
		this.goods_des = goods_des;
	}

	public String getGoods_detail() {
		return goods_detail;
	}

	public void setGoods_detail(String goods_detail) {
		this.goods_detail = goods_detail;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
}
