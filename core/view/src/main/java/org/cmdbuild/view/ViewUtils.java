/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view;

import org.cmdbuild.cleanup.ViewType;
import static com.google.common.base.Preconditions.checkArgument;

public class ViewUtils {

    public static void checkViewIsFilterView(View view) {
        checkArgument(view.isOfType(ViewType.VT_FILTER), "this action is available only for 'filter' type views, this view is of type = %s", view.getType());
    }
}
