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
package fr.paris.lutece.plugins.sitelabels.web;

import fr.paris.lutece.plugins.sitelabels.business.Label;
import fr.paris.lutece.plugins.sitelabels.service.LabelService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.binding.BindingResult;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.ModelAttribute;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


/**
 * This class provides the user interface to manage Label features ( manage, create, modify, remove )
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageLabels.jsp", controllerPath = "jsp/admin/plugins/sitelabels/", right = "SITELABELS_MANAGEMENT" )
public class LabelJspBean extends MVCAdminJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // Right
    public static final String RIGHT_MANAGESITELABELS = "SITELABELS_MANAGEMENT";

    // templates
    private static final String TEMPLATE_MANAGE_LABELS = "/admin/plugins/sitelabels/manage_labels.html";
    private static final String TEMPLATE_CREATE_LABEL = "/admin/plugins/sitelabels/create_label.html";
    private static final String TEMPLATE_MODIFY_LABEL = "/admin/plugins/sitelabels/modify_label.html";

    // Parameters
    private static final String PARAMETER_KEY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_LABELS = "sitelabels.manage_labels.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_LABEL = "sitelabels.modify_label.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_LABEL = "sitelabels.create_label.pageTitle";

    // Markers
    private static final String MARK_LABEL_LIST = "label_list";
    private static final String MARK_LABEL = "label";
    private static final String JSP_MANAGE_LABELS = "jsp/admin/plugins/sitelabels/ManageLabels.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_LABEL = "sitelabels.message.confirmRemoveLabel";
    private static final String PROPERTY_DEFAULT_LIST_LABEL_PER_PAGE = "sitelabels.listLabels.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "sitelabels.model.entity.label.attribute.";

    // Views
    private static final String VIEW_MANAGE_LABELS = "manageLabels";
    private static final String VIEW_CREATE_LABEL = "createLabel";
    private static final String VIEW_MODIFY_LABEL = "modifyLabel";

    // Actions
    private static final String ACTION_CREATE_LABEL = "createLabel";
    private static final String ACTION_MODIFY_LABEL = "modifyLabel";
    private static final String ACTION_REMOVE_LABEL = "removeLabel";
    private static final String ACTION_CONFIRM_REMOVE_LABEL = "confirmRemoveLabel";

    // Infos
    private static final String INFO_LABEL_CREATED = "sitelabels.info.label.created";
    private static final String INFO_LABEL_UPDATED = "sitelabels.info.label.updated";
    private static final String INFO_LABEL_REMOVED = "sitelabels.info.label.removed";

    private Label _label;

    @Inject
    @Pager( listBookmark = MARK_LABEL_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_LABEL_PER_PAGE)
    private IPager<Label, Void> _pager;
    @Inject Models model;

    /**
     * Get ManageLabels View
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_MANAGE_LABELS, defaultView = true )
    public String getManageLabels( HttpServletRequest request )
    {
        List<Label> listLabels = LabelService.getLabelsList(  );

        String strURL = getHomeUrl( request );

        _pager.withBaseUrl(strURL)
                .withListItem(listLabels)
                .populateModels(request, model, getLocale());

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_LABELS, TEMPLATE_MANAGE_LABELS, model );
    }

    /**
     * Returns the form to create a label
     *
     * @param request The Http request
     * @return the html code of the label form
     */
    @View( VIEW_CREATE_LABEL )
    public String getCreateLabel( HttpServletRequest request )
    {
        _label = ( _label != null ) ? _label : new Label(  );

        model.put( MARK_LABEL, _label );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_CREATE_LABEL ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_LABEL, TEMPLATE_CREATE_LABEL, model );
    }

    /**
     * Process the data capture form of a new label
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException if the CSRF token cannot be validated
     */
    @Action( ACTION_CREATE_LABEL )
    public String doCreateLabel(@Valid @ModelAttribute Label label,
                                BindingResult bindingResult,
                                HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !getSecurityTokenService().validate( request, ACTION_CREATE_LABEL ) ) {
            throw new AccessDeniedException( "Invalid CSRF token" );
        }

        if( bindingResult.isFailed() )
        {
            model.put( MVCUtils.MARK_ERRORS, bindingResult.getAllErrors( ) );
            model.put( MARK_LABEL, label );
            model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_CREATE_LABEL ) );
            return getPage( PROPERTY_PAGE_TITLE_CREATE_LABEL, TEMPLATE_CREATE_LABEL, model );
        }

        label.setKey( LabelService.PREFIX + label.getKey(  ) );
        LabelService.create( label );

        return redirectToManageView( request, INFO_LABEL_CREATED );
    }

    /**
     * Manages the removal form of a label whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_LABEL )
    public String getConfirmRemoveLabel( HttpServletRequest request )
    {
        String strKey = request.getParameter( PARAMETER_KEY );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_LABEL ) );
        url.addParameter( PARAMETER_KEY, strKey );
        url.addParameter( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService().getToken( request, ACTION_REMOVE_LABEL ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_LABEL,
                new Object[] { strKey }, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a label
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage labels
     * @throws AccessDeniedException if the CSRF token cannot be validated
     */
    @Action( ACTION_REMOVE_LABEL )
    public String doRemoveLabel( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !getSecurityTokenService().validate( request, ACTION_REMOVE_LABEL ) ) {
            throw new AccessDeniedException( "Invalid CSRF token" );
        }

        String strKey = request.getParameter( PARAMETER_KEY );
        LabelService.remove( strKey );

        return redirectToManageView( request, INFO_LABEL_REMOVED );
    }

    /**
     * Returns the form to update info about a label
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_LABEL )
    public String getModifyLabel( HttpServletRequest request )
    {
        String strKey = request.getParameter( PARAMETER_KEY );

        if ( _label == null || ( !_label.getKey().equals( strKey )))
        {
            _label = LabelService.findByPrimaryKey( strKey );
        }

        model.put( MARK_LABEL, _label );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_MODIFY_LABEL ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_LABEL, TEMPLATE_MODIFY_LABEL, model );
    }

    /**
     * Process the change form of a label
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException if the CSRF token cannot be validated
     */
    @Action( ACTION_MODIFY_LABEL )
    public String doModifyLabel( @Valid @ModelAttribute Label label,
                                    BindingResult bindingResult,
                                    HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !getSecurityTokenService().validate( request, ACTION_MODIFY_LABEL ) ) {
            throw new AccessDeniedException( "Invalid CSRF token" );
        }

        if (bindingResult.isFailed() )
        {
            model.put( MVCUtils.MARK_ERRORS, bindingResult.getAllErrors() );
            model.put( MARK_LABEL, label );
            model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_MODIFY_LABEL ) );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_LABEL, TEMPLATE_MODIFY_LABEL, model );
        }

        LabelService.update( label );

        return redirectToManageView( request, INFO_LABEL_UPDATED );
    }

    /**
     * Redirect to manage view with information message
     * @param request The Http request
     * @param keyInfo key of info message
     * @return The Jsp URL of the process result
     */
    private String redirectToManageView( HttpServletRequest request, String keyInfo )
    {
        addInfo( keyInfo, getLocale() );

        List<Label> listLabels = LabelService.getLabelsList(  );

        String strURL = getHomeUrl( request );

        _pager.withBaseUrl(strURL)
                .withListItem(listLabels)
                .populateModels(request, model, getLocale());

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_LABELS, TEMPLATE_MANAGE_LABELS, model );
    }

}
