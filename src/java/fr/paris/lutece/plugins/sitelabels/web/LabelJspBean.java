/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Label features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageLabels.jsp", controllerPath = "jsp/admin/plugins/sitelabels/", right = "SITELABELS_MANAGEMENT" )
public class LabelJspBean extends ManageSiteLabelsJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

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

    // Session variable to store working values
    private Label _label;
    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Get ManageLabels View
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_MANAGE_LABELS, defaultView = true )
    public String getManageLabels( HttpServletRequest request )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_LABEL_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_LABELS );
        String strUrl = url.getUrl(  );
        List<Label> listLabels = (List<Label>) LabelService.getLabelsList(  );

        // PAGINATOR
        LocalizedPaginator paginator = new LocalizedPaginator( listLabels, _nItemsPerPage, strUrl,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LABEL_LIST, paginator.getPageItems(  ) );

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

        Map<String, Object> model = getModel(  );
        model.put( MARK_LABEL, _label );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_LABEL, TEMPLATE_CREATE_LABEL, model );
    }

    /**
     * Process the data capture form of a new label
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_LABEL )
    public String doCreateLabel( HttpServletRequest request )
    {
        populate( _label, request );

        // Check constraints
        if ( !validateBean( _label, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_LABEL );
        }

        _label.setKey( LabelService.PREFIX + _label.getKey(  ) );
        LabelService.create( _label );
        _label = null;
        addInfo( INFO_LABEL_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_LABELS );
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

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_LABEL,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a label
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage labels
     */
    @Action( ACTION_REMOVE_LABEL )
    public String doRemoveLabel( HttpServletRequest request )
    {
        String strKey = request.getParameter( PARAMETER_KEY );
        LabelService.remove( strKey );
        addInfo( INFO_LABEL_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_LABELS );
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

        if ( _label == null )
        {
            _label = LabelService.findByPrimaryKey( strKey );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_LABEL, _label );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_LABEL, TEMPLATE_MODIFY_LABEL, model );
    }

    /**
     * Process the change form of a label
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_LABEL )
    public String doModifyLabel( HttpServletRequest request )
    {
        populate( _label, request );

        // Check constraints
        if ( !validateBean( _label, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            Map<String, String> mapParameters = new HashMap<String, String>(  );
            mapParameters.put( PARAMETER_KEY, _label.getKey(  ) );

            return redirect( request, VIEW_MODIFY_LABEL, mapParameters );
        }

        LabelService.update( _label );
        _label = null;
        addInfo( INFO_LABEL_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_LABELS );
    }
}
