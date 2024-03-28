package com.newgen.iforms.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Date;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.sun.javafx.collections.MappingChange.Map;

public class Digital_PL_FormLoad extends Digital_PL_Common {

	String processInstanceID = "";
	String cabinetName = "";
	String sessionId = "";
	String serverIp = "";
	String serverPort = "";
	String oldSalaryDetails = "";

	public String formLoadEvent(IFormReference iform, String controlName, String event, String data) {
		this.processInstanceID = getWorkitemName(iform);
		this.cabinetName = getCabinetName(iform);
		this.sessionId = getSessionId(iform);
		this.serverIp = iform.getServerIp();
		this.serverPort = iform.getServerPort();
		String strReturn = "";
		
		String Workstep = iform.getActivityName();
		control("WINAME,CIF,ProspectID,CustomerName,Nationality,Age,PassportNumber,EmiratesID,MobileNo,ProductType,LoanType,DeclaredIncome,IPA_Amount,RequestedLoanAmount,RequestedLoanTenor,LoanAmount,CustomerType,EFMS_Status,FTS_Ref_No,SalaryIBAN,BankName,SalaryTransferToRAK,SourcingUnit,CustomerDeclaredMonthlyIncome,FIRCO_Status,WICreatedDate",iform,"disable","true");
		
		iform.setValue("Decision","");
		iform.setValue("Dec_Remarks","");
		
		control("EmploymentDetails,IncomeandExpenseDetails,AECBDetails,Liabilities,RunPolicyChecks,Decisions,FIRCODetails",iform,"visible","false");
		
		//For loading Decision
		List Dec=new ArrayList();
		if("STL_Check".equalsIgnoreCase(Workstep)){
			Dec = iform.getDataFromDB("select Decision from NG_DPL_Decision_Master where WorkstepName='"+Workstep+"'");
		}
		else{
			Dec = iform.getDataFromDB("select Decision from NG_DPL_Decision_Master where WorkstepName='Common' or WorkstepName='"+Workstep+"'");
			
		}
		String value = "";
		iform.clearCombo("Decision");
		for (int i = 0; i < Dec.size(); i++) {
			List<String> arr1 = (List)Dec.get(i);
			value = arr1.get(0);
			iform.addItemInCombo("Decision", value, value);
		}
		
		
		Digital_PL.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
				+ ", Workstep :" + Workstep);
		
		if(Workstep.equalsIgnoreCase("Initiation")){
			control("EmploymentDetails,IncomeandExpenseDetails,AECBDetails,Liabilities,RunPolicyChecks,Decisions",iform,"visible","true");
		}
		else if(Workstep.equalsIgnoreCase("Discard")){
			iform.setStyle("Decisions", "visible", "true");
		}
		
		else if("Attach_Document".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks,WI_Number,CIF,Prospect_ID,Customer_Name,Nationality,Age,Passport_No,Emirates_ID,Mobile_No,Product_Type,Loan_Type,Declared_Income,IPA_Amount,Requested_Amount,Requested_Tenor,Approved_Loan_Amount,Customer_Type,FIRCO_Flag,EFMS_Status,FTS_Ref_No,IBAN_Number,Bank_Name,Salary_Transfer_To_RAK,Sourcing_Unit,Application_Date",iform,"disable","true");
			
			//iform.setValue("Q_NG_DPL_EmploymentDetails_WI_NAME", getWorkitemName(iform));
		}
		else if("Error_Handling".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
		}
		else if("System_Integration".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
			
		}
		else if("FTS_Hold".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
			
		}
		else if("Collect1".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
			
		}
		else if("EFMS".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
			
		}
		
