package com.mujugroup.data;

import com.github.wxiaoqi.merge.EnableAceMerge;
import com.lveqia.cloud.common.DateUtil;
import com.lveqia.cloud.common.StringUtil;
import com.lveqia.cloud.common.util.Constant;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.client.RestTemplate;

@EnableHystrix
@EnableAceMerge
@EnableScheduling
@EnableFeignClients
@EnableEurekaClient
@EnableRedisHttpSession
@EnableWebSecurity
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ModuleDataApplication {


    public static void main(String[] args) {
        SpringApplication.run(ModuleDataApplication.class, args);
    }

    @Bean
    public MapperFactory getFactory(){
        DefaultMapperFactory defaultMapperFactory = new DefaultMapperFactory.Builder().build();
        //时间戳转时间
        defaultMapperFactory.getConverterFactory().registerConverter("timestampConvert"
                , new BidirectionalConverter<Long, String>(){
                    @Override
                    public String convertTo(Long source, Type<String> destinationType) {
                        return DateUtil.timestampToString(source.intValue(), DateUtil.TYPE_DATETIME_19 );
                    }
                    @Override
                    public Long convertFrom(String source, Type<Long> destinationType) {
                        return DateUtil.stringToDate(source, DateUtil.TYPE_DATETIME_19).getTime()/1000L;
                    }
                });
        //价格分转元，带两位小数
        defaultMapperFactory.getConverterFactory().registerConverter("rmbPriceConvert"
                , new BidirectionalConverter<Object,String>(){
                    @Override
                    public String convertTo(Object source, Type<String> destinationType) {
                        String val = Constant.DIGIT_ZERO;
                        if(source instanceof Integer){
                            val = String.valueOf(source);
                        }else if(source instanceof String){
                            val = (String)source;
                        }
                        return StringUtil.changeF2Y(val);
                    }
                    @Override
                    public Object convertFrom(String source, Type<Object> destinationType) {
                        return StringUtil.changeY2F(source);
                    }
                });

        return defaultMapperFactory;
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
