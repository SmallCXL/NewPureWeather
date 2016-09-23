package com.example.arthur.pureweather.httpUtils;

import android.text.TextUtils;

import com.example.arthur.pureweather.modle.Region;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * RegionConverterFactory：
 * 自定义 Retrofit 中所需的网络请求结果转换器，将请求结果按照需求处理、保存
 */
public class RegionConverterFactory extends Converter.Factory {

    public static RegionConverterFactory create(){
        return new RegionConverterFactory();
    }

    private RegionConverterFactory(){
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
                        switch (infoArray[0].length()){
                            case 2:
                                region.setSuperCode("");
                                break;
                            case 4:
                                region.setSuperCode(infoArray[0].substring(0, 2));
                                break;
                            case 6:
                                region.setSuperCode(infoArray[0].substring(0, 4));
                                break;
                            default:
                                break;
                        }
                        regions.add(region);
                    }//end for
                }//end if(allProvince != null && allProvince.length>0)
            }
            return regions;
        };
    }
}
