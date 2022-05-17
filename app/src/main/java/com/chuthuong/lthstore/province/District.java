package com.chuthuong.lthstore.province;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class District {
    private  String name;
    private int code;
    @SerializedName("codename")
    private String codeName;
    @SerializedName("division_type")
    private String divisionType;
    @SerializedName("short_codename")
    private String shortCodeName;
    private List<Ward> wards;

    public District() {
    }
    public District(String name) {
        this.name = name;
    }
    public District(String name, int code, String codeName, String divisionType, String shortCodeName, List<Ward> wards) {
        this.name = name;
        this.code = code;
        this.codeName = codeName;
        this.divisionType = divisionType;
        this.shortCodeName = shortCodeName;
        this.wards = wards;
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

    public List<Ward> getWards() {
        return wards;
    }

    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    @Override
    public String toString() {
        return "District{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", codeName='" + codeName + '\'' +
                ", divisionType='" + divisionType + '\'' +
                ", shortCodeName='" + shortCodeName + '\'' +
                ", wards=" + wards +
                '}';
    }
}
