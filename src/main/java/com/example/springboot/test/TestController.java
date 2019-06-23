package com.example.springboot.test;

import cn.yixblog.platform.http.HttpRequestGenerator;
import cn.yixblog.platform.http.HttpRequestResult;
import com.alibaba.fastjson.JSONObject;
import com.example.springboot.controller.HelloSender;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rabbitMq")
public class TestController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private HelloSender helloSender;

    @RequestMapping("/test")
    public void hello(@RequestBody JSONObject test, @RequestParam String rabbitName) throws Exception {
        PublicKey publicKey = RSACrypt.resolvePublic(Base64.decodeBase64("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB"));
        byte[] encryptData = JsonSortUtil.encryptByPublicKey(test.toJSONString().getBytes(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tSdvOjDBZLzB5tWpcpYVNRK0DHmPeTEmf67rWU37aPMH3i/caozIGXapPjaqfgS2Bymp8Mr/hKVSHZT3HvaHYLSI0QMiFvpuhv5mWHgOCrunbULiVSjuR/7oJH8DSwd/T1SE+LBJFxq+8lFM92top/zG9fdxe3gcvHnxUdRLPx/OyDfSlNSnA+qoOISTMiUSxvFYf3PdXkJZy0txgzX+70Ak5y4pIcH3oI0siS0hJQP08Z/MduPseNtV+JjB8GDla+SjqantnfLbLu44UqVhSZ279B7zhMZPSpq5/cZIfWbgXkso7Ge9dxA3WmZYZRbP3QIi9+odTOaHPTkZGPTgwIDAQAB");
//            for (int i = 0;i < 2;i++) {
                helloSender.send(rabbitName, "sysCode=PAYJP&timestamp=20190315172743546&data=swI0OLe2f4eRQYHR2TWrQ8t6Sg+yRp8C48Z5/mYb+amr97x0ScNKqNBtxoi6RZcXivCxQLfX1+3t\n" +
                        "q4yVAPZ09v6XGer0qjEAzj+rKfUyn1zaFK1CUl3Cr168YT6sJ8XqLXY95ytaowc5oWXEXk2mEL1n\n" +
                        "3AG4sSnjM7LoaUypwjpICisc1zs9+1yGD+zO4qv1IvxdsLRVTG9GkwM25UyYF5HxffY8cYTHMWXe\n" +
                        "EP/gM5F/Pf15VYCM99LFQT3SyJ9Hp4/Z8GwzAIYe3i9N+mYjn+s/e2VgYRTPkYmd0JvCDvSJ8Jo7\n" +
                        "srHl827Sd2IHBfnwmTdty73tV1kK/c/E/G/OUVG5Vq8cRdAr1hM159a8tYWzZx01IO4KNirpkjsj\n" +
                        "CK2Z1z05DV5TgWD+scfYQjVc4Thtu7wLdqevK6QUKjmfZ08gHMmP48QSIbAM6lYEJbRrICCvWBex\n" +
                        "miceZcp/dKdRwNMBvZlLbIAG73vg0QV7kDU5ZOZUqkRTwGD3k6RhDHNPtHRmJraAes3vvui31l6r\n" +
                        "zPVk9gAQIsqA3SpwIrnuim7sOOA82nTK02EDGJgPsI1KVanch807ZY7W02wAQBPNoHmKditw5znt\n" +
                        "M6BXM4P7+KH/LSoezbHPviYSwy7wHqxSV37cW6O7e8V2PiBtP+Tg9j/T4UaP8LTkrgS/fwbrDGU=");;
//            }
    }

    @RequestMapping("/test/list")
    public void hello(@RequestBody List<JSONObject> json)  {
        JSONObject a = new JSONObject();
        Map<String, JSONObject> map = new HashMap<>();
        List<Map<String, JSONObject>> resultList = new ArrayList<>();
        a.put("todayCount", 9);
        a.put("totalCount", 30);
        map.put("窗帘控制器", a);
        resultList.add(map);
        a = new JSONObject();
        map = new HashMap<>();
        a.put("todayCount", 18);
        a.put("totalCount", 42);
        map.put("2 x Switch", a);
        resultList.add(map);
        a = new JSONObject();
        map = new HashMap<>();
        a.put("todayCount", 7);
        a.put("totalCount", 20);
        map.put("三路开关", a);
        resultList.add(map);
        List<Map<String, JSONObject>> result = new ArrayList<>();
        List<Map<String, JSONObject>> resultLists = resultList.stream()
                .sorted(Comparator.comparing(trade->trade.entrySet().iterator().next().getValue().getIntValue("todayCount"))).collect(Collectors.toList());
//            }
        System.out.println(resultLists);
    }

    @RequestMapping("/json")
    public String generateSign(@RequestBody JSONObject data) throws UnsupportedEncodingException {
        LinkedHashMap dataLink = JSONObject.parseObject(data.toJSONString(), LinkedHashMap.class);
        Iterator<Map.Entry> iterator= dataLink.entrySet().iterator();
        StringBuilder sign = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            sign.append(entry.getKey());
            sign.append(entry.getValue());
        }
        String dasd = DigestUtils.md5DigestAsHex(sign.toString().toUpperCase().getBytes("utf-8"));
        System.out.println(dasd.length());
        return dasd;
    }

    @RequestMapping("/rsa")
    public KeyPair RSA () {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return keyPair;
    }
    @RequestMapping("/token")
    public void token(HttpSession session) {
        JSONObject tokenJSON = new JSONObject();
        tokenJSON.put("token", "123");
        HttpRequestGenerator gen = new HttpRequestGenerator("http://localhost:9002/api/v1.0/external/manage/account/logout", RequestMethod.GET);
        try {
            HttpRequestResult result = gen.execute();
            if (result.isSuccess()) {
                System.out.println("==================");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*@Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "exchangemsxferror", "routingkeymsxferror");
    }


    @RequestMapping("/sendMq")
    @ResponseBody
    public String send(String name) throws Exception {
        String context = "hello " + name + " --" + new Date();
        String sendStr;
        for (int i = 1; i <= 100; i++) {
            sendStr = "第[" + i + "]个 hello  --" + new Date();
            logger.debug("HelloSender: " + sendStr);
            sendMessage("myqueue", sendStr);
        }
        return context;
    }

    *//**
     * 方式一：动态声明exchange和queue它们的绑定关系  rabbitAdmin
     *
     * @param exchangeName
     * @param queueName
     *//*
    protected void declareBinding(String exchangeName, String queueName) {
        if (rabbitAdmin.getQueueProperties(queueName) == null) {
            *//*  queue 队列声明
            durable=true,交换机持久化,rabbitmq服务重启交换机依然存在,保证不丢失; durable=false,相反
            auto-delete=true:无消费者时，队列自动删除; auto-delete=false：无消费者时，队列不会自动删除
            排他性，exclusive=true:首次申明的connection连接下可见; exclusive=false：所有connection连接下*//*
            Queue queue = new Queue(queueName, true, false, false, null);
            rabbitAdmin.declareQueue(queue);
            TopicExchange directExchange = new TopicExchange(exchangeName);
            rabbitAdmin.declareExchange(directExchange);//声明exchange
            Binding binding = BindingBuilder.bind(queue).to(directExchange).with(queueName);    //将queue绑定到exchange
            rabbitAdmin.declareBinding(binding);      //声明绑定关系
        } else {
            rabbitAdmin.getRabbitTemplate().setQueue(queueName);
            rabbitAdmin.getRabbitTemplate().setExchange(queueName);
            rabbitAdmin.getRabbitTemplate().setRoutingKey(queueName);
        }
    }

    *//**
     * 发送消息
     *
     * @param queueName
     * @param message
     * @throws Exception
     *//*
    public void sendMessage(String queueName, String message) throws Exception {
        declareBinding(queueName, queueName);
        rabbitAdmin.getRabbitTemplate().convertAndSend(queueName, queueName, message);
    }*/
    public void test() {

    }
}
