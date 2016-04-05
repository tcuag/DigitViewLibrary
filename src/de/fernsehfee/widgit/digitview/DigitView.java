package de.fernsehfee.widgit.digitview;

import android.app.Service;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.fernsehfee.widget.digitview.R;

public class DigitView extends FrameLayout {

	private Object mOwner = null;
	private View mRootView = null;
	
	private int mMaxDigits = 4;

	private LinearLayout mDigitParent = null;
	private TextView[] mDigits = null;

	private DigitChangedListener mOnDigitChangedListener = null;
	private DigitStatusListener mOnDigitStatusListener = null;
	
	private View.OnKeyListener mDigitListener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
//			Log.v("KEY", "keyCode="+keyCode+" action="+event.getAction());
			
			if(event.getAction() == KeyEvent.ACTION_UP) {
				View focused = mDigitParent.getFocusedChild();
				
				switch(keyCode) {
				case 19: // Up
					return false;
				case 20: // Down
					return false;
				case 21: // Left
					if(!moveLeft()) return false;
					break;
				case 22: // Right
					if(!moveRight()) return false;
					break;
				case 23: // OK
					if(mOnDigitStatusListener != null) mOnDigitStatusListener.onDigitCompleted(DigitView.this, getAllDigits());
					break;
					
	
				case 7: // 0
					setValue((TextView)focused, "0");
					break;
				case 8: // 1
					setValue((TextView)focused, "1");
					break;
				case 9: // 2
					setValue((TextView)focused, "2");
					break;
				case 10: // 3
					setValue((TextView)focused, "3");
					break;
				case 11: // 4
					setValue((TextView)focused, "4");
					break;
				case 12: // 5
					setValue((TextView)focused, "5");
					break;
				case 13: // 6
					setValue((TextView)focused, "6");
					break;
				case 14: // 7
					setValue((TextView)focused, "7");
					break;
				case 15: // 8
					setValue((TextView)focused, "8");
					break;
				case 16: // 9
					setValue((TextView)focused, "9");
					break;
					
				case 4:
					if(mOnDigitStatusListener != null) mOnDigitStatusListener.onDigitCancelled(DigitView.this);
					return false;
					
				default:
					setValue((TextView)focused, "_");
					break;
				}
				
				return true;
			} else {
				switch(keyCode) {
				case 21: // Left
				case 22: // Right
				case 7: // 0
				case 8: // 1
				case 9: // 2
				case 10: // 3
				case 11: // 4
				case 12: // 5
				case 13: // 6
				case 14: // 7
				case 15: // 8
				case 16: // 9
				case 4: // BACK
					return true;
				}
			}
			
			return false;
		}
	};

	public DigitView(Context context) {
		super(context);
		init();
	}

	public DigitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAttrs(attrs);
		init();
	}

	public DigitView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setAttrs(attrs);
		init();
	}
	
	private void setAttrs(AttributeSet attrs) {
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DigitView, 0, 0);

		try {
			mMaxDigits = a.getInteger(R.styleable.DigitView_totalDigits, 4);
		} finally {
			a.recycle();
		}
	}
	
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.digitview_main, null, true);
		
		mDigitParent = (LinearLayout)mRootView.findViewById(R.id.digitParent);
		
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(32, 32);
		params.setMargins(5, 5, 5, 5);

		mDigits = new TextView[mMaxDigits];
		for(int i = 0; i < mMaxDigits; i++) {
			int currentId = i+1;
			int leftId = currentId == 1 ? mMaxDigits : currentId-1;
			int rightId = currentId == mMaxDigits ? 1 : currentId+1;
			
			mDigits[i] = new TextView(getContext());
			
			mDigits[i].setId(currentId);
			mDigits[i].setBackgroundResource(R.drawable.digit_selector);
			mDigits[i].setFocusable(true);
			mDigits[i].setFocusableInTouchMode(true);
			mDigits[i].setGravity(Gravity.CENTER);
			mDigits[i].setNextFocusLeftId(leftId);
			mDigits[i].setNextFocusRightId(rightId);
			mDigits[i].setText("_");
			mDigits[i].setTextColor(ColorStateList.valueOf(Color.BLACK));
			mDigits[i].setTypeface(null, Typeface.BOLD);
			mDigits[i].setOnKeyListener(mDigitListener);
			
			mDigitParent.addView(mDigits[i], params);
		}
		
		addView(mRootView);
	}
	
	private boolean setValue(TextView view, String value) {
		view.setText(value);
		
		if(mOnDigitChangedListener != null) {
			mOnDigitChangedListener.onDigitChanged(this, view.getId()-1, mMaxDigits, value, getAllDigits());
		}

		return moveRight();
	}
	
	public void setDigits(String value) {
		for(int i = 0; i < mDigits.length; i++) {
			int valueIndex = value.length() - i - 1;
			if(valueIndex >= 0) {
				mDigits[(mDigits.length - i - 1)].setText(String.valueOf(value.charAt(valueIndex)));
			} else {
				mDigits[(mDigits.length - i - 1)].setText("_");
			}
		}
	}
	
	public String getAllDigits() {
		StringBuilder sb = new StringBuilder();
		for(TextView view : mDigits) {
			if(view.getText().equals("_")) continue;
			sb.append(view.getText());
		}
		return sb.toString();
	}
	
	public void setTitle(String title) {
		((TextView) mRootView.findViewById(R.id.digitTitle)).setText(title);
	}
	
	public void setMaxDigits(int digits) {
		mMaxDigits = digits;
		removeAllViews();
		init();
	}
	
	private boolean moveLeft() {
		View focused = mDigitParent.getFocusedChild();
		int leftId = focused.getNextFocusLeftId();
		if(leftId == View.NO_ID) return false;
		View left = mRootView.findViewById(leftId);
		left.requestFocus();
		return true;
	}
	
	private boolean moveRight() {
		View focused = mDigitParent.getFocusedChild();
		int rightId = focused.getNextFocusRightId();
		if(rightId == View.NO_ID) return false;
		View right = mRootView.findViewById(rightId);
		right.requestFocus();
		return true;
	}

	public DigitChangedListener getOnDigitChangedListener() {
		return mOnDigitChangedListener;
	}

	public void setOnDigitChangedListener(DigitChangedListener mOnDigitChangedListener) {
		this.mOnDigitChangedListener = mOnDigitChangedListener;
	}

	public DigitStatusListener getOnDigitStatusListener() {
		return mOnDigitStatusListener;
	}

	public void setOnDigitStatusListener(DigitStatusListener mOnDigitStatusListener) {
		this.mOnDigitStatusListener = mOnDigitStatusListener;
	}

	public Object getOwner() {
		return mOwner;
	}

	public void setOwner(Object owner) {
		this.mOwner = owner;
	}

}
