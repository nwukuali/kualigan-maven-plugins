#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%--
 Copyright 2006 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/${parentArtifactId}TldHeader.jsp"%>

<c:set var="rawDepositTypeCode" value="${symbol_dollar}{KualiForm.depositTypeCode}" />
<c:set var="docTitle"
	value="Create a New ${symbol_dollar}{KualiForm.depositTypeString} Deposit" />

<c:set var="depositAttributes"
	value="${symbol_dollar}{DataDictionary.Deposit.attributes}" />
<c:set var="cashReceiptAttributes"
	value="${symbol_dollar}{DataDictionary.CashReceiptDocument.attributes}" />
<c:set var="checkAttributes"
	value="${symbol_dollar}{DataDictionary.CheckBase.attributes}" />
<c:set var="dummyAttributes"
	value="${symbol_dollar}{DataDictionary.AttributeReferenceDummy.attributes}" />


<kul:page showDocumentInfo="false" showTabButtons="false"
	headerTitle="${symbol_dollar}{docTitle}" docTitle="${symbol_dollar}{docTitle}"
	transactionalDocument="false" htmlFormAction="depositWizard">
	<script type="text/javascript">
function checkCRAllOrNone() {
  var masterCRCheckBox = document.getElementById('masterCRCheckBox');
  if (masterCRCheckBox) {
    for(var i = 0; i < document.KualiForm.elements.length; i++) {
      var e = document.KualiForm.elements[i];
      if((e.name.match(/^depositWizardHelper/)) && (e.type == 'checkbox') && (!e.disabled)) {
        e.checked = masterCRCheckBox.checked;
      }
    }
  }
}

