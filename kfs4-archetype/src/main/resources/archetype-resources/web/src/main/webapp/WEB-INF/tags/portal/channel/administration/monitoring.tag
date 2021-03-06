<%--
 Copyright 2007 The Kuali Foundation
 
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Monitoring" />
<div class="body">
	<strong>Service Bus</strong></br>
   	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Message Queue" url="${ConfigProperties.ksb.url}/${ConfigProperties.message.queue.url}" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Registry" url="${ConfigProperties.ksb.url}/${ConfigProperties.service.registry.url}" /></li>
		<li><portal:portalLink displayTitle="true" title="Thread Pool" url="${ConfigProperties.ksb.url}/${ConfigProperties.thread.pool.url}" /></li>
	</ul>
	<strong>Workflow</strong></br>
   	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Document Operation" url="${ConfigProperties.workflow.url}/DocumentOperation.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Statistics Report" url="${ConfigProperties.workflow.url}/Stats.do" /></li>
	</ul>	
</div>
<channel:portalChannelBottom />

