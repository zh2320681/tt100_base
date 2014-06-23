package cn.shrek.base.example;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.LayoutSelector;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.ui.ZWHolderBo;
import cn.tt100.base.R;

@LayoutSelector(id = R.layout.list_test)
public class ListTestActivity extends ZWActivity {
	@AutoInject(idFormat = "lt_?")
	ListView listView;

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		listView.setAdapter(new TestAdapter());
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	class TestAdapter extends BaseAdapter {
		String[] s = new String[] { "张三", "李四", "王五", "赵六", "田七", "五八" ,"马九"};

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return s.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return s[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder mHolder = Holder.newInstance(getApplicationContext(),
					ListTestActivity.TestAdapter.Holder.class,
					TestAdapter.this, R.layout.list_item, convertView, "li_?");
			mHolder.infoView.setText(position + ":" + s[position]);
			return mHolder.getRootView();
		}

		class Holder extends ZWHolderBo {
			TextView infoView;
		}
		
	}
	
	
}


