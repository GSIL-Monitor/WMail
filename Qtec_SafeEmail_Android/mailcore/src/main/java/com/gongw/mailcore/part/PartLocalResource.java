package com.gongw.mailcore.part;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by gongw on 2018/9/14.
 */

public class PartLocalResource {

    public int getPartsCountByMsgId(long id){
        return LitePal.where("localmessage_id = ?", String.valueOf(id))
                .count(LocalPart.class);
    }

    public List<LocalPart> getPartsByMsgId(long id){
        return LitePal.where("localmessage_id = ?", String.valueOf(id))
                .find(LocalPart.class);
    }


}
