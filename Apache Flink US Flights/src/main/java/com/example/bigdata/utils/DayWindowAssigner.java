package com.example.bigdata.utils;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.Collection;
import java.util.Collections;

public class DayWindowAssigner extends WindowAssigner<Object, TimeWindow> {
    private final String delay;
    public long milisecondsInDay = 86400000L;
    public DayWindowAssigner(String delay) {
        this.delay = delay;
    }
    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        long dayStart = timestamp - (timestamp % milisecondsInDay);
        long windowSize = milisecondsInDay;
        return Collections.singletonList(new TimeWindow(dayStart, dayStart + windowSize));
    }
    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
        return delay.equals("A") ? CustomTrigger.create() : EventTimeTrigger.create();
//        return EventTimeTrigger.create();
    }

    @Override
    public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return new TimeWindow.Serializer();
    }

    @Override
    public String toString() {
        return "DayWindowAssigner";
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}
