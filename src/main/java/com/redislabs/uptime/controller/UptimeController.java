package com.redislabs.uptime.controller;

import java.time.Instant;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpServletRequest;


@Controller
public class UptimeController {

    @Autowired
    private StringRedisTemplate template;

    // this renders the template
    @RequestMapping("/")
    public String greeting(Model model) {
        this.reset();

        return "greeting";  
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void reset() {
        //zero out the list and the zset
        this.template.opsForList().trim("uptime",0L,0L);
        this.template.delete("delta");
    }

    @RequestMapping(value = "/largest", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public HashMap largest() {

        HashMap<String, Long> results = new HashMap<String, Long>();
        
        ZSetOperations.TypedTuple<String> largest = this.template.opsForZSet().reverseRangeWithScores("delta",0,-1).iterator().next();
        // System.out.println(largest); 
        // double largest = this.template.opsForZSet().reverseRangeWithScores("delta",0,-1).iterator().next().getScore();

        results.put("largest", largest.getScore().longValue());
        results.put("when", Long.parseLong(largest.getValue()));
        return results;
    }

    @RequestMapping(value = "/try", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public HashMap hello() {//HttpServletRequest request, HttpServletResponse response) {
        
        // example to get connection details:
        // template.getConnectionFactory().getConnection().flushAll();

        ListOperations<String, String> listOps = this.template.opsForList();

        // Instant curtime = Instant.now();
        Long epoch = Instant.now().toEpochMilli();
        // System.out.println(epoch);
        
        //System.out.printf("Current Time: %d", epoch); // or %ts
        listOps.leftPush("uptime",epoch.toString());
        List<String> two = new ArrayList<String> ();
        two = listOps.range("uptime", 0, 1);

        // System.out.println(two);

        HashMap<String, Long> results = new HashMap<String, Long>();
        // if (two.size() > 1) {
            results.put("delta",Long.parseLong(two.get(0)) - Long.parseLong(two.get(1)));
        // } else {
        //     results.put("delta",0L);
        // }
        // System.out.println(results);
        
        this.template.opsForZSet().add("delta",epoch.toString(), (double)results.get("delta"));
        
        ZSetOperations.TypedTuple<String> largest = this.template.opsForZSet().reverseRangeWithScores("delta",0,-1).iterator().next();

        results.put("largest", largest.getScore().longValue());
        results.put("when", Long.parseLong(largest.getValue()));

        return results;
    }
}