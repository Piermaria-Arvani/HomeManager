package it.unitn.disi.homemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piermaria on 28/05/17.
 */

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private ArrayList<Expense> mExpense;
    private Context mContext = null;
    private int mLayout;

    public ExpenseAdapter(Context context, int layoutId, ArrayList<Expense> expenses) {
        super(context, layoutId, expenses);
        mExpense = expenses;
        mContext = context;
        mLayout = layoutId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Expense expense = mExpense.get(position);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(expense.getName());

        TextView money = (TextView) view.findViewById(R.id.money);
        String temp = expense.getMoney() + "€";
        money.setText(temp);

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(expense.getDate());

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(expense.getExpenseDescription());



        return view;
    }


    public void reset() {
        mExpense.clear();
        notifyDataSetChanged();
    }


}

