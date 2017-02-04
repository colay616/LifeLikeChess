package com.ue.common.widget.edittext;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * 分割输入框
 *
 * @author Administrator
 */
public class DivisionEditText extends EditText {

    /* 每组的长度 */
    private Integer eachLength = 4;
    /* 分隔符 */
    private String delimiter = " ";

    private String text = "";

    public DivisionEditText(Context context) {
        super(context);
        init();
    }

    public DivisionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public DivisionEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        // 内容变化监听
        this.addTextChangedListener(new DivisionTextWatcher());
        // 获取焦点监听
        this.setOnFocusChangeListener(new DivisionFocusChangeListener());
    }

    /**
     * 文本监听
     *
     * @author Administrator
     */
    private class DivisionTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // 统计个数
            int len = s.length();
            if (len < eachLength)// 长度小于要求的数
                return;
            if (count > 1) {
                return;
            }
            // 如果包含空格，就清除
            char[] chars = s.toString().replace(" ", "").toCharArray();
            len = chars.length;
            // 每4个分组,加上空格组合成新的字符串
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                if (i % eachLength == 0 && i != 0)// 每次遍历到4的倍数，就添加一个空格
                {
                    sb.append(" ");
                    sb.append(chars[i]);// 添加字符
                } else {
                    sb.append(chars[i]);// 添加字符
                }
            }
            // 设置新的字符到文本
            // System.out.println("*************" + sb.toString());
            text = sb.toString();
            setText(text);
            setSelection(text.length());
        }
    }

    /**
     * 获取焦点监听
     *
     * @author Administrator
     */
    private class DivisionFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                // 设置焦点
                setSelection(getText().toString().length());
            }
        }
    }

    /**
     * 得到每组个数
     */
    public Integer getEachLength() {
        return eachLength;
    }

    /**
     * 设置每组个数
     */
    public void setEachLength(Integer eachLength) {
        this.eachLength = eachLength;
    }

    /**
     * 得到间隔符
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 设置间隔符
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}