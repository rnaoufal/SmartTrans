package smartTransport.com.model;

import java.util.List;

public class Journey {
	
	private String          id;


    private Constraint      constraint;

    private List<Step>      steps;

    private int             totalLoad;

    public Step getStart()
    {
        return steps.get(0);
    }

    public Step getEnd()
    {
        return steps.get(steps.size() - 1);
    }

}
