package com.github.st1hy.countthemcalories.core.permissions;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class PersistentPermissionCache extends ForwardingMap<String, Permission> {

    final Map<String, Permission> cache = Maps.newConcurrentMap();

    public PersistentPermissionCache() {
    }

    @Override
    protected Map<String, Permission> delegate() {
        return cache;
    }
}
