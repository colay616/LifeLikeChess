package com.hyphenate.easeui.game.db;

/**
 * Created by hawk on 2017/1/4.
 */

public interface DataParser<T> {
    T parse(int id,String data);
}
