package fr.tp1.harkame.tp1.activity

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.tp1.harkame.tp1.R

class CustomRecyclerAdapter(private val mDataSet: Array<String>, // Custom Controller used to instruct main activity to update {@link Notification} and/or
        // UI for item selected.
                            private val mController: Controller) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        public val mTextView: TextView

        init {
            mTextView = view.findViewById(R.id.textView) as TextView
        }

        override fun toString(): String {
            return mTextView.text as String
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        viewHolder.mTextView.setOnClickListener { mController.itemSelected(mDataSet[position]) }

        // Replaces content of view with correct element from data set
        viewHolder.mTextView.text = mDataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataSet.size
    }

    companion object {

        private val TAG = "CustomRecyclerAdapter"
    }
}
