package smartTransport.com.model;

import java.time.OffsetDateTime;

public class Constraint
{
    public enum Type
    {
        PICKUP_DT_MIN, DROPOFF_DT_MAX
    }

    private  Type           type;

    private  OffsetDateTime dateTime;
}

