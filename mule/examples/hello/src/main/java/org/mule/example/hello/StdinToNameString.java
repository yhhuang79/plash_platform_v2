/*
 * $Id: StdinToNameString.java 10668 2008-02-01 15:10:52Z romikk $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.hello;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

/**
 * The transformation removes break-lines and newlines from the string, which
 * potentially could have been added during a <code>stdin</code> input operation.
 */
public class StdinToNameString extends AbstractTransformer
{
    public StdinToNameString()
    {
        super();
        this.registerSourceType(String.class);
        this.setReturnClass(NameString.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transformer.AbstractTransformer#doTransform(java.lang.Object)
     */
    public Object doTransform(Object src, String encoding) throws TransformerException
    {
        NameString nameString = new NameString();
        String name = (String) src;
        nameString.setName(name.replaceAll("\r", "").replaceAll("\n", "").trim());            
        return nameString;
    }
}
