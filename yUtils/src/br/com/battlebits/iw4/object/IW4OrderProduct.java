package br.com.battlebits.iw4.object;

import java.util.HashMap;

/**
 *
 * @author Renato
 */
public final class IW4OrderProduct {

	private IW4Event event;
	private final HashMap<String, Object> order;

	@SuppressWarnings("unchecked")
	public IW4OrderProduct(HashMap<String, Object> order_data) {
		this.order = order_data;
		if (order_data.containsKey("pending_id"))
			this.setPendingId((Integer) order_data.get("pending_id"));
		if (order_data.containsKey("product_id"))
			this.setProductId((Integer) order_data.get("product_id"));
		if (order_data.containsKey("product_name"))
			this.setProductName((String) order_data.get("product_name"));
		if (order_data.containsKey("product_group"))
			this.setProductGroup((Integer) order_data.get("product_group"));
		if (order_data.containsKey("product_quantity"))
			this.setProductQuantity((Integer) order_data.get("product_quantity"));
		if (order_data.containsKey("duration_single"))
			this.setDurationSingle((Integer) order_data.get("duration_single"));
		if (order_data.containsKey("duration"))
			this.setDuration((Integer) order_data.get("duration"));
		if (order_data.containsKey("lifetime"))
			this.setLifetime((Boolean) order_data.get("lifetime"));
		if (order_data.containsKey("commands"))
			this.setCommands((HashMap<Integer, String>) order_data.get("commands"));
		if (order_data.containsKey("event"))
			this.setEvent((String) order_data.get("event"));
	}

	public void setPendingId(Integer pending_id) {
		this.order.put("pending_id", pending_id);
	}

	public void setProductId(Integer product_id) {
		this.order.put("product_id", product_id);
	}

	public void setProductName(String product_name) {
		this.order.put("product_name", product_name);
	}

	public void setProductGroup(Integer product_group) {
		this.order.put("product_group", product_group);
	}

	public void setProductQuantity(Integer product_quantity) {
		this.order.put("product_quantity", product_quantity);
	}

	public void setDurationSingle(Integer duration_single) {
		this.order.put("duration_single", duration_single);
	}

	public void setDuration(Integer duration) {
		this.order.put("duration", duration);
	}

	public void setLifetime(Boolean lifetime) {
		this.order.put("lifetime", lifetime);
	}

	public void setCommands(HashMap<Integer, String> commands) {
		this.order.put("commands", commands);
	}

	public void setEvent(String event_name) {
		this.event = new IW4Event(event_name);
	}

	public Integer getPendingId() {
		if (this.order.containsKey("pending_id")) {
			return (Integer) this.order.get("pending_id");
		}
		return null;
	}

	public Integer getProductId() {
		if (this.order.containsKey("product_id")) {
			return (Integer) this.order.get("product_id");
		}
		return null;
	}

	public String getProductName() {
		if (this.order.containsKey("product_name")) {
			return (String) this.order.get("product_name");
		}
		return null;
	}

	public Integer getProductGroup() {
		if (this.order.containsKey("product_group")) {
			return (Integer) this.order.get("product_group");
		}
		return null;
	}

	public Integer getProductQuantity() {
		if (this.order.containsKey("product_quantity")) {
			return (Integer) this.order.get("product_quantity");
		}
		return null;
	}

	public Integer getDurationSingle() {
		if (this.order.containsKey("duration_single")) {
			return (Integer) this.order.get("duration_single");
		}
		return null;
	}

	public Integer getDuration() {
		if (this.order.containsKey("duration")) {
			return (Integer) this.order.get("duration");
		}
		return null;
	}

	public Boolean getLifetime() {
		if (this.order.containsKey("lifetime")) {
			return (Boolean) this.order.get("lifetime");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<Integer, String> getCommands() {
		if (this.order.containsKey("commands")) {
			return (HashMap<Integer, String>) ((HashMap<Integer, String>) this.order.get("commands")).clone();
		}
		return null;
	}

	public IW4Event getEvent() {
		return this.event;
	}

	public HashMap<String, Object> getHashMap() {
		return this.order;
	}

}