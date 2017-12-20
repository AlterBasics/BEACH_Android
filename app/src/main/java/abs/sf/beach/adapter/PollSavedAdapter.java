package abs.sf.beach.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abs.sf.beach.android.R;
import abs.sf.beach.utils.PollActionPerformedListener;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.messaging.PollContent;

public class PollSavedAdapter extends RecyclerView.Adapter<PollSavedAdapter.ViewHolder> {
    private Context context;
    private List<PollContent> polls, pollsOriginal;
    private HashMap<String, Boolean> selectedRecipient;
    private PollActionPerformedListener apl;

    public PollSavedAdapter(Context context, List<PollContent> polls, PollActionPerformedListener apl) {
        this.context = context;
        this.polls = polls;
        this.pollsOriginal = polls;
        selectedRecipient = new HashMap<>();
        this.apl = apl;
    }

    @Override
    public PollSavedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_saved_poll, parent, false);

        return new PollSavedAdapter.ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(final PollSavedAdapter.ViewHolder holder, final int position) {
        PollContent pollContent = polls.get(position);
        holder.tvPollQuestion.setText(pollContent.getQuestion());
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPollQuestion;
        LinearLayout llPollRow;

        ViewHolder(View itemView, int viewType, Context c) {
            super(itemView);

            llPollRow = (LinearLayout) itemView.findViewById(R.id.llPollRow);
            tvPollQuestion = (TextView) itemView.findViewById(R.id.tvPollQuestion);

            llPollRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apl.actionDone("edit_saved_poll", polls.get(getAdapterPosition()).getPollId());
                }
            });
        }
    }

    public void filterData(CharSequence s) {
        if (s.length() == 0) {
            this.polls = pollsOriginal;
        }
        List<PollContent> p = new ArrayList<>();
        for (PollContent data : pollsOriginal) {
            if (data.getQuestion().toLowerCase().contains(s)) {
                p.add(data);
            }
        }
        this.polls = p;
        notifyDataSetChanged();
    }
}
