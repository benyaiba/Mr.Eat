package net.yaiba.eat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {

	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;

	private ArrayList<Category> mListData;
	private LayoutInflater mInflater;


	public CategoryAdapter(Context context, ArrayList<Category> pData) {
		mListData = pData;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		int count = 0;

		if (null != mListData) {
			//  所有分类中item的总和是ListVIew  Item的总个数
			for (Category category : mListData) {
				count += category.getItemCount();
			}
		}

		return count;
	}

	@Override
	public Object getItem(int position) {

		// 异常情况处理
		if (null == mListData || position <  0|| position > getCount()) {
			return null;
		}

		// 同一分类内，第一个元素的索引值
		int categroyFirstIndex = 0;

		for (Category category : mListData) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categroyFirstIndex;
			// item在当前分类内
			if (categoryIndex < size) {
				return  category.getItem( categoryIndex );
			}

			// 索引移动到当前分类结尾，即下一个分类第一个元素索引
			categroyFirstIndex += size;
		}

		return null;
	}

	@Override
	public int getItemViewType(int position) {
		// 异常情况处理
		if (null == mListData || position <  0|| position > getCount()) {
			return TYPE_ITEM;
		}


		int categroyFirstIndex = 0;

		for (Category category : mListData) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex == 0) {
				return TYPE_CATEGORY_ITEM;
			}

			categroyFirstIndex += size;
		}

		return TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int itemViewType = getItemViewType(position);
		switch (itemViewType) {
			case TYPE_CATEGORY_ITEM:
				if (null == convertView) {
					convertView = mInflater.inflate(R.layout.listview_item_header, null);
				}

				TextView textView = (TextView) convertView.findViewById(R.id.header);
				String  itemValue = (String) getItem(position);
				textView.setText( itemValue );
				break;

			case TYPE_ITEM:
				ViewHolder viewHolder = null;
				if (null == convertView) {

					convertView = mInflater.inflate(R.layout.listview_item, null);

					viewHolder = new ViewHolder();
					viewHolder.food_name = (TextView) convertView.findViewById(R.id.food_name);
                    viewHolder.eat_time = (TextView) convertView.findViewById(R.id.eat_time);
                    //viewHolder.create_time = (TextView) convertView.findViewById(R.id.create_time);

					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				// 绑定数据
                String info = (String)getItem(position);
                String[] infoArr =info.split("_///_");

                viewHolder.food_name.setText(infoArr[1]);
                viewHolder.eat_time.setText(infoArr[2]);
                //viewHolder.create_time.setText(infoArr[4]);
				break;
		}

		return convertView;
	}


	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != TYPE_CATEGORY_ITEM;
	}


	private class ViewHolder {
		TextView content;
		ImageView contentIcon;

        TextView food_name;
        TextView eat_time;
        TextView create_time;
	}

}