		else if("Exception".equalsIgnoreCase(Workstep) || "Refer_to_Compliance".equalsIgnoreCase(Workstep)){
			control("Regen_CAM",iform,"visible","true");
			Digital_PL.mLogger.debug("Formload . WorkstepName :- "+Workstep);
			
			
			if("Exception".equalsIgnoreCase(Workstep)){
				Digital_PL.mLogger.debug("Formload . WorkstepName :- "+Workstep);
				control("Decisions",iform,"visible","true");
				String fircoValue=(String) iform.getValue("FIRCO_Decision");
				String EFMSValue=(String) iform.getValue("EFMS_Status");
				if("Negative".equalsIgnoreCase(fircoValue) || "Negative".equalsIgnoreCase(EFMSValue)){
					iform.setValue("Decision","Reject");
					iform.setStyle("Decision", "disable", "true");
				}
				else if("Document Required".equalsIgnoreCase(fircoValue)){
					iform.setValue("Decision","Submit");
					iform.setStyle("Decision", "disable", "true");
				}
				//iform.clearCombo("Decision");
				/*List ExceptionDec = iform.getDataFromDB("select Decision from NG_DPL_Decision_Master where WorkstepName='init' or WorkstepName='Exception'");
				String value = "";
				iform.clearCombo("Decision");
				for (int i = 0; i < ExceptionDec.size(); i++) {
					List<String> arr1 = (List) ExceptionDec.get(i);
					value = arr1.get(0);
					iform.addItemInCombo("Decision", value, value);
				}*/
			}
			
			
			else if("Acc_Update_Hold".equalsIgnoreCase(Workstep)){
				control("Decisions",iform,"visible","true");
				control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
			}
			
			
			/*if("Refer_to_Compliance".equalsIgnoreCase(Workstep)){
				iform.removeItemFromCombo("Decision",3);
			}*/
					
						
			
			String isFIRCOHit= (String) iform.getValue("IsFIRCOHit");
			if("N".equalsIgnoreCase(isFIRCOHit)){
				control("FIRCO_Decision",iform,"disable","true");
			}
			else{
				control("FIRCO_Decision",iform,"enable","true");
			}
			
			String FIRCOFlag= (String) iform.getValue("IsFTSReq");
			iform.setValue("FIRCO_Status", FIRCOFlag);
			if("Y".equalsIgnoreCase(FIRCOFlag)){
				control("FIRCODetails,Decisions,AECBDetails,EmploymentDetails,IncomeandExpenseDetails",iform,"visible","true");
				String FircoDecision=(String) iform.getValue("FIRCO_Decision");
				if("Document Required".equalsIgnoreCase(FircoDecision)){
					control("Additional_Documents",iform,"visible","true");
				}
				else{
					control("Additional_Documents",iform,"visible","false");
				}
			}
			else{
				control("Decisions,AECBDetails,EmploymentDetails,IncomeandExpenseDetails",iform,"visible","true");
			}
			
			
			control("WINAME,CIF,FIRCO_Flag,IsFTSReq,LoanAmount,CustomerDeclaredMonthlyIncome,FIRCO_Status,IsFIRCOHit,IBAN,HIT_Reason,ProspectID,CustomerName,Nationality,Age,PassportNumber,EmiratesID,MobileNo,ProductType,LoanType,DeclaredIncome,IPA_Amount,RequestedLoanAmount,RequestedLoanTenor,ApprovedLoanAmt,CustomerType,FIRCO_Status,EFMS_Status,FTS_Ref_No,SalaryIBAN,BankName,SalaryTransferToRAK,SourcingUnit,ApplicationDate",iform,"disable","true");
			
			String documentName = (String) iform.getValue("Additional_Documents_Document_Name");
			Digital_PL.mLogger.debug("Formload . documentName :- "+documentName);
			if("Other Document".equalsIgnoreCase(documentName)){
				Digital_PL.mLogger.debug("Formload . Other Document  ");
				iform.setStyle("Additional_Documents_Remarks", "visible", "true");
				iform.setStyle("Additional_Documents_Remarks", "mandatory", "true");
				iform.setValue("Additional_Documents_Remarks", "");
			}
			else{
				Digital_PL.mLogger.debug("Formload . Not Other Document  ");
				iform.setStyle("Additional_Documents_Remarks", "visible", "false");
				iform.setValue("Additional_Documents_Remarks", "");
				iform.setStyle("Additional_Documents_Remarks", "mandatory", "false");
			}
			
		}
		else if("WI_Update_Hold".equalsIgnoreCase(Workstep)){
			
			control("FIRCODetails,Decisions",iform,"visible","true");
			control("WINAME,CIF,ProspectID,CustomerName,Nationality,Age,PassportNumber,EmiratesID,MobileNo,ProductType,LoanType,DeclaredIncome,IPA_Amount,RequestedLoanAmount,RequestedLoanTenor,ApprovedLoanAmt,CustomerType,IsFIRCOHit,EFMS_Status,FTS_Ref_No,SalaryIBAN,BankName,SalaryTransferToRAK,SourcingUnit,ApplicationDate,FIRCO_Status,HIT_Reason,FIRCO_Decision,Additional_Documents",iform,"disable","true");
			//iform.removeItemFromCombo("Decision",3);
		}
		else if("Notify_DEH".equalsIgnoreCase(Workstep)){
			control("WINAME,CIF,ProspectID,CustomerName,Nationality,Age,PassportNumber,EmiratesID,MobileNo,ProductType,LoanType,DeclaredIncome,IPA_Amount,RequestedLoanAmount,RequestedLoanTenor,LoanAmount,CustomerType,IsFIRCOHit,EFMS_Status,FTS_Ref_No,SalaryIBAN,BankName,SalaryTransferToRAK,SourcingUnit,CustomerDeclaredMonthlyIncome,ApplicationDate,FIRCO_Status,HIT_Reason,FIRCO_Decision,Additional_Documents,WICreatedDate",iform,"disable","true");
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
		}
		
		else if("STL_Hold".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
		}
		
