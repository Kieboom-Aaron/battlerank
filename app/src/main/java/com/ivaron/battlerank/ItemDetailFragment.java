package com.ivaron.battlerank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ivaron.battlerank.dummy.DummyContent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private LinearLayout pair1, pair2;
    private BattleGame battleGame;
    private View viewHolder;
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            DataRequest dr = new DataRequest();
            dr.execute("/battles/"+getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        pair1 = (LinearLayout)rootView.findViewById(R.id.pair1);
        pair2 = (LinearLayout)rootView.findViewById(R.id.pair2);
        viewHolder = rootView;
        showSpinner();
        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.content);
//        }

        return rootView;
    }

    private void showSpinner(){
        viewHolder.findViewById(R.id.progressbar_view).setVisibility(View.VISIBLE);
        viewHolder.findViewById(R.id.viewholder).setVisibility(View.GONE);
    }

    private void hideSpinner(){
        viewHolder.findViewById(R.id.progressbar_view).setVisibility(View.GONE);
        viewHolder.findViewById(R.id.viewholder).setVisibility(View.VISIBLE);
    }

    private class DataRequest extends HttpJsonRequest{

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                battleGame = new BattleGame(result, getResources().getDrawable(R.drawable.ic_launcher));
                battleGame.startBattle(pair1, pair2);
                hideSpinner();
            }
        }
    }
}
