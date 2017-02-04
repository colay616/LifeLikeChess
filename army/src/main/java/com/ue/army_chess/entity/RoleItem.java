package com.ue.army_chess.entity;

/**
 * Created by hawk on 2017/1/4.
 */

public class RoleItem{
    private int flag;
    private String name;

    public RoleItem(int flag,String name){
        this.flag=flag;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {

        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
