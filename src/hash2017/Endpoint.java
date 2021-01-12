package hash2017;

import java.util.HashMap;

/**
 * Created by User on 23.02.2017.
 */
public class Endpoint {

    public int datacenter;
    public HashMap<Integer, Long> cacheList;
    public HashMap<Integer, Long> requestList;


    public Endpoint(int datacenter, HashMap<Integer, Long> cacheList) {
        this.datacenter = datacenter;
        this.cacheList = cacheList;
        requestList = new HashMap<>();
    }

    public void addRequest(Integer video, Long num) {
        requestList.put(video, num);
    }

}
