package com.erycoking.annex.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class SentCustomer {

    @SerializedName("CustomerId")
    private int CustomerId;

    @SerializedName("FirstName")
    @Expose
    private String FirstName;

    @SerializedName("OtherNames")
    @Expose
    private String OtherNames;

    @SerializedName("Address")
    @Expose
    private String Address;

    @SerializedName("NationalId")
    @Expose
    private int NationalId;

    @SerializedName("MobileNo")
    @Expose
    private int MobileNo;

    public SentCustomer() {
    }

    public SentCustomer(int customerId, String firstName, String otherNames, String address, int nationalId, int mobileNo) {
        CustomerId = customerId;
        FirstName = firstName;
        OtherNames = otherNames;
        Address = address;
        NationalId = nationalId;
        MobileNo = mobileNo;
    }

    public SentCustomer(String firstName, String otherNames, String address, int nationalId, int mobileNo) {
        FirstName = firstName;
        OtherNames = otherNames;
        Address = address;
        NationalId = nationalId;
        MobileNo = mobileNo;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getOtherNames() {
        return OtherNames;
    }

    public void setOtherNames(String otherNames) {
        OtherNames = otherNames;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getNationalId() {
        return NationalId;
    }

    public void setNationalId(int nationalId) {
        NationalId = nationalId;
    }

    public int getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(int mobileNo) {
        MobileNo = mobileNo;
    }

    @Override
    public String toString() {
        return "SentCustomer{" +
                "CustomerId=" + CustomerId +
                ", FirstName='" + FirstName + '\'' +
                ", OtherNames='" + OtherNames + '\'' +
                ", Address='" + Address + '\'' +
                ", NationalId=" + NationalId +
                ", MobileNo=" + MobileNo +
                '}';
    }
}
