package br.com.battlebits.iw4.object;

import java.util.HashMap;

/**
 *
 * @author Renato
 */
public final class IW4PlayerPackage {
    
    private final HashMap<String, Object> order;
    
    public IW4PlayerPackage(HashMap<String, Object> order_data) {
        this.order = order_data;
        if (order_data.containsKey("minecraft_account_package_id")) this.setAPID((Integer) order_data.get("minecraft_account_package_id"));
        if (order_data.containsKey("product_name")) this.setProductName((String) order_data.get("product_name"));
        if (order_data.containsKey("days")) this.setProductDays((Integer) order_data.get("days"));
        if (order_data.containsKey("lifetime")) this.setLifetime((Boolean) order_data.get("lifetime"));
        if (order_data.containsKey("date_added")) this.setDateAdded((String) order_data.get("date_added"));
        if (order_data.containsKey("date_updated")) this.setDateUpdated((String) order_data.get("date_updated"));
    }
    
    public void setAPID(Integer apid) {
        this.order.put("product_apid", apid);
    }

    public void setProductName(String product_name) {
        this.order.put("product_name", product_name);
    }
    
        
    public void setProductDays(Integer product_days) {
        this.order.put("product_days", product_days);
    }
    
    public void setLifetime(Boolean lifetime) {
        this.order.put("product_lifetime", lifetime);
    }
    
    public void setDateAdded(String date_added) {
        this.order.put("date_added", date_added);
    }
    
    public void setDateUpdated(String date_updated) {
        this.order.put("date_updated", date_updated);
    }

    public Integer getAPID() {
        if (this.order.containsKey("product_apid")) {
            return (Integer) this.order.get("product_apid");
        }
        return 0;
    }

    public String getProductName() {
        if (this.order.containsKey("product_name")) {
            return (String) this.order.get("product_name");
        }
        return null;
    }
    
        
    public Integer getProductDays() {
        if (this.order.containsKey("product_days")) {
            return (Integer) this.order.get("product_days");
        }
        return null;
    }
    
    public Boolean getLifetime() {
        if (this.order.containsKey("product_lifetime")) {
            return (Boolean) this.order.get("product_lifetime");
        }
        return null;
    }
    
    public String getDateAdded() {
        if (this.order.containsKey("date_added")) {
            return (String) this.order.get("date_added");
        }
        return null;
    }
    
    public String getDateUpdated() {
        if (this.order.containsKey("date_updated")) {
            return (String) this.order.get("date_updated");
        }
        return null;
    }
    
    public HashMap<String, Object> getHashMap() {
        return this.order;
    }
    
}
