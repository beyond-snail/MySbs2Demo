package com.mysbs2demo.modle;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

public class LoginApiResponse extends DataSupport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5060863143246134147L;
	private int id;
	private int sid; // 商户id
	private String merchantNo; // 商户号
	private String terminalNo; // 商户终端号
	private String terminalName; // 终端名称
	private String other; // 授权码
	private String fyMerchantNo; //扫码商户号
	private String fyMerchantName; //扫码商户名称
	private String activeCode;
	private int scanPayType; //扫码支付通道
	private String operatList; // 权限列表
	private List<OperatorBean> operator_list; //操作员信息


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getFyMerchantNo() {
		return fyMerchantNo;
	}

	public void setFyMerchantNo(String fyMerchantNo) {
		this.fyMerchantNo = fyMerchantNo;
	}

	public String getFyMerchantName() {
		return fyMerchantName;
	}

	public void setFyMerchantName(String fyMerchantName) {
		this.fyMerchantName = fyMerchantName;
	}

	public String getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	public int getScanPayType() {
		return scanPayType;
	}

	public void setScanPayType(int scanPayType) {
		this.scanPayType = scanPayType;
	}

	public String getOperatList() {
		return operatList;
	}

	public void setOperatList(String operatList) {
		this.operatList = operatList;
	}

	public List<OperatorBean> getOperator_list() {
		return operator_list;
	}

	public void setOperator_list(List<OperatorBean> operator_list) {
		this.operator_list = operator_list;
	}
}
