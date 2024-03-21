package com.newgen.ws;

public class EE_EAI_HEADER {
	
	private String MsgFormat;
	private String MsgVersion;
	private String RequestorChannelId;
	private String RequestorUserId;
	private String RequestorLanguage;
	private String RequestorSecurityInfo;
	private String MessageId;
	private String Extra1;
	private String Extra2;
	private String ReturnCode;
	private String ReturnDesc;
	
	public String getMsgVersion() {
		return MsgVersion;
	}
	public void setMsgVersion(String msgVersion) {
		MsgVersion = msgVersion;
	}
	public String getMsgFormat() {
		return MsgFormat;
	}
	public void setMsgFormat(String msgFormat) {
		MsgFormat = msgFormat;
	}
	public String getRequestorUserId() {
		return RequestorUserId;
	}
	public void setRequestorUserId(String requestorUserId) {
		RequestorUserId = requestorUserId;
	}
	public String getRequestorChannelId() {
		return RequestorChannelId;
	}
	public void setRequestorChannelId(String requestorChannelId) {
		RequestorChannelId = requestorChannelId;
	}
	public String getRequestorLanguage() {
		return RequestorLanguage;
	}
	public void setRequestorLanguage(String requestorLanguage) {
		RequestorLanguage = requestorLanguage;
	}
	public String getRequestorSecurityInfo() {
		return RequestorSecurityInfo;
	}
	public void setRequestorSecurityInfo(String requestorSecurityInfo) {
		RequestorSecurityInfo = requestorSecurityInfo;
	}
	public String getExtra1() {
		return Extra1;
	}
	public void setExtra1(String extra1) {
		Extra1 = extra1;
	}
	public String getMessageId() {
		return MessageId;
	}
	public void setMessageId(String messageId) {
		MessageId = messageId;
	}
	public String getExtra2() {
		return Extra2;
	}
	public void setExtra2(String extra2) {
		Extra2 = extra2;
	}
	public String getReturnDesc() {
		return ReturnDesc;
	}
	public void setReturnDesc(String returnDesc) {
		ReturnDesc = returnDesc;
	}
	public String getReturnCode() {
		return ReturnCode;
	}
	public void setReturnCode(String returnCode) {
		ReturnCode = returnCode;
	}

}
