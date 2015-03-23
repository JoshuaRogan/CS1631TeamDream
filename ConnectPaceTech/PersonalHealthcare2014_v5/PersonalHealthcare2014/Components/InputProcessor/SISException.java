//package system;

/**
 * Exception wrapper for SIS system 
 * 
 * @author Jerry Ye (Yinglin Sun)
 *
 */
public class SISException extends Exception {
	public SISException(String str) {
		super(str);
	}
	
	public SISException(Exception e) {
		super(e);
	}

	public SISException(String message, Exception e) {
		super(message, e);
	}

	@Override
	public String getMessage() {
		if ( super.getMessage() != null)
			return super.getMessage();
		
		return getCause().getMessage();
	}
	
}
