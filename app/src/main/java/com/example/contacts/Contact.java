package com.example.contacts;

import java.util.Random;

public class Contact {
    private final String name;
    private final String phone_no;
    private final String imageUrl;
    private boolean expanded = false;
    private boolean selected = false;
    public static boolean selection = false;
    public static int selectedItems = 0;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    Contact(String name, String phone_no){
        this.name = name;
        this.phone_no = phone_no;
        this.imageUrl = getRandomUrl();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private String getRandomUrl() {
        int randomNum = (int)(Math.random() * (99 - 1 + 1) + 1);
        String[] genders = {"women","men"};
        boolean isMen = new Random().nextBoolean();
        int index = isMen ? 1 : 0;
        return "https://randomuser.me/api/portraits/" + genders[index] + "/" + randomNum + ".jpg";
    }

    public String getName() {
        return name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
