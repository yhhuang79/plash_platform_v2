package tw.edu.sinica.iis.ants.components;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class RequestPreProcess
{

    public Object greet(Map req)
    {
    	req.put("step", 1);
    	req.put("timefrom2", Calendar.getInstance().getTimeInMillis());
    	return req;
    }
}
