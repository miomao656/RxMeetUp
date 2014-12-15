package com.mio.rxmeetup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mio.rxmeetup.R;
import com.mio.rxmeetup.models.Contributor;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;

public class RepoContributorAdapter extends BaseAdapter implements Observable.Operator<Contributor, Contributor> {

    List<Contributor> contributors = Collections.emptyList();
    Context myContext;

    public RepoContributorAdapter(Context context) {
        myContext = context;
    }

    public void updateContributors(List<Contributor> contributors) {
        if (contributors.size() > 0) {
            this.contributors = contributors;
            this.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return contributors.size();
    }

    @Override
    public Object getItem(int position) {
        return contributors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater li = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = li.inflate(R.layout.git_hub_contributor, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Contributor contributor = (Contributor) getItem(position);
        Picasso.with(myContext).load(contributor.getAvatar_url()).into(holder.contributorImage);
        holder.contributorName.setText(contributor.getLogin());
        holder.contributorContributions.setText(contributor.getContributions());

        return convertView;
    }

    @Override
    public Subscriber<? super Contributor> call(Subscriber<? super Contributor> subscriber) {
        return new Subscriber<Contributor>() {
            @Override
            public void onCompleted() {
                notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Contributor contributor) {
                if (contributor != null) {
                    contributors.add(contributor);
                }
            }
        };
    }

    static class ViewHolder {
        @InjectView(R.id.contributor_avetar)
        ImageView contributorImage;
        @InjectView(R.id.contributor_username)
        TextView contributorName;
        @InjectView(R.id.contributor_contributions)
        TextView contributorContributions;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
