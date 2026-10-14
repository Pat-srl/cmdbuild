/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_CMDBUILD_CLASS;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.service.rest.v4.endpoint.ConfigurationWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class ConfigurationWsTest extends WsTestBase {

    private final ConfigurationWs instance;

    public ConfigurationWsTest() {
        this.instance = new ConfigurationWs(dmsConfiguration, objectTranslationService, multitenantConfiguration, webSocketConfiguration, workflowConfiguration, gisConfiguration, bimConfiguration, coreConfiguration, graphConfiguration, buildInfoService, uiConfiguration, mobileConfiguration, subscriptionConfiguration, authenticationConfiguration, passwordManagementConfiguration, operationUserSupplier, schedulerConfiguration, etlConfiguration, calendarServiceConfiguration, emailConfiguration, homepageConfiguration, aiConfiguration);
    }

    @Test
    public void testGetPublicConfig() {
        System.out.println("getPublicConfig");

        //arrange:
        when(authenticationConfiguration.isDefaultLoginModuleEnabled()).thenReturn(true);
        when(multitenantConfiguration.isMultitenantEnabled()).thenReturn(true);

        //act:
        Object resultObject = instance.getPublicConfig();

        //assert:
        verify(coreConfiguration, times(2)).getInstanceName();
        verify(objectTranslationService).translateByCode(eq("config.core.instance_name"), anyString());
        verify(buildInfoService).getVersionNumber();
        verify(buildInfoService).getPatchVersionNumber();
        verify(buildInfoService).getCompleteVersionNumberWithVertName();
        verify(coreConfiguration).getDefaultLanguage();
        verify(coreConfiguration).useLanguagePrompt();
        verify(multitenantConfiguration, times(2)).isMultitenantEnabled();
        verify(passwordManagementConfiguration).isPasswordChangeEnabled();
        verify(passwordManagementConfiguration).getDifferentFromPrevious();
        verify(passwordManagementConfiguration).getDifferentFromUsername();
        verify(passwordManagementConfiguration).getPasswordMinLength();
        verify(passwordManagementConfiguration).requireDigit();
        verify(passwordManagementConfiguration).requireLowercase();
        verify(passwordManagementConfiguration).requireUppercase();
        verify(passwordManagementConfiguration).isPasswordManagementEnabled();
        verify(authenticationConfiguration, times(2)).isDefaultLoginModuleEnabled();
        verify(webSocketConfiguration).isEnabled();
        verify(authenticationConfiguration).getLoginInputSource();
        verify(authenticationConfiguration).isLoginMobileUsernameEnabled();
        verify(authenticationConfiguration).isDefaultLoginModuleEnabledAndVisible();
        verify(authenticationConfiguration, times(2)).getNonDefaultNonHiddenActiveLoginModules();
        verify(authenticationConfiguration).isHeaderEnabled();
        verify(authenticationConfiguration).getLoginHelp();

        verify(coreConfiguration).getCompanyLogoUploadsId();
        verify(uiConfiguration).getUiTimeout();
        verify(multitenantConfiguration).getTenantName();
    }

    @Test
    public void testGetSystemConfig() {
        System.out.println("getSystemConfig");

        //arrange:
        when(authenticationConfiguration.isDefaultLoginModuleEnabled()).thenReturn(true);
        when(multitenantConfiguration.isMultitenantEnabled()).thenReturn(true);
        when(gisConfiguration.isEnabled()).thenReturn(true);
        when(coreConfiguration.isChatEnabled()).thenReturn(true);
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(operationUserSupplier.hasPrivileges(any(OperationUserSupplier.PrivilegeChecker.class))).thenReturn(true);
        when(multitenantConfiguration.getMultitenantMode()).thenReturn(MTM_CMDBUILD_CLASS);

        //act:
        Object resultObject = instance.getSystemConfig();

        //assert:
        verify(coreConfiguration, times(2)).getInstanceName();
        verify(objectTranslationService).translateByCode(eq("config.core.instance_name"), anyString());
        verify(buildInfoService).getVersionNumber();
        verify(buildInfoService).getPatchVersionNumber();
        verify(buildInfoService).getCompleteVersionNumberWithVertName();
        verify(coreConfiguration).getDefaultLanguage();
        verify(coreConfiguration).useLanguagePrompt();
        verify(multitenantConfiguration, times(2)).isMultitenantEnabled();
        verify(passwordManagementConfiguration).isPasswordChangeEnabled();
        verify(passwordManagementConfiguration).getDifferentFromPrevious();
        verify(passwordManagementConfiguration).getDifferentFromUsername();
        verify(passwordManagementConfiguration).getPasswordMinLength();
        verify(passwordManagementConfiguration).requireDigit();
        verify(passwordManagementConfiguration).requireLowercase();
        verify(passwordManagementConfiguration).requireUppercase();
        verify(passwordManagementConfiguration).isPasswordManagementEnabled();
        verify(authenticationConfiguration, times(2)).isDefaultLoginModuleEnabled();
        verify(webSocketConfiguration).isEnabled();
        verify(authenticationConfiguration).getLoginInputSource();
        verify(authenticationConfiguration).isLoginMobileUsernameEnabled();
        verify(authenticationConfiguration).isDefaultLoginModuleEnabledAndVisible();
        verify(authenticationConfiguration, times(2)).getNonDefaultNonHiddenActiveLoginModules();
        verify(authenticationConfiguration).isHeaderEnabled();
        verify(authenticationConfiguration).getLoginHelp();
        verify(coreConfiguration).getCompanyLogoUploadsId();
        verify(uiConfiguration).getUiTimeout();
        verify(multitenantConfiguration).getTenantName();

        verify(authenticationConfiguration).getLogoutRedirectUrl();
        verify(coreConfiguration).sessionKeepaliveEnabled();
        verify(dmsConfiguration).isEnabled();
        verify(dmsConfiguration).getDefaultDmsCategory();
        verify(dmsConfiguration).isRegularAttachmentsFileExtensionCheckEnabled();
        verify(dmsConfiguration).getRegularAttachmentsAllowedFileExtensions();
        verify(dmsConfiguration).getMaxFileSize();
        verify(emailConfiguration).getMaxAttachmentSizeForEmailMegs();
        verify(workflowConfiguration).isEnabled();
        verify(workflowConfiguration).hideSaveButton();
        verify(workflowConfiguration).enableAddAttachmentOnClosedActivities();
        verify(gisConfiguration, times(2)).isEnabled();
        verify(gisConfiguration).isGeoServerEnabled();
        verify(gisConfiguration).isNavigationEnabled();
        verify(bimConfiguration).isEnabled();
        verify(mobileConfiguration, times(2)).isMobileEnabled();
        verify(uiConfiguration).getDetailWindowWidth();
        verify(uiConfiguration).getDetailWindowHeight();
        verify(uiConfiguration).getInlineCardHeight();
        verify(uiConfiguration).getPopupWindowWidth();
        verify(uiConfiguration).getPopupWindowHeight();
        verify(uiConfiguration).getStartDay();
        verify(uiConfiguration).getReferenceComboLimit();
        verify(uiConfiguration).getDmsMaxPreviewLimit();
        verify(uiConfiguration).getEmailGroupByStatus();
        verify(uiConfiguration).getEmailDefaultDelay();
        verify(coreConfiguration).getCardlockEnabled();
        verify(coreConfiguration).getCardlockShowUser();
        verify(uiConfiguration).getKeepFilterOnUpdatedCard();
        verify(uiConfiguration).getDecimalsSeparator();
        verify(uiConfiguration).getThousandsSeparator();
        verify(uiConfiguration).getDateFormat();
        verify(uiConfiguration).getTimeFormat();
        verify(coreConfiguration).getRelationLimit();
        verify(coreConfiguration).isChangePasswordRequiredForNewUser();
        verify(schedulerConfiguration).isEnabled();
        verify(schedulerConfiguration).getSelectableClasses();
        verify(etlConfiguration).getValueArraySeparator();
        verify(coreConfiguration).isChatEnabled();
        verify(operationUserSupplier, times(2)).hasPrivileges(any(OperationUserSupplier.PrivilegeChecker.class));
        verify(calendarServiceConfiguration).isEnabled();
        verify(uiConfiguration).isRedirectToSubclassEnabled();
        verify(uiConfiguration).isRedirectToViewEnabled();
        verify(graphConfiguration).getConfig();

        verify(gisConfiguration).getCenterLat();
        verify(gisConfiguration).getCenterLon();
        verify(gisConfiguration).getInitialZoomLevel();
        verify(gisConfiguration).isKeepZoomAndPositionEnabled();
        verify(gisConfiguration).minZoomLevel();
        verify(gisConfiguration).maxZoomLevel();

        verify(subscriptionConfiguration).getSubscriptionCustomerCode();
        verify(mobileConfiguration).getMobileDeviceNamePrefix();
        verify(multitenantConfiguration).getMultitenantMode();
    }
}
