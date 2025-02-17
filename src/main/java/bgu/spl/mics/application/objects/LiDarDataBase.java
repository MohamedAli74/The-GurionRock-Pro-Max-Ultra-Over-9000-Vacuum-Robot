package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.Root;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static volatile LiDarDataBase instance =null;
    private List<StampedCloudPoints> cloudPoints;
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if(instance == null)
            instance= new LiDarDataBase(filePath);
        return instance;
    }

    private LiDarDataBase(String filePath) {
        Gson gson = new Gson();
        try {
            FileReader reader = new FileReader(filePath);
            Type listType = new TypeToken<List<Root.StampedCloudPointsJSON>>() {}.getType();
            List<Root.StampedCloudPointsJSON> tmp = gson.fromJson(reader, listType);
            cloudPoints = new ArrayList<StampedCloudPoints>();
            for(int i=0 ; i<tmp.size() ; i++){
                List<List<Double>> list = tmp.get(i).getCloudPoints();
                List<CloudPoint> cp = new ArrayList<CloudPoint>();
                for(int j = 0; j < list.size() ; j++){
                    List<Double> from = list.get(j);
                    CloudPoint to = new CloudPoint(from.get(0),from.get(1));
                    cp.add(to);
                }
                cloudPoints.add(new StampedCloudPoints(tmp.get(i).getId(),tmp.get(i).getTime(),cp));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
}
