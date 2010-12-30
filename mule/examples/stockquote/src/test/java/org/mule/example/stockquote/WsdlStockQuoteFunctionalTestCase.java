/*
 * $Id: WsdlStockQuoteFunctionalTestCase.java 10669 2008-02-01 15:27:01Z romikk $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.stockquote;


public class WsdlStockQuoteFunctionalTestCase extends AbstractStockQuoteFunctionalTestCase
{
    protected String getConfigResources()
    {
        return "stockquote-wsdl-config.xml";
    }
}
