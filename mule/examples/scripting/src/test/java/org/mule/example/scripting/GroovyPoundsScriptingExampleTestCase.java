/*
 * $Id: GroovyPoundsScriptingExampleTestCase.java 13129 2008-10-24 15:31:16Z aperepel $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.scripting;


public class GroovyPoundsScriptingExampleTestCase extends AbstractPoundsScriptingExampleTestCase
{   
    @Override
    protected String getScriptFile()
    {
        return "greedy.groovy";
    }    
}


