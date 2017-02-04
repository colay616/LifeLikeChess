package com.ue.chess.widget;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.ue.chess.R;
import com.ue.chess.util.ChessUtil;
import com.ue.common.util.DisplayUtil;

/**
 * Created by hawk on 2016/12/9.
 */

public class PromotionDialog extends DialogFragment{
    private RadioGroup promotion_group;
    private OnRoleSelectListener mOnRoleSelectListener;
    private boolean isWhitePromote;
    private int selectedRoleFlag =ChessUtil.ROOK;

    public void setOnRoleSelectListener(OnRoleSelectListener onRoleSelectListener) {
        mOnRoleSelectListener = onRoleSelectListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE,-1);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (DisplayUtil.getScreenWidth(getContext()) * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView=inflater.inflate(R.layout.ic_promotion_dialog,null);

        promotion_group= (RadioGroup) layoutView.findViewById(R.id.promotion_group);
        promotion_group.check(R.id.promotion_rook);
        promotion_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.promotion_rook){
                    selectedRoleFlag =ChessUtil.ROOK;
                    return;
                }
                if(checkedId==R.id.promotion_knight){
                    selectedRoleFlag =ChessUtil.KNIGHT;
                    return;
                }
                if(checkedId==R.id.promotion_bishop){
                    selectedRoleFlag =ChessUtil.BISHOP;
                    return;
                }
                if(checkedId==R.id.promotion_queen){
                    selectedRoleFlag =ChessUtil.QUEEN;
                    return;
                }
            }
        });

        if(isWhitePromote){
            promotion_group.findViewById(R.id.promotion_rook).setBackgroundResource(R.drawable.chess_wrook);
            promotion_group.findViewById(R.id.promotion_knight).setBackgroundResource(R.drawable.chess_wknight);
            promotion_group.findViewById(R.id.promotion_bishop).setBackgroundResource(R.drawable.chess_wbishop);
            promotion_group.findViewById(R.id.promotion_queen).setBackgroundResource(R.drawable.chess_wqueen);
        }else{
            promotion_group.findViewById(R.id.promotion_rook).setBackgroundResource(R.drawable.chess_brook);
            promotion_group.findViewById(R.id.promotion_knight).setBackgroundResource(R.drawable.chess_bknight);
            promotion_group.findViewById(R.id.promotion_bishop).setBackgroundResource(R.drawable.chess_bbishop);
            promotion_group.findViewById(R.id.promotion_queen).setBackgroundResource(R.drawable.chess_bqueen);
        }

        layoutView.findViewById(R.id.promotion_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return layoutView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(null!=mOnRoleSelectListener){
            mOnRoleSelectListener.onRoleSelected(selectedRoleFlag);
        }
    }

    public void show(FragmentManager manager,boolean isWhitePromote) {
        this.isWhitePromote=isWhitePromote;
        super.show(manager,null);
    }

    public interface OnRoleSelectListener{
        void onRoleSelected(int roleFlag);
    }
}
