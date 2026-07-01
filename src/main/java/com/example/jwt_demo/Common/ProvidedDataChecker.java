package com.example.jwt_demo.Common;

import com.example.jwt_demo.Common.Annotations.RequiredField;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProvidedDataChecker {

    @SneakyThrows
    public <T> void checkEmptyValue(T callItems, Class<T> tClass){

        for(var field  : getClassValues(tClass)){

            if(!field.isAnnotationPresent(RequiredField.class)){
                continue;
            }

            field.setAccessible(true);

            Object value = field.get(callItems);

            if(value == null){
                    throw new ValidationException(formatFieldName(field.getName()) + " cannot be empty", Warnings.ERROR);
            }

            // check values
            if (value instanceof String str) {
                if (str.isBlank()) {
                    throw new ValidationException(
                            formatFieldName(field.getName()) + " cannot be empty",
                            Warnings.ERROR
                    );
                }
            }

            // check lists
            if (value instanceof List<?> list) {
                if (list.isEmpty()) {
                    throw new ValidationException(
                            formatFieldName(field.getName()) + " cannot be empty",
                            Warnings.ERROR
                    );
                }
            }







        }






    }




    @SneakyThrows
    public <T> T getdefaultssss(T values, Class<T> tClass){

        T defaultValue = tClass.getDeclaredConstructor().newInstance();

        for(var s : tClass.getDeclaredFields()){

            Object defaultValues = s.get(defaultValue);
            Object existingValues = s.get(values);

            s.setAccessible(true);

            if(Objects.equals(defaultValues,existingValues)){
                s.set(values,null);
            }



        }


        return values;



    }























    @SneakyThrows
    public <T> T defaultValueChecker(T valueGiven, Class<T> tClass){

        T defaultConst = tClass.getDeclaredConstructor().newInstance();

        for(var s : getClassValues(tClass)){

            if(s.getType().isPrimitive()){
                continue;
            }

            s.setAccessible(true);


            Object defaultValue = s.get(defaultConst);
            Object givenValue = s.get(valueGiven);

            if(Objects.equals(defaultValue,givenValue)){
                s.set(valueGiven,null);
            }

        }

        return valueGiven;

    }



    public <T> List<Field> getClassValues(Class<T> tClass){
        return List.of(tClass.getDeclaredFields());
    }


    private String formatFieldName(String fieldName) {
        String result = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");

        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }





}
