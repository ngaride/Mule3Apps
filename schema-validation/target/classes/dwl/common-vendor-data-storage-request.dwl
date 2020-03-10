%dw 1.0
%output application/xml indent=true, skipNullOn="everywhere", encoding="UTF-8"
%namespace ns0 http://services.mig.com/datastorage
---
ns0#DataStorageRequest: {
	ns0#ReportId: flowVars.reportId,
	ns0#Source: flowVars.source,
	ns0#Version: flowVars.version,
	ns0#Service: flowVars.service,
	ns0#ResponseFormat: flowVars.responseFormat,
	ns0#ResponseDatetime: now as :datetime,
	ns0#ResponsePayload: payload
}