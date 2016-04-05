package de.fernsehfee.widgit.digitview;

public interface DigitStatusListener {
	public void onDigitCompleted(DigitView view, String digits);
	public void onDigitCancelled(DigitView view);
}
