package org.myorg.quickstart.utils;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.lang.reflect.ParameterizedType;

public class Mapper<T> implements FlatMapFunction<String, Tuple1<T>> {

    private static ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static<T> T getObject(String serializedObject, Class<T> outputClass)throws Exception{
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            return objectMapper.readValue(serializedObject, outputClass);
        }catch (Exception ex){
            throw ex;
        }
    }
//
//    public static String getSerializedObject(Object object)throws Exception{
//        try{
//            return objectMapper.writeValueAsString(object);
//        }catch (Exception ex){
//            throw ex;
//        }
//    }


    @Override
    public void flatMap(String s, Collector<Tuple1<T>> collector) throws Exception {
        Class c = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        collector.collect(new Tuple1<T>((T) getObject(s, c)));
    }
}
