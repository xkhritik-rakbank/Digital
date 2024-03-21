/***************************************************************************
 *       Product         : OmniFlow
 *       Application     : WISearchRequest Webservice
 *       File            : WISearchRequest.java
 *       Author          : Sahiba Matta
 *       Created on      : December 3, 2015
 *       Purpose         : This class contains getter setter methods for web service request.
 *
 * Change History  :
 *********************************************************************************************************************
 * Date		Problem No           description                                             changed by
 *---------	---------	-----------------------                                     -------------
 ***********************************************************************************************************/



package com.newgen.ws.request;

import com.newgen.ws.EE_EAI_HEADER;

public class WICreateRequest
{
	private String ProcessName;
	private String SubProcess;
	private String InitiateAlso;
	private Documents Documents;
	private Attributes Attributes;
	private EE_EAI_HEADER EE_EAI_HEADER;
	private String InputMessage;
	
	public String getProcessName() {
		return ProcessName;
	}
	public void setProcessName(String processName) {
		ProcessName = processName;
	}
	public String getSubProcess() {
		return SubProcess;
	}
	public void setSubProcess(String subProcess) {
		SubProcess = subProcess;
	}
 
	public String getInitiateAlso() {
		return InitiateAlso;
	}
	public void setInitiateAlso(String initiateAlso) {
		InitiateAlso = initiateAlso;
	}
	public Documents getDocuments() {
		return Documents;
	}
	public void setDocuments(Documents documents) {
		Documents = documents;
	}
	public Attributes getAttributes() {
		return Attributes;
	}
	public void setAttributes(Attributes attributes) {
		Attributes = attributes;
	}
	public EE_EAI_HEADER getEE_EAI_HEADER() {
		return EE_EAI_HEADER;
	}
	public void setEE_EAI_HEADER(EE_EAI_HEADER eE_EAI_HEADER) {
		EE_EAI_HEADER = eE_EAI_HEADER;
	}
	
	
	public String getInputMessage() {
		return InputMessage;
	}
	public void setInputMessage(String inputMessage) {
		InputMessage = inputMessage;
	}
	
	
}
