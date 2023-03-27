package in.rbihub.error;

import in.rbihub.error.InvalidParamException.ErrorCodes;

public class InvalidParamException extends Exception {

	private ErrorCodes errorCode;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCodes errorCode) {
		this.errorCode = errorCode;
	}

	private String message = null;

	public InvalidParamException(ErrorCodes errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;

	}

	public static enum ErrorCodes {
		E001, E002, E003, E004, E005, E006, E007, E008, E009, E010, E011, E012, E013, E014, E015, E016, E017, E018,
		E019, E020, E021, E022, E023, E024, E025, E026, E027, E028, E029, E030, E031, E032, E033, E034, E035, E036,
		E037, E038, E039, E040, E041, E042, E043, E044, E045, E046, E176, E177, E178, E179, E180, E181, E182, E183,
		E184, E185, E501, E502, E503, E504, E999, E000
	}

	public static String getErrorDescription(ErrorCodes code) {

		if (code.equals(ErrorCodes.E001)) {
			return "Invalid license key";
		} else if (code.equals(ErrorCodes.E002)) {
			return "Unauthorized user entity";
		} else if (code.equals(ErrorCodes.E003)) {
			return "Unauthorized source IP address";
		} else if (code.equals(ErrorCodes.E004)) {
			return "Invalid digital signature or the payload";
		} else if (code.equals(ErrorCodes.E005)) {
			return "Invalid authorization token found in the http request";
		} else if (code.equals(ErrorCodes.E006)) {
			return "Invalid service name";
		} else if (code.equals(ErrorCodes.E007)) {
			return "Session key has expired";
		} else if (code.equals(ErrorCodes.E008)) {
			return "Invalid version code";
		} else if (code.equals(ErrorCodes.E009)) {
			return "Unsupported version of the API";
		} else if (code.equals(ErrorCodes.E010)) {
			return "Invalid timestamp format";
		} else if (code.equals(ErrorCodes.E011)) {
			return "Blank or invalid user transaction code";
		} else if (code.equals(ErrorCodes.E012)) {
			return "Key not decrypted";
		} else if (code.equals(ErrorCodes.E013)) {
			return "Invalid Base64 encoding";
		} else if (code.equals(ErrorCodes.E014)) {
			return "Unsupported format – permissible formats are JSON only";
		} else if (code.equals(ErrorCodes.E015)) {
			return "Data format does not comply with the format type indicated";
		} else if (code.equals(ErrorCodes.E016)) {
			return "Digital signature verification failed";
		} else if (code.equals(ErrorCodes.E017)) {
			return "Invalid key information on the signature";
		} else if (code.equals(ErrorCodes.E018)) {
			return "No payload received";
		} else if (code.equals(ErrorCodes.E019)) {
			return "Invalid service name";
		} else if (code.equals(ErrorCodes.E020)) {
			return "Invalid user transaction identifier";
		} else if (code.equals(ErrorCodes.E021)) {
			return "Third party provider error";
		} else if (code.equals(ErrorCodes.E022)) {
			return "Digital signature of sender not found on the server";
		} else if (code.equals(ErrorCodes.E023)) {
			return "Digital signature not within validity date or expired ";
		} else if (code.equals(ErrorCodes.E024)) {
			return "Data encryption error ";
		} else if (code.equals(ErrorCodes.E025)) {
			return "Internal Error";
		} else if (code.equals(ErrorCodes.E026)) {
			return "Mandatory parameters not supplied";
		} else if (code.equals(ErrorCodes.E027)) {
			return "Invalid entity version or an unsupported version";
		} else if (code.equals(ErrorCodes.E028)) {
			return "Invalid parameter supplied";
		} else if (code.equals(ErrorCodes.E029)) {
			return "Invalid entity name";
		} else if (code.equals(ErrorCodes.E030)) {
			return "Consent not provided";
		} else if (code.equals(ErrorCodes.E031)) {
			return "Provider is down or not reachable";
		} else if (code.equals(ErrorCodes.E032)) {
			return "Invalid API Key";
		} else if (code.equals(ErrorCodes.E033)) {
			return "Invalid lang value";
		} else if (code.equals(ErrorCodes.E034)) {
			return "Invalid txncode value";
		} else if (code.equals(ErrorCodes.E035)) {
			return "Invalid Consent value";
		} else if (code.equals(ErrorCodes.E036)) {
			return "Invalid Consent ID value";
		} else if (code.equals(ErrorCodes.E037)) {
			return "Authentication failure at provider";
		} else if (code.equals(ErrorCodes.E038)) {
			return "User validity expired with provider";
		} else if (code.equals(ErrorCodes.E039)) {
			return "Invalid state value";
		} else if (code.equals(ErrorCodes.E040)) {
			return "Request payload is invalid";
		} else if (code.equals(ErrorCodes.E041)) {
			return "Request payload meta data is missing";
		} else if (code.equals(ErrorCodes.E042)) {
			return "Request payload hmac is missing or invalid";
		} else if (code.equals(ErrorCodes.E043)) {
			return "Request payload data is missing or invalid";
		} else if (code.equals(ErrorCodes.E044)) {
			return "Request payload timestamp should be  less than 60 seconds old or cannot be in future";
		} else if (code.equals(ErrorCodes.E045)) {
			return "Request payload content length is not set or missing";
		} else if (code.equals(ErrorCodes.E046)) {
			return "Request payload content length does not match with payload body";
		} else if (code.equals(ErrorCodes.E501)) {
			return "Invalid district id value";
		} else if (code.equals(ErrorCodes.E502)) {
			return "Invalid tehsil or taluk  id value";
		} else if (code.equals(ErrorCodes.E503)) {
			return "Invalid khasara id value";
		} else if (code.equals(ErrorCodes.E504)) {
			return "Owner data not found";
		} else if (code.equals(ErrorCodes.E176)) {
			return "Invalid name value";
		} else if (code.equals(ErrorCodes.E177)) {
			return "Invalid name value found for source language set to english";
		} else if (code.equals(ErrorCodes.E178)) {
			return "Invalid name value found for source language set to hindi";
		} else if (code.equals(ErrorCodes.E179)) {
			return "Invalid name value found for source language set to tamil";
		} else if (code.equals(ErrorCodes.E180)) {
			return "Invalid source language";
		} else if (code.equals(ErrorCodes.E181)) {
			return "Transliteration not supported for provided source language and destination language";
		} else if (code.equals(ErrorCodes.E182)) {
			return "Invalid address value";
		} else if (code.equals(ErrorCodes.E183)) {
			return "Invalid address value found for source language set to english";
		} else if (code.equals(ErrorCodes.E184)) {
			return "Invalid address value found for source language set to hindi";
		} else if (code.equals(ErrorCodes.E185)) {
			return "Invalid address value found for source language set to tamil";
		}
		return "";
	}

}
