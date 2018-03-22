package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class FilterButton extends ImageObserverButton implements ActionListener {
    private FilteredImage filteredImage;
    private boolean needToInitFilter = true;
    private Filter filter;

    public FilterButton(String filterName, FilteredImage filteredImage) {
        super(filterName, filteredImage);
        this.filteredImage = filteredImage;
        addActionListener(this);
    }

    public boolean isNeedToInitFilter() {
        return needToInitFilter;
    }

    public FilterButton setNeedToInitFilter(boolean needToInitFilter) {
        this.needToInitFilter = needToInitFilter;
        return this;
    }

    public Filter getFilter() {
        return filter;
    }

    public FilterButton setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public abstract boolean initFilter();
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (needToInitFilter) {
            if (initFilter()) {
                filteredImage.setAppliedFilter(filter);
            }
        } else {
            filteredImage.setAppliedFilter(filter);
        }
    }
}
