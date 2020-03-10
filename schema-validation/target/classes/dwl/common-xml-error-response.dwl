%dw 1.0
%output application/xml indent=false, skipNullOn="everywhere", encoding="UTF-8"
---
{
	error : {
		errorCode: flowVars.errorCode,
		errorMessage: flowVars.errorMessage
	}
}