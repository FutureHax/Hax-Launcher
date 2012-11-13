package com.t3hh4xx0r.haxlauncher.menu;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

public class ReorderFragment extends ListFragment {

    ArrayAdapter<String> adapter;
	static ArrayList<String> selectedList = new ArrayList<String>();

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        String item = adapter.getItem(from);
                        adapter.remove(item);
                        adapter.insert(item, to);
                    }
                }
            };

    private DragSortListView.RemoveListener onRemove = 
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    adapter.remove(adapter.getItem(which));
                }
            };

    protected int getLayout() {
        return R.layout.drag_list;
    }

    protected int getItemLayout() {
    	return R.layout.list_item_handle_left;
    }

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean dragEnabled = true;

    public static ReorderFragment newInstance(int headers, int footers) {
    	ReorderFragment f = new ReorderFragment();
        return f;
    }

    public DragSortController getController() {
        return mController;
    }


    public void setListAdapter() {
    	parseSelected(getActivity());
        adapter = new ArrayAdapter<String>(getActivity(), getItemLayout(), R.id.text, selectedList);
        setListAdapter(adapter);
    }

    public static void parseSelected(Context ctx) {
		selectedList.clear();
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllHotseats();
    	Log.d("CHECKING EM ALL", "YA!");

		while (c.moveToNext()) {
			if (!selectedList.contains(c.getString(c.getColumnIndex("name"))))
			selectedList.add(c.getString(c.getColumnIndex("name")));
		}
	}

    public DragSortController buildController(DragSortListView dslv) {    
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setDragInitMode(dragStartMode);
        return controller;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDslv = (DragSortListView) inflater.inflate(getLayout(), container, false);

        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        return mDslv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDslv = (DragSortListView) getListView(); 
        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);
        setListAdapter();
    }
}

