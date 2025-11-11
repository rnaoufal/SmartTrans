package smartTransport.com.model;

import java.time.OffsetDateTime;


public class Step {
	
	public enum State
    {
        IN, OUT
    }

    private  String         id;

    private  String         orderId;

    private  State          way;

    private  int            load;

    private  Position       position;

    private  OffsetDateTime dateTime;

    private  Journey        journey;

	public Object getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public Object getOrderId() {
		// TODO Auto-generated method stub
		return orderId;
	}

	public Object getWay() {
		// TODO Auto-generated method stub
		return way;
	}

	public Object getLoad() {
		// TODO Auto-generated method stub
		return load;
	}

	public Object getPosition() {
		// TODO Auto-generated method stub
		return position;
	}
	

}
