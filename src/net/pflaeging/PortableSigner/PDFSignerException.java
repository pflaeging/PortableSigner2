/*
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Peter Pfläging <peter@pflaeging.net>
 */
package net.pflaeging.PortableSigner;

public class PDFSignerException extends Exception {

	private String resultText;
	private Boolean errorState;
	private String errorString;
	
	public PDFSignerException(String resultText, Boolean errorState, String errorString) {
		super(); // call superclass constructor
		this.resultText = resultText;
		this.errorState = errorState;
		this.errorString = errorString;
	}

	public String getResultText() {
		return resultText;
	}

	public Boolean getErrorState() {
		return errorState;
	}

	public String getErrorString() {
		return errorString;
	}

}
