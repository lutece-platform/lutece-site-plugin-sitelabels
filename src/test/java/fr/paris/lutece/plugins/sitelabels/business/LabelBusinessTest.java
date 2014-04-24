/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.sitelabels.business;

import fr.paris.lutece.plugins.sitelabels.service.LabelService;
import fr.paris.lutece.test.LuteceTestCase;


public class LabelBusinessTest extends LuteceTestCase
{
    private final static String KEY1 = "Key1";
    private final static String KEY2 = "Key2";
    private final static String VALUE1 = "Value1";
    private final static String VALUE2 = "Value2";

    public void testBusiness(  )
    {
        // Initialize an object
        Label label = new Label(  );
        label.setKey( KEY1 );
        label.setValue( VALUE1 );

        // Create test
        LabelService.create( label );

        Label labelStored = LabelService.findByPrimaryKey( label.getKey(  ) );
        assertEquals( labelStored.getKey(  ), label.getKey(  ) );
        assertEquals( labelStored.getValue(  ), label.getValue(  ) );

        // Update test
        label.setKey( KEY2 );
        label.setValue( VALUE2 );
        LabelService.update( label );
        labelStored = LabelService.findByPrimaryKey( label.getKey(  ) );
        assertEquals( labelStored.getKey(  ), label.getKey(  ) );
        assertEquals( labelStored.getValue(  ), label.getValue(  ) );

        // List test
        LabelService.getLabelsList(  );

        // Delete test
        LabelService.remove( label.getKey(  ) );
        labelStored = LabelService.findByPrimaryKey( label.getKey(  ) );
        assertNull( labelStored );
    }
}
