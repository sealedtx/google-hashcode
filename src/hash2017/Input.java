package hash2017;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by User on 23.02.2017.
 */
public class Input {

    private final int cacheNum;
    private final int cacheSize;
    private final ArrayList<Endpoint> endpoints = new ArrayList();
    private final int videos[];

    public ArrayList<Endpoint> getEndpoints() {
        return endpoints;
    }

    public int getCacheNum() {
        return cacheNum;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int[] getVideos() {
        return videos;
    }

    public Input(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        int V = scan.nextInt();
        int E = scan.nextInt();
        int R = scan.nextInt();
        cacheNum = scan.nextInt();
        cacheSize = scan.nextInt();
        videos = new int[V];
        for (int i = 0; i < V; i++)
            videos[i] = scan.nextInt();
        for (int i = 0; i < E; i++) {
            HashMap<Integer, Long> cacheList = new HashMap<>();
            int datacenter = scan.nextInt();
            int cacheNum = scan.nextInt();
            for (int j = 0; j < cacheNum; j++) {
                int num = scan.nextInt();
                long lat = scan.nextLong();
                cacheList.put(num, lat);
            }
            endpoints.add(new Endpoint(datacenter, cacheList));
        }
        for (int i = 0; i < R; i++) {
            int video = scan.nextInt();
            int endpoint = scan.nextInt();
            long requests = scan.nextLong();
            endpoints.get(endpoint).addRequest(video, requests);
        }
    }
}
