package com.example.demo.empty;

public class KvmImageDto {
	private Long imageId;
	private Long imageParentId;
	private String imageName;
	private Object basicData;
	private CheckAttrDto checkArr;
	private Boolean disabled;

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public Long getImageParentId() {
		return imageParentId;
	}

	public void setImageParentId(Long imageParentId) {
		this.imageParentId = imageParentId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Object getBasicData() {
		return basicData;
	}

	public void setBasicData(Object basicData) {
		this.basicData = basicData;
	}

	public CheckAttrDto getCheckArr() {
		return checkArr;
	}

	public void setCheckArr(CheckAttrDto checkArr) {
		this.checkArr = checkArr;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}
