/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

public interface DxfGroupCodes {

    public static final int //
            DXF_GROUPCODE_TYPE0 = 0,//Text string indicating the entity type (fixed)
            DXF_GROUPCODE_VALUE1 = 1,//Primary text value for an entity
            DXF_GROUPCODE_NAME2 = 2,//Name (attribute tag, block name, and so on)
            DXF_GROUPCODE_NAME3 = 3,//Other text or name values
            DXF_GROUPCODE_NAME4 = 4,//Other text or name values
            DXF_GROUPCODE_HANDLE5 = 5,//Entity handle; text string of up to 16 hexadecimal digits (fixed)
            DXF_GROUPCODE_LINETYPE6 = 6,//Linetype name (fixed)
            DXF_GROUPCODE_TEXTSTYLE7 = 7,//Text style name (fixed)
            DXF_GROUPCODE_LAYERNAME8 = 8,//Layer name (fixed)
            DXF_GROUPCODE_HEADERVARNAME9 = 9;//DXF: variable name identifier (used only in HEADER section of the DXF file)

}
