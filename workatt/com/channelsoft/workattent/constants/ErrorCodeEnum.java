package com.channelsoft.workattent.constants;

import org.apache.commons.lang.StringUtils;


public enum ErrorCodeEnum {
	SIGN_KEY_PASS("0000","签名正确"),
	SIGN_KEY_OTHERDEVICE("-0003","已在其他设备登录"),
	SIGN_KEY_OUTTIME("-0002","签名过期"),
	SIGN_KEY_ERROR("-0001","签名错误"),
	ELSE("","未知");
	public String value;
	public String desc;
	private ErrorCodeEnum(String value, String desc){
		this.value=value;
		this.desc=desc;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public static ErrorCodeEnum getEnum(String value){
		if (value!=null)
			for(ErrorCodeEnum e:values()){
				if(StringUtils.equalsIgnoreCase(e.value, value)){
					return e;
				}
			}
			return ELSE;
	}
}
