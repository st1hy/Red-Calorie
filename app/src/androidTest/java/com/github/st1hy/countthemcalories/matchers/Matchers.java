package com.github.st1hy.countthemcalories.matchers;

import android.content.res.Resources;

class Matchers {

    static String resIdToString(Resources viewResources, int id) {
        if (viewResources != null) {
            try {
                return viewResources.getResourceName(id);
            } catch (Resources.NotFoundException e) {
                //OK, going back to the integer
            }
        }
        return Integer.toString(id);
    }
}
