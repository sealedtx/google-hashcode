package hash2017;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 23.02.2017.
 */
public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        Input input = new Input(new File("kittens.in"));
        ArrayList<Endpoint> endpoints = input.getEndpoints();

        int[] videos = input.getVideos();
        ArrayList<Integer> myVideos = new ArrayList<>();
        for (int i =0; i < videos.length; i++) {
            myVideos.add(videos[i]);
        }
        Collections.sort(myVideos);

        List<Cache> cacheList = new ArrayList<>();
        for (int i = 0; i < input.getCacheNum(); i++) {
            Cache cache = new Cache(i, input.getCacheSize(), endpoints, videos);
            cacheList.add(cache);
        }

        for (int i = 0; i < videos.length; i++) {
            for (Cache cache : cacheList) {
                cache.addVideo(i, videos[i]);
            }
        }

        for (Cache cache : cacheList) {
            while (cache.size >= myVideos.get(0)) {
                int index = cache.saveVideo();
                if (index != -1) {
                    for (Cache c : cacheList) {
                        c.removeVideo(index);
                        myVideos.remove(new Integer(videos[index]));
                    }
                } else
                    break;
            }
        }

        int usedCache = 0;
        for (Cache cache : cacheList) {
            if (cache.size < input.getCacheSize())
                usedCache++;
        }
        System.out.println(usedCache);
        for (Cache cache : cacheList) {
            String result = cache.index + "";
            for (Map.Entry<Integer, Long> entry : cache.cachedVideos.entrySet()) {
                result += " " + entry.getKey();
            }
            System.out.println(result);
        }
    }

}
