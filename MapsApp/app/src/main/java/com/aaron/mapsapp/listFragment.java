package com.aaron.mapsapp;

        import android.app.ListFragment;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;


public class listFragment extends ListFragment {
    String[] AndroidOS = new String[] { "Cupcake","Donut","Eclair","Froyo"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, AndroidOS));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }
}
