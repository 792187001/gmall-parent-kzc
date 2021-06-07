package com.atguigu.gmall.gateway.filter;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.common.util.ResultCodeEnum;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class AuthFilter implements GlobalFilter {

    @Autowired
    UserFeignClient userFeignClient;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${authUrls.url}")
    String authUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uri = request.getURI().toString();
        String path = request.getPath().toString();

        if(uri.contains(".jpg")||uri.contains(".png")||uri.contains(".ico")||uri.contains(".js")||uri.contains(".css")||uri.contains("passport")){
            return chain.filter(exchange);
        }

        // 内部请求
        //首个/代表host根路径
        if(antPathMatcher.match("/**/inner/**",path)){
            return out(response,ResultCodeEnum.SECKILL_ILLEGAL);
            //直接返回mono给请求端
        }

        //登入认证
        // 不管该功能是否需要登录，都进行登录认证,并且将用户userId传递到后台
        String token = getCookieOrHeaderValue(request, "token");
        Map<String, Object> verifyMap = new HashMap<>();
        if (!StringUtils.isEmpty(token)) {
            verifyMap = userFeignClient.verify(token);
            String success = (String) verifyMap.get("success");
            String userId = (String) verifyMap.get("userId");
            if(!StringUtils.isEmpty(success) && success.equals("success")){
                // 验证成功，将用户userId传递到后台
                request.mutate().header("userId",userId).build();
                exchange.mutate().request(request).build();
            }
        }

        //针对同步请求
           //若token为空 表示用户未登入过,将userTempId传递到chain.filter()
           String userTempId = getCookieOrHeaderValue(request, "userTempId");
        if(!StringUtils.isEmpty(userTempId)){
            // 有userTempId，将用户userTempId传递到后台
            request.mutate().header("userTempId",userTempId).build();
            exchange.mutate().request(request).build();
        }



        // web请求
        String[] split = authUrls.split(",");
        for (String authUrl : split) {
            if(uri.contains(authUrl)){
                // 如果当前请求包含在白名单中，则需要进行身份验证
                String success = (String)verifyMap.get("success");
                String userId = (String)verifyMap.get("userId");
                //如果在白名单中 ， 并且还没有登入
                if (StringUtils.isEmpty(success) || !success.equals("success")) {
                    // 重定向到登陆页面
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    URI uriTemp = request.getURI();
                    String uriTempStr = uriTemp.toString();
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://passport.gmall.com/login.html?originUrl=" + uriTempStr);
                    Mono<Void> voidMono = response.setComplete();
                    return voidMono;
                }
            }
        }
        //不在白名单且没有登入
        return chain.filter(exchange);
    }

    private String getCookieOrHeaderValue(ServerHttpRequest request, String token) {
        //同步请求携带token 异步请求中 可能不携带token
        //将token 放在Header中一份
        String tokenResult ="";
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();

        if(null!=cookies&&cookies.size()>0){
            List<HttpCookie> tokens = cookies.get(token);
            if(null!=tokens&&tokens.size()>0){
                for (HttpCookie httpCookie : tokens) {
                    tokenResult = httpCookie.getValue();
                }
            }
        }

        //若为异步请求，cookie中没有token，只能在header中获取，
        // userTempId默认在header中
        if(StringUtils.isEmpty(tokenResult)){
            List<String> strings = request.getHeaders().get(token);
            if(null!=strings&&strings.size()>0){
                tokenResult = strings.get(0);
            }
        }
        return tokenResult;
    }



    private Mono<Void> out(ServerHttpResponse response,ResultCodeEnum resultCodeEnum) {
        //result是数据
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        // 设置编码格式
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输入到页面
        Mono<Void> voidMono = response.writeWith(Mono.just(wrap));
        return voidMono;
    }

}
