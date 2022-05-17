package com.chuthuong.lthstore.province;

import com.google.gson.annotations.SerializedName;

public class Ward {
    private  String name;
    private int code;
    @SerializedName("codename")
    private String codeName;
    @SerializedName("division_type")
    private String divisionType;
    @SerializedName("short_codename")
    private String shortCodeName;

    public Ward() {
    }
    public Ward(String name) {

    }

    public Ward(String name, int code, String codeName, String divisionType, String shortCodeName) {
        this.name = name;
        this.code = code;
        this.codeName = codeName;
        this.divisionType = divisionType;
        this.shortCodeName = shortCodeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getDivisionType() {
        return divisionType;
    }

    public void setDivisionType(String divisionType) {
        this.divisionType = divisionType;
    }

    public String getShortCodeName() {
        return shortCodeName;
    }

    public void setShortCodeName(String shortCodeName) {
        this.shortCodeName = shortCodeName;
    }

    @Override
    public String toString() {
        return "Ward{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", codeName='" + codeName + '\'' +
                ", divisionType='" + divisionType + '\'' +
                ", shortCodeName='" + shortCodeName + '\'' +
                '}';
    }
}
