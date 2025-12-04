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
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

public class LabelJspBeanTest extends LuteceTestCase
{

    private @Inject LabelJspBean instance;
    private @Inject ISecurityTokenService _securityTokenService;

    @Test
    public void testGetConfirmRemoveLabel(  ) throws AccessDeniedException
    {

        MockHttpServletRequest request = new MockHttpServletRequest( );
        final String testKey = "test_key";
        request.addParameter( "id", testKey );
        request.addParameter( "action", "confirmRemoveLabel" );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

        instance.processController( request, new MockHttpServletResponse());

        AdminMessage message = AdminMessageService.getMessage( request );
        assertNotNull( message );
        ReferenceList listLanguages = I18nService.getAdminLocales( Locale.FRANCE );
        for ( ReferenceItem lang : listLanguages )
        {
            assertTrue( message.getText( new Locale( lang.getCode( ) ) ).contains( testKey ) );
        }
    }

    @Test
    public void testDoCreateLabel(  )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        try
        {
            // populate LabelJspBean#_label
            instance.getCreateLabel( request );
        } catch ( Throwable t)
        {
        }
        final String testKey = "test_key";
        request.addParameter( "key", testKey );
        request.addParameter( "value", testKey );
        request.addParameter( "action", "createLabel" );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

        try {
            instance.processController( request, new MockHttpServletResponse( ) );
            fail( "Should not succeed without CSRF token" );
        } catch ( AccessDeniedException e)
        {
        } finally
        {
            LabelService.remove( LabelService.PREFIX + testKey );
        }
        request = new MockHttpServletRequest( );
        request.addParameter( "key", testKey );
        request.addParameter( "value", testKey );
        request.addParameter( "action", "createLabel" );
        request.addParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, "createLabel" ) );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

        try {
            instance.processController( request, new MockHttpServletResponse( ) );
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

    @Test
    public void testDoModifyLabel(  )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        try
        {
            // populate LabelJspBean#_label
            request.addParameter( "id", LabelService.PREFIX + "testkeytomodify" );
            instance.getModifyLabel( request );
        } catch ( Throwable t)
        {
        }
        Label toModify = new Label( );
        toModify.setKey( LabelService.PREFIX + "testkeytomodify" );
        toModify.setValue( "orig" );
        LabelService.create( toModify );
        try
        {
            request.addParameter( "key", toModify.getKey( ) );
            request.addParameter( "value", "mod" );
            request.addParameter( "action", "modifyLabel" );
            AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

            try {
                instance.processController( request, new MockHttpServletResponse( ) );
                fail( "Should not succeed without CSRF token" );
            } catch ( AccessDeniedException e)
            {
            }
            request = new MockHttpServletRequest( );
            request.addParameter( "key", toModify.getKey( ) );
            request.addParameter( "value", "mod" );
            request.addParameter( "action", "modifyLabel" );
            request.addParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, "modifyLabel" ) );
            AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

            try {
                instance.processController( request, new MockHttpServletResponse( ) );
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

    @Test
    public void testDoRemoveLabel(  )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        Label toRemove = new Label( );
        toRemove.setKey( LabelService.PREFIX + "testkeytoremove" );
        toRemove.setValue( "orig" );
        LabelService.create( toRemove );
        try
        {
            request.addParameter( "id", toRemove.getKey( ) );
            request.addParameter( "action", "removeLabel" );
            AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

            try {
                instance.processController( request, new MockHttpServletResponse( ) );
                fail( "Should not succeed without CSRF token" );
            } catch ( AccessDeniedException e)
            {
            }
            request = new MockHttpServletRequest( );
            request.addParameter( "id", toRemove.getKey( ) );
            request.addParameter( "action", "removeLabel" );
            request.addParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, "removeLabel" ) );
            AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), LabelJspBean.RIGHT_MANAGESITELABELS );

            try {
                instance.processController( request, new MockHttpServletResponse( ) );
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
