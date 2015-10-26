package com.jianzhiniu.views;

import java.util.Calendar;

import net.simonvt.numberpicker.NumberPicker;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jianzhiniu.activity.R;

/**
 * ����ѡ�����
 * @author Administrator
 *
 */
@SuppressLint({ "NewApi", "ViewConstructor" })
public class DateTimePicker extends FrameLayout
{
	private final NumberPicker mDateSpinner;
	private final NumberPicker mMonthSpinner;
	private final NumberPicker mDaySpinner;
	private TextView textView;
	private Button button;
	private Calendar mDate;
	private int mMonth, mDay, mYear; 
//    private String[] mDateDisplayValues = new String[7];
	private OnDateTimeChangedListener mOnDateTimeChangedListener;
	private OnClickListenered clListener;
	
	public DateTimePicker(Context context, boolean is, int n)
	{
		super(context);
		mDate = Calendar.getInstance();
		
		mMonth=mDate.get(Calendar.MONTH)+1;
		mDay=mDate.get(Calendar.DAY_OF_MONTH);
		mYear=mDate.get(Calendar.YEAR);
		
//		sYear = Integer.valueOf(dateString.split("-")[0]);
//		if (!is) {
//			sMonth = Integer.valueOf(dateString.split("-")[1]);
//			sDay = Integer.valueOf(dateString.split("-")[2]);
//		}
		
		inflate(context, R.layout.datedialog, this);
		//��
		mDateSpinner=(NumberPicker)this.findViewById(R.id.np_date);
		//��
		mMonthSpinner=(NumberPicker)this.findViewById(R.id.np_hour);
		//��
		mDaySpinner=(NumberPicker)this.findViewById(R.id.np_minute);
		
		textView = (TextView)this.findViewById(R.id.top_view);
		if (is) {
			mDaySpinner.setVisibility(View.GONE);
			mMonthSpinner.setVisibility(View.GONE);
			if (n == 0) {
				textView.setText("���ٽ��");
			}else {
				textView.setText("���ӽ��");
			}
			mDateSpinner.setMinValue(1);
			mDateSpinner.setMaxValue(1000000);
			mDateSpinner.setValue(2);
			mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
		}else {
			mDateSpinner.setMinValue(1900);
			mDateSpinner.setMaxValue(mYear);
			mDateSpinner.setValue(mYear);
			mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
			mMonthSpinner.setMinValue(1);
			mMonthSpinner.setMaxValue(12);
			mMonthSpinner.setValue(mMonth);
			mMonthSpinner.setOnValueChangedListener(mOnHourChangedListener);
			mDaySpinner.setMinValue(1);
			mDaySpinner.setMaxValue(31);
			mDaySpinner.setValue(mDay);
			mDaySpinner.setOnValueChangedListener(mOnMinuteChangedListener);
		}
		//ȷ����ť
		button = (Button)this.findViewById(R.id.sure_btn);
		button.setOnClickListener(clickListener);
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onButtonClicked(v);
		}
	};
	//���ֹ�������
	private NumberPicker.OnValueChangeListener mOnDateChangedListener=new net.simonvt.numberpicker.NumberPicker.OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mYear = mDateSpinner.getValue();
//			updateDateControl();
			onDateTimeChanged();
		}
	};
	//���ֹ�������
	private NumberPicker.OnValueChangeListener mOnHourChangedListener=new net.simonvt.numberpicker.NumberPicker.OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mMonth = mMonthSpinner.getValue();
			onDateTimeChanged();
		}
	};
	//���ֹ�������
	private NumberPicker.OnValueChangeListener mOnMinuteChangedListener=new net.simonvt.numberpicker.NumberPicker.OnValueChangeListener()
	{
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal)
		{
			mDay = mDaySpinner.getValue();
			onDateTimeChanged();
		}
	};
	
	
	//���ֹ��������ӿ�
	public interface OnDateTimeChangedListener 
	{
		void onDateTimeChanged(DateTimePicker view, int year, int month, int day);
	}
	
	public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) 
	{
		mOnDateTimeChangedListener = callback;
	}
	
	private void onDateTimeChanged() 
	{
		if (mOnDateTimeChangedListener != null)
		{
			mOnDateTimeChangedListener.onDateTimeChanged(this, mYear, mMonth-1, mDay);
		}
	}
	public interface OnClickListenered
	{
		void OnButtonClickListener(View v);
	}
	
	public void setOnClickListenered(OnClickListenered callback) 
	{
		clListener = callback;
	}
	
	private void onButtonClicked(View v) 
	{
		if (clListener != null)
		{
			clListener.OnButtonClickListener(v);
		}
	}
}
