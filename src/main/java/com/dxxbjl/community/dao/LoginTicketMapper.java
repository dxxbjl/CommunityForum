package com.dxxbjl.community.dao;

import com.dxxbjl.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    /**
     *  @Insert({""})  不写xml了，直接使用注解写
     * @param loginTicket
     * @return
     */
     //插入ticket
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id") //设置主键 id 自增
     int insertLoginTicket(LoginTicket loginTicket);

     //通过ticket查找
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
     LoginTicket selectByTicket(String ticket);

     //更新状态
    @Update({
            "update login_ticket set status=#{status} where ticket =#{ticket}"
    })
     int updateStatus(String ticket,int status);
}
