package ru.toir.mobile.multi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

import ru.toir.mobile.multi.R;

public class OneDayDecorator implements DayViewDecorator {

    private Date date;
    private Drawable drawable;

    public OneDayDecorator(Date date, Drawable drawable) {
        this.date = date;
        this.drawable = drawable;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.getDate().equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        //view.setBackgroundDrawable(drawable);
    }
}