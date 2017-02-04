package com.ue.army_chess.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.db.LayoutItem;
import com.ue.army_chess.R;
import com.ue.army_chess.widget.LayoutArmyView;
import com.ue.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2017/1/3.
 */

public class EditLayoutAty extends AppCompatActivity{
    private static final String TAG="EditLayoutAty";
    private EditText aelat_name;
    private TextView aelat_ok;
    private LayoutArmyView aelat_board;
    private boolean isEdit;
    private LayoutItem targetItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_aty_edit_layout);

        aelat_name= (EditText) findViewById(R.id.aelat_name);
        aelat_ok= (TextView) findViewById(R.id.aelat_ok);
        aelat_board= (LayoutArmyView) findViewById(R.id.aelat_board);

        findViewById(R.id.aelat_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        aelat_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String layoutName=aelat_name.getText().toString();
                if(TextUtils.isEmpty(layoutName)){
                    layoutName="布局_"+System.currentTimeMillis();
                }
                if(isEdit){
                    boolean updateRes=GameDbManager.getInstance().updateLayoutItem(targetItem.getId(),layoutName,aelat_board.getMyChessList().toString());
                    LogUtil.i(TAG,"update layout result="+updateRes);
                }else{
                    boolean addRes=GameDbManager.getInstance().addLayoutItem(layoutName,aelat_board.getMyChessList().toString());
                    LogUtil.i(TAG,"add layout result="+addRes);
                }
                finish();
            }
        });

        targetItem =getIntent().getParcelableExtra("targetItem");
        if(null==targetItem){
            isEdit=false;
            aelat_board.initChessBoard(null);
        }else{
            isEdit=true;
            aelat_name.setText(targetItem.getName());
            String dataStr=targetItem.getData();
            try {
                JSONArray dataJson=new JSONArray(dataStr);
                List<Integer>myChessList=new ArrayList<>(25);
                for(int i=0;i<25;i++){
                    myChessList.add(dataJson.optInt(i,-1));
                }
                aelat_board.initChessBoard(myChessList);
            } catch (JSONException e) {
                LogUtil.i(TAG,"parse data error:"+e.getMessage());
                isEdit=false;
                targetItem=null;
                aelat_board.initChessBoard(null);
            }
        }
    }
}
