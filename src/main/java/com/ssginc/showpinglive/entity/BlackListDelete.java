package com.ssginc.showpinglive.entity;

import lombok.Getter;

@Getter
public enum BlackListDelete {

    Y("Y"),
    N("N");

    private final String blackListDelete;

    BlackListDelete(String blackListDelete) {
        this.blackListDelete = blackListDelete;
    }

}
