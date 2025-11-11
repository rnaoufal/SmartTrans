package smartTransport.com.graphOpperImpl;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.DefaultFlagEncoderFactory;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.PMap;


public class CustomFlagEncoderFactory extends DefaultFlagEncoderFactory
{
    private static final String BUS_TAXI = "bus_taxi";

    private static final String TAXI     = "taxi";

    @Override
    public FlagEncoder createFlagEncoder(String name, PMap configuration)
    {
        if (BUS_TAXI.equals(name))
            return new BusTaxiFlagEncoder(configuration);
        else if (TAXI.equals(name))
            return new TaxiFlagEncoder(configuration);
        return super.createFlagEncoder(name, configuration);
    }

    private static class BusTaxiFlagEncoder extends TaxiFlagEncoder
    {
        public BusTaxiFlagEncoder(PMap configuration)
        {
            super(configuration);
            defaultSpeedMap.put("cycleway", 30);
            absoluteBarriers.remove("bus_trap");
            init();
        }

        @Override
        public long acceptWay(ReaderWay way)
        {
            if (way.hasTag("area", "yes")
                    || way.hasTag("highway", "platform")
                    || way.hasTag("public_transport", "platform"))
            {
                return 0;
            }
            if ((way.hasTag("bus", "yes") || way.hasTag("minibus", "yes"))
                    && way.getTag("highway") != null)
            {
                return acceptBit;
            }
            return super.acceptWay(way);
        }

        @Override
        public String toString()
        {
            return BUS_TAXI;
        }
    }

    private static class TaxiFlagEncoder extends CarFlagEncoder
    {
        public TaxiFlagEncoder(PMap configuration)
        {
            super(configuration);
            defaultSpeedMap.put("construction", 20);
            defaultSpeedMap.put("bus_stop", 20);
            defaultSpeedMap.put("footway", 20);
            defaultSpeedMap.put("pedestrian", 20);
            init();
        }

        @Override
        public long acceptWay(ReaderWay way)
        {
            if (way.hasTag("area", "yes"))
            {
                return 0;
            }
            if ((way.hasTag("taxi", "yes") || way.hasTag("share_taxi", "yes"))
                    && !way.hasTag("amenity", "parking"))
            {
                return acceptBit;
            }
            return super.acceptWay(way);
        }

        @Override
        public String toString()
        {
            return TAXI;
        }
    }
}