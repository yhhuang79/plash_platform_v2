/*
 * $Id: LenderService.java 10669 2008-02-01 15:27:01Z romikk $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.loanbroker.lender;

import org.mule.example.loanbroker.bank.Bank;
import org.mule.example.loanbroker.messages.CreditProfile;

public interface LenderService
{
    /** 
     * Returns a list of banks willing to offer a loan based on a customer's credit profile and the amount of 
     * the loan.
     */
    public abstract Bank[] getLenders(CreditProfile creditProfile, Double loanAmount);
}
