public class ClickEvent {
    private Long eventTime;
    private String eventType;
    private String productId;
    private String categoryId;
    private String categoryCode;
    private String brand;
    private String price;
    private String userid;
    private String userSession;

    public ClickEvent() {}

    public ClickEvent(Long eventTime, String eventType, String productId, String categoryId, String categoryCode, String brand, String price, String userid, String userSession) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.brand = brand;
        this.price = price;
        this.userid = userid;
        this.userSession = userSession;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "eventTime=" + eventTime +
                ", eventType='" + eventType + '\'' +
                ", productId='" + productId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", brand='" + brand + '\'' +
                ", price='" + price + '\'' +
                ", userid='" + userid + '\'' +
                ", userSession='" + userSession + '\'' +
                '}';
    }
}
