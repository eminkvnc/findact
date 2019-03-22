package com.example.emin.findact.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.emin.findact.InfoCheckboxData;
import com.example.emin.findact.R;

import java.util.ArrayList;

public class CheckBoxAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> selectedList;
    private String [] itemsArray;
    private ArrayList<InfoCheckboxData> infoDataList;

    public CheckBoxAdapter(Context context, ArrayList<String> selectedList, String[] itemsArray, ArrayList<InfoCheckboxData> infoDataList) {
        this.context = context;
        this.selectedList = selectedList;
        this.itemsArray = itemsArray;
        this.infoDataList = infoDataList;
    }

    @Override
    public int getCount() {
        return itemsArray.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view = View.inflate(context, R.layout.custom_checkbox_list ,null );
        TextView subCategoryTextView =new TextView(context);
        final CardView cardView = view.findViewById(R.id.custom_checkbox_list_cv);
        cardView.addView(subCategoryTextView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoDataList.get(position).isClicked){
                    infoDataList.get(position).isClicked = false;
                    cardView.setCardBackgroundColor(Color.TRANSPARENT);
                    selectedList.remove(itemsArray[position]);

                } else {
                    infoDataList.get(position).isClicked = true;
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    if (!selectedList.contains(itemsArray[position])){
                        selectedList.add(itemsArray[position]);
                    }
                }

            }
        });
        if (infoDataList.get(position).isClicked) {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        else {
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }
}
