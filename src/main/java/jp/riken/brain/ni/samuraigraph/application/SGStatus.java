package jp.riken.brain.ni.samuraigraph.application;

public class SGStatus {
	private boolean mSucceeded = false;
	private String mMessage = null;
	public SGStatus(final boolean succeeded) {
		super();
		this.mSucceeded = succeeded;
	}
	public SGStatus(final boolean succeeded, final String msg) {
		this(succeeded);
		this.mMessage = msg;
	}
	public boolean isSucceeded() {
		return this.mSucceeded;
	}
	public String getMessage() {
		return this.mMessage;
	}
	@Override
	public String toString() {
		return this.getMessage();
	}
}