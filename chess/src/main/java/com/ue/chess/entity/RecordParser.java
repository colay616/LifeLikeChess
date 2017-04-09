package com.ue.chess.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hyphenate.easeui.game.db.DataParser;

import java.lang.reflect.Type;

/**
 * Created by hawk on 2017/1/9.
 */

public class RecordParser implements DataParser<ChessRecord> {
    @Override
    public ChessRecord parse(int id, String data) {
        Gson gson=new GsonBuilder().registerTypeAdapter(ChessRecord.class,new ChessRecordSerializer()).create();
        ChessRecord chessRecord= gson.fromJson(data,ChessRecord.class);
        chessRecord.id=id;
        return chessRecord;
    }

    private class ChessRecordSerializer implements JsonDeserializer<ChessRecord>{
        @Override
        public ChessRecord deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject=json.getAsJsonObject();

            ChessPoint fromPoint=getChessPointFromJson(jsonObject.getAsJsonObject("fromPoint"));
            ChessPoint toPoint=getChessPointFromJson(jsonObject.getAsJsonObject("toPoint"));
            boolean isExchange=jsonObject.get("isExchange").getAsBoolean();

            return new ChessRecord(fromPoint,toPoint,isExchange);
        }

        private ChessPoint getChessPointFromJson(JsonObject chessPointJson){
            if(chessPointJson==null){
                return null;
            }
            int f=chessPointJson.get("f").getAsInt();
            int x=chessPointJson.get("x").getAsInt();
            int y=chessPointJson.get("y").getAsInt();
            int c=chessPointJson.get("c").getAsInt();
            int s=chessPointJson.get("s").getAsInt();
            ChessPoint chessPoint=new ChessPoint(f,x,y,c);
            chessPoint.s=s;
            return chessPoint;
        }
    }
}
