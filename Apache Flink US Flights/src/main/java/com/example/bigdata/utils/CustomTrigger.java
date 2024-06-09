package com.example.bigdata.utils;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
public class CustomTrigger extends Trigger<Object, TimeWindow> {

    @Override
    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) {
        if (FlinkPropertiesUtil.getDelay().equals("A")) {
            return TriggerResult.FIRE;
        } else {
            ctx.registerEventTimeTimer(window.maxTimestamp());
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }
    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) {
        String delay = FlinkPropertiesUtil.getDelay();
        if (delay.equals("A")) {
            return TriggerResult.CONTINUE;
        } else {
            return time == window.maxTimestamp() ? TriggerResult.FIRE : TriggerResult.CONTINUE;
        }
    }
    @Override
    public void clear(TimeWindow window, TriggerContext ctx) {
        if (FlinkPropertiesUtil.getDelay().equals("A")) {
            ctx.deleteEventTimeTimer(window.maxTimestamp());
        }
    }
    public static CustomTrigger create() {
        return new CustomTrigger();
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void onMerge(TimeWindow window, OnMergeContext ctx) {
        if (!FlinkPropertiesUtil.getDelay().equals("A")) {
            long windowMaxTimestamp = window.maxTimestamp();
            if (windowMaxTimestamp > ctx.getCurrentWatermark()) {
                ctx.registerEventTimeTimer(windowMaxTimestamp);
            }
        }
    }

}