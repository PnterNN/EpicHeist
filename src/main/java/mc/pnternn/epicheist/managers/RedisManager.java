package mc.pnternn.epicheist.managers;

import com.google.gson.Gson;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.game.state.DataHolder;
import mc.pnternn.epicheist.game.state.WaitingState;
import org.bukkit.Bukkit;
import org.flywaydb.core.internal.util.logging.Log;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

public class RedisManager {
    Jedis jedis;
    Thread redisThread;
    public Integer[] timer = new Integer[4];
    public RedisManager(String host, int port) {
        jedis = new Jedis(host, port);
        jedis.auth(ConfigurationHandler.getValue("redis.password"));
        jedis.connect();
        subscribe();
        if(redisThread!=null) redisThread.start();
    }
    public void subscribe(){
         redisThread = new Thread("Redis Subscriber"){
            @Override
            public void run(){
                jedis.subscribe(new JedisPubSub(){
                    @Override
                    public void onMessage(String channel, String message){
                        if(channel.equals(ConfigurationHandler.getValue("redis.channel"))){
                            JSONObject obj = new JSONObject(message);
                            if(obj.get("type").toString().equals("TIMER_START")) {
                                if (!ConfigurationHandler.getValue("main-server").equals("true")) {
                                    Bukkit.getLogger().info("EpicHeist Timer synchronized");
                                    timer[0] = obj.getInt("days");
                                    timer[1] = obj.getInt("hours");
                                    timer[2] = obj.getInt("minutes");
                                    timer[3] = obj.getInt("seconds");
                                    EpicHeist.setMatch(new Match());
                                    EpicHeist.getMatch().start();
                                }
                            }else if(obj.get("type").toString().equals("SEND_TIMER")) {
                                if (ConfigurationHandler.getValue("main-server").equals("true")) {
                                    Bukkit.getLogger().info("EpicHeist Timer sent to all servers.");
                                    JSONObject timerObj = new JSONObject();
                                    timerObj.put("type", "TIMER_START");
                                    timerObj.put("days", EpicHeist.getMatch().getDataHolder().getDay());
                                    timerObj.put("hours", EpicHeist.getMatch().getDataHolder().getHour());
                                    timerObj.put("minutes", EpicHeist.getMatch().getDataHolder().getMinute());
                                    timerObj.put("seconds", EpicHeist.getMatch().getDataHolder().getSecond());
                                    publish(ConfigurationHandler.getValue("redis.channel"), timerObj);
                                }
                            }
                        }
                    }
                }, ConfigurationHandler.getValue("redis.channel"));
            }
        };
    }


    public void close() {
        redisThread.stop();
        jedis.close();
    }
    public void publish(String channel, JSONObject obj){
        try(Jedis publisher = new Jedis(ConfigurationHandler.getValue("redis.host"), Integer.parseInt(ConfigurationHandler.getValue("redis.port")))) {
            publisher.auth(ConfigurationHandler.getValue("redis.password"));
            publisher.publish(channel, obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

