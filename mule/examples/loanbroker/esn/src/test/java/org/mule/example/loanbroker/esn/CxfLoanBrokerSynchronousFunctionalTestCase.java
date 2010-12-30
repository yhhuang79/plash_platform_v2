/*
 * $Id: CxfLoanBrokerSynchronousFunctionalTestCase.java 11445 2008-03-20 11:03:00Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.loanbroker.esn;

import org.mule.example.loanbroker.tests.AbstractLoanBrokerTestCase;

public class CxfLoanBrokerSynchronousFunctionalTestCase extends AbstractLoanBrokerTestCase
{
    
    @Override
    public void testSingleLoanRequest() throws Exception
    {
        // TODO Auto-generated method stub
        super.testSingleLoanRequest();
    }

    @Override
    protected String getConfigResources()
    {
        return "loan-broker-sync-config.xml, loan-broker-cxf-endpoints-config.xml";
    }
}
