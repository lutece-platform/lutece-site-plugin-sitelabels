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
package fr.paris.lutece.plugins.sitelabels.service;

import fr.paris.lutece.plugins.sitelabels.business.Label;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * Label objects
 */
public final class LabelService
{
    public static final String PREFIX = "sitelabels.site_property.";

    /**
     * Private constructor - this class need not be instantiated
     */
    private LabelService(  )
    {
    }

    /**
     * Create an instance of the label class
     *
     * @param label The instance of the Label which contains the informations to
     * store
     * @return The instance of label which has been created with its primary
     * key.
     */
    public static Label create( Label label )
    {
        DatastoreService.setDataValue( label.getKey(  ), label.getValue(  ) );

        return label;
    }

    /**
     * Update of the label which is specified in parameter
     *
     * @param label The instance of the Label which contains the data to store
     * @return The instance of the label which has been updated
     */
    public static Label update( Label label )
    {
        DatastoreService.setDataValue( label.getKey(  ), label.getValue(  ) );

        return label;
    }

    /**
     * Remove the label whose identifier is specified in parameter
     *
     * @param strKey The Key
     */
    public static void remove( String strKey )
    {
        DatastoreService.removeData( strKey );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Returns an instance of a label whose identifier is specified in parameter
     *
     * @param strKey The label primary key
     * @return an instance of Label
     */
    public static Label findByPrimaryKey( String strKey )
    {
        Label label = new Label(  );
        label.setKey( strKey );
        label.setValue( DatastoreService.getDataValue( strKey, "" ) );

        return label;
    }

    /**
     * Load the data of all the label objects and returns them in form of a
     * collection
     *
     * @return the collection which contains the data of all the label objects
     */
    public static List<Label> getLabelsList(  )
    {
        List<Label> listLabels = new ArrayList<Label>(  );
        ReferenceList list = DatastoreService.getInstanceDataByPrefix( PREFIX );

        for ( ReferenceItem item : list )
        {
            Label label = new Label(  );
            label.setKey( item.getCode(  ) );
            label.setValue( item.getName(  ) );
            listLabels.add( label );
        }

        return listLabels;
    }
}
