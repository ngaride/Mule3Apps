%dw 1.0
%output application/json indent=false, skipNullOn="everywhere", encoding="UTF-8"
---
{
	error : {
		errorCode: flowVars.errorCode,
		errorMessage: flowVars.errorMessage
	}
}