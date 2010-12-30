/*
 * $Id: Order.java 13643 2008-12-15 21:16:15Z aperepel $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.example.bookstore;

/**
 * Simple class which represents a Book Order.
 */
public class Order 
{
    private Book book;
    private int quantity;
    /** Shipping address */
    private String address;
    /** E-mail address used to receive order notifications */
    private String email;
    
    public Order()
    {
        // empty constructor
    }
    
    public Order(Book book, int quantity, String address, String email)
    {
        this.book = book;
        this.quantity = quantity;
        this.address = address;
        this.email = email;
    }
    
    public Book getBook()
    {
        return book;
    }

    public void setBook(Book book)
    {
        this.book = book;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