function checkCheckAllOrNone() {
  var masterCheckCheckBox = document.getElementById('masterCheckCheckBox');
  if (masterCheckCheckBox) {
    for(var i = 0; i < document.KualiForm.elements.length; i++) {
      var e = document.KualiForm.elements[i];
      if((e.name.match(/^depositWizardCashieringCheckHelper/)) && (e.type == 'checkbox') && (!e.disabled)) {
        e.checked = masterCheckCheckBox.checked;
      }
    }
  }
}
</script>

	<html:hidden property="cashDrawerCampusCode" />
	<html:hidden property="cashManagementDocId" />
	<html:hidden property="depositTypeCode" />
	<html:hidden property="currencyDetail.documentNumber" />
	<html:hidden property="currencyDetail.cashieringRecordSource" />
	<html:hidden property="currencyDetail.financialDocumentTypeCode" />
	<html:hidden property="coinDetail.documentNumber" />
	<html:hidden property="coinDetail.cashieringRecordSource" />
	<html:hidden property="coinDetail.financialDocumentTypeCode" />

	<c:if test="${symbol_dollar}{!empty KualiForm.depositableCashReceipts || !empty KualiForm.depositableCashieringChecks || !empty KualiForm.checkFreeCashReceipts}">
		<kul:tabTop tabTitle="Deposit Header" defaultOpen="true"
			tabErrorKey="depositHeaderErrors">
			<div class="tab-container" align=center>
			<h3>Deposit Header</h3>

			<!-- deposit header fields -->
			<div id="workarea">
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="deposit header info">
				<tr>
				    <sys:bankLabel align="left"/>
					<kul:htmlAttributeHeaderCell labelFor="depositTypeCode"
						attributeEntry="${symbol_dollar}{depositAttributes.depositTypeCode}"
						hideRequiredAsterisk="true" horizontal="true" align="left" />
					<kul:htmlAttributeHeaderCell labelFor="depositTicketNumber"
						attributeEntry="${symbol_dollar}{depositAttributes.depositTicketNumber}"
						hideRequiredAsterisk="true" horizontal="true" align="left" />
				</tr>
				<tr>
                    <sys:bankControl property="bankCode" objectProperty="bank" depositOnly="true" readOnly="${symbol_dollar}{readOnly}" style="infoline" />				
					<td class="infoline"><kul:htmlControlAttribute
						property="depositTypeCode"
						attributeEntry="${symbol_dollar}{depositAttributes.depositTypeCode}"
						readOnly="true" /> <br />
					&nbsp;</td>
					<td class="infoline"><kul:htmlControlAttribute
						property="depositTicketNumber"
						attributeEntry="${symbol_dollar}{depositAttributes.depositTicketNumber}" /> <br />
					&nbsp;</td>
				</tr>
			</table>
			</div>
			</div>
		</kul:tabTop>
    
    <c:if test="${symbol_dollar}{KualiForm.depositFinal}">
      <kul:tab tabTitle="Currency and Coin Detail" defaultOpen="true">
        <div class="tab-container" align="center">
            <h3>Currency and Coin Detail</h3>
          <fp:currencyCoinLine currencyProperty="currencyDetail" coinProperty="coinDetail" readOnly="false" />
        </div>
      </kul:tab>
    </c:if>

    <c:set var="crCounter" value="0" />
    <c:if test="${symbol_dollar}{!empty KualiForm.depositableCashReceipts || !empty KualiForm.checkFreeCashReceipts}">
		<kul:tab tabTitle="Cash Receipts" defaultOpen="true"
			tabErrorKey="cashReceiptErrors">
			<div class="tab-container" align="center">
			<div width="100%" align="left"
				style="padding-left: 10px; padding-bottom: 10px"><b>Please select
			the Cash Receipt documents that you would like to deposit.</b></div>
			<h3>Cash Receipts Available for Deposit</h3>
			<div id="workarea">
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="cash receipts available for deposit">
				<tr>
					<td>
					<div align="center"><input type="checkbox" title="Check All or None" name="masterCRCheckBox"
						onclick="checkCRAllOrNone();" id="masterCRCheckBox" /></div>
					</td>
					<kul:htmlAttributeHeaderCell labelFor="masterCRCheckBox" literalLabel="${symbol_pound}" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Document Number"
						scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Description" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Create Date" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Check Total" scope="col" />
					
				</tr>

      <c:if test="${symbol_dollar}{!empty KualiForm.depositableCashReceipts}">
				<logic:iterate name="KualiForm" id="cashReceipt"
					property="depositableCashReceipts" indexId="ctr">
          <c:set var="crCounter" value="${symbol_dollar}{crCounter + 1}" />
					<tr>
						<td colspan="7"
							style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
							src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
					</tr>

					<tr>
						<td>
						<div align="center"><html:checkbox
							styleId="depositWizardHelper[${symbol_dollar}{ctr}].selectedValue" property="depositWizardHelper[${symbol_dollar}{ctr}].selectedValue"
							value="${symbol_dollar}{cashReceipt.documentNumber}" /></div>
						</td>
						<td>
						<div align="center"><b><label for="depositWizardHelper[<c:out value="${symbol_dollar}{ctr}" />].selectedValue"><c:out value="${symbol_dollar}{(ctr + 1)}" /></label></b></div>
						</td>
						<td>
						<div align="center"><a
							href="financialCashReceipt.do?methodToCall=docHandler&docId=${symbol_dollar}{cashReceipt.documentHeader.documentNumber}&command=displayDocSearchView"
							target="new"> <kul:htmlControlAttribute
							property="depositableCashReceipt[${symbol_dollar}{ctr}].documentNumber"
							attributeEntry="${symbol_dollar}{cashReceiptAttributes.documentNumber}"
							readOnly="true" /> </a> <html:hidden
							property="depositableCashReceipt[${symbol_dollar}{ctr}].documentHeader.documentNumber" />
						</div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="depositableCashReceipt[${symbol_dollar}{ctr}].documentHeader.documentDescription"
							attributeEntry="${symbol_dollar}{cashReceiptAttributes.documentDescription}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="depositWizardHelper[${symbol_dollar}{ctr}].cashReceiptCreateDate"
							attributeEntry="${symbol_dollar}{dummyAttributes.genericTimestamp}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center">
						${symbol_dollar}&nbsp;<c:out value="${symbol_dollar}{cashReceipt.currencyFormattedTotalCheckAmount}" /> <html:hidden
							property="depositableCashReceipt[${symbol_dollar}{ctr}].totalCheckAmount" /></div>
						</td>
						
					</tr>

					<c:if test="${symbol_dollar}{cashReceipt.checkCount > 0}">
						<tr>
							<td class="infoline" rowspan="${symbol_dollar}{cashReceipt.checkCount + 1}"
								colspan="2">&nbsp;</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${symbol_dollar}{checkAttributes.checkNumber}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${symbol_dollar}{checkAttributes.checkDate}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${symbol_dollar}{checkAttributes.description}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${symbol_dollar}{checkAttributes.amount}" readOnly="true" /></td>
							
						</tr>

						<logic:iterate name="cashReceipt" property="checks" id="check"
							indexId="checkIndex">
							<tr>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${symbol_dollar}{ctr}].check[${symbol_dollar}{checkIndex}].checkNumber"
									attributeEntry="${symbol_dollar}{checkAttributes.checkNumber}" readOnly="true" />
								</td>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${symbol_dollar}{ctr}].check[${symbol_dollar}{checkIndex}].checkDate"
									attributeEntry="${symbol_dollar}{checkAttributes.checkDate}" readOnly="true" />
								</td>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${symbol_dollar}{ctr}].check[${symbol_dollar}{checkIndex}].description"
									attributeEntry="${symbol_dollar}{checkAttributes.description}" readOnly="true" />
								</td>
								<td>${symbol_dollar}<kul:htmlControlAttribute
									property="depositableCashReceipt[${symbol_dollar}{ctr}].check[${symbol_dollar}{checkIndex}].amount"
									attributeEntry="${symbol_dollar}{checkAttributes.amount}" readOnly="true" />
								</td>
							</tr>
						</logic:iterate>
					</c:if>
				</logic:iterate>
      </c:if>
        
        <%-- check free cash receipts - only show on final deposit, when they are automatically deposited --%>
        <c:if test="${symbol_dollar}{!empty KualiForm.checkFreeCashReceipts && KualiForm.depositFinal}">
        <tr>
          <td colspan="7"><strong>The following Cash Receipts had no checks associated with them; they are automatically deposited as part of the final deposit.</strong></td> 
        </tr>
        <logic:iterate name="KualiForm" id="cashReceipt"
					property="checkFreeCashReceipts" indexId="ctr">
          <c:set var="crCounter" value="${symbol_dollar}{crCounter + 1}" />
					<tr>
						<td colspan="7"
							style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
							src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
						<div align="center"><b>${symbol_dollar}{crCounter}</b></div>
						</td>
						<td>
						<div align="center"><a
							href="financialCashReceipt.do?methodToCall=docHandler&docId=${symbol_dollar}{cashReceipt.documentHeader.documentNumber}&command=displayDocSearchView"
							target="new"> <kul:htmlControlAttribute
							property="checkFreeCashReceipt[${symbol_dollar}{ctr}].documentNumber"
							attributeEntry="${symbol_dollar}{cashReceiptAttributes.documentNumber}"
							readOnly="true" /> </a> <html:hidden
							property="checkFreeCashReceipt[${symbol_dollar}{ctr}].documentHeader.documentNumber" />
						</div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="checkFreeCashReceipt[${symbol_dollar}{ctr}].documentHeader.documentDescription"
							attributeEntry="${symbol_dollar}{cashReceiptAttributes.documentDescription}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="checkFreeCashReceipt[${symbol_dollar}{ctr}].documentHeader.workflowDocument.createDate"
							attributeEntry="${symbol_dollar}{dummyAttributes.genericTimestamp}"
							readOnly="true" /></div>
						</td>
						<td>&nbsp;</td>
						<td>
						<div align="center">
						${symbol_dollar}&nbsp;<c:out value="${symbol_dollar}{cashReceipt.currencyFormattedSumTotalAmount}" /></div>
						</td>
					</tr>
				</logic:iterate>
        </c:if>

				<tr>
					<td colspan="7"
						style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
						src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
				</tr>

			</table>
			</div>
      </kul:tab>
			
    </c:if>
    
    <c:if test="${symbol_dollar}{!empty KualiForm.depositableCashieringChecks}">
    
      <kul:tab tabTitle="Cashiering Transaction Checks" defaultOpen="true" tabErrorKey="cashieringCheckErrors">
        <div class="tab-container" align="center">
        <div width="100%" align="left" style="padding-left: 10px; padding-bottom: 10px">
          <strong>Please select Cashiering Checks to deposit.</strong>
        </div>
          <h3>Cashiering Checks Available for Deposit</h3>
        <div id="workarea">
          <table cellpadding="0" cellspacing="0" class="datatable" summary="cashiering checks available for deposit">
            <tr>
              <td>
                <div align="center"><input type="checkbox" title="Check All or None" name="masterCheckCheckBox" onclick="checkCheckAllOrNone();" id="masterCheckCheckBox" /></div>
              </td>
              <kul:htmlAttributeHeaderCell literalLabel="${symbol_pound}" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Number" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Description" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Date" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Amount" scope="col" />
            </tr>

            <logic:iterate name="KualiForm" id="cashieringCheck" property="depositableCashieringChecks" indexId="ctr">
              <tr>
                <td>
                  <div style="text-align: center">
                    <html:checkbox name="KualiForm" property="depositWizardCashieringCheckHelper[${symbol_dollar}{ctr}].sequenceId" value="${symbol_dollar}{KualiForm.depositableCashieringChecks[ctr].sequenceId}" />
                  </div>
                </td>
                <%-- if you change the spacing of the table cell below to put the different elements on different lines, giant monkeys will hurt you. Also, the DepositWizard form won't look quite as good --%>
                <td><strong><c:out value="${symbol_dollar}{ctr + 1}" /></strong><html:hidden name="KualiForm" property="depositableCashieringCheck[${symbol_dollar}{ctr}].sequenceId" /></td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${symbol_dollar}{ctr}].checkNumber" attributeEntry="${symbol_dollar}{checkAttributes.checkNumber}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${symbol_dollar}{ctr}].description" attributeEntry="${symbol_dollar}{checkAttributes.description}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${symbol_dollar}{ctr}].checkDate" attributeEntry="${symbol_dollar}{checkAttributes.checkDate}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${symbol_dollar}{ctr}].amount" attributeEntry="${symbol_dollar}{checkAttributes.amount}" readOnly="true" />
                </td>
              </tr>
            </logic:iterate>
          </table>
        </div>
      </kul:tab>
    </c:if>
    
    </div>
			<kul:panelFooter />

			<div id="globalbuttons" class="globalbuttons"><html:image
				property="methodToCall.createDeposit"
				src="${symbol_dollar}{ConfigProperties.externalizable.images.url}buttonsmall_create.gif" alt="create" title="create"
				styleClass="tinybutton" /> <html:image
				property="methodToCall.refresh" src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif"
				alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
				property="methodToCall.cancel" src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
				alt="cancel" title="cancel" styleClass="tinybutton" /></div>
	</c:if>

	<c:if test="${symbol_dollar}{empty KualiForm.depositableCashReceipts && empty KualiForm.depositableCashieringChecks && empty KualiForm.checkFreeCashReceipts}">
		<%-- manually handle parameter-substitution --%>
		<c:set var="msg0">
            ${symbol_dollar}{fn:replace(ConfigProperties.depositWizard.status.noCashReceipts, "{0}", KualiForm.cashDrawerCampusCode )}
        </c:set>
		<c:set var="msg1">
            ${symbol_dollar}{fn:replace(msg0, "{1}", KualiForm.cashManagementDocId)}
        </c:set>

		<table width="100%">
			<tr>
				<td align="center"><c:out value="${symbol_dollar}{msg1}" /></td>
			</tr>
		</table>

		<div id="globalbuttons" class="globalbuttons"><html:image
			property="methodToCall.refresh" src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif"
			alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
			property="methodToCall.cancel" src="${symbol_dollar}{ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
			alt="cancel" title="cancel" styleClass="tinybutton" /></div>
	</c:if>
</kul:page>