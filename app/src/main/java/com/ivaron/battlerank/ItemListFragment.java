package com.ivaron.battlerank;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.ivaron.battlerank.Adapters.BattleListAdapter;
import com.ivaron.battlerank.dummy.DummyContent;
import com.ivaron.battlerank.models.Battle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ArrayList<Battle> items;
    private ListCallbacks mCallbacks;
    private BattleListAdapter adapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<Battle>();
        Battle b = new Battle();
        b.name = "test1";
        b.id = 1;
        items.add(b);
        adapter = new BattleListAdapter(getActivity(), items);
        setListAdapter(adapter);
        DataRequest dr = new DataRequest();
        dr.execute("/battles");

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof ListCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (ListCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        if(mCallbacks != null){
            mCallbacks.onItemSelected(items.get(position).id);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class DataRequest extends HttpJsonRequest{

        @Override
        protected void onPostExecute(String result) {
            ArrayList<Battle> newItems = new ArrayList<Battle>();
            if(result != null){
                try{
                    JSONArray battles = new JSONArray(result);
                    for(int c = 0; c < battles.length(); c++){
                        Battle b = new Battle();
                        JSONObject battle = battles.getJSONObject(c);
                        b.name = battle.getString("name");
                        b.id = battle.getInt("id");
                        b.completed = CompletedBattles.getInstance().isBattleCompleted(b.id);
                        b.amountOfImages = battle.getJSONArray("enteries").length();
                        newItems.add(b);
                    }
                    items.clear();
                    items.addAll(newItems);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Battle b = new Battle();
                b.name = getString(R.string.connection_error);
                newItems.add(b);
            }
        }
    }
}
