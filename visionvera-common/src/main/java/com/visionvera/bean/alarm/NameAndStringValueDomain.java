package com.visionvera.bean.alarm;

/**
 * Created by always on 2018/6/6.
 */
public class NameAndStringValueDomain {
    private String name;
    private String value;

    public NameAndStringValueDomain() {
    }

    public NameAndStringValueDomain(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
