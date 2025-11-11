package smartTransport.com.model;

import java.util.List;

public class WorkSetResponse extends WorkSet implements Comparable<WorkSetResponse>
{
    private String runnerId;

    private Long   runnerSeed;

    private long   computeDuration;

    private long   totalCost;

    @Override
    public int compareTo(WorkSetResponse o)
    {
        if (totalCost < o.totalCost)
            return -1;
        if (totalCost == o.totalCost)
            return 0;
        return 1;
    }

	public void setTotalCost(long cost) {
		// TODO Auto-generated method stub
		this.totalCost = cost;
	}

	public void setJourneys(List<Journey> journeys) {
		// TODO Auto-generated method stub
		
	}
}
