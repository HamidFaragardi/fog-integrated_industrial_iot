package fogcloudproject.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import fogcloudproject.VMTypes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Importer {

    public static ArrayList<VMTypes> importVMs() {

        ArrayList<VMTypes> VMTypes = new ArrayList<>();
        JSONObject jo = null;

        try {
            jo = (JSONObject) new JSONParser().parse(new FileReader("src/fogcloudproject/data/Servers.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray ja = (JSONArray) jo.get("vms");

        for (Object vm : ja) {
            JSONObject vmo = (JSONObject) vm;
            fogcloudproject.VMTypes VMonCloud = new VMTypes(Integer.parseInt(vmo.get("no_cores")+""),Integer.parseInt(vmo.get("memory")+""),Double.parseDouble(vmo.get("cost")+""));


            VMTypes.add(VMonCloud);
        }

        VMTypes.sort(new Comparator<fogcloudproject.VMTypes>() {
            @Override
            public int compare(fogcloudproject.VMTypes o1, fogcloudproject.VMTypes o2) {
                if (o1.getCost() > o2.getCost())
                    return 1;
                return -1;
            }
        });

        return VMTypes;
    }
}