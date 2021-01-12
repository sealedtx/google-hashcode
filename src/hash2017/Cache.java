package hash2017;

import java.util.*;

/**
 * Created by User on 23.02.2017.
 */
public class Cache {

    private final int[] videos;
    int index;
    int size;
    long winMillis = 0;
    List<Endpoint> endpoints;
    HashMap<Integer, Long> usefullness;
    HashMap<Integer, Long> cachedVideos;


    public Cache(int index, int size, List<Endpoint> endpoints, int[] videos) {
        this.videos = videos;
        this.index = index;
        this.size = size;
        this.endpoints = new ArrayList<>();
        this.usefullness = new HashMap<>();
        this.cachedVideos = new HashMap<>();
        for (Endpoint endpoint : endpoints) {
            Long lat = endpoint.cacheList.get(index);
            if (lat != null)
                this.endpoints.add(endpoint);
        }
    }

    public void addVideo(int video, int videoSize) {
        long sum = 0;
        if (videoSize > this.size) {
            usefullness.put(video, -1L);
            return;
        }
        for (Endpoint endpoint : endpoints) {
            Long cachLat = endpoint.cacheList.get(index);
            Long requestNum = endpoint.requestList.get(video);
            if (requestNum != null)
                sum += requestNum * (endpoint.datacenter - cachLat);
        }
        usefullness.put(video, sum);
    }

    public int saveVideo() {
        List<Long> usefull = new ArrayList<>(usefullness.values());
        Collections.sort(usefull);
        Collections.reverse(usefull);
        for (Map.Entry<Integer, Long> entry : usefullness.entrySet()) {
            Integer key = entry.getKey();
            Long value = entry.getValue();
            if (value == usefull.get(0)) {
                if (size >= videos[key]) {
                    size -= videos[key];
                    usefullness.remove(key);
                    cachedVideos.put(key, value);
                    return key;
                }
            }
        }
        return -1;
    }

    public void removeVideo(int index) {
        usefullness.remove(index);
    }
}
