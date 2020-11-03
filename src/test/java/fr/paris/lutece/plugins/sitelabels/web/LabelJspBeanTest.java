/*
 * Copyright (c) 2015, Mairie de Paris
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
package fr.paris.lutece.plugins.sitelabels.web;

import java.util.Locale;

import fr.paris.lutece.plugins.sitelabels.business.Label;
import fr.paris.lutece.plugins.sitelabels.service.LabelService;
import fr.paris.lutece.plugins.sitelabels.utils.MockHttpServletResponse;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.MokeHttpServletRequest;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

public class LabelJspBeanTest extends LuteceTestCase
{
    public void testGetConfirmRemoveLabel(  ) throws AccessDeniedException
    {
        LabelJspBean bean = new LabelJspBean( );
        MokeHttpServletRequest request = new MokeHttpServletRequest( );
        final String testKey = "test_key";
        request.addMokeParameters( "id", testKey );
        request.addMokeParameters( "action", "confirmRemoveLabel" );
        request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
        bean.processController( request, new MockHttpServletResponse( ) );
        AdminMessage message = AdminMessageService.getMessage( request );
        assertNotNull( message );
        ReferenceList listLanguages = I18nService.getAdminLocales( Locale.FRANCE );
        for ( ReferenceItem lang : listLanguages )
        {
            assertTrue( message.getText( new Locale( lang.getCode( ) ) ).contains( testKey ) );
        }
    }

    public void testDoCreateLabel(  )
    {
        LabelJspBean bean = new LabelJspBean( );
        MokeHttpServletRequest request = new MokeHttpServletRequest( );
        try
        {
            // populate LabelJspBean#_label
            bean.getCreateLabel( request );
        } catch ( Throwable t)
        {
        }
        final String testKey = "test_key";
        request.addMokeParameters( "key", testKey );
        request.addMokeParameters( "value", testKey );
        request.addMokeParameters( "action", "createLabel" );
        request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
        try {
            bean.processController( request, new MockHttpServletResponse( ) );
            fail( "Should not succeed without CSRF token" );
        } catch ( AccessDeniedException e)
        {
        } finally
        {
            LabelService.remove( LabelService.PREFIX + testKey );
        }
        request = new MokeHttpServletRequest( );
        request.addMokeParameters( "key", testKey );
        request.addMokeParameters( "value", testKey );
        request.addMokeParameters( "action", "createLabel" );
        request.addMokeParameters( SecurityTokenService.PARAMETER_TOKEN, SecurityTokenService.getInstance( ).getToken( request, "createLabel" ) );
        request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
        try {
            bean.processController( request, new MockHttpServletResponse( ) );
            Label label = LabelService.findByPrimaryKey( LabelService.PREFIX + testKey );
            assertNotNull( label );
            assertEquals( LabelService.PREFIX + testKey, label.getKey( ) );
            assertEquals( testKey, label.getValue( ) );
        } catch ( AccessDeniedException e)
        {
            fail( "Should succeed with CSRF token" );
        } finally
        {
            LabelService.remove( LabelService.PREFIX + testKey );
        }
    }

    public void testDoModifyLabel(  )
    {
        LabelJspBean bean = new LabelJspBean( );
        MokeHttpServletRequest request = new MokeHttpServletRequest( );
        try
        {
            // populate LabelJspBean#_label
            request.addMokeParameters( "id", LabelService.PREFIX + "testkeytomodify" );
            bean.getModifyLabel( request );
        } catch ( Throwable t)
        {
        }
        Label toModify = new Label( );
        toModify.setKey( LabelService.PREFIX + "testkeytomodify" );
        toModify.setValue( "orig" );
        LabelService.create( toModify );
        try
        {
            request.addMokeParameters( "key", toModify.getKey( ) );
            request.addMokeParameters( "value", "mod" );
            request.addMokeParameters( "action", "modifyLabel" );
            request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
            try {
                bean.processController( request, new MockHttpServletResponse( ) );
                fail( "Should not succeed without CSRF token" );
            } catch ( AccessDeniedException e)
            {
            }
            request = new MokeHttpServletRequest( );
            request.addMokeParameters( "key", toModify.getKey( ) );
            request.addMokeParameters( "value", "mod" );
            request.addMokeParameters( "action", "modifyLabel" );
            request.addMokeParameters( SecurityTokenService.PARAMETER_TOKEN, SecurityTokenService.getInstance( ).getToken( request, "modifyLabel" ) );
            request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
            try {
                bean.processController( request, new MockHttpServletResponse( ) );
                Label label = LabelService.findByPrimaryKey( toModify.getKey( ) );
                assertNotNull( label );
                assertEquals( toModify.getKey( ), label.getKey( ) );
                assertEquals( "mod", label.getValue( ) );
            } catch ( AccessDeniedException e)
            {
                fail( "Should succeed with CSRF token" );
            }
        } finally
        {
            LabelService.remove( toModify.getKey( ) );
        }
    }

    public void testDoRemoveLabel(  )
    {
        LabelJspBean bean = new LabelJspBean( );
        MokeHttpServletRequest request = new MokeHttpServletRequest( );
        Label toRemove = new Label( );
        toRemove.setKey( LabelService.PREFIX + "testkeytoremove" );
        toRemove.setValue( "orig" );
        LabelService.create( toRemove );
        try
        {
            request.addMokeParameters( "id", toRemove.getKey( ) );
            request.addMokeParameters( "action", "removeLabel" );
            request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
            try {
                bean.processController( request, new MockHttpServletResponse( ) );
                fail( "Should not succeed without CSRF token" );
            } catch ( AccessDeniedException e)
            {
            }
            request = new MokeHttpServletRequest( );
            request.addMokeParameters( "id", toRemove.getKey( ) );
            request.addMokeParameters( "action", "removeLabel" );
            request.addMokeParameters( SecurityTokenService.PARAMETER_TOKEN, SecurityTokenService.getInstance( ).getToken( request, "removeLabel" ) );
            request.registerAdminUserWithRigth( new AdminUser( ), "SITELABELS_MANAGEMENT" );
            try {
                bean.processController( request, new MockHttpServletResponse( ) );
                Label label = LabelService.findByPrimaryKey( toRemove.getKey( ) );
                assertNotNull( label );
                assertEquals( toRemove.getKey( ), label.getKey( ) );
                assertFalse( "orig".equals( label.getValue( ) ) );
            } catch ( AccessDeniedException e)
            {
                fail( "Should succeed with CSRF token" );
            }
        } finally
        {
            LabelService.remove( toRemove.getKey( ) );
        }
    }
}
