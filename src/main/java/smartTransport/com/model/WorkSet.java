package smartTransport.com.model;

import java.time.OffsetDateTime;
import java.util.List;

public class WorkSet {
	
	private String         id;
    private String         contractId;
    private OffsetDateTime maxResponseWantedAt;
    private List<Journey>  journeys;

}
