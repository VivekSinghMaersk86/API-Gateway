package in.rbihub.validation;

import in.rbihub.error.InvalidParamException;

public interface IDataValidation {

	/**
	 * Validation for all common URL Parameters like version, apikey, lang, txncode
	 * and consent
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @param contentType
	 * @param timestamp
	 * @return true in case validation is success
	 */
	public boolean validateCommonURLParams(String version, String apikey, String lang, String txncode, String consent,
			String consentId, String timestamp) throws InvalidParamException;

	/**
	 * Validation for all common URL Parameters for POST like version, apikey, lang
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @param contentType
	 * @param contentLength
	 * @param recievedContentLegnth
	 * @return true in case validation is success
	 */
	public boolean validateCommonURPOSTLParams(String version, String apikey, String lang, String contentType,
			Integer contentLength, int recievedContentLegnth) throws InvalidParamException;

	/**
	 * Validation for all common URL Parameters like version, apikey, lang, txncode
	 * and consent
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @return true in case validation is success
	 */
	public boolean validateCommonURLParamsWithOutConsent(String version, String apikey, String lang, String txncode)
			throws InvalidParamException;

	/**
	 * This specific validation for the version field in url can take values like
	 * 1.0, 2.1 etc
	 * 
	 * @param version
	 * @return true in case validation is success
	 */
	public boolean isVersionValid(String version) throws InvalidParamException;

	/**
	 * This specific validation for the timestamp field values like below "timestamp
	 * in ISO-8601 format e.g. 2023-01-03T21:10:23+05:30[Asia/Kolkata]"
	 * 
	 * @param timestamp
	 * @return true in case validation is success
	 */
	public boolean isTSValid(String timestamp) throws InvalidParamException;

	/**
	 * This specific validation for the apikey field in url. this field needs to be
	 * filled as it caters for log tracing for dispute resolution
	 * 
	 * @param apikey
	 * @return true in case validation is success
	 */
	public boolean isAPIKeyValid(String apikey) throws InvalidParamException;

	/**
	 * This specific validation for the lang field in header and can have value like
	 * english 99 hindi 6
	 * 
	 * @param lang
	 * @return true in case validation is success
	 */
	public boolean isLangValid(Integer lang) throws InvalidParamException;

	/**
	 * This specific validation for the txncode field in header this needs to be
	 * filled as it caters for log tracing for dispute resolution
	 * 
	 * @param txncode
	 * @return true in case validation is success
	 */
	public boolean isTxncodeValid(String txncode) throws InvalidParamException;

	/**
	 * This specific validation for the hmac field this needs to be checked for the
	 * data payload verification
	 * 
	 * @param txncode
	 * @return true in case validation is success
	 */
	public boolean isHmacValid(String hmac) throws InvalidParamException;

	/**
	 * This specific validation for the ClientId field in header this needs to be
	 * filled as it caters for log tracing for dispute resolution
	 * 
	 * @param clientId
	 * @return true in case validation is success
	 */
	public boolean isClientIdValid(String clientId) throws InvalidParamException;

	/**
	 * This specific validation for the consent field in header . Can take value y/n
	 * 
	 * @param consent
	 * @return
	 */
	public boolean isConsentValid(String consent) throws InvalidParamException;

	/**
	 * This specific validation for the consent ID in header .
	 * 
	 * @param consent
	 * @return
	 */
	public boolean isConsentIDValid(String consentId, String consent) throws InvalidParamException;

	/**
	 * Validation for all URL header specific to Madhya Pradesh like distId,tehId
	 * and khasraId
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @return true in case validation is success
	 */
	public boolean validatePersonMP(String khasraId, Integer distId, String tehId) throws InvalidParamException;

	/**
	 * This specific validation for the distId field in header and can have values
	 * like 18
	 * 
	 * @param distId
	 * @return
	 */
	public boolean isValidDistrict(Integer distId) throws InvalidParamException;

	/**
	 * This specific validation for the tehId field in header and can have values
	 * like 03
	 * 
	 * @param tehId
	 * @return true in case validation is success
	 */
	public boolean isValidTehId(String tehId) throws InvalidParamException;

	/**
	 * This specific validation for the KhasaraId field in header and can have
	 * values like 118040200107207000995
	 * 
	 * @param khasraId
	 * @return true in case validation is success
	 */
	public boolean isValidKhasaraId(String khasraId) throws InvalidParamException;

	/**
	 * Validation for all URL header specific to Tamilnadu like distId, talukcode,
	 * villcode , surveyno, subdivno and lgd
	 * 
	 * @param distId
	 * @param talukcode
	 * @param villcode
	 * @param surveyno
	 * @param subdivno
	 * @param lgd
	 * @return true in case validation is success
	 */
	public boolean validatePersonTN(Integer distId, Integer talukcode, Integer villcode, Integer surveyno,
			String subdivno, Boolean lgd) throws InvalidParamException;

	/**
	 * This specific validation for the state field in header and can have values
	 * like mp,tn,mh, or and up
	 * 
	 * @param khasraId
	 * @return true in case validation is success
	 */
	public boolean isValidState(Integer state) throws InvalidParamException;

	/**
	 * This specific validation for the lang field in url and can have value like
	 * english -en,hindi -hi, Marathi - mr , Oriya - or, tamil - ta Codes for the
	 * Representation of Names of Languages Codes arranged alphabetically by
	 * alpha-3/ISO 639-2 Code
	 * https://www.loc.gov/standards/iso639-2/php/code_list.php
	 * 
	 * @param lang
	 * @return true in case validation is success
	 */
	public boolean isISOLangValid(String lang) throws InvalidParamException;

	/**
	 * This specific validation for the address field and can have values of name in
	 * local language MDDS Standard : G01.02-02
	 * 
	 * @param name
	 * @return
	 */
	public boolean isValidAddress(String address, String srclang) throws InvalidParamException;

	/**
	 * This specific validation for the name field and can have values of name in
	 * local language MDDS Standard : G01.02-02
	 * 
	 * @param name
	 * @return boolean
	 */
	public boolean isValidSrcAndDestLang(String srcLang, String destLang) throws InvalidParamException;

	/**
	 * This specific validation for the name field and can have values of name in
	 * local language MDDS Standard : G01.02-02
	 * 
	 * @param name
	 * @return
	 */
	public boolean isValidName(String name, String srclang) throws InvalidParamException;

	/**
	 * Validation for all URL Parameters specific to Transliteration like name,
	 * address ,srclang and lang
	 * 
	 * @param version
	 * @param apikey
	 * @param lang
	 * @param txncode
	 * @return true in case validation is success
	 */
	public boolean validateTransliterationInput(String name, String address, String srcLang, String lang)
			throws InvalidParamException;
}
