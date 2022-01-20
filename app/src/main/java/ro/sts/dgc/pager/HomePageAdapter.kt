package ro.sts.dgc.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePageAdapter(fragment: Fragment, private val itemsCount: Int) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return HomePagerFragment.getInstance(position)
    }
}