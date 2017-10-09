package com.blockchain.store.playmarket.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playmarket.R;
import com.blockchain.store.playmarket.crypto.CryptoUtils;
import com.blockchain.store.playmarket.data.content.AppContent;
import com.blockchain.store.playmarket.data.types.EthereumPrice;
import com.blockchain.store.playmarket.utilities.data.ClipboardUtils;
import com.blockchain.store.playmarket.utilities.data.ImageUtils;
import com.blockchain.store.playmarket.utilities.drawable.HamburgerDrawable;
import com.blockchain.store.playmarket.utilities.net.APIUtils;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

import org.ethereum.geth.Account;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import io.ethmobile.ethdroid.KeyManager;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean mTwoPane;

    private AppContent content;
    private AppContent content1;

    private RecyclerView recyclerViewTop;
    private RecyclerView recyclerView2;
    private LinearLayoutManager layoutManager;
    private ProgressBar loadingSpinner;
    public MenuItem balanceMenuItem;
    private KeyManager keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerArrowDrawable(new HamburgerDrawable(this));
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        balanceMenuItem = navigationView.getMenu().getItem(0);

        setupKeyManager();
        displayBalanceAlert();
        setupRecyclersAndFetchContent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_balance) {
            showAddFundsDialog();
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_account) {
            goToAccountsPage();
        } else if (id == R.id.nav_add_funds) {
            showAddFundsDialog();
        } else if (id == R.id.nav_ico) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToAccountsPage() {
        Intent myIntent=new Intent(getApplicationContext(), AccountManagementActivity.class );
        startActivityForResult(myIntent,0);
    }

    public void showAddFundsDialog() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.show_address_dialog);

        final TextView addressTextView = (TextView) d.findViewById(R.id.addressTextView);
        try {
            addressTextView.setText(keyManager.getAccounts().get(0).getAddress().getHex());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView balanceTextView = (TextView) d.findViewById(R.id.balanceText);
        balanceTextView.setText(APIUtils.api.balance.getDisplayPrice());

        Button close_btn = (Button) d.findViewById(R.id.close_button);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                d.dismiss();
            }
        });

        Button copyAddressButton = (Button) d.findViewById(R.id.copyAddressButton);
        copyAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyToClipboard(getApplicationContext(), addressTextView.getText().toString());
                showCopiedAlert();
            }
        });

        d.show();
    }

    private void showCopiedAlert() {
        Toast.makeText(getApplicationContext(), "Address Copied!",
                Toast.LENGTH_LONG).show();
    }

    protected void setupKeyManager() {
        keyManager = CryptoUtils.setupKeyManager(getFilesDir().getAbsolutePath());
    }

    protected void hideLoadingSpinner() {
        loadingSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.GONE);
    }

    public void displayBalanceAlert() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String balance = String.valueOf(APIUtils.api.getBalance(keyManager.getAccounts().get(0).getAddress().getHex()));

                    APIUtils.api.balance = new EthereumPrice(balance);

                    final String ether = balance.substring(0, balance.length() - 18);
                    new Handler(Looper.getMainLooper()).post(new Runnable () {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Balance: " + ether + "." + balance.substring(ether.length(), balance.length() - 16),
                                    Toast.LENGTH_LONG).show();

                            balanceMenuItem.setTitle(APIUtils.api.balance.getDisplayPrice());

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, final AppContent content) {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MainMenuActivity.SimpleItemRecyclerViewAdapter(content.ITEMS));
    }

    private InfiniteScrollListener createInfiniteScrollListener(final AppContent AppContent, final RecyclerView recyclerView) {
        return new InfiniteScrollListener(AppContent.FETCH_COUNT, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                // load your items here
                // logic of loading items will be different depending on your specific use case
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AppContent.loadMoreItems();
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
                                refreshView(recyclerView, new MainMenuActivity.SimpleItemRecyclerViewAdapter(AppContent.ITEMS), firstVisibleItemPosition - AppContent.FETCH_COUNT );
                            }
                        });
                    }
                });

                thread.start();
            }
        };
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<MainMenuActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<AppContent.AppItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<AppContent.AppItem> items) {
            mValues = items;
        }

        @Override
        public MainMenuActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_list_content, parent, false);
            return new MainMenuActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MainMenuActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIconView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_snapchat));
            holder.mContentView.setText(mValues.get(position).name);
            holder.mIconView.setImageBitmap(ImageUtils.getBitmapFromBase64(mValues.get(position).icon));
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
            public AppContent.AppItem mItem;

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

    private void setupRecyclersAndFetchContent() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                content = new AppContent("1");
                content1 = new AppContent("2");

                while (!content.READY) {
                }
                while (!content1.READY) {
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewTop = (RecyclerView) findViewById(R.id.app_list2);
                        assert recyclerViewTop != null;
                        setupRecyclerView(recyclerViewTop, content);

                        recyclerView2 = (RecyclerView) findViewById(R.id.app_list);
                        assert recyclerView2 != null;
                        setupRecyclerView(recyclerView2, content1);
                        recyclerViewTop.setAdapter(new MainMenuActivity.SimpleItemRecyclerViewAdapter(content.ITEMS));
                        recyclerView2.setAdapter(new MainMenuActivity.SimpleItemRecyclerViewAdapter(content1.ITEMS));

                        recyclerViewTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                int visibleThreshold = 6;
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
                                int visibleThreshold = 6;
                                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                                if (!content1.IS_LOADING && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    createInfiniteScrollListener(content1, recyclerView2).onScrolledToEnd(lastVisibleItem);
                                    content1.IS_LOADING = true;
                                }
                            }
                        });

                        hideLoadingSpinner();
                    }
                });
            }
        });

        thread.start();
    }
}
