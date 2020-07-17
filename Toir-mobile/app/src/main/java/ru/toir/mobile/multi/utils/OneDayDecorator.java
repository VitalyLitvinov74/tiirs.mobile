package ru.toir.mobile.multi.utils;

import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class OneDayDecorator implements DayViewDecorator {

    private Date date;
    private int color;

    public OneDayDecorator(int color, Date date) {
        this.color = color;
        this.date = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.getDate().equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(color));
    }
}