package com.kaworu.booktrack.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Option {
    private String value;
    private String label;

    public Option(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
