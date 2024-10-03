package com.javarush.telegram;

public class UserInfo {
    public String name; //Имя
    public String sex; //Пол
    public String age; //Возраст
    public String city; //Город
    public String occupation; //Профессия
    public String hobby; //Хобби
    public String handsome; //Красота, привлекательность
    public String wealth; //Доход, богатство
    public String annoys; //Меня раздражает в людях
    public String goals; //Цели знакомства

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(name, "Name");
        result += fieldToString(sex, "Sex");
        result += fieldToString(age, "Age");
        result += fieldToString(city, "City");
        result += fieldToString(occupation, "Occupation");
        result += fieldToString(hobby, "Hobbies");
        result += fieldToString(handsome, "Beauty, attractiveness in points (maximum 10 points)");
        result += fieldToString(wealth, "Income, wealth");
        result += fieldToString(annoys, "What irritates me about people");
        result += fieldToString(goals, "Dating goals");

        return result;
    }
}
