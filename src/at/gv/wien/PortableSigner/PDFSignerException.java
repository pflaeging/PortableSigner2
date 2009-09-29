/*
 * This File is part of PortableSigner (http://portablesigner.sf.net/)
 *  and is under the European Public License V1.1 (http://www.osor.eu/eupl)
 * (c) Stadt Wien, Peter Pfläging <peter.pflaeging@wien.gv.at>
 */
package at.gv.wien.PortableSigner;

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
