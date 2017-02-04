package com.hyphenate.easeui.game;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.game.iterface.OnAIChangedListener;
import com.ue.common.util.DisplayUtil;
import com.ue.common.widget.FlowRadioGroup;
import com.ue.common.xsharedpref.XSharedPref;

/**
 * Created by hawk on 2017/1/26.
 */

public class AILevelDialog extends DialogFragment {
    private static final String[] aiNames = new String[]{"新手", "菜鸟", "入门", "棋手", "棋士", "棋圣"};
    private String prefDifficulty;
    private String prefIsFirst;
    private int difficulty;
    private boolean isFirst;
    private int firstImgRes;
    private int lastImgRes;
    private int aiNum;
    private RadioGroup group_who_first;
    private FlowRadioGroup ai_level_panel;
    private OnAIChangedListener aiChangedListener;

    public static AILevelDialog newInstance(int gameFlag, OnAIChangedListener aiChangedListener) {
        AILevelDialog aiLevelDialog = new AILevelDialog();
        Bundle arguments=new Bundle();
        arguments.putInt("gameFlag",gameFlag);
        aiLevelDialog.setArguments(arguments);
        aiLevelDialog.setOnAIChangedListener(aiChangedListener);
        return aiLevelDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, -1);

        Bundle arguments = getArguments();
        if (null == arguments) {
            return;
        }
        int gameFlag=arguments.getInt("gameFlag");
        switch (gameFlag){
            case GameConstants.GAME_AP:
                prefIsFirst=GameConstants.PREF_AP_IS_FIRST;
                prefDifficulty=GameConstants.PREF_AP_DIFFICULTY;
                firstImgRes=R.drawable.game_radio_button_black;
                lastImgRes=R.drawable.game_radio_button_white;
                aiNum = 6;
                break;
            case GameConstants.GAME_CN:
                prefIsFirst=GameConstants.PREF_CC_IS_FIRST;
                prefDifficulty=GameConstants.PREF_CC_DIFFICULTY;
                firstImgRes=R.drawable.game_radio_button_black;
                lastImgRes=R.drawable.game_radio_button_white;
                aiNum = 3;
                break;
            case GameConstants.GAME_GB:
                prefIsFirst=GameConstants.PREF_GB_IS_FIRST;
                prefDifficulty=GameConstants.PREF_GB_DIFFICULTY;
                firstImgRes=R.drawable.game_radio_button_black;
                lastImgRes=R.drawable.game_radio_button_white;
                aiNum = 3;
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.dialog_ai_level, null);

        group_who_first = (RadioGroup) layoutView.findViewById(R.id.group_who_first);
        ai_level_panel = (FlowRadioGroup) layoutView.findViewById(R.id.ai_level_panel);

        View.OnClickListener btnClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                int viewId = v.getId();
                if (viewId == R.id.ok) {
                    isFirst = group_who_first.getCheckedRadioButtonId() == R.id.black;
                    difficulty = ai_level_panel.getCheckedRadioBtnIndex();

                    XSharedPref.putBoolean(prefIsFirst, isFirst);
                    XSharedPref.putInt(prefDifficulty, difficulty);
                    aiChangedListener.onAIChanged(difficulty, isFirst);
                }
            }
        };

        layoutView.findViewById(R.id.ok).setOnClickListener(btnClicker);
        layoutView.findViewById(R.id.cancel).setOnClickListener(btnClicker);

        layoutView.findViewById(R.id.black).setBackgroundResource(firstImgRes);
        layoutView.findViewById(R.id.white).setBackgroundResource(lastImgRes);

        int iLen = aiNum % 3 == 0 ? aiNum / 3 : aiNum / 3 + 1;
        for (int i = 0, index = 0; i < iLen; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(llLayoutParams);
            int jLen = aiNum - index > 2 ? 3 : aiNum - index;
            for (int j = 0; j < jLen; j++) {
                RadioButton radioButton = new RadioButton(getContext());
                LinearLayout.LayoutParams rbLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                rbLayoutParams.topMargin = 15;
                rbLayoutParams.leftMargin = 15;
                radioButton.setPadding(0, 20, 0, 20);
                radioButton.setLayoutParams(rbLayoutParams);
                radioButton.setButtonDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                radioButton.setGravity(Gravity.CENTER);
                radioButton.setText(aiNames[index]);
                radioButton.setBackgroundResource(R.drawable.game_radio_button_ai);
                linearLayout.addView(radioButton);
                index++;
            }
            ai_level_panel.addView(linearLayout);
        }
        return layoutView;
    }

    @Override
    public void onResume() {
        super.onResume();

        isFirst = XSharedPref.getBoolean(prefIsFirst, true);
        difficulty = XSharedPref.getInt(prefDifficulty, 0);
        group_who_first.check(isFirst ? R.id.black : R.id.white);
        ai_level_panel.check(ai_level_panel.getRadioBtn(difficulty).getId());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(getContext()) * 0.9f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    private void setOnAIChangedListener(OnAIChangedListener aiChangedListener) {
        this.aiChangedListener = aiChangedListener;
    }
}
