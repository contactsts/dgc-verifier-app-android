package ro.sts.dgc.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class StaticViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Make sure all are loaded at once
        val childrenCount = childCount
        offscreenPageLimit = childrenCount - 1

        // Attach the adapter
        adapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                return container.getChildAt(position)
            }

            override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
                return arg0 === arg1
            }

            override fun getCount(): Int {
                return childrenCount
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            }
        }
    }

}