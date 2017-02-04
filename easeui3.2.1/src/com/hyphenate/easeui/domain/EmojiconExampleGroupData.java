package com.hyphenate.easeui.domain;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon.Type;

import java.util.Arrays;

public class EmojiconExampleGroupData {
    
    private static int[] icons = new int[]{
        R.drawable.icon_002_cover,
        R.drawable.icon_007_cover,  
        R.drawable.icon_010_cover,  
        R.drawable.icon_012_cover,  
        R.drawable.icon_013_cover,  
        R.drawable.icon_018_cover,  
        R.drawable.icon_019_cover,  
        R.drawable.icon_020_cover,  
        R.drawable.icon_021_cover,  
        R.drawable.icon_022_cover,  
        R.drawable.icon_024_cover,  
        R.drawable.icon_027_cover,  
        R.drawable.icon_029_cover,  
        R.drawable.icon_030_cover,  
        R.drawable.icon_035_cover,  
        R.drawable.icon_040_cover,  
    };
    
    private static int[] bigIcons = new int[]{
        R.drawable.icon_002,  //泪流满面
        R.drawable.icon_007, //得意忘形
        R.drawable.icon_010,  //仙飘飘
        R.drawable.icon_012,//抖胸
        R.drawable.icon_013,//挥泪送别
        R.drawable.icon_018,  //奋发向上
        R.drawable.icon_019,  //爱死你了
        R.drawable.icon_020, //睡觉去了
        R.drawable.icon_021,  //春心荡漾
        R.drawable.icon_022,  //不行 不行
        R.drawable.icon_024,  //你醒醒吧
        R.drawable.icon_027,  //生日快乐
        R.drawable.icon_029,  //趾高气昂
        R.drawable.icon_030,  //挠痒痒
        R.drawable.icon_035,  //悠悠哉
        R.drawable.icon_040,  //跳跳跳
    };
    
    
    private static final EaseEmojiconGroupEntity DATA = createData();
    
    private static EaseEmojiconGroupEntity createData(){
        EaseEmojiconGroupEntity emojiconGroupEntity = new EaseEmojiconGroupEntity();
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        String[]iconName=new String[]{
                "泪流满面","得意忘形","仙飘飘","抖胸",
                "挥泪送别","奋发向上","爱死你了","睡觉去了",
                "春心荡漾","不行 不行","你醒醒吧","生日快乐",
                "趾高气昂","挠痒痒","悠悠哉","跳跳跳"};
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], null, Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            //you can replace this to any you want
            datas[i].setName(iconName[i]);
            datas[i].setIdentityCode("em"+ (1000+i+1));
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.ee_2);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }
    
    
    public static EaseEmojiconGroupEntity getData(){
        return DATA;
    }
}
