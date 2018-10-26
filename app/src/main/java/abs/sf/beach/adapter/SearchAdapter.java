package abs.sf.beach.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import abs.ixi.client.xmpp.packet.UserSearchData;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.android.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<UserSearchData.Item> search;
    private List<UserSearchData.Item> searchOriginal;

    public SearchAdapter(Context context, List<UserSearchData.Item> search) {
        this.context = context;
        this.search = search;
        this.searchOriginal = search;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        return new ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UserSearchData.Item searchModel = search.get(position);
        holder.ivSearchContactImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));
        holder.tvSearchContactName.setText((CharSequence) searchModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                //intent.putExtra("jid", search.get(position).getJid());
               // intent.putExtra("name", search.get(position).getName());
                //intent.putExtra("from", "Search");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return search.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSearchContactImage;
        public TextView tvSearchContactName;

        public ViewHolder(View itemView, int viewType, Context context) {
            super(itemView);
            itemView.setClickable(true);
            ivSearchContactImage = (ImageView) itemView.findViewById(R.id.ivSearchContactImage);
            tvSearchContactName = (TextView) itemView.findViewById(R.id.tvSearchName);
        }

    }

    private String getUserName(UserSearchData.Item item){

    }
}
