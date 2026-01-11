package com.siyam.travelschedulemanager.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response wrapper for desktop app API responses
 * Desktop returns: { "value": [...], "Count": X }
 */
public class ApiResponseWrapper<T> {
    
    @SerializedName("value")
    private List<T> value;
    
    @SerializedName("Count")
    private int count;

    public List<T> getValue() {
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
