package com.chuthuong.lthstore.province;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Province {
    private  String name;
    private int code;
    @SerializedName("codename")
    private String codeName;
    @SerializedName("division_type")
    private String divisionType;
    @SerializedName("phone_code")
    private int phoneCode;
    private List<District> districts;

    public Province() {
    }
    public Province(String name) {
        this.name=  name;
    }
    public Province(String name, int code, String codeName, String divisionType, int phoneCode, List<District> districts) {
        this.name = name;
        this.code = code;
        this.codeName = codeName;
        this.divisionType = divisionType;
        this.phoneCode = phoneCode;
        this.districts = districts;
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

    public int getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(int phoneCode) {
        this.phoneCode = phoneCode;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", codeName='" + codeName + '\'' +
                ", divisionType='" + divisionType + '\'' +
                ", phoneCode=" + phoneCode +
                ", districts=" + districts +
                '}';
    }
}
