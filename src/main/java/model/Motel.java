package model;

import java.util.Date;

public class Motel {
    private int motelId;
    private Date createDate;
    private String descriptions;
    private String detailAddress;
    private String district;
    private String districtId;
    private String image;
    private String province;
    private String provinceId;
    private boolean status;
    private String ward;
    private int accountId;

    // Constructor
    public Motel() {
    }

    public Motel(int motelId, Date createDate, String descriptions, String detailAddress, String district, String districtId, String image, String province, String provinceId, boolean status, String ward, int accountId) {
        this.motelId = motelId;
        this.createDate = createDate;
        this.descriptions = descriptions;
        this.detailAddress = detailAddress;
        this.district = district;
        this.districtId = districtId;
        this.image = image;
        this.province = province;
        this.provinceId = provinceId;
        this.status = status;
        this.ward = ward;
        this.accountId = accountId;
    }

    public int getMotelId() {
        return motelId;
    }

    public void setMotelId(int motelId) {
        this.motelId = motelId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Override
    public String toString() {
        return "Motel{" +
                "motelId=" + motelId +
                ", createDate=" + createDate +
                ", descriptions='" + descriptions + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", district='" + district + '\'' +
                ", districtId='" + districtId + '\'' +
                ", image='" + image + '\'' +
                ", province='" + province + '\'' +
                ", provinceId='" + provinceId + '\'' +
                ", status=" + status +
                ", ward='" + ward + '\'' +
                ", accountId=" + accountId +
                '}';
    }
}