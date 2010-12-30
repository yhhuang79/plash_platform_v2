/*
 * $Id: LoanBrokerApp.java 11449 2008-03-20 12:27:50Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.loanbroker.esn;

import org.mule.example.loanbroker.AbstractLoanBrokerApp;
import org.mule.example.loanbroker.LocaleMessage;
import org.mule.example.loanbroker.messages.LoanQuote;
import org.mule.util.DateUtils;
import org.mule.util.StringMessageUtils;
import org.mule.util.StringUtils;
import org.mule.util.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Runs the LoanBroker ESN example application.
 */
public class LoanBrokerApp extends AbstractLoanBrokerApp
{
    public static final String CLI_OPTIONS[][] = {
            { "config", "true", "Configuration File(s)" },
            { "main", "true", "LoanBrokerApp Class"},
            { "req", "true", "Number of loan requests to use"},
            { "sync", "true", "Whether to run in synchronous mode or not"}
        };

    private static boolean synchronous = false;

    // Needed for webapp version!
    // TODO Travis ... sadly, it doesn't quite work
    public LoanBrokerApp() throws Exception
    {
        super();
    }

    public LoanBrokerApp(String config) throws Exception
    {
        super(config);
    }

    public static void main(String[] args) throws Exception
    {
        LoanBrokerApp loanBrokerApp = null;
        
        /////////////////////////////////////////
        // Command-line config
        /////////////////////////////////////////
        Map options = SystemUtils.getCommandLineOptions(args, CLI_OPTIONS);
        String config = (String)options.get("config");
        if (StringUtils.isNotBlank(config))
        {
            loanBrokerApp = new LoanBrokerApp(config);

            int i = 100;
            String requests = (String)options.get("req");
            if (requests != null)
            {
                i = Integer.parseInt(requests);
            }

            String sync = (String)options.get("sync");
            if (sync != null)
            {
                synchronous = Boolean.valueOf(sync).booleanValue();
            }

            if (synchronous)
            {
                long start = System.currentTimeMillis();
                List results = loanBrokerApp.requestSend(i, "CustomerRequests");
                System.out.println(LocaleMessage.responseNumQuotes(results.size()));
                List output = new ArrayList(results.size());
                int x = 1;
                for (Iterator iterator = results.iterator(); iterator.hasNext(); x++)
                {
                    LoanQuote quote = (LoanQuote)iterator.next();
                    output.add(x + ". " + quote.toString());
                }
                System.out.println(StringMessageUtils.getBoilerPlate(output, '*', 80));
                long cur = System.currentTimeMillis();
                System.out.println(DateUtils.getFormattedDuration(cur - start));
                System.out.println(LocaleMessage.responseAvgRequest(((cur - start) / x) ));
            }
            else
            {
                loanBrokerApp.requestDispatch(i, "CustomerRequests");
            }
        }
        /////////////////////////////////////////
        // Interactive config
        /////////////////////////////////////////
        else
        {
            loanBrokerApp = new LoanBrokerApp(getInteractiveConfig());
            loanBrokerApp.run(synchronous);
        }
    }

    protected static String getInteractiveConfig() throws IOException
    {
        System.out.println(StringMessageUtils.getBoilerPlate(LocaleMessage.welcome()));
                    
        int response = 0;
        String provider = "axis";

        while (response != 'a' && response != 'c')
        {
            System.out.println("\n" + LocaleMessage.menuOptionSoap());
            response = readCharacter();
            switch (response)
            {
                case 'a' :
                {
                    provider = "axis";
                    break;
                }
                case 'c' :
                {
                    provider = "cxf";
                    break;
                }
            }
        }

        response = 0;
        while (response != 'a' && response != 's')
        {
            System.out.println("\n" + LocaleMessage.menuOptionMode());
            response = readCharacter();
            switch (response)
            {
                case 'a' :
                {
                    System.out.println(LocaleMessage.loadingAsync());
                    synchronous = false;
                    break;
                }

                case 's' :
                {
                    System.out.println(LocaleMessage.loadingSync());
                    synchronous = true;
                    break;
                }
            }
        }

        String config = "loan-broker-" + (synchronous ? "sync" : "async") + "-config.xml";
        config += ",loan-broker-" + provider + "-endpoints-config.xml";
        return config;
    }
}
