package com.ka12.ayirimatrimony;

public class description
{

    public String get_description(String qualification,String work,String place)
    {
        String desc="";
        switch (work) {
            case "Government":
            case "Private":
                desc = "Working in " + work + " sector after having completed my " + qualification + " degree." +
                        "I'm currently living in " + place+".";
                         break;
            case "Business":
                desc = "Working as a Business owner after having completed my " + qualification + " degree." +
                        "I'm currently living in " + place+".";
                         break;
            case "Self employed":
                desc = "Currently self employed after having completed my " + qualification + " degree." +
                        "I'm currently living in " + place+".";
                         break;
            case "Defence":
                desc="Working in defence after having completed my " + qualification + " degree."+
                "I'm currently living in " + place+".";
                        break;
            case "Not working":
                desc="Currently umemployed after having completed my " + qualification + " degree."+
                        "I'm currently living in " + place+".";
        }
        return desc;
    }
}
