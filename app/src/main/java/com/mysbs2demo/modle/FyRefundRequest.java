package com.mysbs2demo.modle;

public class FyRefundRequest {
	private String mchnt_cd; // 商户号， 富友分配给二级商户的商户号
	private String term_id; // 终端号，富友分配的终端设备号
	private String mchnt_order_no; // 商户订单号，商户系统内部的订单号
	private String order_type; // 订单类型：ALYPAY, WECHAT
	private int amount;// 总金额
	private int Refund_amt;// 退款金额

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

	public String getMchnt_order_no() {
		return mchnt_order_no;
	}

	public void setMchnt_order_no(String mchnt_order_no) {
		this.mchnt_order_no = mchnt_order_no;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRefund_amt() {
		return Refund_amt;
	}

	public void setRefund_amt(int refund_amt) {
		Refund_amt = refund_amt;
	}
}
