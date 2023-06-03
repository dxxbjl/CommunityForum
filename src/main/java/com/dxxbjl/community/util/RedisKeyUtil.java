package com.dxxbjl.community.util;

public class RedisKeyUtil {
    private static final String SPLIT =":";
    //前缀
    private static final String PREFIX_ENTITY_LIKE ="like:entity";
    private static final String PREFIX_USER_LIKE ="like:user";
    private static final String PREFIX_FOLLOWEE = "followee";  //目标
    private static final String PREFIX_FOLLOWER = "follower";  //粉丝

    private static final String PREFIX_KAPTCHA = "kaptcha"; //验证码

    private static  final String PREFIX_TICKET = "ticket"; //登录凭证

    private static  final String PREFIX_USER = "user"; //登录凭证

    private static final  String PREFIX_UV = "uv"; //独立访客量

    private static final  String PREFIX_DAU = "dau";//日活跃用户

    private static final String PREFIX_POST = "post";



    //某个实体的赞
    //like:entity:entityType:entityId  -> set(userID)
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT +entityId;
    }


    /**
     * 某个用户的赞
     * @param userId
     * @return
     */
    // like:user:userId - int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体/目标
    //followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId +SPLIT + entityType;
    }

    //某个用户拥有的粉丝
    //follower：entityType:entityId ->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType +SPLIT +entityId;
    }

    //拼验证码得key
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT +owner;
    }

    //登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //单日 UV 独立访客量
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间UV
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV + SPLIT +  startDate +SPLIT + endDate;
    }

    //单日活跃用户 dau
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT+ date;
    }

    //区间活跃用户 dau
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT+ startDate + SPLIT + endDate;
    }

    //统计帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }
}
