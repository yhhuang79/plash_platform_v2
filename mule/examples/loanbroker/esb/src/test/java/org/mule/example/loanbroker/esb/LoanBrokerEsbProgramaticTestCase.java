/*
 * $Id: LoanBrokerEsbProgramaticTestCase.java 12729 2008-09-24 15:55:52Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.loanbroker.esb;

import org.mule.api.config.ConfigurationBuilder;
import org.mule.example.loanbroker.tests.AbstractLoanBrokerTestCase;

public class LoanBrokerEsbProgramaticTestCase extends AbstractLoanBrokerTestCase
{
    protected String getConfigResources()
    {
        // this method is never invoked as we override getBuilder here
        return null;
    }

    @Override
    protected ConfigurationBuilder getBuilder() throws Exception
    {
        return new LoanBrokerEsbConfigurationBuilder();
    }
}
