package cn.shrek.base.example.adapter;

import android.content.Context;
import android.widget.TextView;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.example.adapter.StudentAdapter.StudentHolder;
import cn.shrek.base.example.bean.CEO;
import cn.shrek.base.ui.ZWBaseAdapter;
import cn.shrek.base.ui.ZWHolderBo;
import cn.tt100.base.R;

@Controller(idFormat = "ili_?", layoutId = R.layout.img_list_item)
public class StudentAdapter extends ZWBaseAdapter<CEO, StudentHolder> {

	public StudentAdapter(Context ctx) {
		super(ctx, StudentHolder.class);
		// TODO Auto-generated constructor stub
	}

	class StudentHolder extends ZWHolderBo {
		TextView nameView;
	}

	@Override
	public void optView(StudentHolder tagHolder, CEO source, int position) {
		// TODO Auto-generated method stub
		tagHolder.nameView.setText(source.name);
	}
}
