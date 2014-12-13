package cn.shrek.base.example;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.example.adapter.ImgListAdapter;
import cn.shrek.base.ui.ZWActivity;
import cn.tt100.base.R;

@Controller(idFormat="il_?",layoutId=R.layout.img_list)
public class ListImgActivity extends ZWActivity {

	@AutoInject
	ListView listView;
	
	ImgListAdapter adapter;
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		adapter = new ImgListAdapter(this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				adapter.setSelectPosition(position);
			}
		});
	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

}
