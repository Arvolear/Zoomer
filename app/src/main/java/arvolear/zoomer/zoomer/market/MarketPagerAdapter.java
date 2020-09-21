package arvolear.zoomer.zoomer.market;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

public class MarketPagerAdapter extends PagerAdapter
{
    private AppCompatActivity activity;
    private MarketController controller;

    public MarketPagerAdapter(AppCompatActivity activity, MarketController controller)
    {
        this.activity = activity;
        this.controller = controller;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup collection, int position)
    {
        MarketPage page = controller.getPages().get(position);
        collection.addView(page);

        return page;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view)
    {
        collection.removeView((View) view);
    }

    @Override
    public int getCount()
    {
        return controller.getPages().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }
}
