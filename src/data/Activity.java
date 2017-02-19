package data;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Melanie Hartmann (msg systems ag) 20.11.2016.
 */
public class Activity {
    private DateTime endDate;
    private ActivityType activity;

    public Activity(){
    }

    public Activity(DateTime endDate, ActivityType activity) {
        this.endDate = endDate;
        this.activity = activity;
    }

    public void setActivity(ActivityType activity) {
        this.activity = activity;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }



    public DateTime getEndDate(){
        return endDate;
    }

    public ActivityType getActivity(){
        return activity;
    }

}
