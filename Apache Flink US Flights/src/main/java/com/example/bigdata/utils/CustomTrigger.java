package com.example.bigdata.utils;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Objects;

public class CustomTrigger extends Trigger<Object, TimeWindow> {

    @Override
    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) {
        if (Objects.equals(FlinkPropertiesUtil.getDelay(), "A")) {
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
        if (Objects.equals(delay, "A")) {
            return TriggerResult.CONTINUE;
        } else {
            return time == window.maxTimestamp() ? TriggerResult.FIRE : TriggerResult.CONTINUE;
        }
    }
    @Override
    public void clear(TimeWindow window, TriggerContext ctx) {
        if (Objects.equals(FlinkPropertiesUtil.getDelay(), "A")) {
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
        if (!Objects.equals(FlinkPropertiesUtil.getDelay(), "A")) {
            long windowMaxTimestamp = window.maxTimestamp();
            if (windowMaxTimestamp > ctx.getCurrentWatermark()) {
                ctx.registerEventTimeTimer(windowMaxTimestamp);
            }
        }
    }

}
