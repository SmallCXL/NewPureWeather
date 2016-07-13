package com.example.arthur.myapplication.httpUtils;

import android.text.TextUtils;

import com.example.arthur.myapplication.modle.Region;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2016/7/3.
 */
public class RegionConverterFactory extends Converter.Factory {
    private String superCode;

    public static RegionConverterFactory create(String superCode){
        return new RegionConverterFactory(superCode);
    }

    private RegionConverterFactory(String superCode){
        this.superCode = superCode;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return responseBody -> {
            String response = responseBody.string();
            List<Region> regions = new ArrayList<>();
            if (!TextUtils.isEmpty(response)) {
                String[] allRegions = response.split(",");
                if (allRegions != null && allRegions.length > 0) {
                    for (String r : allRegions) {
                        Region region = new Region();
                        String[] infoArray = r.split("\\|");
                        region.setCode(infoArray[0]);
                        region.setName(infoArray[1]);
                        region.setSuperCode(superCode);
//                        switch (infoArray[0].length()){
//                            case 2:
//                                region.setSuperRegion(superRegion);
//                                break;
//                            case 4:
//                                region.setSuperCityCode(infoArray[0].substring(0, 2));
//                                region.setSuperCityName(superCityName+" - ");
//                                break;
//                            case 6:
//                                region.setSuperCityCode(infoArray[0].substring(0, 4));
//                                break;
//                            default:
//                                break;
//                        }
                        regions.add(region);
                    }//end for
                }//end if(allProvince != null && allProvince.length>0)
            }
            return regions;
        };
    }
}
