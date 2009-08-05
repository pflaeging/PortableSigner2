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
