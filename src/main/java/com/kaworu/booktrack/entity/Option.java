package com.kaworu.booktrack.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Option {
    private Object value;
    private String label;

    public Option(Object value, String label) {
        this.value = value;
        this.label = label;
    }
}
