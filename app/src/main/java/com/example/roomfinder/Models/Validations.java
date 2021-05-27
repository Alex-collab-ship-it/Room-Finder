package com.example.roomfinder.Models;


import android.widget.Toast;

public class Validations {
    public static String regValidation(String login,String pas1, String pas2, String name, String surname, String phone){
        if (!login.matches("^[a-z0-9_-]{3,15}$")){
            return "Неверный формат логина";
        }
        if (!pas1.matches("((?=.*[a-z])(?=.*[A-Z]).{6,20})")){
            return "Неверный формат пароля";
        }
        if (!pas1.equals(pas2)){ return "Пароли не совпадают"; }
        if (!name.matches("^[a-zA-ZА-Яа-я]{3,15}$")){ return "Неверный формат имени"; }
        if (!surname.matches("^[a-zA-ZА-Яа-я]{3,15}$")){ return "Неверный формат фамилии"; }
        if (!phone.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")){ return "Неверный формат номера телефона"; }
        return "";
    }
    public static String changePassValidation(String old_pas, String pas1, String pas2){
        if (old_pas.equals(pas1)){ return "Старый пароль не должен совпадать с новым!"; }
        if (!pas1.matches("((?=.*[a-z])(?=.*[A-Z]).{6,20})")){ return "Неверный формат пароля"; }
        if (!pas1.equals(pas2)) { return "Неверный формат пароля"; }
        return "";
    }
    public static String adValidation(String address, String description,String price){
        if (address.length()<5 || address.length()>60){
            return "Некорректно указан адрес";
        }
        if (description.length()<5 || description.length()>300){
            return "Некорректно указано описание";
        }
        if (Integer.parseInt(price)>100000 || price.equals("")){
            return "Некорректно указана цена";
        }
        return "";
    }

}
