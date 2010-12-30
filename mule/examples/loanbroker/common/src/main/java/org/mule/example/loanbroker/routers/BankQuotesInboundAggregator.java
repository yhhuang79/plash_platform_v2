/*
 * $Id: BankQuotesInboundAggregator.java 13207 2008-11-04 09:14:38Z rossmason $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.loanbroker.routers;

import org.mule.api.MuleMessage;
import org.mule.routing.AggregationException;
import org.mule.routing.inbound.AbstractCorrelationAggregator;
import org.mule.routing.inbound.EventGroup;

/**
 * <code>BankQuotesInboundAggregator</code> receives a number of quotes and selects the
 * lowest
 */
public class BankQuotesInboundAggregator extends AbstractCorrelationAggregator
{
    /**
     * This method is invoked if the shouldAggregate method is called and returns
     * true. Once this method returns an aggregated message the event group is
     * removed from the router
     * 
     * @param events the event group for this request
     * @return an aggregated message
     * @throws AggregationException if the aggregation fails. in this scenario the
     *             whole event group is removed and passed to the exception handler
     *             for this componenet
     */
    protected MuleMessage aggregateEvents(EventGroup events) throws AggregationException
    {
        try
        {
            return BankQuotesAggregationLogic.aggregateEvents(events);
        }
        catch (Exception e)
        {
            throw new AggregationException(events, null, e);
        }
    }

}
