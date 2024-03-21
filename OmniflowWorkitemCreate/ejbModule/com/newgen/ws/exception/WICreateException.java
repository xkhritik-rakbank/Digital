package com.newgen.ws.exception;

public class WICreateException extends Exception{
	
	private String errorCode;
	private String errorDesc;
	
	public WICreateException (String errorCode,String errorDesc)
	{
		this.errorCode=errorCode;
		this.errorDesc=errorDesc;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	 
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public String toString()
	{
		return "WICreateException- ErrorCode:"+getErrorCode()+", ErrorDescription:"+getErrorDesc();
	}
	

}
