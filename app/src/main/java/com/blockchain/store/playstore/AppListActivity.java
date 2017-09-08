package com.blockchain.store.playstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playstore.dummy.DummyContent;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.blockchain.store.playstore.R.attr.layoutManager;

/**
 * An activity representing a list of Apps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AppDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AppListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private DummyContent content;
    private DummyContent content1;

    private RecyclerView recyclerViewTop;
    private RecyclerView recyclerView2;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                content = new DummyContent("1");
                content1 = new DummyContent("2");

                while (!content.READY) {}
                while (!content1.READY) {}

                new Handler(Looper.getMainLooper()).post(new Runnable () {
                    @Override
                    public void run() {
                        recyclerViewTop = (RecyclerView) findViewById(R.id.app_list2);
                        assert recyclerViewTop != null;
                        setupRecyclerView(recyclerViewTop, content);

                        recyclerView2 = (RecyclerView) findViewById(R.id.app_list);
                        assert recyclerView2 != null;
                        setupRecyclerView(recyclerView2, content1);
                        recyclerViewTop.setAdapter(new SimpleItemRecyclerViewAdapter(content.ITEMS));
                        recyclerView2.setAdapter(new SimpleItemRecyclerViewAdapter(content1.ITEMS));

                        recyclerViewTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                int visibleThreshold = 4;
                                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                                if (!content.IS_LOADING && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    createInfiniteScrollListener(content, recyclerViewTop).onScrolledToEnd(lastVisibleItem);
                                    content.IS_LOADING = true;
                                }
                            }
                        });

                        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                int visibleThreshold = 4;
                                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                                if (!content1.IS_LOADING && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    createInfiniteScrollListener(content1, recyclerView2).onScrolledToEnd(lastVisibleItem);
                                    content1.IS_LOADING = true;
                                }
                            }
                        });
                    }
                });
            }
        });

        thread.start();

        if (findViewById(R.id.app_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        displayBalanceAlert();
    }

    @Override
    public void onBackPressed() {}

    public void displayBalanceAlert() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String balance = String.valueOf(APIUtils.api.getBalance(CryptoUtils.ethdroid.getMainAccount().getAddress().getHex().toString()));

                    final String ether = balance.substring(0, balance.length() - 18);
                    new Handler(Looper.getMainLooper()).post(new Runnable () {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Balance: " + ether + "." + balance.substring(ether.length(), balance.length() - 16),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, final DummyContent content) {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(content.ITEMS));
    }

    private InfiniteScrollListener createInfiniteScrollListener(final DummyContent dummyContent, final RecyclerView recyclerView) {
        return new InfiniteScrollListener(20, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                // load your items here
                // logic of loading items will be different depending on your specific use case
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dummyContent.loadMoreItems();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable () {
                            @Override
                            public void run() {
                                // when new items are loaded, combine old and new items, pass them to your adapter
                                // and call refreshView(...) method from InfiniteScrollListener class to refresh RecyclerView
                                refreshView(recyclerView, new SimpleItemRecyclerViewAdapter(dummyContent.ITEMS), firstVisibleItemPosition);
                            }
                        });
                    }
                });

                thread.start();
            }
        };
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIconView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_snapchat));
            holder.mContentView.setText(mValues.get(position).content);
            holder.mPriceView.setText(String.valueOf(new EthereumPrice(mValues.get(position).price).getDisplayPrice()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(AppDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        AppDetailFragment fragment = new AppDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.app_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, AppDetailActivity.class);
                        intent.putExtra("item", holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mIconView;
            public final TextView mPriceView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIconView = (ImageView) view.findViewById(R.id.imageView);
                mContentView = (TextView) view.findViewById(R.id.content);
                mPriceView = (TextView) view.findViewById(R.id.Price);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
