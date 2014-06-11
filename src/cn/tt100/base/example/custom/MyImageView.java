package cn.tt100.base.example.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	boolean isShowText;
	String showText="����";
	Paint paint;
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		isShowText = true ;
		
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setTextSize(25);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//������1
		if(isShowText){
			Rect rect = new Rect();
			//���ذ�Χ�����ַ�������С��һ��Rect����
			paint.getTextBounds(showText, 0, 1, rect); 
			canvas.drawText(showText, getWidth()/2-rect.width()/2, getHeight()/2-rect.height()/2, paint);
		}
	}
	
	public void setText(String teString){
		isShowText= true;
		this.showText = teString;
		invalidate();
	}
	
	public void hideText(){
		isShowText = false;
		invalidate();
	}
	
}
