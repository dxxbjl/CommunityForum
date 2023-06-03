package com.dxxbjl.community.config;

import com.dxxbjl.community.quartz.AlphaJob;
import com.dxxbjl.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置 -》 数据库  -》调用
@Configuration
public class QuartzConfig {

    //FactoryBean 可简化Bean的实例化过程
    //1、通过Factory Bean封装Bean的实例化过程
    //2、将FactoryBean装配到Spring容器中
    //3、将Factory Bean 注入给其他的Bean
    //4、该Bean得到的是Factory bean所管理的对象实例


    //配置JobDetail
    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){

        JobDetailFactoryBean factoryBean =new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true); //是否持久
        factoryBean.setRequestsRecovery(true);//是否可恢复
        return factoryBean;
    }

    //配置Trigger
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){

        SimpleTriggerFactoryBean factoryBean =new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);//频率
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

    //刷新帖子分数的任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){

        JobDetailFactoryBean factoryBean =new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true); //是否持久
        factoryBean.setRequestsRecovery(true);//是否可恢复
        return factoryBean;
    }


    //配置Trigger
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){

        SimpleTriggerFactoryBean factoryBean =new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setRepeatInterval(1000*60*5);//频率
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }
}
