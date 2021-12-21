/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.HelperClasses;

public class UserPostItemHelperClass {

    private String postItemCategory, postOrderName, postItemDescription, postItemPackageSize, postItemPackageWeight,
            postItemDueDatAndTime, postItemOriginLocation, postItemDestinationLocation, itemImageUri,
            userID, orderID;


    public UserPostItemHelperClass() {
    }

    public UserPostItemHelperClass(String postItemCategory, String postOrderName, String postItemDescription, String postItemPackageSize, String postItemPackageWeight, String postItemDueDatAndTime, String postItemOriginLocation, String postItemDestinationLocation, String itemImageUri, String userID, String orderID) {
        this.postItemCategory = postItemCategory;
        this.postOrderName = postOrderName;
        this.postItemDescription = postItemDescription;
        this.postItemPackageSize = postItemPackageSize;
        this.postItemPackageWeight = postItemPackageWeight;
        this.postItemDueDatAndTime = postItemDueDatAndTime;
        this.postItemOriginLocation = postItemOriginLocation;
        this.postItemDestinationLocation = postItemDestinationLocation;
        this.itemImageUri = itemImageUri;
        this.userID = userID;
        this.orderID = orderID;
    }

    public String getPostItemCategory() {
        return postItemCategory;
    }

    public void setPostItemCategory(String postItemCategory) {
        this.postItemCategory = postItemCategory;
    }

    public String getPostOrderName() {
        return postOrderName;
    }

    public void setPostOrderName(String postOrderName) {
        this.postOrderName = postOrderName;
    }

    public String getPostItemDescription() {
        return postItemDescription;
    }

    public void setPostItemDescription(String postItemDescription) {
        this.postItemDescription = postItemDescription;
    }

    public String getPostItemPackageSize() {
        return postItemPackageSize;
    }

    public void setPostItemPackageSize(String postItemPackageSize) {
        this.postItemPackageSize = postItemPackageSize;
    }

    public String getPostItemPackageWeight() {
        return postItemPackageWeight;
    }

    public void setPostItemPackageWeight(String postItemPackageWeight) {
        this.postItemPackageWeight = postItemPackageWeight;
    }

    public String getPostItemDueDatAndTime() {
        return postItemDueDatAndTime;
    }

    public void setPostItemDueDatAndTime(String postItemDueDatAndTime) {
        this.postItemDueDatAndTime = postItemDueDatAndTime;
    }

    public String getPostItemOriginLocation() {
        return postItemOriginLocation;
    }

    public void setPostItemOriginLocation(String postItemOriginLocation) {
        this.postItemOriginLocation = postItemOriginLocation;
    }

    public String getPostItemDestinationLocation() {
        return postItemDestinationLocation;
    }

    public void setPostItemDestinationLocation(String postItemDestinationLocation) {
        this.postItemDestinationLocation = postItemDestinationLocation;
    }

    public String getItemImageUri() {
        return itemImageUri;
    }

    public void setItemImageUri(String itemImageUri) {
        this.itemImageUri = itemImageUri;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
