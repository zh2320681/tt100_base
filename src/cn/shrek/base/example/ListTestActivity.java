package cn.shrek.base.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import cn.shrek.base.annotation.AutoInject;
import cn.shrek.base.annotation.Controller;
import cn.shrek.base.example.bean.TagData;
import cn.shrek.base.ui.CustomRule;
import cn.shrek.base.ui.ZWActivity;
import cn.shrek.base.ui.ZWBaseAdapter;
import cn.shrek.base.ui.ZWHolderBo;
import cn.tt100.base.R;

@Controller(layoutId = R.layout.list_test)
public class ListTestActivity extends ZWActivity {
	@AutoInject(idFormat = "lt_?")
	ListView listView;
	
	@AutoInject(idFormat = "lt_?")
	Button reflashBtn;
	
	TestAdapter adapter;

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
//		adapter = new TestAdapter(this,HolderTest.class);
		adapter = new TestAdapter(this,ListTestActivity.TestAdapter.Holder.class);
		listView.setAdapter(adapter);
		
		reflashBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged(new CustomRule<TagData>() {

					@Override
					public boolean ruleJudge(TagData orgin) {
						// TODO Auto-generated method stub
						if(orgin.age < 50){
							orgin.isCheck = !orgin.isCheck;
							return true;
						}
						return false;
					}
				});
			}
		});
	}

	@Override
	protected void addListener() {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				adapter.getItem(position).name = "1111111111";
//				long before = System.currentTimeMillis();
//				adapter.notifyDataSetChanged(position);
//				long after = System.currentTimeMillis();
//				System.out.println("单刷新消耗时间==========>"+(after - before));
				
//				long before = System.currentTimeMillis();
//				adapter.notifyDataSetChanged();
//				long after = System.currentTimeMillis();
//				System.out.println("全刷新消耗时间==========>"+(after - before));
				
				long before = System.currentTimeMillis();
				adapter.notifyDataSetChanged(new TagData());
				long after = System.currentTimeMillis();
				System.out.println("单刷数据消耗时间==========>"+(after - before));
			}
		});
	}

	@Override
	public void notifyObserver(Object oldObj, Object newObj) {
		// TODO Auto-generated method stub

	}

	//ZWBaseAdapter<String, ListTestActivity.TestAdapter.Holder>
//	@Controller(layoutId = R.layout.list_item, idFormat = "li_?")
//	class TestAdapter extends
//			ZWBaseAdapter<String, HolderTest> {
//
//		public TestAdapter(Context ctx, Class<HolderTest> holderClazz) {
//			super(ctx, holderClazz);
//			// TODO Auto-generated constructor stub
//		}
//
//		@Override
//		public Collection<String> initData() {
//			// TODO Auto-generated method stub
//			String[] s = new String[] { "张三", "李四", "王五", "赵六", "田七", "五八",
//					"马九","张三1", "李四1", "王五1", "赵六1", "田七1", "五八1",
//					"马九1" };
//			return Arrays.asList(s);
//		}
//
//		@Override
//		public void optView(HolderTest tagHolder, String source, int position) {
//			// TODO Auto-generated method stub
//			tagHolder.infoView.setText(source);
//			tagHolder.checkBox.setChecked(!tagHolder.checkBox.isChecked());
//			for(int i = 0;i<100000000;i++){}
//		}
//	}
	
	
		@Controller(layoutId = R.layout.list_item, idFormat = "li_?")
		class TestAdapter extends
				ZWBaseAdapter<TagData, ListTestActivity.TestAdapter.Holder> {

			public TestAdapter(Context ctx, Class<Holder> holderClazz) {
				super(ctx, holderClazz);
				// TODO Auto-generated constructor stub
			}

			@Override
			public Collection<TagData> initData() {
				// TODO Auto-generated method stub
				List<TagData> list = new ArrayList<TagData>();
				for(int i = 0 ;i<20;i++){
					list.add(new TagData());
				}
				return list;
			}

			@Override
			public void optView(Holder holder, TagData data, int position) {
				// TODO Auto-generated method stub
				holder.infoView.setText(data.name);
				holder.ageView.setText("年龄:"+data.age);
				holder.checkBox.setChecked(!data.isCheck);
				for(int i = 0;i<100000000;i++){}
			}
			
			class Holder extends ZWHolderBo{
				TextView infoView,ageView;
				CheckBox checkBox;
			}
		}
	

}
