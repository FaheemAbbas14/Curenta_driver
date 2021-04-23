package com.curenta.driver.adaptors;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.curenta.driver.R;

import org.zakariya.stickyheaders.SectioningAdapter;


import java.util.ArrayList;

/**
 * SimpleDemoAdapter, just shows demo data
 */
public class EarningListAdapter extends SectioningAdapter {

    static final String TAG = EarningListAdapter.class.getSimpleName();
    static final boolean USE_DEBUG_APPEARANCE = false;

    public static class Section {
        public int index;
        public int copyCount;
        public String header;
        public String footer;
        public ArrayList<Item> items = new ArrayList<>();


    }

    public static class Item {
        public String title;
        public String value;

        public Item(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {
        TextView textView;
        TextView adapterPositionTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

            if (!EarningListAdapter.this.showAdapterPositions) {
                adapterPositionTextView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final int section = EarningListAdapter.this.getSectionForAdapterPosition(adapterPosition);
            final int item = EarningListAdapter.this.getPositionOfItemInSection(section, adapterPosition);
//            if (v == cloneButton) {
//                EarningListAdapter.this.onCloneItem(section, item);
//            } else if (v == deleteButton) {
//                EarningListAdapter.this.onDeleteItem(section, item);
//            }
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder implements View.OnClickListener {
        TextView textView;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);


        }

        void updateSectionCollapseToggle(boolean sectionIsCollapsed) {
            @DrawableRes int id = sectionIsCollapsed
                    ? R.drawable.ic_expand_more_black_24dp
                    : R.drawable.ic_expand_less_black_24dp;


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final int section = EarningListAdapter.this.getSectionForAdapterPosition(position);
//            if (v == cloneButton) {
//                EarningListAdapter.this.onCloneSection(section);
//            } else if (v == deleteButton) {
//                EarningListAdapter.this.onDeleteSection(section);
//            } else if (v == collapseButton) {
//                EarningListAdapter.this.onToggleSectionCollapse(section);
//                updateSectionCollapseToggle(EarningListAdapter.this.isSectionCollapsed(section));
//            }
        }
    }

    public class FooterViewHolder extends SectioningAdapter.FooterViewHolder {
        TextView textView;
        TextView adapterPositionTextView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

            if (!EarningListAdapter.this.showAdapterPositions) {
                adapterPositionTextView.setVisibility(View.GONE);
            }
        }
    }


    public ArrayList<Section> sections = new ArrayList<>();
    boolean showModificationControls;
    boolean showCollapsingSectionControls;
    boolean showAdapterPositions;
    boolean hasFooters;

    public EarningListAdapter(int numSections, int numItemsPerSection, boolean hasFooters, boolean showModificationControls, boolean showCollapsingSectionControls, boolean showAdapterPositions) {
        this.showModificationControls = showModificationControls;
        this.showCollapsingSectionControls = showCollapsingSectionControls;
        this.showAdapterPositions = showAdapterPositions;
        this.hasFooters = hasFooters;

    }


    void onToggleSectionCollapse(int sectionIndex) {
        Log.d(TAG, "onToggleSectionCollapse() called with: " + "sectionIndex = [" + sectionIndex + "]");
        setSectionIsCollapsed(sectionIndex, !isSectionCollapsed(sectionIndex));
    }

    void onDeleteSection(int sectionIndex) {
        Log.d(TAG, "onDeleteSection() called with: " + "sectionIndex = [" + sectionIndex + "]");
        sections.remove(sectionIndex);
        notifySectionRemoved(sectionIndex);
    }

    public void setData(ArrayList<Section> sections) {
        this.sections = sections;
    }






    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.get(sectionIndex).items.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return !TextUtils.isEmpty(sections.get(sectionIndex).header);
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return !TextUtils.isEmpty(sections.get(sectionIndex).footer);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_simple_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_simple_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int footerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_simple_footer, parent, false);
        return new FooterViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        Section s = sections.get(sectionIndex);
        ItemViewHolder ivh = (ItemViewHolder) viewHolder;
        ivh.textView.setText(s.items.get(itemIndex).title);
        ivh.adapterPositionTextView.setText(s.items.get(itemIndex).value);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section s = sections.get(sectionIndex);
        HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;

        if (USE_DEBUG_APPEARANCE) {
            hvh.textView.setText(pad(sectionIndex * 2) + s.header);
            viewHolder.itemView.setBackgroundColor(0x55FF9999);
        } else {
            hvh.textView.setText(s.header);
        }

        hvh.updateSectionCollapseToggle(isSectionCollapsed(sectionIndex));
    }

    @Override
    public void onBindGhostHeaderViewHolder(SectioningAdapter.GhostHeaderViewHolder viewHolder, int sectionIndex) {
        if (USE_DEBUG_APPEARANCE) {
            viewHolder.itemView.setBackgroundColor(0xFF9999FF);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindFooterViewHolder(SectioningAdapter.FooterViewHolder viewHolder, int sectionIndex, int footerType) {
        Section s = sections.get(sectionIndex);
        FooterViewHolder fvh = (FooterViewHolder) viewHolder;
        fvh.textView.setText(s.footer);
        fvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionFooter(sectionIndex)));
    }

    private String pad(int spaces) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            b.append(' ');
        }
        return b.toString();
    }

}