		else if("STL_Check".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			//iform.clearCombo("Decision");
			/*List STLdec = iform.getDataFromDB("select decision from NG_DPL_Decision_Master where workstepname='STL_Check'");
			String value = "";
			iform.clearCombo("DocumentTypeReq");
			for (int i = 0; i < STLdec.size(); i++) {
				List<String> arr1 = (List) STLdec.get(i);
				value = arr1.get(0);
				iform.addItemInCombo("Decision", value, value);
			}*/
				
		}
		
		else if("Doc_Generation".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
		}
		
		else if("AWB_Generation".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
		}
		
		else if("Offer_Letter".equalsIgnoreCase(Workstep)){
			control("Decisions",iform,"visible","true");
			control("Decision,Dec_RejectReason,Dec_Remarks",iform,"disable","true");
		}

		
			//data display on AECB grid added by rubi
			try{
				if(iform.getDataFromGrid("AECB_Pipelines").size()>0)
				{
					iform.clearTable("AECB_Pipelines");
				}
					
				String QueryForAECBPipelineCases = "select CifId, AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,"
						+ "TotalAmt,TotalNoOfInstalments,CreditLimit,'' as col1,NoOfDaysInPipeline,"
					+ "isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' "
					+ "then 'Y' else 'N' end as 'IsDuplicate',case when WriteoffStat= 'W' then  OutstandingAmt else '' end as "
					+ "'OutstandingAmt' from ng_dpl_cust_extexpo_LoanDetails with (nolock)"
					+ "where Wi_Name= '"+ processInstanceID +"' and LoanStat= 'Pipeline'"
					+ "union select CifId, CardEmbossNum,ProviderNo,CardType,CardTypeDesc, CustRoleType,LastUpdateDate,'' "
					+ "as col2,NoOfInstallments, '' as col3, TotalAmount, "
					+ "NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when "
					+ "IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate','' as 'OutstandingAmt'  from "
					+ "ng_dpl_cust_extexpo_CardDetails with (nolock) where Wi_Name= '" + processInstanceID + "' and "
							+ "cardstatus='Pipeline'";
				
				
				Digital_PL.mLogger.debug("DBQuery4 : " + QueryForAECBPipelineCases);
				
				List<List<String>> AECBPipelineCases = iform.getDataFromDB(QueryForAECBPipelineCases);
				Digital_PL.mLogger.debug("twoDarray: " + AECBPipelineCases);
				
				if (!AECBPipelineCases.isEmpty()) {
					
					JSONArray jsonArray = new JSONArray();
					iform.clearTable("AECB_Pipelines");
					
					for (int i = 0; i < AECBPipelineCases.size(); i++) {
						JSONObject obj1 = new JSONObject();
						String typeOfContract_LoanType = AECBPipelineCases.get(i).get(3);
						
						Digital_PL.mLogger.debug("Reason_Code : " + typeOfContract_LoanType);

						String provider_No = AECBPipelineCases.get(i).get(2);
						Digital_PL.mLogger.debug("Reason_Code : " + provider_No);

						String phasE = AECBPipelineCases.get(i).get(4);
						Digital_PL.mLogger.debug("Reason_Code : " + provider_No);
						Digital_PL.mLogger.debug("Reason_Code : " + phasE);

						String loanType = AECBPipelineCases.get(i).get(3);
						String financeAmt = "";
						if (loanType.toUpperCase().contains("LOAN")) {
							financeAmt += AECBPipelineCases.get(i).get(7);
						} else {
							financeAmt += AECBPipelineCases.get(i).get(10);
						}
						Digital_PL.mLogger.debug("financeAmt : " + financeAmt);
						String RequestDate = AECBPipelineCases.get(i).get(6);
						Digital_PL.mLogger.debug("RequestDate : " + RequestDate);
						obj1.put("Type of Contract", typeOfContract_LoanType);
						obj1.put("Provider No", provider_No);
						obj1.put("Phase", phasE);
						obj1.put("Finance Amount", financeAmt);
						obj1.put("Request Date", RequestDate);
						
						if (jsonArray.contains(obj1)) {
							continue;
						}
						jsonArray.add(obj1);
					}
					Digital_PL.mLogger.debug("jsonArray : " + jsonArray);
					iform.addDataToGrid("AECB_Pipelines", jsonArray);

					for (int i = 0; i < AECBPipelineCases.size(); i++) {

						String extChargeoff = AECBPipelineCases.get(i).get(14);
						//Digital_PL.mLogger.debug("extChargeoff : " + extChargeoff);
						
						if(!(extChargeoff == null || "".equalsIgnoreCase(extChargeoff))){
						iform.setTableCellValue("AECB_Exposure", i, 7, extChargeoff);
						}
					}
				}
				
				
			
			}catch(Exception e){
				Digital_PL.mLogger.error("Error while parse AECB pipeline cases:"+e.getMessage());
			}

		

		return strReturn;
	}

	
}
